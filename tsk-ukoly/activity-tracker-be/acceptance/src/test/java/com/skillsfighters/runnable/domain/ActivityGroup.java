package com.skillsfighters.runnable.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@NoArgsConstructor
@Setter
@Getter
public class ActivityGroup {
    private long id;
    private long createdAt;
    private long updatedAt;
    private String name;
    private long userId;
    private Optional<Long> parentId;
}
