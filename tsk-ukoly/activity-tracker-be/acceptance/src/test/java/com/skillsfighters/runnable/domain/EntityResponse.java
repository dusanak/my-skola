package com.skillsfighters.runnable.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class EntityResponse {
    private long id;
    private long createdAt;
    private long updatedAt;

    public EntityResponse() {
    }

    public EntityResponse(final long id) {
        this.id = id;
    }
}
