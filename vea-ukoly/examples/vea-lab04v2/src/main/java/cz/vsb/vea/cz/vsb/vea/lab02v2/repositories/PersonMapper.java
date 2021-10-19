package cz.vsb.vea.cz.vsb.vea.lab02v2.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import cz.vsb.vea.cz.vsb.vea.lab02v2.models.Person;

public class PersonMapper implements RowMapper<Person> {

	@Override
	public Person mapRow(ResultSet rs, int index) throws SQLException {
		return new Person(
				rs.getInt("id"),
				rs.getString("firstName"),
				rs.getString("lastName"),
				rs.getDate("dayOfBirth").toLocalDate());
	}

}
