package com.skillsfighters.runnable.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class Activity {
    private long id;
    private long createdAt;
    private long updatedAt;
    private long timestamp;
    private long groupId;
}
