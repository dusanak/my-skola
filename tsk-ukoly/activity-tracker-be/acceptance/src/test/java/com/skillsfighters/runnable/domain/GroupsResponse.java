package com.skillsfighters.runnable.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupsResponse extends EntityResponse {
    private String name;
    private long activityCount;

    public GroupsResponse() {
    }

    public GroupsResponse(final long id, final String name, final long activityCount) {
        super(id);
        this.name = name;
        this.activityCount = activityCount;
    }
}
