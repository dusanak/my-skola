package com.skillsfighters;

import com.skillsfighters.controllers.responses.GroupResponseList;
import com.skillsfighters.controllers.responses.GroupsResponse;
import com.skillsfighters.domain.ActivityGroup;
import com.skillsfighters.repository.ActivityGroupRepository;
import com.skillsfighters.repository.ActivityGroupRepositoryImpl;
import com.skillsfighters.repository.extractor.ActivityGroupExtractor;
import com.skillsfighters.repository.extractor.GroupsExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ActivityGroupRepositoryTest {

    @DisplayName("ActivityGroup Repository - show - everything OK")
    @Test
    public void show() {
        JdbcOperations template = mock(JdbcOperations.class);

        ActivityGroup activityToShow = ActivityGroup.builder().id(20L).name("CocaCola").userId(10L).build();

        doReturn(Optional.of(activityToShow)).when(template).query(eq("SELECT id, name, user_id, created_at, updated_at, parent_id FROM groups_tb WHERE id = ?"),
                any(Object[].class),
                any(ActivityGroupExtractor.class));

        ActivityGroupRepository activityRepository = new ActivityGroupRepositoryImpl(template);
        Optional<ActivityGroup> showedActivityGroup = activityRepository.show(20L);

        verify(template).query(eq("SELECT id, name, user_id, created_at, updated_at, parent_id FROM groups_tb WHERE id = ?"),
                any(Object[].class),
                any(ActivityGroupExtractor.class));
        verifyNoMoreInteractions(template);

        assertEquals(20L, showedActivityGroup.get().getId());
        assertEquals("CocaCola", showedActivityGroup.get().getName());
        assertEquals(10L, showedActivityGroup.get().getUserId());
    }

    @DisplayName("ActivityGroup Repository - show all groups - everything OK")
    @Test
    public void showGroups() {
        JdbcOperations template = mock(JdbcOperations.class);
        GroupResponseList groupsToShow = new GroupResponseList();
        groupsToShow.add(new GroupsResponse());

        doReturn(Optional.of(groupsToShow)).when(template).query(eq("SELECT groups_tb.id, groups_tb.parent_id, groups_tb.name, count(activities.id)\n"
                        + "FROM groups_tb\n"
                        + "LEFT JOIN activities ON groups_tb.id = activities.group_id\n"
                        + "WHERE groups_tb.user_id=?\n"
                        + "GROUP BY groups_tb.id;"),
                any(Object[].class),
                any(GroupsExtractor.class));

        ActivityGroupRepository activityRepository = new ActivityGroupRepositoryImpl(template);
        Optional<GroupResponseList> showedActivities = activityRepository.showGroups(20L);

        verify(template).query(eq("SELECT groups_tb.id, groups_tb.parent_id, groups_tb.name, count(activities.id)\n"
                        + "FROM groups_tb\n"
                        + "LEFT JOIN activities ON groups_tb.id = activities.group_id\n"
                        + "WHERE groups_tb.user_id=?\n"
                        + "GROUP BY groups_tb.id;"),
                any(Object[].class),
                any((GroupsExtractor.class)));
        verifyNoMoreInteractions(template);

        assertEquals(1, showedActivities.get().size());
    }

    @DisplayName("ActivityGroup Repository - show all groups - everything OK")
    @Test
    public void showGroupsByParentId() {
        JdbcOperations template = mock(JdbcOperations.class);
        GroupResponseList groupsToShow = new GroupResponseList();
        groupsToShow.add(new GroupsResponse());

        doReturn(Optional.of(groupsToShow)).when(template).query(eq("SELECT groups_tb.id, groups_tb.parent_id, groups_tb.name, count(activities.id)\n"
                        + "FROM groups_tb\n"
                        + "LEFT JOIN activities ON groups_tb.id = activities.group_id\n"
                        + "WHERE groups_tb.user_id=? AND groups_tb.parent_id=?\n"
                        + "GROUP BY groups_tb.id;"),
                any(Object[].class),
                any(GroupsExtractor.class));

        ActivityGroupRepository activityRepository = new ActivityGroupRepositoryImpl(template);
        Optional<GroupResponseList> showedActivities = activityRepository.showGroupsByParentId(20L, 20L);

        verify(template).query(eq("SELECT groups_tb.id, groups_tb.parent_id, groups_tb.name, count(activities.id)\n"
                        + "FROM groups_tb\n"
                        + "LEFT JOIN activities ON groups_tb.id = activities.group_id\n"
                        + "WHERE groups_tb.user_id=? AND groups_tb.parent_id=?\n"
                        + "GROUP BY groups_tb.id;"),
                any(Object[].class),
                any((GroupsExtractor.class)));
        verifyNoMoreInteractions(template);

        assertEquals(1, showedActivities.get().size());
    }

    @DisplayName("ActivityGroup Repository - add group - everything OK")
    @Test
    public void addActivityGroup() {
        JdbcOperations template = mock(JdbcOperations.class);
        doReturn(1).when(template).update(any(PreparedStatementCreator.class), any(KeyHolder.class));

        KeyHolder keyHolder = mock(GeneratedKeyHolder.class);
        when(keyHolder.getKey()).thenReturn(5L);

        Optional<Long> parentId = Optional.of(1L);
        ActivityGroupRepositoryImpl activityGroupRepository = new ActivityGroupRepositoryImpl(template);
        Optional<Long> groupId = activityGroupRepository.add("CocaCola", 10L, parentId, keyHolder);

        verify(template).update(any(PreparedStatementCreator.class), any(KeyHolder.class));
        verifyNoMoreInteractions(template);

        assertEquals(Optional.of(5L), groupId);
    }

    @DisplayName("ActivityGroup Repository - add group - more affected rows, add error")
    @Test
    public void addActivityGroupMoreAffectedRows() {
        JdbcOperations template = mock(JdbcOperations.class);
        doReturn(3).when(template).update(any(PreparedStatementCreator.class), any(KeyHolder.class));

        Optional<Long> parentId = Optional.of(1L);
        ActivityGroupRepository activityRepository = new ActivityGroupRepositoryImpl(template);
        Optional<Long> activityId = activityRepository.add("CocaCola", 10L, parentId);

        verify(template).update(any(PreparedStatementCreator.class), any(KeyHolder.class));
        verifyNoMoreInteractions(template);

        assertEquals(Optional.empty(), activityId);
    }

    @DisplayName("ActivityGroup Repository - update - everything OK")
    @Test
    public void update() {
        JdbcOperations template = mock(JdbcOperations.class);
        when(template.update("UPDATE groups_tb SET name = ?, user_id = ? WHERE id = ?", "CocaCola", 10L, 20L)).thenReturn(1);

        ActivityGroupRepository activityGroupRepository = new ActivityGroupRepositoryImpl(template);
        boolean successful = activityGroupRepository.update("CocaCola", 10L, 20L);

        verify(template).update("UPDATE groups_tb SET name = ?, user_id = ? WHERE id = ?", "CocaCola", 10L, 20L);
        verifyNoMoreInteractions(template);

        assertTrue(successful);
    }

    @DisplayName("ActivityGroup Repository - update - more affected rows error")
    @Test
    public void updateMoreAffectedRows() {
        JdbcOperations template = mock(JdbcOperations.class);
        when(template.update("UPDATE groups_tb SET name = ?, user_id = ? WHERE id = ?", "CocaCola", 10L, 20L)).thenReturn(3);

        ActivityGroupRepository activityGroupRepository = new ActivityGroupRepositoryImpl(template);
        boolean successful = activityGroupRepository.update("CocaCola", 10L, 20L);

        verify(template).update("UPDATE groups_tb SET name = ?, user_id = ? WHERE id = ?", "CocaCola", 10L, 20L);
        verifyNoMoreInteractions(template);

        assertTrue(successful);
    }

    @DisplayName("ActivityGroup Repository - update - zero affected rows, update error")
    @Test
    public void updateZeroAffectedRows() {
        JdbcOperations template = mock(JdbcOperations.class);
        when(template.update("UPDATE groups_tb SET name = ?, user_id = ? WHERE id = ?", "CocaCola", 10L, 20L)).thenReturn(0);

        ActivityGroupRepository activityGroupRepository = new ActivityGroupRepositoryImpl(template);
        boolean successful = activityGroupRepository.update("CocaCola", 10L, 20L);

        verify(template).update("UPDATE groups_tb SET name = ?, user_id = ? WHERE id = ?", "CocaCola", 10L, 20L);
        verifyNoMoreInteractions(template);

        assertFalse(successful);
    }

    @DisplayName("ActivityGroup Repository - delete - everything OK")
    @Test
    public void delete() {
        JdbcOperations template = mock(JdbcOperations.class);
        when(template.update("DELETE FROM groups_tb WHERE id = ?", 20L)).thenReturn(1);

        ActivityGroupRepository activityGroupRepository = new ActivityGroupRepositoryImpl(template);
        boolean successful = activityGroupRepository.delete(20L);

        verify(template).update("DELETE FROM groups_tb WHERE id = ?", 20L);
        verifyNoMoreInteractions(template);

        assertTrue(successful);
    }

    @DisplayName("ActivityGroup Repository - delete - more affected rows error")
    @Test
    public void deleteMoreAffectedRows() {
        JdbcOperations template = mock(JdbcOperations.class);
        when(template.update("DELETE FROM groups_tb WHERE id = ?", 20L)).thenReturn(3);

        ActivityGroupRepository activityGroupRepository = new ActivityGroupRepositoryImpl(template);
        boolean successful = activityGroupRepository.delete(20L);

        verify(template).update("DELETE FROM groups_tb WHERE id = ?", 20L);
        verifyNoMoreInteractions(template);

        assertTrue(successful);
    }

    @DisplayName("ActivityGroup Repository - delete - zero affected rows, update error")
    @Test
    public void deleteZeroAffectedRows() {
        JdbcOperations template = mock(JdbcOperations.class);
        when(template.update("DELETE FROM groups_tb WHERE id = ?", 20L)).thenReturn(0);

        ActivityGroupRepository activityGroupRepository = new ActivityGroupRepositoryImpl(template);
        boolean successful = activityGroupRepository.delete(20L);

        verify(template).update("DELETE FROM groups_tb WHERE id = ?", 20L);
        verifyNoMoreInteractions(template);

        assertFalse(successful);
    }
}
