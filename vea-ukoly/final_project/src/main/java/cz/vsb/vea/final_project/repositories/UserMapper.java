package cz.vsb.vea.final_project.repositories;

import cz.vsb.vea.final_project.entities.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int index) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("name")
        );
    }

}
