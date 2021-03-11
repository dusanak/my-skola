package com.skillsfighters;

import com.skillsfighters.controllers.ActivityController;
import com.skillsfighters.controllers.ActivityGroupController;
import com.skillsfighters.controllers.requests.ActivityCreate;
import com.skillsfighters.controllers.requests.ActivityUpdate;
import com.skillsfighters.controllers.responses.ActivityList;
import com.skillsfighters.domain.Activity;
import com.skillsfighters.domain.ActivityDTO;
import com.skillsfighters.repository.ActivityGroupRepository;
import com.skillsfighters.repository.ActivityRepositoryCrud;
import com.skillsfighters.repository.DeviceGroupRepositoryCrud;
import com.skillsfighters.security.SecurityInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

public class ActivityControllerUnitTest {

    @DisplayName("Activity Controller - show activity - everything OK")
    @Test
    public void show() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        Date timestamp = new Date();
        ActivityDTO activityDTOToShow = new ActivityDTO(20L, timestamp, 10L, timestamp, timestamp, false);
        doReturn(Optional.of(activityDTOToShow)).when(activityRepositoryCrud).findById(20L);

        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ResponseEntity<Activity> responseEntity = activityController.showActivity(20L);

        verify(activityRepositoryCrud).findById(20L);
        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(20L, responseEntity.getBody().getId());
        assertEquals(10L, responseEntity.getBody().getGroupId());
        assertEquals(timestamp.getTime(), responseEntity.getBody().getTimestamp());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - show activity with negative ID(invalid input)")
    @Test
    public void showActivityWithNegativeId() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ResponseEntity<Activity> responseEntity = activityController.showActivity(-1L);

        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - show activity with zero ID(invalid input)")
    @Test
    public void showActivityWithZeroId() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ResponseEntity<Activity> responseEntity = activityController.showActivity(0L);

        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - show activity - no activity found in database")
    @Test
    public void showNoActivityFound() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);
        doReturn(Optional.empty()).when(activityRepositoryCrud).findById(20L);

        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ResponseEntity<Activity> responseEntity = activityController.showActivity(20L);

        verify(activityRepositoryCrud).findById(20L);
        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - show all activities - activities returned, everything OK")
    @Test
    public void showAllActivities() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        List<ActivityDTO> activities = new ArrayList<>();
        activities.add(new ActivityDTO(1L, new Date(), 10L, new Date(), new Date(), false));
        activities.add(new ActivityDTO(2L, new Date(), 10L, new Date(), new Date(), false));
        activities.add(new ActivityDTO(3L, new Date(), 10L, new Date(), new Date(), false));

        doReturn(activities).when(activityRepositoryCrud).findAllByGroupId(20L);

        ActivityController activityController = spy(new ActivityController(
                activityRepositoryCrud,
                activityGroupRepository,
                deviceGroupRepositoryCrud)
        );

        doReturn(Optional.of(10L)).when((SecurityInfo)activityController).getLoggedUserId();
        ResponseEntity<ActivityList> responseEntity = activityController.showActivitiesByParentId(20L);

        verify(activityRepositoryCrud).findAllByGroupId(20L);
        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - show all activities - no activities returned, everything OK")
    @Test
    public void showAllActivitiesEmpty() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        when(activityGroupRepository.showGroupsByParentId(10L, 20L)).thenReturn(Optional.empty());

        ActivityList activities = new ActivityList(new ArrayList<>());
        doReturn(activities).when(activityRepositoryCrud).findAllByGroupId(20L);

        ActivityController activityController = spy(new ActivityController(
                activityRepositoryCrud,
                activityGroupRepository,
                deviceGroupRepositoryCrud)
        );

        doReturn(Optional.of(10L)).when((SecurityInfo)activityController).getLoggedUserId();
        ResponseEntity<ActivityList> responseEntity = activityController.showActivitiesByParentId(20L);

        verify(activityRepositoryCrud).findAllByGroupId(20L);
        verifyNoMoreInteractions(activityRepositoryCrud);

        verify(activityGroupRepository).showGroupsByParentId(10L, 20L);
        verifyNoMoreInteractions(activityGroupRepository);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - show activity with negative group ID(invalid input)")
    @Test
    public void showAllActivitiesWithNegativeGroupId() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ResponseEntity<ActivityList> responseEntity = activityController.showActivitiesByParentId(-1L);

        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - show activity with zero group ID(invalid input)")
    @Test
    public void showAllActivitiesWithZeroGroupId() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ResponseEntity<ActivityList> responseEntity = activityController.showActivitiesByParentId(0L);

        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - create activity - everything is OK")
    @Test
    public void add() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityDTO savedActivityDTO = new ActivityDTO();
        Date timestamp = new Date();
        savedActivityDTO.setTimestamp(timestamp);
        savedActivityDTO.setId(20L);
        savedActivityDTO.setGroupId(10L);
        savedActivityDTO.setCreatedAt(timestamp);
        savedActivityDTO.setUpdatedAt(timestamp);

        when(activityRepositoryCrud.save(any(ActivityDTO.class))).thenReturn(savedActivityDTO);

        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ActivityCreate activityCreate = new ActivityCreate();
        activityCreate.setGroupId(20L);
        ResponseEntity<Activity> responseEntity = activityController.addActivity(activityCreate);

        verify(activityRepositoryCrud).save(any(ActivityDTO.class));
        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(responseEntity.getBody().getId(), 20L);
        assertEquals(responseEntity.getBody().getGroupId(), 10L);
        assertEquals(responseEntity.getBody().getTimestamp(), timestamp.getTime());
    }

    @DisplayName("Activity Controller - create activity with negative group ID(invalid input)")
    @Test
    public void addActivityWithNegativeGroupId() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityCreate activityCreate = new ActivityCreate();
        activityCreate.setGroupId(-1L);

        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ResponseEntity<Activity> responseEntity = activityController.addActivity(activityCreate);

        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - create activity with zero group ID(invalid input)")
    @Test
    public void addActivityWithZeroGroupId() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityCreate activityCreate = new ActivityCreate();
        activityCreate.setGroupId(0L);
        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ResponseEntity<Activity> responseEntity = activityController.addActivity(activityCreate);

        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - update activity - everything OK")
    @Test
    public void update() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityUpdate activityUpdate = new ActivityUpdate();
        Date timestamp = new Date();
        activityUpdate.setTimestamp(timestamp.getTime());
        activityUpdate.setId(20L);

        ActivityDTO activityToUpdate = new ActivityDTO();
        activityToUpdate.setTimestamp(timestamp);
        activityToUpdate.setId(20L);
        activityToUpdate.setGroupId(10L);
        activityToUpdate.setCreatedAt(timestamp);
        activityToUpdate.setUpdatedAt(timestamp);

        doReturn(Optional.of(activityToUpdate)).when(activityRepositoryCrud).findById(20L);
        doReturn(activityToUpdate).when(activityRepositoryCrud).save(activityToUpdate);

        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ResponseEntity<Activity> responseEntity = activityController.updateActivity(activityUpdate);

        verify(activityRepositoryCrud).findById(20L);
        verify(activityRepositoryCrud).save(activityToUpdate);
        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(20L, responseEntity.getBody().getId());
        assertEquals(10L, responseEntity.getBody().getGroupId());
        assertEquals(timestamp.getTime(), responseEntity.getBody().getTimestamp());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - update activity with negative timestamp(invalid input)")
    @Test
    public void updateActivityWithNegativeTimestamp() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityUpdate updatedActivity = new ActivityUpdate();
        updatedActivity.setTimestamp(new Date().getTime());
        updatedActivity.setId(-1L);

        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ResponseEntity<Activity> responseEntity = activityController.updateActivity(updatedActivity);

        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - update activity with zero timestamp(invalid input)")
    @Test
    public void updateActivityWithZeroTimestamp() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityUpdate updatedActivity = new ActivityUpdate();
        updatedActivity.setTimestamp(new Date().getTime());
        updatedActivity.setId(0L);

        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ResponseEntity<Activity> responseEntity = activityController.updateActivity(updatedActivity);

        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - update activity - no activity found in database cannot execute update")
    @Test
    public void updateNoActivityFound() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);
        doReturn(Optional.empty()).when(activityRepositoryCrud).findById(20L);

        ActivityUpdate activityUpdate = new ActivityUpdate();
        Date timestamp = new Date();
        activityUpdate.setTimestamp(timestamp.getTime());
        activityUpdate.setId(20L);

        ActivityDTO activityToUpdate = new ActivityDTO();
        activityToUpdate.setTimestamp(timestamp);
        activityToUpdate.setId(20L);
        activityToUpdate.setGroupId(10L);

        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ResponseEntity<Activity> responseEntity = activityController.updateActivity(activityUpdate);

        verify(activityRepositoryCrud).findById(20L);
        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - update activity to now - everything OK")
    @Test
    public void updateToNow() throws InterruptedException {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityDTO activityToUpdateNow = new ActivityDTO();
        Date timestamp = new Date();
        activityToUpdateNow.setTimestamp(timestamp);
        activityToUpdateNow.setId(20L);
        activityToUpdateNow.setGroupId(10L);
        activityToUpdateNow.setCreatedAt(timestamp);
        activityToUpdateNow.setUpdatedAt(timestamp);

        Thread.sleep(1000);

        ActivityDTO activityUpdatedToNow = new ActivityDTO();
        Date afterTimestamp = new Date();
        activityUpdatedToNow.setTimestamp(afterTimestamp);
        activityUpdatedToNow.setId(20L);
        activityUpdatedToNow.setGroupId(10L);
        activityUpdatedToNow.setCreatedAt(afterTimestamp);
        activityUpdatedToNow.setUpdatedAt(afterTimestamp);

        doReturn(Optional.of(activityToUpdateNow)).when(activityRepositoryCrud).findById(20L);
        doReturn(activityUpdatedToNow).when(activityRepositoryCrud).save(any(ActivityDTO.class));

        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ResponseEntity<Activity> responseEntity = activityController.updateToNowActivity(20L);

        verify(activityRepositoryCrud).findById(20L);
        verify(activityRepositoryCrud).save(any(ActivityDTO.class));
        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(20L, responseEntity.getBody().getId());
        assertEquals(10L, responseEntity.getBody().getGroupId());
        assertNotEquals(timestamp, responseEntity.getBody().getTimestamp());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - update activity to now - negative activityID (invalid input)")
    @Test
    public void updateToNowNegativeActivityId() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ResponseEntity<Activity> responseEntity = activityController.updateToNowActivity(-1L);

        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - update activity to now - zero activityID (invalid input)")
    @Test
    public void updateToNowZeroActivityId() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ResponseEntity<Activity> responseEntity = activityController.updateToNowActivity(0L);

        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - update activity to now - no activity found in database cannot execute update to now")
    @Test
    public void updateToNowNoActivityFound() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        doReturn(Optional.empty()).when(activityRepositoryCrud).findById(20L);

        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ResponseEntity<Activity> responseEntity = activityController.updateToNowActivity(20L);

        verify(activityRepositoryCrud).findById(20L);
        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - delete activity - everything OK")
    @Test
    public void delete() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityDTO activityToUpdateNow = new ActivityDTO();
        Date afterTimestamp = new Date();
        activityToUpdateNow.setTimestamp(afterTimestamp);
        activityToUpdateNow.setId(20L);
        activityToUpdateNow.setGroupId(10L);
        activityToUpdateNow.setCreatedAt(afterTimestamp);
        activityToUpdateNow.setUpdatedAt(afterTimestamp);

        doReturn(true).when(activityRepositoryCrud).existsById(20L);
        doReturn(Optional.of(activityToUpdateNow)).when(activityRepositoryCrud).findById(20L);

        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ResponseEntity<String> responseEntity = activityController.deleteActivity(20L);

        verify(activityRepositoryCrud).existsById(20L);
        verify(activityRepositoryCrud).findById(20L);
        verify(activityRepositoryCrud).save(any(ActivityDTO.class));
        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - delete activity with negative ID(invalid input)")
    @Test
    public void deleteActivityWithNegativeId() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ResponseEntity<String> responseEntity = activityController.deleteActivity(-1L);

        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - delete activity with zero ID(invalid input)")
    @Test
    public void deleteActivityWithZeroId() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ResponseEntity<String> responseEntity = activityController.deleteActivity(0L);

        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @DisplayName("Activity Controller - delete activity - no activity found in database cannot execute delete")
    @Test
    public void deleteNoActivityFound() {
        ActivityRepositoryCrud activityRepositoryCrud = mock(ActivityRepositoryCrud.class);
        ActivityGroupRepository activityGroupRepository = mock(ActivityGroupRepository.class);
        DeviceGroupRepositoryCrud deviceGroupRepositoryCrud = mock(DeviceGroupRepositoryCrud.class);

        doReturn(false).when(activityRepositoryCrud).existsById(20L);

        ActivityController activityController = new ActivityController(activityRepositoryCrud, activityGroupRepository, deviceGroupRepositoryCrud);
        ResponseEntity<String> responseEntity = activityController.deleteActivity(20L);

        verify(activityRepositoryCrud).existsById(20L);
        verifyNoMoreInteractions(activityRepositoryCrud);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}
