package cz.vsb.vea.final_project.repositories;

import cz.vsb.vea.final_project.entities.User;
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
public class UserRepository {
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert userInsert;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        userInsert = new SimpleJdbcInsert(dataSource).withTableName("User").usingGeneratedKeyColumns("id")
                .usingColumns("name");
    }

    @PostConstruct
    public void init() {
        try (Statement stm = jdbcTemplate.getDataSource().getConnection().createStatement()) {
            stm.executeUpdate("CREATE TABLE User (" + "id INT NOT NULL auto_increment," + " name varchar(255), " + " PRIMARY KEY (id)" + ");");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUser(long id) {
        return jdbcTemplate.queryForObject("select * from User where id = ?", new UserMapper(), id);
    }

    public List<User> getAllUsers() {
        return jdbcTemplate.query("select * from User", new UserMapper());
    }

    public void save(User user) {
        if (user.getId() == 0) {
            userInsert.execute(new BeanPropertySqlParameterSource(user));
        } else {
            // update
        }
    }
}
