package com.skillsfighters.controllers;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.skillsfighters.controllers.requests.ActivityGroupCreate;
import com.skillsfighters.controllers.responses.EntityCreated;
import com.skillsfighters.controllers.responses.GroupResponseList;
import com.skillsfighters.domain.ActivityDTO;
import com.skillsfighters.domain.ActivityGroup;
import com.skillsfighters.domain.DeviceGroup;
import com.skillsfighters.domain.DeviceGroupDTO;
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

import static java.lang.Character.isWhitespace;

@RestController
@Slf4j
@RequestMapping("/group")
public class ActivityGroupController implements SecurityInfo {
    private final ActivityGroupRepository activityGroupRepository;
    private final ActivityRepositoryCrud activityRepositoryCrud;
    private final DeviceGroupRepositoryCrud deviceGroupRepositoryCrud;


    @Autowired
    public ActivityGroupController(@NotNull final ActivityGroupRepository activityGroupRepository,
                                   @NotNull final ActivityRepositoryCrud activityRepositoryCrud,
                                   @NotNull final DeviceGroupRepositoryCrud deviceGroupRepositoryCrud) {
        this.activityGroupRepository = activityGroupRepository;
        this.activityRepositoryCrud = activityRepositoryCrud;
        this.deviceGroupRepositoryCrud = deviceGroupRepositoryCrud;
    }


    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public ResponseEntity<ActivityGroup> showActivityGroup(@RequestParam(value = "groupid") final long groupId) {
        if (groupId <= 0L) {
            log.warn("user: {} - showActivityGroup - group ID is not positive - value is: {}", printLoggedUserFirebaseUid(), groupId);
            return ResponseEntity.badRequest().build();
        } else {
            Optional<ActivityGroup> groupToShow = activityGroupRepository.show(groupId);
            if (groupToShow.isPresent()) {
                log.debug("user: {} - showActivityGroup - activityGroup: {} successfully showed", printLoggedUserFirebaseUid(), groupId);
                return new ResponseEntity<>(groupToShow.get(), HttpStatus.OK);
            } else {
                log.warn("user: {} - showActivityGroup - activityGroup: {} not found", printLoggedUserFirebaseUid(), groupId);
                return ResponseEntity.notFound().build();
            }
        }
    }

    @RequestMapping(value = "/showall", method = RequestMethod.GET)
    public ResponseEntity<GroupResponseList> showGroups() {
        final Optional<Long> userId = getLoggedUserId();
        if (!userId.isPresent() || userId.get() <= 0L) {
            log.warn("user: {} - showGroups - user ID is not present or is not positive", printLoggedUserFirebaseUid());
            return ResponseEntity.badRequest().build();
        } else {
            Optional<GroupResponseList> groupsResponses = activityGroupRepository.showGroups(userId.get());
            if (groupsResponses.isPresent()) {
                log.debug("user: {} - showGroups - groups from user: {} successfully showed", printLoggedUserFirebaseUid(), userId);
                return new ResponseEntity<>(groupsResponses.get(), HttpStatus.OK);
            } else {
                log.warn("user: {} - showGroups - no groups from user {} found", printLoggedUserFirebaseUid(), userId);
                return ResponseEntity.notFound().build();
            }
        }
    }

    @RequestMapping(value = "/showallbyparentid", method = RequestMethod.GET)
    public ResponseEntity<GroupResponseList> showGroupsByParentId(@RequestParam(value = "parentid") final long parentId) {
        final Optional<Long> userId = getLoggedUserId();
        if (!userId.isPresent() || userId.get() <= 0L || parentId < 0L) {
            log.warn("user: {} - showGroupsByParentId - parent ID is negative - value is: {}", printLoggedUserFirebaseUid(), parentId);
            return ResponseEntity.badRequest().build();
        } else {
            Optional<GroupResponseList> groupsResponses = activityGroupRepository.showGroupsByParentId(userId.get(), parentId);
            if (groupsResponses.isPresent()) {
                log.debug("user: {} - showGroupsByParentId - groups with parentId: {} successfully showed", printLoggedUserFirebaseUid(), parentId);
                return new ResponseEntity<>(groupsResponses.get(), HttpStatus.OK);
            } else {
                log.warn("user: {} - showGroupsByParentId - no groups with parentId {} found", printLoggedUserFirebaseUid(), parentId);
                return ResponseEntity.notFound().build();
            }
        }
    }

