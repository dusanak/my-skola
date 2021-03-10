package com.skillsfighters.controllers;

import com.skillsfighters.controllers.responses.GroupResponseList;
import com.skillsfighters.controllers.responses.GroupsResponse;
import com.skillsfighters.domain.ActivityGroup;
import com.skillsfighters.domain.UIOptions;
import com.skillsfighters.domain.UIOptionsDTO;
import com.skillsfighters.repository.ActivityGroupRepository;
import com.skillsfighters.repository.UIOptionsRepositoryCrud;
import com.skillsfighters.security.SecurityInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/uioptions")
public class UIOptionsController implements SecurityInfo {
    private final UIOptionsRepositoryCrud uiOptionsRepositoryCrud;
    private final ActivityGroupRepository activityGroupRepository;

    @Autowired
    public UIOptionsController(@NotNull UIOptionsRepositoryCrud uiOptionsRepositoryCrud,
                               @NotNull ActivityGroupRepository activityGroupRepository) {
        this.uiOptionsRepositoryCrud = uiOptionsRepositoryCrud;
        this.activityGroupRepository = activityGroupRepository;
    }

    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public ResponseEntity<UIOptions> showUIOptions(@RequestParam(value = "groupid") final long groupId) {
        if (groupId <= 0L) {
            log.warn("user: {} - showUIOptions - group ID is not positive - value is: {}", printLoggedUserFirebaseUid(), groupId);
            return ResponseEntity.badRequest().build();
        } else {
            Optional<UIOptionsDTO> uiOptionsDTO = uiOptionsRepositoryCrud.findByGroupId(groupId);
            if (uiOptionsDTO.isPresent()) {
                final UIOptionsDTO uiOptionsToShow = uiOptionsDTO.get();
                log.debug("user: {} - showUIOptions - uiOptions: {} successfully shown", printLoggedUserFirebaseUid(), groupId);
                return new ResponseEntity<>(uiOptionsToShow.toUIOptions(), HttpStatus.OK);
            } else {
                UIOptionsDTO newUiOptionsDTO = new UIOptionsDTO();
                newUiOptionsDTO.setId(0);
                newUiOptionsDTO.setGroupId(groupId);
                newUiOptionsDTO.setColor(null);
                newUiOptionsDTO.setIcon(null);
                newUiOptionsDTO.setUnit(null);
                final UIOptionsDTO uiOptionsToShow = uiOptionsRepositoryCrud.save(newUiOptionsDTO);
                log.debug("user: {} - showUIOptions - uiOptions: {} successfully created and shown", printLoggedUserFirebaseUid(), groupId);
                return new ResponseEntity<>(uiOptionsToShow.toUIOptions(), HttpStatus.OK);
            }
        }
    }

    @RequestMapping(value = "/showall", method = RequestMethod.GET)
    public ResponseEntity<List<UIOptions>> showAllUIOptions() {
        final Optional<Long> userId = getLoggedUserId();
        Optional<GroupResponseList> groupsResponses = activityGroupRepository.showGroups(userId.get());
        List<UIOptions> uiOptionsList = new ArrayList<>();

        for (GroupsResponse groupsResponse : groupsResponses.get()) {
            Optional<UIOptionsDTO> uiOptionsDTO = uiOptionsRepositoryCrud.findByGroupId(groupsResponse.getId());
            if (uiOptionsDTO.isPresent()) {
                final UIOptions uiOptions = uiOptionsDTO.get().toUIOptions();
                uiOptionsList.add(uiOptions);
            } else {
                UIOptionsDTO newUiOptionsDTO = new UIOptionsDTO();
                newUiOptionsDTO.setId(0);
                newUiOptionsDTO.setGroupId(groupsResponse.getId());
                newUiOptionsDTO.setColor(null);
                newUiOptionsDTO.setIcon(null);
                newUiOptionsDTO.setUnit(null);
                final UIOptions uiOptions = uiOptionsRepositoryCrud.save(newUiOptionsDTO).toUIOptions();
                uiOptionsList.add(uiOptions);
            }
        }

        log.debug("user: {} - showAllUIOptions - successfully gathered and returned", printLoggedUserFirebaseUid());

        return new ResponseEntity<>(uiOptionsList, HttpStatus.OK);
    }

