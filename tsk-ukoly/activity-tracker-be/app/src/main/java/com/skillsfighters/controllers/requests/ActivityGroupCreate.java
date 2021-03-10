package com.skillsfighters.controllers.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityGroupCreate {
    private String name;
    private long parentId;
}
