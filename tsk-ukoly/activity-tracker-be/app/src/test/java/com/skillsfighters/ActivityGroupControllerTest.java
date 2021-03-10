package com.skillsfighters;

import com.skillsfighters.controllers.ActivityGroupController;
import com.skillsfighters.controllers.responses.GroupResponseList;
import com.skillsfighters.domain.ActivityGroup;
import com.skillsfighters.repository.ActivityGroupRepository;
import com.skillsfighters.repository.ActivityRepositoryCrud;
import com.skillsfighters.repository.DeviceGroupRepositoryCrud;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

//FIXME: solve: how to mock static method getLoggedUserId()? POSSIBLE FIX USE POWERMOCK
//FIXME: applies to all commented out tests
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

//    @DisplayName("ActivityGroup Controller - count group activities - everything OK")
//    @Test
//    public void count() {
//        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
//        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
//        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);
//
//        ActivityGroup activityGroupToShow = ActivityGroup.builder().id(20L).name("CocaCola").userId(10L).build();
//        List<ActivityDTO> activities = new ArrayList<>();
//        activities.add(new ActivityDTO());
//        activities.add(new ActivityDTO());
//        activities.add(new ActivityDTO());
//
//        doReturn(Optional.of(activityGroupToShow)).when(activityGroupRepository).show(20L);
//        doReturn(activities).when(activityRepositoryCrud).findAllByGroupId(20L);
//
//        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
//                activityRepositoryCrud,
//                deviceGroupRepositoryCrud);
//        ResponseEntity<Long> responseEntity = activityGroupController.countActivityGroup(20L);
//
//        verify(activityGroupRepository).show(20L);
//        verifyNoMoreInteractions(activityGroupRepository);
//
//        verify(activityRepositoryCrud).findAllByGroupId(20L);
//        verifyNoMoreInteractions(activityRepositoryCrud);
//
//        assertNotNull(responseEntity.getBody());
//        assertEquals(3L, responseEntity.getBody().longValue());
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//    }
//
//    @DisplayName("ActivityGroup Controller - count group activities - zero OK")
//    @Test
//    public void countZero() {
//        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
//        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
//        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);
//
//        ActivityGroup activityGroupToShow = ActivityGroup.builder().id(20L).name("CocaCola").userId(10L).build();
//        List<ActivityDTO> activities = new ArrayList<>();
//
//        doReturn(Optional.of(activityGroupToShow)).when(activityGroupRepository).show(20L);
//        doReturn(Optional.empty()).when(activityGroupRepository).showGroupsByParentId(20L,20L);
//        doReturn(activities).when(activityRepositoryCrud).findAllByGroupId(20L);
//
//        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
//                activityRepositoryCrud,
//                deviceGroupRepositoryCrud);
//        ResponseEntity<Long> responseEntity = activityGroupController.countActivityGroup(20L);
//
//        verify(activityGroupRepository).show(20L);
//        verify(activityGroupRepository).showGroupsByParentId(20L, 20L);
//        verifyNoMoreInteractions(activityGroupRepository);
//
//        verify(activityRepositoryCrud).findAllByGroupId(20L);
//        verifyNoMoreInteractions(activityRepositoryCrud);
//
//        assertNotNull(responseEntity.getBody());
//        assertEquals(0L, responseEntity.getBody().longValue());
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//    }

//    @DisplayName("ActivityGroup Controller - count group activities - everything OK")
//    @Test
//    public void countChildrenGroups() {
//        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
//        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
//        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);
//
//
//
//        ActivityGroup activityGroupToShow = ActivityGroup.builder().id(20L).name("CocaCola").userId(10L).build();
//
//        List<ActivityDTO> activities20 = new ArrayList<>();
//
//        List<ActivityDTO> activities30 = new ArrayList<>();
//        activities30.add(new ActivityDTO());
//        activities30.add(new ActivityDTO());
//        activities30.add(new ActivityDTO());
//
//        List<ActivityDTO> activities40 = new ArrayList<>();
//        activities40.add(new ActivityDTO());
//        activities40.add(new ActivityDTO());
//        activities40.add(new ActivityDTO());
//
//        GroupResponseList groupResponseList = new GroupResponseList();
//        GroupsResponse groupsResponse30 = new GroupsResponse();
//        GroupsResponse groupsResponse40 = new GroupsResponse();
//        groupsResponse30.setId(30L);
//        groupsResponse40.setId(40L);
//        groupResponseList.add(groupsResponse30);
//        groupResponseList.add(groupsResponse40);
//
//        doReturn(Optional.of(activityGroupToShow)).when(activityGroupRepository).show(20L);
//        doReturn(Optional.of(groupResponseList)).when(activityGroupRepository).showGroupsByParentId(20L,20L);
//        doReturn(activities20).when(activityRepositoryCrud).findAllByGroupId(20L);
//        doReturn(activities30).when(activityRepositoryCrud).findAllByGroupId(30L);
//        doReturn(activities40).when(activityRepositoryCrud).findAllByGroupId(40L);
//
//        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
//                activityRepositoryCrud,
//                deviceGroupRepositoryCrud);
//        ResponseEntity<Long> responseEntity = activityGroupController.countActivityGroup(20L);
//
//        verify(activityGroupRepository).show(20L);
//        verify(activityGroupRepository).showGroupsByParentId(20L, 20L);
//        verifyNoMoreInteractions(activityGroupRepository);
//
//        verify(activityRepositoryCrud).findAllByGroupId(20L);
//        verify(activityRepositoryCrud).findAllByGroupId(30L);
//        verify(activityRepositoryCrud).findAllByGroupId(40L);
//        verifyNoMoreInteractions(activityRepositoryCrud);
//
//        assertNotNull(responseEntity.getBody());
//        assertEquals(6L, responseEntity.getBody().longValue());
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//    }

    @DisplayName("ActivityGroup Controller - count group activities - everything OK")
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