    @RequestMapping(value = "/showallbyparentid", method = RequestMethod.GET)
    public ResponseEntity<List<UIOptions>> showAllUIOptionsByParentId(@RequestParam(value = "parentid") final long parentId) {
        final Optional<Long> userId = getLoggedUserId();
        if (parentId < 0L) {
            log.warn("user: {} - showUIOptions - group ID is negative - value is: {}", printLoggedUserFirebaseUid(), parentId);
            return ResponseEntity.badRequest().build();
        } else {
            Optional<GroupResponseList> groupsResponses = activityGroupRepository.showGroupsByParentId(userId.get(), parentId);
            List<UIOptions> uiOptionsList = new ArrayList<>();

            for (GroupsResponse groupsResponse: groupsResponses.get()) {
                Optional<UIOptionsDTO> uiOptionsDTO = uiOptionsRepositoryCrud.findByGroupId(groupsResponse.getId());
                if (uiOptionsDTO.isPresent()) {
                    final UIOptions uiOptions = uiOptionsDTO.get().toUIOptions();
                    uiOptionsList.add(uiOptions);
                } else {
                    UIOptionsDTO newUiOptionsDTO = new UIOptionsDTO();
                    newUiOptionsDTO.setId(0);
                    newUiOptionsDTO.setGroupId(groupsResponse.getId());
                    newUiOptionsDTO.setColor(null);
                    newUiOptionsDTO.setIcon(null);
                    newUiOptionsDTO.setUnit(null);
                    final UIOptions uiOptions = uiOptionsRepositoryCrud.save(newUiOptionsDTO).toUIOptions();
                    uiOptionsList.add(uiOptions);
                }
            }

            log.debug("user: {} - showAllUIOptionsByParentId - successfully gathered and returned", printLoggedUserFirebaseUid());

            return new ResponseEntity<>(uiOptionsList, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<UIOptions> addUIOptions(@RequestBody final UIOptions uiOptions) {
        final Optional<Long> userId = getLoggedUserId();

        if (!userId.isPresent() || userId.get() <= 0L || uiOptions.getGroupId() < 0L) {
            log.warn("user: {} - addUIOptions - group ID is negative - value is: {}", printLoggedUserFirebaseUid(), uiOptions.getGroupId());
            return ResponseEntity.badRequest().build();
        } else {
            UIOptionsDTO uiOptionsDTO = new UIOptionsDTO();
            uiOptionsDTO.setGroupId(uiOptions.getGroupId());
            uiOptionsDTO.setColor(uiOptions.getColor());
            uiOptionsDTO.setIcon(uiOptions.getIcon());
            uiOptionsDTO.setUnit(uiOptions.getUnit());
            final UIOptionsDTO newUIOptions = uiOptionsRepositoryCrud.save(uiOptionsDTO);
            log.debug("user: {} - addUIOptions - uiOptions {} with groupId {} successfully added to user: {}",
                    printLoggedUserFirebaseUid(), uiOptionsDTO.getId(), uiOptionsDTO.getGroupId(), userId);
            return new ResponseEntity<>(newUIOptions.toUIOptions(), HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseEntity<UIOptions> updateUIOptions(@RequestBody final UIOptions uiOptions) {
        final Optional<Long> userId = getLoggedUserId();

        if (!userId.isPresent() || userId.get() <= 0L || uiOptions.getGroupId() < 0L) {
            log.warn("user: {} - updateUIOptions - group ID is negative - value is: {}", printLoggedUserFirebaseUid(), uiOptions.getGroupId());
            return ResponseEntity.badRequest().build();
        } else {
            UIOptionsDTO uiOptionsDTO = new UIOptionsDTO();
            uiOptionsDTO.setId(uiOptions.getId());
            uiOptionsDTO.setGroupId(uiOptions.getGroupId());
            uiOptionsDTO.setColor(uiOptions.getColor());
            uiOptionsDTO.setIcon(uiOptions.getIcon());
            uiOptionsDTO.setUnit(uiOptions.getUnit());
            final UIOptionsDTO newUIOptions = uiOptionsRepositoryCrud.save(uiOptionsDTO);
            log.debug("user: {} - updateUIOptions - uiOptions {} with groupId {} successfully added to user: {}",
                    printLoggedUserFirebaseUid(), uiOptionsDTO.getId(), uiOptionsDTO.getGroupId(), userId);
            return new ResponseEntity<>(newUIOptions.toUIOptions(), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteUIOptions(@RequestParam(value = "id") final long id){
        final Optional<Long> userId = getLoggedUserId();

        if (!userId.isPresent() || userId.get() <= 0L || id < 0L) {
            log.warn("user: {} - deleteUIOptions - id is negative - value is: {}", printLoggedUserFirebaseUid(), id);
            return ResponseEntity.badRequest().build();
        } else {
            Optional<UIOptionsDTO> uiOptionsDTO = uiOptionsRepositoryCrud.findById(id);
            if (uiOptionsDTO.isPresent()) {
                uiOptionsRepositoryCrud.delete(uiOptionsDTO.get());
                log.debug("user: {} - deleteUIOptions - uiOptions {} with groupId {} successfully removed from user: {}",
                        printLoggedUserFirebaseUid(), uiOptionsDTO.get().getId(), uiOptionsDTO.get().getGroupId(), userId);
                return ResponseEntity.ok().build();
            }
            else {
                log.warn("user: {} - deleteUIOptions - no UIOptions {} found", printLoggedUserFirebaseUid(), id);
                return ResponseEntity.notFound().build();
            }
        }
    }
}
