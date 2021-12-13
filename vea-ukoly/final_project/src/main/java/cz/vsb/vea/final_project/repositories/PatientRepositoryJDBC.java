package cz.vsb.vea.final_project.repositories;

import cz.vsb.vea.final_project.entities.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(
        value="accessType",
        havingValue = "jdbc",
        matchIfMissing = true)
public class PatientRepositoryJDBC implements PatientRepositoryInterface{
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert patientInsert;
    private SimpleJdbcCall patientUpdate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        patientInsert = new SimpleJdbcInsert(dataSource).withTableName("patient").usingGeneratedKeyColumns("person_id")
                .usingColumns("first_name", "last_name", "date_of_birth", "dentist_id");
    }

    @Override
    public List<Patient> findAll() {
        return jdbcTemplate.query("select * from patient", new PatientMapper());
    }

    @Override
    public List<Patient> findAllPatientsByDentist(long id) {
        return jdbcTemplate.query("select * from patient where dentist_id == " + id, new PatientMapper());
    }

    @Override
    public Patient save(Patient patient) {
        Long result;
        if (patient.getId() == 0) {
            result = (Long) patientInsert.executeAndReturnKey(new BeanPropertySqlParameterSource(patient));
        } else {
            jdbcTemplate.execute("update patient set" +
                                    " first_name =" + patient.getFirstName() +
                                    ",last_name =" + patient.getLastName() +
                                    ",date_of_birth =" + patient.getDateOfBirth() +
                                    ",dentist_id =" + patient.getDentist().getId() +
                                    " where person_id == " + patient.getId());
            result = patient.getId();
        }

        return findPatientById(result).get();
    }

    @Override
    public Optional<Patient> findPatientById(long id) {
        List<Patient> patients = jdbcTemplate.query("select * from patient where person_id == " + id, new PatientMapper());

        if (patients.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(patients.get(0));
    }

    @Override
    public void delete(Patient patient) {
        jdbcTemplate.execute("delete from patient where person_id == " + patient.getId());
    }
}