//    @DisplayName("ActivityGroup Controller - count group activities - everything OK")
//    @Test
//    public void countWithNonexistentGroupId() {
//        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
//        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
//        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);
//
//        doReturn(Optional.empty()).when(activityGroupRepository).show(20L);
//
//        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
//                activityRepositoryCrud,
//                deviceGroupRepositoryCrud);
//        ResponseEntity<Long> responseEntity = activityGroupController.countActivityGroup(20L);
//
//        verify(activityGroupRepository).show(20L);
//        verifyNoMoreInteractions(activityGroupRepository);
//        verifyNoMoreInteractions(activityRepositoryCrud);
//
//        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
//    }

//    @DisplayName("ActivityGroup Controller - show all groups - everything OK")
//    @Test
//    public void showAll() {
//        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
//        GroupResponseList groups = new GroupResponseList();
//        doReturn(Optional.of(groups)).when(activityGroupRepository).showGroups(20L);
//
//        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository);
//        ResponseEntity<GroupResponseList> responseEntity = activityGroupController.showGroups(20L);
//
//        verify(activityGroupRepository).showGroups(20L);
//        verifyNoMoreInteractions(activityGroupRepository);
//
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//    }
//
//    @DisplayName("ActivityGroup Controller - show all groups with negative user ID(invalid input)")
//    @Test
//    public void showAllGroupsWithNegativeUserId() {
//        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
//
//        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository);
//        ResponseEntity<GroupResponseList> responseEntity = activityGroupController.showGroups(-1L);
//
//        verifyNoMoreInteractions(activityGroupRepository);
//
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//    }
//
//    @DisplayName("ActivityGroup Controller - show all groups with zero user ID(invalid input)")
//    @Test
//    public void showAllGroupsWithZeroUserId() {
//        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
//
//        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository);
//        ResponseEntity<GroupResponseList> responseEntity = activityGroupController.showGroups(0L);
//
//        verifyNoMoreInteractions(activityGroupRepository);
//
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//    }

//    @DisplayName("ActivityGroup Controller - show all groups - no groups found in database")
//    @Test
//    public void showAllNoGroupsFound() {
//        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
//        doReturn(Optional.empty()).when(activityGroupRepository).showGroups(20L);
//
//        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository);
//        ResponseEntity<GroupResponseList> responseEntity = activityGroupController.showGroups(20L);
//
//        verify(activityGroupRepository).showGroups(20L);
//        verifyNoMoreInteractions(activityGroupRepository);
//
//        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
//    }

//    @DisplayName("ActivityGroup Controller - create group - everything is OK")
//    @Test
//    public void add() {
//        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
//        when(activityGroupRepository.add("CocaCola", 20L, Optional.of(1L))).thenReturn(Optional.of(10L));
//
//        ActivityGroupCreate activityGroupCreate = new ActivityGroupCreate();
//        activityGroupCreate.setName("CocaCola");
//        activityGroupCreate.setParentId(1L);
//
//        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository);
//        ResponseEntity<EntityCreated> responseEntity = activityGroupController.addActivityGroup(activityGroupCreate);
//
//        verify(activityGroupRepository).add("CocaCola", 20L, Optional.of(1L));
//        verifyNoMoreInteractions(activityGroupRepository);
//
//        assertEquals(10L, responseEntity.getBody().getId());
//        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
//    }

