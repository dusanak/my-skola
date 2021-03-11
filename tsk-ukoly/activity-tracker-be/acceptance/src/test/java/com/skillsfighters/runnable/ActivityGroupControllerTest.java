package com.skillsfighters.runnable;

import com.skillsfighters.runnable.domain.ActivityGroup;
import com.skillsfighters.runnable.helper.CreateHelper;
import com.skillsfighters.runnable.helper.HttpHelperAuth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;


public class ActivityGroupControllerTest {
    private long groupId;

    @BeforeEach
    public void initialization() {
        groupId = CreateHelper.createGroup();
        CreateHelper.createActivity(groupId);
        CreateHelper.createActivity(groupId);
        CreateHelper.createActivity(groupId);
    }

    @Test
    public void testAddActivityGroup() {
        String urlAddGroup = "http://localhost:8080/group/add";
        String requestJson = "{\"name\":\"CocaCola\"}";

        ResponseEntity<ActivityGroup> responseEntity = HttpHelperAuth.createAuth(urlAddGroup, requestJson, ActivityGroup.class);

        long localGroupId = responseEntity.getBody().getId();

        assertNotNull(responseEntity.getBody());
        assertTrue(localGroupId > 0);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void testAddActivityGroupWithParentId() {
        String urlAddGroup = "http://localhost:8080/group/add";
        String requestJson = "{\"name\":\"Vanilla\",\"parentId\":\"" + groupId + "\"}";

        ResponseEntity<ActivityGroup> responseEntity = HttpHelperAuth.createAuth(urlAddGroup, requestJson, ActivityGroup.class);

        long localGroupId = responseEntity.getBody().getId();

        assertNotNull(responseEntity.getBody());
        assertTrue(localGroupId > 0);
        assertFalse(groupId == localGroupId);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void testAddActivityGroupWithoutName() {
        String urlAddGroup = "http://localhost:8080/group/add";
        String requestJson = "{\"name\":\"\"}";

        HttpClientErrorException thrownException = null;
        try {
            HttpHelperAuth.createAuth(urlAddGroup, requestJson, ActivityGroup.class);
        } catch (HttpClientErrorException exception) {
            thrownException = exception;
        }
        assertNotNull(thrownException);
        assertEquals(HttpStatus.BAD_REQUEST, thrownException.getStatusCode());
    }

    @Test
    public void testDeleteActivityGroup() {
        String urlDeleteActivityGroup = ("http://localhost:8080/group/delete?groupid=" + groupId);
        HttpHelperAuth.deleteAuth(urlDeleteActivityGroup);
        String urlShowGroup = ("http://localhost:8080/usernamegroup/show?groupid=" + groupId);

        HttpClientErrorException thrownException = null;
        try {
            HttpHelperAuth.showAuth(urlShowGroup, ActivityGroup.class);
        } catch (HttpClientErrorException exception) {
            thrownException = exception;
        }

        assertNotNull(thrownException);
        assertEquals(HttpStatus.NOT_FOUND, thrownException.getStatusCode());
    }

    @Test
    public void testUpdateActivityGroup() {
        String urlUpdateGroup = "http://localhost:8080/group/update";
        String requestJson = "{\"name\":\"Fanta\", \"id\":" + groupId + "}";
        ResponseEntity<ActivityGroup> responseEntity = HttpHelperAuth.updateAuth(urlUpdateGroup, requestJson, ActivityGroup.class);

        assertEquals(groupId, responseEntity.getBody().getId());
        assertEquals("Fanta", responseEntity.getBody().getName());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody().getCreatedAt());
        assertNotNull(responseEntity.getBody().getUpdatedAt());
        assertTrue(responseEntity.getBody().getCreatedAt() < responseEntity.getBody().getUpdatedAt());
    }

    @Test
    public void testShowActivityGroup() {
        String urlShowActivityGroup = "http://localhost:8080/group/show?groupid=" + groupId;
        ResponseEntity<ActivityGroup> responseEntity = HttpHelperAuth.showAuth(urlShowActivityGroup, ActivityGroup.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(groupId, responseEntity.getBody().getId());
        assertNotNull(responseEntity.getBody().getCreatedAt());
        assertNotNull(responseEntity.getBody().getUpdatedAt());
    }

    @Test
    public void testCountActivityGroup() {
        String urlCountActivityGroup = "http://localhost:8080/group/count?groupid=" + groupId;
        ResponseEntity<Long> responseEntity = HttpHelperAuth.showAuth(urlCountActivityGroup, Long.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(3L, responseEntity.getBody().longValue());
    }
}
