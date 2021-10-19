package cz.vsb.vea.cz.vsb.vea.lab02v2.repositories;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import cz.vsb.vea.cz.vsb.vea.lab02v2.models.Person;

@Repository
public class PersonRepository {

	private JdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert personInsert;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		personInsert = new SimpleJdbcInsert(dataSource).withTableName("Person").usingGeneratedKeyColumns("id")
				.usingColumns("firstName", "lastName", "dayOfBirth");
	}

	@PostConstruct
	public void init() {
		try (Statement stm = jdbcTemplate.getDataSource().getConnection().createStatement()) {
			stm.executeUpdate("CREATE TABLE Person (" + "id INT NOT NULL auto_increment," + " firstName varchar(255), "
					+ "lastName varchar(255), " + "dayOfBirth DATE, " + " PRIMARY KEY (id)" + ");");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Person> getAllPersons() {
		return jdbcTemplate.query("select * from Person", new PersonMapper());
	}

	public void save(Person person) {
		if (person.getId() == 0) {
			personInsert.execute(new BeanPropertySqlParameterSource(person));
		} else {
			// update
		}
	}
}