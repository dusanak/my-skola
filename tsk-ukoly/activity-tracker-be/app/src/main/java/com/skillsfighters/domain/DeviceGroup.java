package com.skillsfighters.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@JsonDeserialize(builder = com.skillsfighters.domain.DeviceGroup.DeviceGroupBuilder.class)
public class DeviceGroup {

    private final long id;
    private final String firebaseUid;
    private final List<String> FCMTokens;

    //Important component for functioning builder and JSON deserialization
    @JsonPOJOBuilder(withPrefix = "")
    public static class DeviceGroupBuilder {
    }
}
