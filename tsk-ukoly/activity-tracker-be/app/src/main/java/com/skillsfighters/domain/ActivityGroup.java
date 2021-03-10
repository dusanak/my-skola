package com.skillsfighters.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Builder
@Getter
@JsonDeserialize(builder = ActivityGroup.ActivityGroupBuilder.class)
public class ActivityGroup {
    private final long id;
    private final long createdAt;
    private final long updatedAt;
    private final String name;
    private final long userId;
    private final Optional<Long> parentId;

    //Important component for functioning builder and JSON deserialization
    @JsonPOJOBuilder(withPrefix = "")
    public static class ActivityGroupBuilder {
    }
}
