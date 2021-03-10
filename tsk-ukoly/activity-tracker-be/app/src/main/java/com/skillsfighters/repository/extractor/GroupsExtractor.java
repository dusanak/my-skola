package com.skillsfighters.repository.extractor;

import com.skillsfighters.controllers.responses.GroupResponseList;
import com.skillsfighters.controllers.responses.GroupsResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class GroupsExtractor implements ResultSetExtractor<Optional<GroupResponseList>> {
    @Override
    public Optional<GroupResponseList> extractData(final ResultSet rs) throws SQLException, DataAccessException {
        GroupResponseList groupsResponses = new GroupResponseList();
        while (rs.next()) {
            long columnId = rs.getLong("groups_tb.id");
            String name = rs.getString("groups_tb.name");
            Optional<Long> parentId = Optional.of(rs.getLong("groups_tb.parent_id"));
            long numberOfActivities = rs.getLong("count(activities.id)");
            groupsResponses.add(new GroupsResponse(columnId, name, numberOfActivities, parentId));
        }
        return Optional.of(groupsResponses);
    }
}
