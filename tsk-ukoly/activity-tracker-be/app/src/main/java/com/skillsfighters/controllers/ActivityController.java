package com.skillsfighters.controllers;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.skillsfighters.controllers.requests.ActivityCreate;
import com.skillsfighters.controllers.requests.ActivityUpdate;
import com.skillsfighters.controllers.responses.ActivityList;
import com.skillsfighters.controllers.responses.GroupResponseList;
import com.skillsfighters.controllers.responses.GroupsResponse;
import com.skillsfighters.domain.*;
import com.skillsfighters.repository.ActivityGroupRepository;
import com.skillsfighters.repository.ActivityRepositoryCrud;
import com.skillsfighters.repository.DeviceGroupRepositoryCrud;
import com.skillsfighters.security.SecurityInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/activity")
public class ActivityController implements SecurityInfo {
    private final ActivityRepositoryCrud activityRepositoryCrud;
    private final ActivityGroupRepository activityGroupRepository;
    private final DeviceGroupRepositoryCrud deviceGroupRepositoryCrud;

    @Autowired
    public ActivityController(@NotNull final ActivityRepositoryCrud activityRepositoryCrud,
                              @NotNull final ActivityGroupRepository activityGroupRepository,
                              @NotNull final DeviceGroupRepositoryCrud deviceGroupRepositoryCrud) {
        this.activityRepositoryCrud = activityRepositoryCrud;
        this.activityGroupRepository = activityGroupRepository;
        this.deviceGroupRepositoryCrud = deviceGroupRepositoryCrud;
    }

    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public ResponseEntity<Activity> showActivity(@RequestParam(value = "activityid") final long activityId) {
        if (activityId <= 0L) {
            log.warn("user: {} - showActivity - activity ID is not positive - value is: {}", printLoggedUserFirebaseUid(), activityId);
            return ResponseEntity.badRequest().build();
        } else {
            final Optional<ActivityDTO> activityDTO = activityRepositoryCrud.findById(activityId);
            if (activityDTO.isPresent()) {
                final Activity activityToShow = activityDTO.get().toActivity();
                log.debug("user: {} - showActivity - activity: {} successfully showed", printLoggedUserFirebaseUid(), activityId);
                return new ResponseEntity<>(activityToShow, HttpStatus.OK);
            } else {
                log.warn("user: {} - showActivity - activity: {} not found", printLoggedUserFirebaseUid(), activityId);
                return ResponseEntity.notFound().build();
            }
        }
    }

    @RequestMapping(value = "/showall", method = RequestMethod.GET)
    public ResponseEntity<ActivityList> showActivities() {

        final Optional<Long> userId = getLoggedUserId();
        Optional<GroupResponseList> groupsResponses = activityGroupRepository.showGroups(userId.get());

        final List<Activity> activities = new ArrayList<>();
        for (GroupsResponse groupsResponse : groupsResponses.get()) {
            final List<ActivityDTO> activityDTOs = activityRepositoryCrud.findAllByGroupId(groupsResponse.getId());
            activities.addAll(activityDTOs.stream()
                    .map(ActivityDTO::toActivity)
                    .collect(Collectors.toList()));
        }

        log.debug("user: {} - showActivities - activities successfully showed", printLoggedUserFirebaseUid());
        return new ResponseEntity<>(new ActivityList(activities), HttpStatus.OK);
    }

