package com.skillsfighters.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class User {
    private final long id;
    private final String firebaseUid;
}