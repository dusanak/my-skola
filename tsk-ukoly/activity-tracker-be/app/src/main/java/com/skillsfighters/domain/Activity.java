package com.skillsfighters.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

@Builder
@Getter
@JsonDeserialize(builder = Activity.ActivityBuilder.class)
public class Activity {
    private final long id;
    private final long createdAt;
    private final long updatedAt;
    private final long timestamp;
    private final long groupId;

    //Important component for functioning builder and JSON deserialization
    @JsonPOJOBuilder(withPrefix = "")
    public static class ActivityBuilder {
    }
}
