package cz.vsb.vea.cz.vsb.vea.lab01.repositories;

import cz.vsb.vea.cz.vsb.vea.lab01.model.Person;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonMapper implements RowMapper<Person> {
    @Override
    public Person mapRow(ResultSet resultSet, int i) throws SQLException {
        Person person = new Person(resultSet.getInt("id"),
                                   resultSet.getString("firstName"),
                                   resultSet.getString("lastName"),
                                   resultSet.getDate("dayOfBirth").toLocalDate());
        return person;
    }
}
