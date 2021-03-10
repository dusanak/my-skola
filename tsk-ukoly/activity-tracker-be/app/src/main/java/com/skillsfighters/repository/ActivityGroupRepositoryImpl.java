package com.skillsfighters.repository;

import com.google.common.annotations.VisibleForTesting;
import com.skillsfighters.controllers.responses.GroupResponseList;
import com.skillsfighters.domain.ActivityGroup;
import com.skillsfighters.repository.extractor.ActivityGroupExtractor;
import com.skillsfighters.repository.extractor.GroupsExtractor;
import com.skillsfighters.security.SecurityInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.sql.PreparedStatement;
import java.util.Optional;

@Component
@Slf4j
public class ActivityGroupRepositoryImpl implements ActivityGroupRepository, SecurityInfo {
    private JdbcOperations template;
    private ActivityGroupExtractor activityGroupExtractor;
    private GroupsExtractor groupsExtractor;


    @Autowired
    public ActivityGroupRepositoryImpl(@NotNull final JdbcOperations template) {
        this.template = template;
        this.activityGroupExtractor = new ActivityGroupExtractor();
        this.groupsExtractor = new GroupsExtractor();
    }

    @Override
    public Optional<ActivityGroup> show(@NotNull final long groupId) {
        log.debug("user: {} - activityGroup repository - show group {} successful", printLoggedUserFirebaseUid(), groupId);
        return template.query(
                "SELECT id, name, user_id, created_at, updated_at, parent_id FROM groups_tb WHERE id = ?",
                new Object[]{groupId},
                activityGroupExtractor);

    }

    @Override
    public Optional<GroupResponseList> showGroups(@NotNull final long userId) {
        log.debug("user: {} - activityGroup repository - show all groups successful, userID = {}", printLoggedUserFirebaseUid(), userId);
        return template.query("SELECT groups_tb.id, groups_tb.parent_id, groups_tb.name, count(activities.id)\n"
                        + "FROM groups_tb\n"
                        + "LEFT JOIN activities ON groups_tb.id = activities.group_id\n"
                        + "WHERE groups_tb.user_id=?\n"
                        + "GROUP BY groups_tb.id;",
                new Object[]{userId},
                groupsExtractor);
    }

    @Override
    public Optional<GroupResponseList> showGroupsByParentId(long userId, long parentId) {
        log.debug("user: {} - activityGroup repository - show groups by parentId successful, userID = {}, parentID = {}", printLoggedUserFirebaseUid(), userId, parentId);
        return template.query("SELECT groups_tb.id, groups_tb.parent_id, groups_tb.name, count(activities.id)\n"
                        + "FROM groups_tb\n"
                        + "LEFT JOIN activities ON groups_tb.id = activities.group_id\n"
                        + "WHERE groups_tb.user_id=? AND groups_tb.parent_id=?\n"
                        + "GROUP BY groups_tb.id;",
                new Object[]{userId, parentId},
                groupsExtractor);
    }

    @Override
    public Optional<Long> add(@NotNull final String name, @NotNull final long userId, Optional<Long> parentId) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        return add(name, userId, parentId, keyHolder);
    }

    @VisibleForTesting
    public Optional<Long> add(@NotNull final String name, @NotNull final long userId, final Optional<Long> parentId, @NotNull final KeyHolder keyHolder) {
        final int affectedRows = template.update((con) -> {
                    if (parentId.isPresent()) {
                        final PreparedStatement pst =
                                con.prepareStatement("INSERT INTO groups_tb (name, user_id, parent_id) VALUES (? , ?, ?);", new String[]{"id"});
                        pst.setString(1, name);
                        pst.setLong(2, userId);
                        pst.setLong(3, parentId.get());
                        return pst;
                    } else {
                        final PreparedStatement pst =
                                con.prepareStatement("INSERT INTO groups_tb (name, user_id) VALUES (? , ?);", new String[]{"id"});
                        pst.setString(1, name);
                        pst.setLong(2, userId);
                        return pst;
                    }
                },
                keyHolder);
        if (affectedRows == 1) {
            log.debug("user: {} - activityGroup repository - adding group {} successful, userID = {}",
                    printLoggedUserFirebaseUid(), keyHolder.getKey().longValue(), userId);
            return Optional.of(keyHolder.getKey().longValue());
        } else {
            log.error("user: {} - activityGroup repository - something went wrong with adding group - added {} groups, userID = {}",
                    printLoggedUserFirebaseUid(), affectedRows, userId);
            return Optional.empty();
        }
    }

    @Override
    public boolean update(@NotNull final String name, @NotNull final long userId, @NotNull final long groupId) {
        int affectedRows = template.update("UPDATE groups_tb SET name = ?, user_id = ? WHERE id = ?", name, userId, groupId);
        if (affectedRows > 1) {
            log.warn("user: {} - activityGroup repository - something went wrong - updated {} groups, groupId = {}, userID = {}",
                    printLoggedUserFirebaseUid(), affectedRows, groupId, userId);
            return true;
        } else if (affectedRows == 1) {
            log.debug("user: {} - activityGroup repository - update of group {} successful, userID = {}", printLoggedUserFirebaseUid(), groupId, userId);
            return true;
        } else {
            log.error("user: {} - activityGroup repository - something went wrong with updating group {}, userID = {}", printLoggedUserFirebaseUid(), groupId, userId);
            return false;
        }
    }

    @Override
    public boolean delete(@NotNull final long groupId) {
        int affectedRows = template.update("DELETE FROM groups_tb WHERE id = ?", groupId);
        if (affectedRows > 1) {
            log.warn("user: {} - activityGroup repository - something went wrong - deleted {} groups, groupId = {}",
                    printLoggedUserFirebaseUid(), affectedRows, groupId);
            return true;
        } else if (affectedRows == 1) {
            log.debug("user: {} - activityGroup repository - delete group {} successful", printLoggedUserFirebaseUid(), groupId);
            return true;
        } else {
            log.error("user: {} - activityGroup repository - something went wrong with deleting group {}", printLoggedUserFirebaseUid(), groupId);
            return false;
        }
    }
}
