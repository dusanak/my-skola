package com.skillsfighters.repository;

import com.skillsfighters.controllers.responses.GroupResponseList;
import com.skillsfighters.domain.ActivityGroup;

import java.util.Optional;

public interface ActivityGroupRepository {
    Optional<Long> add(String name, long userId, Optional<Long> parentId);

    boolean delete(long groupId);

    boolean update(String name, long userId, long groupId);

    Optional<ActivityGroup> show(long groupId);

    Optional<GroupResponseList> showGroups(long userId);

    Optional<GroupResponseList> showGroupsByParentId(long userId, long parentId);
}
