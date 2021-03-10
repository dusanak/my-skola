package com.skillsfighters.repository.extractor;

import com.skillsfighters.domain.ActivityGroup;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;

public class ActivityGroupExtractor implements ResultSetExtractor<Optional<ActivityGroup>> {
    @Override
    public Optional<ActivityGroup> extractData(final ResultSet rs) throws SQLException, DataAccessException {
        if (rs.next()) {
            long columnId = rs.getLong("id");
            String name = rs.getString("name");
            long userId = rs.getLong("user_id");
            Optional<Long> parentId = Optional.of(rs.getLong("parent_id"));
            Date createdAt = rs.getTimestamp("created_at");
            Date updatedAt = rs.getTimestamp("updated_at");
            return Optional.of(ActivityGroup.builder()
                    .id(columnId)
                    .name(name)
                    .userId(userId)
                    .parentId(parentId)
                    .createdAt(createdAt.getTime())
                    .updatedAt(updatedAt.getTime())
                    .build());
        } else {
            return Optional.empty();
        }
    }
}