    @RequestMapping(value = "/containsactivities", method = RequestMethod.GET)
    public ResponseEntity<Boolean> containsActivities(@RequestParam(value = "parentid") final long parentId) {
        final Optional<Long> userId = getLoggedUserId();
        if (!userId.isPresent() || userId.get() <= 0L || parentId < 0L) {
            log.warn("user: {} - containsGroups - groupId is negative - value is: {}", printLoggedUserFirebaseUid(), parentId);
            return ResponseEntity.badRequest().build();
        } else {
            if (parentId == 0L) {
                log.debug("user: {} - containsActivities -  parentId {} - always false", printLoggedUserFirebaseUid(), parentId);
                return new ResponseEntity<>(false, HttpStatus.OK);
            }

            List<ActivityDTO> activityDTOList = activityRepositoryCrud.findAllByGroupId(parentId);
            if (activityDTOList.isEmpty()) {
                log.debug("user: {} - containsActivities - no activities with parentId {} found", printLoggedUserFirebaseUid(), parentId);
                return new ResponseEntity<>(false, HttpStatus.OK);
            } else {
                log.debug("user: {} - containsActivities - activities with parentId: {} found", printLoggedUserFirebaseUid(), parentId);
                return new ResponseEntity<>(true, HttpStatus.OK);
            }
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.PUT)
    public ResponseEntity<EntityCreated> addActivityGroup(@RequestBody final ActivityGroupCreate activityGroupCreate) {
        final Optional<Long> userID = getLoggedUserId();
        final String name = activityGroupCreate.getName();
        final Optional<Long> parentId = Optional.ofNullable(activityGroupCreate.getParentId());

        if (isNameInvalid(name) || !userID.isPresent() || userID.get() <= 0L) {
            log.warn("user: {} - addActivityGroup - invalid input value is: name {}",
                    printLoggedUserFirebaseUid(), name);
            return ResponseEntity.badRequest().build();
        } else {
            if (parentId.get() < 0L) {
                log.warn("user: {} - addActivityGroup - one or more invalid inputs values are: name {}, userID {}, parentID {}",
                        printLoggedUserFirebaseUid(), name, userID, parentId.get());
                return ResponseEntity.badRequest().build();
            } else if (parentId.get() > 0L) {
                final Optional<Long> groupId = activityGroupRepository.add(name, userID.get(), parentId);
                final EntityCreated groupCreated = new EntityCreated(groupId.get());
                log.debug("user: {} - addActivityGroup - group {} with parent {} successfully added to user: {}",
                        printLoggedUserFirebaseUid(), name, parentId.get(), userID);
                sendRefreshMessage();
                return new ResponseEntity(groupCreated, HttpStatus.CREATED);
            } else {
                final Optional<Long> groupId = activityGroupRepository.add(name, userID.get(), parentId);
                final EntityCreated groupCreated = new EntityCreated(groupId.get());
                log.debug("user: {} - addActivityGroup - group {} successfully added to user: {}",
                        printLoggedUserFirebaseUid(), name, userID);
                sendRefreshMessage();
                return new ResponseEntity(groupCreated, HttpStatus.CREATED);
            }
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<ActivityGroup> updateActivityGroup(@RequestBody final ActivityGroup groupToUpdate) {
        final Optional<Long> userId = getLoggedUserId();
        if (isNameInvalid(groupToUpdate.getName()) || !userId.isPresent() || userId.get() <= 0L || groupToUpdate.getId() <= 0L) {
            log.warn("user: {} - updateActivityGroup - one or more invalid inputs values are: name: {}, group ID: {}",
                    printLoggedUserFirebaseUid(), groupToUpdate.getName(), groupToUpdate.getId());
            return ResponseEntity.badRequest().build();
        } else {
            if (activityGroupRepository.update(groupToUpdate.getName(), userId.get(), groupToUpdate.getId())) {
                Optional<ActivityGroup> updatedGroup = activityGroupRepository.show(groupToUpdate.getId());
                if (updatedGroup.isPresent()) {
                    log.debug("user: {} - updateActivityGroup - group {} successfully updated",
                            printLoggedUserFirebaseUid(), groupToUpdate.getId());
                    sendRefreshMessage();
                    return new ResponseEntity<>(updatedGroup.get(), HttpStatus.OK);
                } else {
                    log.error("user: {} - updateActivityGroup - something went wrong with updating group {}, name: {}",
                            printLoggedUserFirebaseUid(), groupToUpdate.getId(), groupToUpdate.getName());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            } else {
                log.warn("user: {} - updateActivityGroup - no group {} found, group name: {}",
                        printLoggedUserFirebaseUid(), groupToUpdate.getId(), groupToUpdate.getName());
                return ResponseEntity.notFound().build();
            }
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteActivityGroup(@RequestParam(value = "groupid") final long groupId) {
        if (groupId <= 0L) {
            log.warn("user: {} - delete activityGroup - invalid input group ID value is: {}", printLoggedUserFirebaseUid(), groupId);
            return ResponseEntity.badRequest().build();
        } else {
            Optional<ActivityGroup> groupToDelete = activityGroupRepository.show(groupId);
            if (groupToDelete.isPresent()) {
                if (activityGroupRepository.delete(groupId)) {
                    log.debug("user: {} - deleteActivityGroup - group {} successfully deleted", printLoggedUserFirebaseUid(), groupId);
                    sendRefreshMessage();
                    return ResponseEntity.ok().build();
                } else {
                    log.error("user: {} - deleteActivityGroup - something went wrong with deleting group {}", printLoggedUserFirebaseUid(), groupId);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            } else {
                log.warn("user: {} - deleteActivityGroup - no group {} found", printLoggedUserFirebaseUid(), groupId);
                return ResponseEntity.notFound().build();
            }
        }
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET, params = {"groupid"})
    public ResponseEntity<Long> countActivityGroup(@RequestParam(value = "groupid") final long groupId) {
        final Optional<Long> userId = getLoggedUserId();
        if (!userId.isPresent() || userId.get() <= 0L || groupId <= 0L) {
            log.warn("user: {} - countActivityGroup - group ID is not positive - value is: {}", printLoggedUserFirebaseUid(), groupId);
            return ResponseEntity.badRequest().build();
        } else {
            Optional<ActivityGroup> groupToCount = activityGroupRepository.show(groupId);
            if (groupToCount.isPresent()) {
                long count = 0L;
                Queue<Long> activityGroups = new LinkedList<>();
                activityGroups.add(groupToCount.get().getId());

                while (!activityGroups.isEmpty()) {
                    Long activityGroup = activityGroups.remove();
                    List<ActivityDTO> allByGroupId = activityRepositoryCrud.findAllByGroupId(activityGroup);

                    if (allByGroupId.isEmpty()) {
                        Optional<GroupResponseList> groupsResponses = activityGroupRepository.showGroupsByParentId(userId.get(), activityGroup);
                        groupsResponses.ifPresent(responses -> responses.forEach((i) ->
                                activityGroups.add(i.getId())
                        ));
                    } else {
                        count += allByGroupId.size();
                    }
                }

                log.debug("user: {} - countActivityGroup - activityGroup: {} successfully counted", printLoggedUserFirebaseUid(), groupId);
                return new ResponseEntity<>(count, HttpStatus.OK);
            } else {
                log.warn("user: {} - countActivityGroup - activityGroup: {} not found", printLoggedUserFirebaseUid(), groupId);
                return ResponseEntity.notFound().build();
            }
        }
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET, params = {"groupid", "startdate", "enddate"})
    public ResponseEntity<Long> countActivityGroup(@RequestParam(value = "groupid") final long groupId,
                                                   @RequestParam(value = "startdate") final long startDate,
                                                   @RequestParam(value = "enddate") final long endDate) {
        final Optional<Long> userId = getLoggedUserId();
        if (!userId.isPresent() || userId.get() <= 0L || groupId <= 0L) {
            log.warn("user: {} - countActivityGroup - group ID is not positive - value is: {}", printLoggedUserFirebaseUid(), groupId);
            return ResponseEntity.badRequest().build();
        } else {
            Optional<ActivityGroup> groupToCount = activityGroupRepository.show(groupId);
            if (groupToCount.isPresent()) {
                long count = 0L;
                Queue<Long> activityGroups = new LinkedList<>();
                activityGroups.add(groupToCount.get().getId());

                while (!activityGroups.isEmpty()) {
                    Long activityGroup = activityGroups.remove();
                    List<ActivityDTO> allByGroupId = activityRepositoryCrud.findAllByGroupId(activityGroup);

                    if (allByGroupId.isEmpty()) {
                        Optional<GroupResponseList> groupsResponses = activityGroupRepository.showGroupsByParentId(userId.get(), activityGroup);
                        groupsResponses.ifPresent(responses -> responses.forEach((i) ->
                                activityGroups.add(i.getId())
                        ));
                    } else {
                        for (ActivityDTO i : allByGroupId) {
                            if (i.getTimestamp().getTime() >= startDate && i.getTimestamp().getTime() <= endDate) {
                                count += 1;
                            }
                        }
                    }
                }

                log.debug("user: {} - countActivityGroup - activityGroup: {} successfully counted", printLoggedUserFirebaseUid(), groupId);
                return new ResponseEntity<>(count, HttpStatus.OK);
            } else {
                log.warn("user: {} - countActivityGroup - activityGroup: {} not found", printLoggedUserFirebaseUid(), groupId);
                return ResponseEntity.notFound().build();
            }
        }
    }

    @RequestMapping(value = "/defaults", method = RequestMethod.POST)
    public ResponseEntity<GroupResponseList> createDefaultActivityGroups(@RequestBody final String localeString) {
        final Optional<Long> userId = getLoggedUserId();
        final Locale locale = Locale.forLanguageTag(localeString.substring(1, localeString.length() - 1));
        if (!userId.isPresent() || userId.get() <= 0L) {
            log.warn("user: {} - showGroups - user ID is not present or is not positive", printLoggedUserFirebaseUid());
            return ResponseEntity.badRequest().build();
        } else {
            log.debug("user: {} - createdefaultgroups - default groups in {} successfully added to user: {}",
                    printLoggedUserFirebaseUid(), locale, userId);

            addDefaultGroupsToDb(userId.get(), locale);
            sendRefreshMessage();
            Optional<GroupResponseList> groupsResponses = activityGroupRepository.showGroupsByParentId(userId.get(), 0L);
            return new ResponseEntity<>(groupsResponses.get(), HttpStatus.OK);
        }
    }

    private static boolean isNameInvalid(String name) {
        return name == null || name.equals("") || (name.charAt(0) == '-') || isWhitespace(name.charAt(0)) || isNumeric(name.substring(0, 1));
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private void addDefaultGroupsToDb(final long userId, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("default-groups", locale);

        long groupId = activityGroupRepository.add(resourceBundle.getString("drinks"), userId, Optional.of(0L)).get();
        activityGroupRepository.add(resourceBundle.getString("beer"), userId, Optional.of(groupId));
        activityGroupRepository.add(resourceBundle.getString("coffee"), userId, Optional.of(groupId));
        activityGroupRepository.add(resourceBundle.getString("cider"), userId, Optional.of(groupId));
        groupId = activityGroupRepository.add(resourceBundle.getString("wine"), userId, Optional.of(groupId)).get();
        activityGroupRepository.add(resourceBundle.getString("red"), userId, Optional.of(groupId));
        activityGroupRepository.add(resourceBundle.getString("white"), userId, Optional.of(groupId));
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
