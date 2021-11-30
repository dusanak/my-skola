package cz.vsb.vea.final_project.repositories;

import cz.vsb.vea.final_project.entities.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class AccountRepository {
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert personInsert;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        personInsert = new SimpleJdbcInsert(dataSource).withTableName("Person").usingGeneratedKeyColumns("id")
                .usingColumns("name");
    }

    @PostConstruct
    public void init() {
        try (Statement stm = jdbcTemplate.getDataSource().getConnection().createStatement()) {
            stm.execute("CREATE TABLE IF NOT EXISTS Person("
                    + "id SERIAL PRIMARY KEY,"
                    + "name varchar(255) NOT NULL"
                    + ");");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Person getPerson(long id) {
        return jdbcTemplate.queryForObject("select * from Person where id = ?", new PatientMapper(), id);
    }

    public List<Person> getAllPersons() {
        return null;
    }

    public void save(Person person) {
        if (person.getId() == 0) {
            personInsert.execute(new BeanPropertySqlParameterSource(person));
        } else {
            // update
        }
    }
}
