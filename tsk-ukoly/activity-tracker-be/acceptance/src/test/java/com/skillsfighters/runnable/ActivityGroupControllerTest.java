package com.skillsfighters.runnable;

import com.skillsfighters.runnable.domain.*;
import com.skillsfighters.runnable.helper.CreateHelper;
import com.skillsfighters.runnable.helper.HttpHelperAuth;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;


public class ActivityGroupControllerTest {
    private long groupId;

    @Before
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

        Assert.assertNotNull(responseEntity.getBody());
        Assert.assertTrue(localGroupId > 0);
        Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void testAddActivityGroupWithParentId() {
        String urlAddGroup = "http://localhost:8080/group/add";
        String requestJson = "{\"name\":\"Vanilla\",\"parentId\":\"" + groupId + "\"}";

        ResponseEntity<ActivityGroup> responseEntity = HttpHelperAuth.createAuth(urlAddGroup, requestJson, ActivityGroup.class);

        long localGroupId = responseEntity.getBody().getId();

        Assert.assertNotNull(responseEntity.getBody());
        Assert.assertTrue(localGroupId > 0);
        Assert.assertFalse(groupId == localGroupId);
        Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
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
        Assert.assertNotNull(thrownException);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, thrownException.getStatusCode());
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

        Assert.assertNotNull(thrownException);
        Assert.assertEquals(HttpStatus.NOT_FOUND, thrownException.getStatusCode());
    }

    @Test
    public void testUpdateActivityGroup() {
        String urlUpdateGroup = "http://localhost:8080/group/update";
        String requestJson = "{\"name\":\"Fanta\", \"id\":" + groupId + "}";
        ResponseEntity<ActivityGroup> responseEntity = HttpHelperAuth.updateAuth(urlUpdateGroup, requestJson, ActivityGroup.class);

        Assert.assertEquals(groupId, responseEntity.getBody().getId());
        Assert.assertEquals("Fanta", responseEntity.getBody().getName());
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertNotNull(responseEntity.getBody().getCreatedAt());
        Assert.assertNotNull(responseEntity.getBody().getUpdatedAt());
        Assert.assertTrue(responseEntity.getBody().getCreatedAt() < responseEntity.getBody().getUpdatedAt());
    }

    @Test
    public void testShowActivityGroup() {
        String urlShowActivityGroup = "http://localhost:8080/group/show?groupid=" + groupId;
        ResponseEntity<ActivityGroup> responseEntity = HttpHelperAuth.showAuth(urlShowActivityGroup, ActivityGroup.class);

        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals(groupId, responseEntity.getBody().getId());
        Assert.assertNotNull(responseEntity.getBody().getCreatedAt());
        Assert.assertNotNull(responseEntity.getBody().getUpdatedAt());
    }

    @Test
    public void testCountActivityGroup() {
        String urlCountActivityGroup = "http://localhost:8080/group/count?groupid=" + groupId;
        ResponseEntity<Long> responseEntity = HttpHelperAuth.showAuth(urlCountActivityGroup, Long.class);

        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertNotNull(responseEntity.getBody());
        Assert.assertEquals(3L, responseEntity.getBody().longValue());
    }
}