//    @DisplayName("ActivityGroup Controller - update group - everything OK")
//    @Test
//    public void update() {
//        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
//        when(activityGroupRepository.update("Coffee", 10L, 20L)).thenReturn(true);
//
//        ActivityGroup updatedActivityGroup = ActivityGroup.builder().id(20L).name("Coffee").userId(10L).build();
//        doReturn(Optional.of(updatedActivityGroup)).when(activityGroupRepository).show(20L);
//
//        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository);
//        ResponseEntity<ActivityGroup> responseEntity = activityGroupController.updateActivityGroup(updatedActivityGroup);
//
//        verify(activityGroupRepository).update("Coffee", 10L, 20L);
//        verify(activityGroupRepository).show(20L);
//        verifyNoMoreInteractions(activityGroupRepository);
//
//        assertEquals(20L, responseEntity.getBody().getId());
//        assertEquals(10L, responseEntity.getBody().getUserId());
//        assertEquals("Coffee", responseEntity.getBody().getName());
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//    }
//
//    @DisplayName("ActivityGroup Controller - update group with negative user ID(invalid input)")
//    @Test
//    public void updateGroupWithNegativeUserId() {
//        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
//
//        ActivityGroup updatedActivityGroup = ActivityGroup.builder().userId(-1L).build();
//
//        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository);
//        ResponseEntity<ActivityGroup> responseEntity = activityGroupController.updateActivityGroup(updatedActivityGroup);
//
//        verifyNoMoreInteractions(activityGroupRepository);
//
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//    }
//
//    @DisplayName("ActivityGroup Controller - update group with zero user ID(invalid input)")
//    @Test
//    public void updateGroupWithZeroUserId() {
//        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
//
//        ActivityGroup updatedActivityGroup = ActivityGroup.builder().userId(0L).build();
//
//        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository);
//        ResponseEntity<ActivityGroup> responseEntity = activityGroupController.updateActivityGroup(updatedActivityGroup);
//
//        verifyNoMoreInteractions(activityGroupRepository);
//
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//    }
//
//    @DisplayName("ActivityGroup Controller - update group with no name(invalid input)")
//    @Test
//    public void updateGroupWithNoName() {
//        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
//
//        ActivityGroup updatedActivityGroup = ActivityGroup.builder().name("").build();
//
//        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository);
//        ResponseEntity<ActivityGroup> responseEntity = activityGroupController.updateActivityGroup(updatedActivityGroup);
//
//        verifyNoMoreInteractions(activityGroupRepository);
//
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//    }
//
//    @DisplayName("ActivityGroup Controller - update group - no group found in database cannot execute update")
//    @Test
//    public void updateNoGroupFound() {
//        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
//        when(activityGroupRepository.update("Coffee", 10L, 20L)).thenReturn(false);
//
//        ActivityGroup updatedActivityGroup = ActivityGroup.builder().id(20L).name("Coffee").userId(10L).build();
//
//        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository);
//        ResponseEntity<ActivityGroup> responseEntity = activityGroupController.updateActivityGroup(updatedActivityGroup);
//
//        verify(activityGroupRepository).update("Coffee", 10L, 20L);
//        verifyNoMoreInteractions(activityGroupRepository);
//
//        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
//    }

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

//    @DisplayName("ActivityGroup Controller - show group by parentId - everything OK")
//    @Test
//    public void showGroupsByParentId() {
//        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
//        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
//        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);
//
//        GroupResponseList groupResponseList = new GroupResponseList();
//        groupResponseList.add(new GroupsResponse(30L, "", 0, Optional.of(20L)));
//        groupResponseList.add(new GroupsResponse(31L, "", 0, Optional.of(20L)));
//        groupResponseList.add(new GroupsResponse(32L, "", 0, Optional.of(20L)));
//
//        doReturn(Optional.of(groupResponseList)).when(activityGroupRepository).showGroupsByParentId(20L, 20L);
//
//        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
//                activityRepositoryCrud,
//                deviceGroupRepositoryCrud);
//        ResponseEntity<GroupResponseList> responseEntity = activityGroupController.showGroupsByParentId(20L);
//
//        verify(activityGroupRepository).showGroupsByParentId(20L, 20L);
//        verifyNoMoreInteractions(activityGroupRepository);
//
//        assertEquals(groupResponseList, responseEntity.getBody());
//    }

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

//    @DisplayName("ActivityGroup Controller - show group by parentId - everything OK")
//    @Test
//    public void showGroupsByParentIdNoPresent() {
//        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
//        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
//        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);
//
//        doReturn(Optional.empty()).when(activityGroupRepository).showGroupsByParentId(20L, 20L);
//
//        ActivityGroupController activityGroupController = new ActivityGroupController(activityGroupRepository,
//                activityRepositoryCrud,
//                deviceGroupRepositoryCrud);
//        ResponseEntity<GroupResponseList> responseEntity = activityGroupController.showGroupsByParentId(20L);
//
//        verify(activityGroupRepository).showGroupsByParentId(20L, 20L);
//        verifyNoMoreInteractions(activityGroupRepository);
//
//        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
//    }
}
