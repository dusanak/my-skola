package com.skillsfighters;

import com.google.api.Http;
import com.skillsfighters.controllers.ActivityGroupController;
import com.skillsfighters.controllers.requests.ActivityGroupCreate;
import com.skillsfighters.controllers.responses.EntityCreated;
import com.skillsfighters.controllers.responses.GroupResponseList;
import com.skillsfighters.controllers.responses.GroupsResponse;
import com.skillsfighters.domain.ActivityDTO;
import com.skillsfighters.domain.ActivityGroup;
import com.skillsfighters.repository.ActivityGroupRepository;
import com.skillsfighters.repository.ActivityRepositoryCrud;
import com.skillsfighters.repository.DeviceGroupRepositoryCrud;
import com.skillsfighters.security.SecurityInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class ActivityGroupControllerTest {

    @DisplayName("ActivityGroup Controller - show group - everything OK")
    @Test
    public void show() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroup activityGroupToShow = ActivityGroup.builder().id(20L).name("CocaCola").userId(10L).build();

        doReturn(Optional.of(activityGroupToShow)).when(activityGroupRepository).show(20L);

        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
                                                                                      activityRepositoryCrud,
                                                                                      deviceGroupRepositoryCrud);
        ResponseEntity<ActivityGroup> responseEntity = activityGroupController.showActivityGroup(20L);

        verify(activityGroupRepository).show(20L);
        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(20L, responseEntity.getBody().getId());
        assertEquals(10L, responseEntity.getBody().getUserId());
        assertEquals("CocaCola", responseEntity.getBody().getName());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - show group with negative ID(invalid input)")
    @Test
    public void showGroupWithNegativeGroupId() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud);
        ResponseEntity<ActivityGroup> responseEntity = activityGroupController.showActivityGroup(-1L);

        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - show group with zero ID(invalid input)")
    @Test
    public void showGroupWithZeroGroupId() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud);
        ResponseEntity<ActivityGroup> responseEntity = activityGroupController.showActivityGroup(0L);

        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - show group - no group found in database")
    @Test
    public void showNoGroupFound() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);
        doReturn(Optional.empty()).when(activityGroupRepository).show(20L);

        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud);
        ResponseEntity<ActivityGroup> responseEntity = activityGroupController.showActivityGroup(20L);

        verify(activityGroupRepository).show(20L);
        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - count group activities - everything OK")
    @Test
    public void count() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroup activityGroupToShow = ActivityGroup.builder().id(20L).name("CocaCola").userId(10L).build();
        List<ActivityDTO> activities = new ArrayList<>();
        activities.add(new ActivityDTO());
        activities.add(new ActivityDTO());
        activities.add(new ActivityDTO());

        doReturn(Optional.of(activityGroupToShow)).when(activityGroupRepository).show(20L);
        doReturn(activities).when(activityRepositoryCrud).findAllByGroupId(20L);

        ActivityGroupController activityGroupController = spy(new ActivityGroupController(
                activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud)
        );

        doReturn(Optional.of(10L)).when((SecurityInfo)activityGroupController).getLoggedUserId();

        ResponseEntity<Long> responseEntity = activityGroupController.countActivityGroup(20L);

        verify(activityGroupRepository).show(20L);
        verifyNoMoreInteractions(activityGroupRepository);

        verify(activityRepositoryCrud).findAllByGroupId(20L);
        verifyNoMoreInteractions(activityRepositoryCrud);

        assertNotNull(responseEntity.getBody());
        assertEquals(3L, responseEntity.getBody().longValue());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - count group activities - zero OK")
    @Test
    public void countZero() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroup activityGroupToShow = ActivityGroup.builder().id(20L).name("CocaCola").userId(10L).build();
        List<ActivityDTO> activities = new ArrayList<>();

        doReturn(Optional.of(activityGroupToShow)).when(activityGroupRepository).show(20L);
        doReturn(Optional.empty()).when(activityGroupRepository).showGroupsByParentId(10L,20L);
        doReturn(activities).when(activityRepositoryCrud).findAllByGroupId(20L);

        ActivityGroupController activityGroupController = spy(new ActivityGroupController(
                activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud)
        );

        doReturn(Optional.of(10L)).when((SecurityInfo)activityGroupController).getLoggedUserId();

        ResponseEntity<Long> responseEntity = activityGroupController.countActivityGroup(20L);

        verify(activityGroupRepository).show(20L);
        verify(activityGroupRepository).showGroupsByParentId(10L, 20L);
        verifyNoMoreInteractions(activityGroupRepository);

        verify(activityRepositoryCrud).findAllByGroupId(20L);
        verifyNoMoreInteractions(activityRepositoryCrud);

        assertNotNull(responseEntity.getBody());
        assertEquals(0L, responseEntity.getBody().longValue());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - count group activities - everything OK")
    @Test
    public void countChildrenGroups() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroup activityGroupToShow = ActivityGroup.builder().id(20L).name("CocaCola").userId(10L).build();

        List<ActivityDTO> activities20 = new ArrayList<>();

        List<ActivityDTO> activities30 = new ArrayList<>();
        activities30.add(new ActivityDTO());
        activities30.add(new ActivityDTO());
        activities30.add(new ActivityDTO());

        List<ActivityDTO> activities40 = new ArrayList<>();
        activities40.add(new ActivityDTO());
        activities40.add(new ActivityDTO());
        activities40.add(new ActivityDTO());

        GroupResponseList groupResponseList = new GroupResponseList();
        GroupsResponse groupsResponse30 = new GroupsResponse();
        GroupsResponse groupsResponse40 = new GroupsResponse();
        groupsResponse30.setId(30L);
        groupsResponse40.setId(40L);
        groupResponseList.add(groupsResponse30);
        groupResponseList.add(groupsResponse40);

        doReturn(Optional.of(activityGroupToShow)).when(activityGroupRepository).show(20L);
        doReturn(Optional.of(groupResponseList)).when(activityGroupRepository).showGroupsByParentId(10L,20L);
        doReturn(activities20).when(activityRepositoryCrud).findAllByGroupId(20L);
        doReturn(activities30).when(activityRepositoryCrud).findAllByGroupId(30L);
        doReturn(activities40).when(activityRepositoryCrud).findAllByGroupId(40L);

        ActivityGroupController activityGroupController = spy(new ActivityGroupController(
                activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud)
        );

        doReturn(Optional.of(10L)).when((SecurityInfo)activityGroupController).getLoggedUserId();

        ResponseEntity<Long> responseEntity = activityGroupController.countActivityGroup(20L);

        verify(activityGroupRepository).show(20L);
        verify(activityGroupRepository).showGroupsByParentId(10L, 20L);
        verifyNoMoreInteractions(activityGroupRepository);

        verify(activityRepositoryCrud).findAllByGroupId(20L);
        verify(activityRepositoryCrud).findAllByGroupId(30L);
        verify(activityRepositoryCrud).findAllByGroupId(40L);
        verifyNoMoreInteractions(activityRepositoryCrud);

        assertNotNull(responseEntity.getBody());
        assertEquals(6L, responseEntity.getBody().longValue());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - count group activities - non positive group ID")
    @Test
    public void countWithNonPositiveId() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud);
        ResponseEntity<Long> responseEntity = activityGroupController.countActivityGroup(-1L);

        verifyNoMoreInteractions(activityGroupRepository);
        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - count group activities - nonexistent group ID")
    @Test
    public void countWithNonexistentGroupId() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        doReturn(Optional.empty()).when(activityGroupRepository).show(20L);

        ActivityGroupController activityGroupController = spy(new ActivityGroupController(
                activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud)
        );

        doReturn(Optional.of(10L)).when((SecurityInfo)activityGroupController).getLoggedUserId();

        ResponseEntity<Long> responseEntity = activityGroupController.countActivityGroup(20L);

        verify(activityGroupRepository).show(20L);
        verifyNoMoreInteractions(activityGroupRepository);
        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - show all groups - everything OK")
    @Test
    public void showAll() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        GroupResponseList groups = new GroupResponseList();
        doReturn(Optional.of(groups)).when(activityGroupRepository).showGroupsByParentId(10L, 20L);

        ActivityGroupController activityGroupController = spy(new ActivityGroupController(
                activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud)
        );

        doReturn(Optional.of(10L)).when((SecurityInfo)activityGroupController).getLoggedUserId();

        ResponseEntity<GroupResponseList> responseEntity = activityGroupController.showGroupsByParentId(20L);

        verify(activityGroupRepository).showGroupsByParentId(10L, 20L);
        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - show all groups with negative user ID(invalid input)")
    @Test
    public void showAllGroupsWithNegativeUserId() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud);
        ResponseEntity<GroupResponseList> responseEntity = activityGroupController.showGroupsByParentId(-1L);

        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - show all groups with zero user ID(invalid input)")
    @Test
    public void showAllGroupsWithZeroUserId() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud);
        ResponseEntity<GroupResponseList> responseEntity = activityGroupController.showGroupsByParentId(0L);

        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - show all groups - no groups found in database")
    @Test
    public void showAllNoGroupsFound() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroupController activityGroupController = spy(new ActivityGroupController(
                activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud)
        );

        doReturn(Optional.empty()).when(activityGroupRepository).showGroupsByParentId(10L,20L);
        doReturn(Optional.of(10L)).when((SecurityInfo)activityGroupController).getLoggedUserId();

        ResponseEntity<GroupResponseList> responseEntity = activityGroupController.showGroupsByParentId(20L);

        verify(activityGroupRepository).showGroupsByParentId(10L, 20L);
        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - create group - everything is OK")
    @Test
    public void add() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        when(activityGroupRepository.add("CocaCola", 10L, Optional.of(1L))).thenReturn(Optional.of(10L));

        ActivityGroupController activityGroupController = spy(new ActivityGroupController(
                activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud)
        );

        doReturn(Optional.of(10L)).when((SecurityInfo)activityGroupController).getLoggedUserId();

        ActivityGroupCreate activityGroupCreate = new ActivityGroupCreate();
        activityGroupCreate.setName("CocaCola");
        activityGroupCreate.setParentId(1L);

        ResponseEntity<EntityCreated> responseEntity = activityGroupController.addActivityGroup(activityGroupCreate);

        verify(activityGroupRepository).add("CocaCola", 10L, Optional.of(1L));
        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(10L, responseEntity.getBody().getId());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - update group - everything OK")
    @Test
    public void update() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        when(activityGroupRepository.update("Coffee", 10L, 20L)).thenReturn(true);

        ActivityGroupController activityGroupController = spy(new ActivityGroupController(
                activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud)
        );

        doReturn(Optional.of(10L)).when((SecurityInfo)activityGroupController).getLoggedUserId();

        ActivityGroup updatedActivityGroup = ActivityGroup.builder().id(20L).name("Coffee").userId(10L).build();
        doReturn(Optional.of(updatedActivityGroup)).when(activityGroupRepository).show(20L);

        ResponseEntity<ActivityGroup> responseEntity = activityGroupController.updateActivityGroup(updatedActivityGroup);

        verify(activityGroupRepository).update("Coffee", 10L, 20L);
        verify(activityGroupRepository).show(20L);
        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(20L, responseEntity.getBody().getId());
        assertEquals(10L, responseEntity.getBody().getUserId());
        assertEquals("Coffee", responseEntity.getBody().getName());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - update group - internal server error")
    @Test
    public void updateInternalServerError() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        when(activityGroupRepository.update("Coffee", 10L, 20L)).thenReturn(true);

        ActivityGroupController activityGroupController = spy(new ActivityGroupController(
                activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud)
        );

        doReturn(Optional.of(10L)).when((SecurityInfo)activityGroupController).getLoggedUserId();

        ActivityGroup updatedActivityGroup = ActivityGroup.builder().id(20L).name("Coffee").userId(10L).build();
        doReturn(Optional.empty()).when(activityGroupRepository).show(20L);

        ResponseEntity<ActivityGroup> responseEntity = activityGroupController.updateActivityGroup(updatedActivityGroup);

        verify(activityGroupRepository).update("Coffee", 10L, 20L);
        verify(activityGroupRepository).show(20L);
        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - update group with negative user ID(invalid input)")
    @Test
    public void updateGroupWithNegativeUserId() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroup updatedActivityGroup = ActivityGroup.builder().userId(-1L).build();

        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud);
        ResponseEntity<ActivityGroup> responseEntity = activityGroupController.updateActivityGroup(updatedActivityGroup);

        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - update group with zero user ID(invalid input)")
    @Test
    public void updateGroupWithZeroUserId() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroup updatedActivityGroup = ActivityGroup.builder().userId(0L).build();

        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud);
        ResponseEntity<ActivityGroup> responseEntity = activityGroupController.updateActivityGroup(updatedActivityGroup);

        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - update group with no name(invalid input)")
    @Test
    public void updateGroupWithNoName() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroup updatedActivityGroup = ActivityGroup.builder().name("").build();

        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud);
        ResponseEntity<ActivityGroup> responseEntity = activityGroupController.updateActivityGroup(updatedActivityGroup);

        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - update group - no group found in database cannot execute update")
    @Test
    public void updateNoGroupFound() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        when(activityGroupRepository.update("Coffee", 10L, 20L)).thenReturn(false);

        ActivityGroupController activityGroupController = spy(new ActivityGroupController(
                activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud)
        );

        doReturn(Optional.of(10L)).when((SecurityInfo)activityGroupController).getLoggedUserId();

        ActivityGroup updatedActivityGroup = ActivityGroup.builder().id(20L).name("Coffee").userId(10L).build();

        ResponseEntity<ActivityGroup> responseEntity = activityGroupController.updateActivityGroup(updatedActivityGroup);

        verify(activityGroupRepository).update("Coffee", 10L, 20L);
        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - contains activities - everything OK")
    @Test
    public void containsActivities() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroupController activityGroupController = spy(new ActivityGroupController(
                activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud)
        );

        List<ActivityDTO> activityDTOList = new ArrayList<>();
        activityDTOList.add(new ActivityDTO());

        when(activityRepositoryCrud.findAllByGroupId(10L)).thenReturn(activityDTOList);
        doReturn(Optional.of(10L)).when((SecurityInfo)activityGroupController).getLoggedUserId();

        ResponseEntity<Boolean> response = activityGroupController.containsActivities(10L);

        verify(activityRepositoryCrud).findAllByGroupId(10L);
        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(true, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - contains activities - contains no activities, everything OK")
    @Test
    public void containsActivitiesNoActivities() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroupController activityGroupController = spy(new ActivityGroupController(
                activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud)
        );

        List<ActivityDTO> activityDTOList = new ArrayList<>();

        when(activityRepositoryCrud.findAllByGroupId(10L)).thenReturn(activityDTOList);
        doReturn(Optional.of(10L)).when((SecurityInfo)activityGroupController).getLoggedUserId();

        ResponseEntity<Boolean> response = activityGroupController.containsActivities(10L);

        verify(activityRepositoryCrud).findAllByGroupId(10L);
        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(false, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - contains activities - root group, everything OK")
    @Test
    public void containsActivitiesRootGroup() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroupController activityGroupController = spy(new ActivityGroupController(
                activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud)
        );

        doReturn(Optional.of(10L)).when((SecurityInfo)activityGroupController).getLoggedUserId();

        ResponseEntity<Boolean> response = activityGroupController.containsActivities(0L);

        verifyNoInteractions(activityRepositoryCrud);

        assertEquals(false, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - contains activities - non positive UserID, ERROR")
    @Test
    public void containsActivitiesWithNonPositiveUserId() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroupController activityGroupController = spy(new ActivityGroupController(
                activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud)
        );

        doReturn(Optional.of(-1L)).when((SecurityInfo)activityGroupController).getLoggedUserId();

        ResponseEntity<Boolean> response = activityGroupController.containsActivities(10L);

        verifyNoInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - contains activities - no UserID, ERROR")
    @Test
    public void containsActivitiesWithNoUserId() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroupController activityGroupController = spy(new ActivityGroupController(
                activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud)
        );

        doReturn(Optional.empty()).when((SecurityInfo)activityGroupController).getLoggedUserId();

        ResponseEntity<Boolean> response = activityGroupController.containsActivities(10L);

        verifyNoInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - contains activities - negative parent ID, ERROR")
    @Test
    public void containsActivitiesWithNegativeParentId() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroupController activityGroupController = spy(new ActivityGroupController(
                activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud)
        );

        doReturn(Optional.of(10L)).when((SecurityInfo)activityGroupController).getLoggedUserId();

        ResponseEntity<Boolean> response = activityGroupController.containsActivities(-1L);

        verifyNoInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - delete group - everything OK")
    @Test
    public void delete() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroup activityGroupToDelete = ActivityGroup.builder().id(20L).name("CocaCola").userId(10L).build();

        doReturn(Optional.of(activityGroupToDelete)).when(activityGroupRepository).show(20L);
        doReturn(true).when(activityGroupRepository).delete(20L);

        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud);
        ResponseEntity<String> responseEntity = activityGroupController.deleteActivityGroup(activityGroupToDelete.getId());

        verify(activityGroupRepository).show(20L);
        verify(activityGroupRepository).delete(20L);
        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - delete group with negative ID(invalid input)")
    @Test
    public void deleteGroupWithNegativeId() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroup activityGroupToDelete = ActivityGroup.builder().id(-1L).build();

        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud);
        ResponseEntity<String> responseEntity = activityGroupController.deleteActivityGroup(activityGroupToDelete.getId());

        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - delete group with zero ID(invalid input)")
    @Test
    public void deleteGroupWithZeroId() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroup activityGroupToDelete = ActivityGroup.builder().id(0L).build();

        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud);
        ResponseEntity<String> responseEntity = activityGroupController.deleteActivityGroup(activityGroupToDelete.getId());

        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - delete group - no group found in database cannot execute delete")
    @Test
    public void deleteNoGroupFound() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);
        doReturn(Optional.empty()).when(activityGroupRepository).show(20L);

        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud);
        ResponseEntity<String> responseEntity = activityGroupController.deleteActivityGroup(20L);

        verify(activityGroupRepository).show(20L);
        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - delete group - no success when executing delete")
    @Test
    public void deleteGroupWithoutSuccess() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroup activityGroupToDelete = ActivityGroup.builder().id(20L).name("Coffee").userId(10L).build();

        doReturn(Optional.of(activityGroupToDelete)).when(activityGroupRepository).show(20L);
        doReturn(false).when(activityGroupRepository).delete(20L);

        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud);
        ResponseEntity<String> responseEntity = activityGroupController.deleteActivityGroup(activityGroupToDelete.getId());

        verify(activityGroupRepository).show(20L);
        verify(activityGroupRepository).delete(20L);
        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - show group by parentId - everything OK")
    @Test
    public void showGroupsByParentId() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        GroupResponseList groupResponseList = new GroupResponseList();
        groupResponseList.add(new GroupsResponse(30L, "", 0, Optional.of(20L)));
        groupResponseList.add(new GroupsResponse(31L, "", 0, Optional.of(20L)));
        groupResponseList.add(new GroupsResponse(32L, "", 0, Optional.of(20L)));

        doReturn(Optional.of(groupResponseList)).when(activityGroupRepository).showGroupsByParentId(10L, 20L);

        ActivityGroupController activityGroupController = spy(new ActivityGroupController(
                activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud)
        );

        doReturn(Optional.of(10L)).when((SecurityInfo)activityGroupController).getLoggedUserId();

        ResponseEntity<GroupResponseList> responseEntity = activityGroupController.showGroupsByParentId(20L);

        verify(activityGroupRepository).showGroupsByParentId(10L, 20L);
        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(groupResponseList, responseEntity.getBody());
    }

    @DisplayName("ActivityGroup Controller - show group by parentId - everything OK")
    @Test
    public void showGroupsByNonPositiveParentId() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud);
        ResponseEntity<GroupResponseList> responseEntity = activityGroupController.showGroupsByParentId(-1L);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("ActivityGroup Controller - show group by parentId - everything OK")
    @Test
    public void showGroupsByParentIdNoPresent() {
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        doReturn(Optional.empty()).when(activityGroupRepository).showGroupsByParentId(10L, 20L);

        ActivityGroupController activityGroupController = spy(new ActivityGroupController(
                activityGroupRepository,
                activityRepositoryCrud,
                deviceGroupRepositoryCrud)
        );

        doReturn(Optional.of(10L)).when((SecurityInfo)activityGroupController).getLoggedUserId();

        ResponseEntity<GroupResponseList> responseEntity = activityGroupController.showGroupsByParentId(20L);

        verify(activityGroupRepository).showGroupsByParentId(10L, 20L);
        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}
