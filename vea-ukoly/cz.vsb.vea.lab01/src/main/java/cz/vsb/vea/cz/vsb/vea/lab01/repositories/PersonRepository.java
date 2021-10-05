package cz.vsb.vea.cz.vsb.vea.lab01.repositories;

import cz.vsb.vea.cz.vsb.vea.lab01.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class PersonRepository {
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert personInsert;

    @Autowired
    public void setDatasource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @PostConstruct
    public void init() {
        try {
            Statement stm = jdbcTemplate.getDataSource().getConnection().createStatement();
            stm.executeUpdate("CREATE TABLE Person ( " +
                    "id INT NOT NULL " +
                    "firstName VARCHAR(255) " +
                    "lastName VARCHAR(255) " +
                    "dayOfBirth DATE " +
                    "PRIMARY KEY (id);");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Person> getPersonList() {
        return jdbcTemplate.query(
                "select * from Person", new PersonMapper()
        );
    }

    public Person save(Person person) {
        //TODO
        return null;
    }
}
