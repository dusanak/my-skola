package cz.vsb.vea.final_project.repositories;

import cz.vsb.vea.final_project.entities.Person;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonMapper implements RowMapper<Person> {

    @Override
    public Person mapRow(ResultSet rs, int index) throws SQLException {
        return new Person(
                rs.getLong("id"),
                rs.getString("name")
        );
    }

}
