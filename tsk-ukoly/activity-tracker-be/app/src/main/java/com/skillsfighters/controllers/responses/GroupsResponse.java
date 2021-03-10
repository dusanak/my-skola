package com.skillsfighters.controllers.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class GroupsResponse extends EntityResponse {
    private String name;
    private long activityCount;
    private Optional<Long> parentId;

    public GroupsResponse() {
    }

    public GroupsResponse(final long id, final String name, final long activityCount, final Optional<Long> parentId) {
        super(id);
        this.name = name;
        this.activityCount = activityCount;
        this.parentId = parentId;
    }
}
