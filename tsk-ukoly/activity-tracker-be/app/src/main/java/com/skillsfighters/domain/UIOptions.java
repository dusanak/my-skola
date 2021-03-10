package com.skillsfighters.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Builder
@Getter
@JsonDeserialize(builder = com.skillsfighters.domain.UIOptions.UIOptionsBuilder.class)
public class UIOptions {
    private long id;
    private long groupId;
    private Integer color;
    private String unit;
    private String icon;

    //Important component for functioning builder and JSON deserialization
    @JsonPOJOBuilder(withPrefix = "")
    public static class UIOptionsBuilder {
    }
}