    @RequestMapping(value = "/showallbyparentid", method = RequestMethod.GET, params = {"groupid"})
    public ResponseEntity<ActivityList> showActivitiesByParentId(@RequestParam(value = "groupid") final long groupId) {
        final Optional<Long> userId = getLoggedUserId();
        if (!userId.isPresent() || groupId < 0L) {
            log.warn("user: {} - showActivitiesByParentId - group ID is negative - value is: {}", printLoggedUserFirebaseUid(), groupId);
            return ResponseEntity.badRequest().build();
        } else {
            log.debug("user: {} - showActivitiesByParentId - activities from group: {} successfully showed", printLoggedUserFirebaseUid(), groupId);
            final List<ActivityDTO> activityDTOs = new ArrayList<ActivityDTO>();

            Queue<Long> activityGroups = new LinkedList<>();
            activityGroups.add(groupId);

            while (!activityGroups.isEmpty()) {
                Long activityGroup = activityGroups.remove();
                List<ActivityDTO> allByGroupId = activityRepositoryCrud.findAllByGroupId(activityGroup);

                if (allByGroupId.isEmpty()) {
                    Optional<GroupResponseList> groupsResponses = activityGroupRepository.showGroupsByParentId(userId.get(), activityGroup);
                    groupsResponses.ifPresent(responses -> responses.forEach((i) ->
                            activityGroups.add(i.getId())
                    ));
                } else {
                    activityDTOs.addAll(allByGroupId);
                }
            }

            final List<Activity> activities = activityDTOs.stream()
                    .map(ActivityDTO::toActivity)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(new ActivityList(activities), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/showallbyparentid", method = RequestMethod.GET, params = {"groupid", "startdate", "enddate"})
    public ResponseEntity<ActivityList> showActivitiesByParentId(@RequestParam(value = "groupid") final long groupId,
                                                                 @RequestParam(value = "startdate") final long startDate,
                                                                 @RequestParam(value = "enddate") final long endDate) {
        if (groupId <= 0L) {
            log.warn("user: {} - showActivitiesByParentId - group ID is not positive - value is: {}", printLoggedUserFirebaseUid(), groupId);
            return ResponseEntity.badRequest().build();
        } else {
            log.debug("user: {} - showActivitiesByParentId - activities from group: {} successfully showed", printLoggedUserFirebaseUid(), groupId);
            final List<ActivityDTO> activityDTOs = activityRepositoryCrud.findAllByGroupId(groupId);
            final List<Activity> activities = activityDTOs.stream()
                    .map(activityDTO -> activityDTO.toActivity())
                    .filter(activity -> activity.getTimestamp() >= startDate &&
                                        activity.getTimestamp() <= endDate)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(new ActivityList(activities), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.PUT)
    public ResponseEntity<Activity> addActivity(@RequestBody final ActivityCreate activityCreate) {
        if (activityCreate.getGroupId() <= 0L) {
            log.warn("user: {} - addActivity - group ID is not positive - value is: {}", printLoggedUserFirebaseUid(), activityCreate.getGroupId());
            return ResponseEntity.badRequest().build();
        } else {
            Date timestamp = new Date();
            final ActivityDTO activityToSaveDTO = new ActivityDTO();
            activityToSaveDTO.setTimestamp(timestamp);
            activityToSaveDTO.setGroupId(activityCreate.getGroupId());
            activityToSaveDTO.setCreatedAt(timestamp);
            activityToSaveDTO.setUpdatedAt(timestamp);
            final ActivityDTO savedActivityDTO = activityRepositoryCrud.save(activityToSaveDTO);
            log.debug("user: {} - addActivity - activity {} successfully added to group: {}", printLoggedUserFirebaseUid(), activityToSaveDTO.getId(), activityCreate.getGroupId());
            sendRefreshMessage();
            return new ResponseEntity(savedActivityDTO.toActivity(), HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<Activity> updateActivity(@RequestBody final ActivityUpdate activityToUpdate) {
        if (activityToUpdate.getTimestamp() <= 0L || activityToUpdate.getId() <= 0L) {
            log.warn("user: {} - updateActivity - one or more invalid inputs values are: id: {}, timestamp: {}",
                    printLoggedUserFirebaseUid(), activityToUpdate.getId(), activityToUpdate.getTimestamp());
            return ResponseEntity.badRequest().build();
        } else {
            final Optional<ActivityDTO> activityDTO = activityRepositoryCrud.findById(activityToUpdate.getId());
            if (activityDTO.isPresent()) {
                activityDTO.get().setTimestamp(new Date(activityToUpdate.getTimestamp()));
                activityDTO.get().setUpdatedAt(new Date());
                ActivityDTO updatedActivity = activityRepositoryCrud.save(activityDTO.get());
                log.debug("user: {} - updateActivity - activity {} successfully updated", printLoggedUserFirebaseUid(), activityToUpdate.getId());
                sendRefreshMessage();
                return new ResponseEntity(updatedActivity.toActivity(), HttpStatus.OK);
            } else {
                log.warn("user: {} - updateActivity - no activity {} found",
                        printLoggedUserFirebaseUid(), activityToUpdate.getId());
                return ResponseEntity.notFound().build();
            }
        }
    }

    @RequestMapping(value = "/updatetonow", method = RequestMethod.POST)
    public ResponseEntity<Activity> updateToNowActivity(@RequestParam(value = "activityid") final long activityId) {
        if (activityId <= 0L) {
            log.warn("user: {} - updateToNowActivity - invalid input value is: id: {}",
                    printLoggedUserFirebaseUid(), activityId);
            return ResponseEntity.badRequest().build();
        } else {
            final Optional<ActivityDTO> activityDTO = activityRepositoryCrud.findById(activityId);
            if (activityDTO.isPresent()) {
                Date timestamp = new Date();
                activityDTO.get().setTimestamp(timestamp);
                activityDTO.get().setUpdatedAt(timestamp);
                ActivityDTO updatedActivity = activityRepositoryCrud.save(activityDTO.get());
                log.debug("user: {} - updateActivity - activity {} successfully updated, group: {}", printLoggedUserFirebaseUid(), activityId);
                sendRefreshMessage();
                return new ResponseEntity(updatedActivity.toActivity(), HttpStatus.OK);
            } else {
                log.warn("user: {} - updateToNowActivity - no activity {} found in group: {}",
                        printLoggedUserFirebaseUid(), activityId);
                return ResponseEntity.notFound().build();
            }
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteActivity(@RequestParam(value = "activityid") final long activityId) {
        if (activityId <= 0L) {
            log.warn("user: {} - delete activity - invalid input activity ID value is: {}", printLoggedUserFirebaseUid(), activityId);
            return ResponseEntity.badRequest().build();
        } else {
            if (activityRepositoryCrud.existsById(activityId)) {
                Optional<ActivityDTO> activityToDelete = activityRepositoryCrud.findById(activityId);
                activityToDelete.get().setDeleted(true);
                activityRepositoryCrud.save(activityToDelete.get());
                log.debug("user: {} - deleteActivity - activity {} successfully deleted", printLoggedUserFirebaseUid(), activityId);
                sendRefreshMessage();
                return ResponseEntity.ok().build();
            } else {
                log.warn("user: {} - deleteActivity - no activity {} found", printLoggedUserFirebaseUid(), activityId);
                return ResponseEntity.notFound().build();
            }
        }
    }

    private void sendRefreshMessage() {
        Optional<DeviceGroupDTO> deviceGroupDTO = deviceGroupRepositoryCrud.findByUserId(printLoggedUserFirebaseUid());
        if (deviceGroupDTO.isPresent()) {
            DeviceGroup deviceGroup = deviceGroupDTO.get().toDeviceGroup();

            for (String token: deviceGroup.getFCMTokens()) {
                Message message = Message.builder()
                        .putData("Data", "Refresh")
                        .setToken(token)
                        .build();
                try {
                    FirebaseMessaging.getInstance().send(message);
                } catch (FirebaseMessagingException e) {
                    log.error("user: {} - sendRefreshMessage - FCMToken {}", printLoggedUserFirebaseUid(), token);
                    e.printStackTrace();
                }
            }
        }
    }
}
