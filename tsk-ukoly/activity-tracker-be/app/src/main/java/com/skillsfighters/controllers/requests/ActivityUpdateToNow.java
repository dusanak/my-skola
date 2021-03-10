package com.skillsfighters.controllers.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityUpdateToNow {
    private long groupId;
    private long activityId;
}
