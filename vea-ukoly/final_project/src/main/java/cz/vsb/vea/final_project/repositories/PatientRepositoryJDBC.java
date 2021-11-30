package cz.vsb.vea.final_project.repositories;

import cz.vsb.vea.final_project.entities.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
@ConditionalOnProperty(
        value="accessType",
        havingValue = "jdbc",
        matchIfMissing = true)
public class PatientRepositoryJDBC implements PatientRepositoryInterface{
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert patientInsert;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        patientInsert = new SimpleJdbcInsert(dataSource).withTableName("patient").usingGeneratedKeyColumns("person_id")
                .usingColumns("first_name", "last_name", "day_of_birth", "dentist_id");
    }

    @Override
    public List<Patient> getAllPatient() {
        return jdbcTemplate.query("select * from patient", new PatientMapper());
    }

    @Override
    public Patient save(Patient patient) {
        if (patient.getId() == 0) {
            patientInsert.execute(new BeanPropertySqlParameterSource(patient));
        } else {
        }
        return null;
    }

    @Override
    public Patient find(long id) {
        return null;
    }
}
