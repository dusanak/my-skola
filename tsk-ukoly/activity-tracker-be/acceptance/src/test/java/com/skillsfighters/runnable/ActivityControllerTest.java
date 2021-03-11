package com.skillsfighters.runnable;

import com.skillsfighters.runnable.domain.Activity;
import com.skillsfighters.runnable.domain.ActivityList;
import com.skillsfighters.runnable.helper.CreateHelper;
import com.skillsfighters.runnable.helper.HttpHelperAuth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;


public class ActivityControllerTest {
    private long groupId;
    private long activityId;

    @BeforeEach
    public void initialization() {
        groupId = CreateHelper.createGroup();
        activityId = CreateHelper.createActivity(groupId);
    }

    @Test
    public void testAddActivity() {
        String urlAddActivity = "http://localhost:8080/activity/add?groupid=" + groupId;
        String requestJson = "{\"groupId\":" + groupId + "}";

        ResponseEntity<Activity> responseEntity = HttpHelperAuth.createAuth(urlAddActivity, requestJson, Activity.class);
        long localActivityId = responseEntity.getBody().getId();

        assertTrue(localActivityId > 0);
        assertTrue(responseEntity.getBody().getTimestamp() > 0);
        assertNotEquals(0L, responseEntity.getBody().getCreatedAt());
        assertNotEquals(0L, responseEntity.getBody().getUpdatedAt());
        assertEquals(groupId, responseEntity.getBody().getGroupId());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void testDeleteActivity() {
        String urlDeleteActivity = "http://localhost:8080/activity/delete?activityid=" + activityId;
        HttpHelperAuth.deleteAuth(urlDeleteActivity);
        String urlShowActivity = ("http://localhost:8080/activity/show?activityid=" + activityId);

        HttpClientErrorException thrownException = null;
        try {
            HttpHelperAuth.showAuth(urlShowActivity, Activity.class);
        } catch (HttpClientErrorException exception) {
            thrownException = exception;
        }

        assertNotNull(thrownException);
        assertEquals(HttpStatus.NOT_FOUND, thrownException.getStatusCode());
    }

    @Test
    public void testUpdateActivity() {
        String urlUpdateActivity = "http://localhost:8080/activity/update";
        String requestJson = "{\"timestamp\":1220227200000,\"id\":" + activityId + "}";
        ResponseEntity<Activity> responseEntity = HttpHelperAuth.updateAuth(urlUpdateActivity, requestJson, Activity.class);

        assertEquals(groupId, responseEntity.getBody().getGroupId());
        assertEquals(activityId, responseEntity.getBody().getId());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotEquals(0L, responseEntity.getBody().getCreatedAt());
        assertNotEquals(0L, responseEntity.getBody().getUpdatedAt());
        assertTrue(responseEntity.getBody().getCreatedAt() < responseEntity.getBody().getUpdatedAt());
    }

    @Test
    public void testUpdateToNowActivity() {
        String urlUpdateActivityToNow = "http://localhost:8080/activity/updatetonow?activityid=" + activityId;
        ResponseEntity<Activity> responseEntity = HttpHelperAuth.updateToNowAuth(urlUpdateActivityToNow, Activity.class);

        assertEquals(groupId, responseEntity.getBody().getGroupId());
        assertEquals(activityId, responseEntity.getBody().getId());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotEquals(0L, responseEntity.getBody().getCreatedAt());
        assertNotEquals(0L, responseEntity.getBody().getUpdatedAt());
        assertTrue(responseEntity.getBody().getCreatedAt() < responseEntity.getBody().getUpdatedAt());
    }

    @Test
    public void testShowActivity() {
        String urlShowActivity = ("http://localhost:8080/activity/show?activityid=" + activityId);
        ResponseEntity<Activity> responseEntity = HttpHelperAuth.showAuth(urlShowActivity, Activity.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(activityId, responseEntity.getBody().getId());
        assertNotEquals(0L, responseEntity.getBody().getCreatedAt());
        assertNotEquals(0L, responseEntity.getBody().getUpdatedAt());
    }

    @Test
    public void testShowActivityWithNegativeId() {
        String urlShowActivity = ("http://localhost:8080/activity/show?activityid=-1");

        HttpClientErrorException thrownException = null;
        try {
            HttpHelperAuth.showAuth(urlShowActivity, Activity.class);
        } catch (HttpClientErrorException exception) {
            thrownException = exception;
        }

        assertNotNull(thrownException);
        assertEquals(HttpStatus.BAD_REQUEST, thrownException.getStatusCode());
    }

    @Test
    public void testShowActivities() {
        String urlAddActivity = "http://localhost:8080/activity/add";
        String requestJson = "{\"groupId\":" + groupId + "}";

        ResponseEntity<Activity> secondActivityResponseEntity = HttpHelperAuth.createAuth(urlAddActivity, requestJson, Activity.class);
        long localSecondActivityId = secondActivityResponseEntity.getBody().getId();
        ResponseEntity<Activity> thirdActivityResponseEntity = HttpHelperAuth.createAuth(urlAddActivity, requestJson, Activity.class);
        long localThirdActivityId = thirdActivityResponseEntity.getBody().getId();

        String urlShowActivities = "http://localhost:8080/activity/showallbyparentid?groupid=" + groupId;
        ResponseEntity<ActivityList> activitiesResponseEntity = HttpHelperAuth.showAuth(urlShowActivities, ActivityList.class);

        ActivityList body = activitiesResponseEntity.getBody();
        Activity first = body.get(0);
        Activity second = body.get(1);
        Activity third = body.get(2);

        assertEquals(HttpStatus.OK, activitiesResponseEntity.getStatusCode());
        assertEquals(body.size(), 3);

        assertEquals(first.getGroupId(), groupId);
        assertEquals(first.getId(), activityId);

        assertEquals(second.getGroupId(), groupId);
        assertEquals(second.getId(), localSecondActivityId);

        assertEquals(third.getGroupId(), groupId);
        assertEquals(third.getId(), localThirdActivityId);
    }
}
