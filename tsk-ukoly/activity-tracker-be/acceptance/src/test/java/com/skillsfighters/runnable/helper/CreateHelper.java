package com.skillsfighters.runnable.helper;

import com.skillsfighters.runnable.domain.Activity;
import com.skillsfighters.runnable.domain.ActivityGroup;
import org.springframework.http.*;


public class CreateHelper {
    public static long createGroup() {
        String urlAddGroup = "http://localhost:8080/group/add";
        String requestJson = "{\"name\":\"CocaCola\"}";

        ResponseEntity<ActivityGroup> responseEntity = HttpHelperAuth.createAuth(urlAddGroup, requestJson, ActivityGroup.class);
        return responseEntity.getBody().getId();
    }

    public static long createActivity(long groupId) {
        String urlAddActivity = "http://localhost:8080/activity/add?groupid=" + groupId;
        String requestJson = "{\"groupId\":" + groupId + "}";

        ResponseEntity<Activity> responseEntity = HttpHelperAuth.createAuth(urlAddActivity, requestJson, Activity.class);
        return responseEntity.getBody().getId();
    }
}
