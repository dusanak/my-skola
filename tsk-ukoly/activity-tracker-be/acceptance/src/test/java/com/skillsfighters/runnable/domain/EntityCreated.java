package com.skillsfighters.runnable.domain;

import javax.validation.constraints.NotNull;

public class EntityCreated extends EntityResponse {

    public EntityCreated() {
    }

    public EntityCreated(@NotNull final long id) {
        super(id);
    }
}
