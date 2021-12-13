package cz.vsb.vea.final_project.repositories;

import cz.vsb.vea.final_project.entities.Appointment;
import cz.vsb.vea.final_project.entities.Dentist;
import cz.vsb.vea.final_project.entities.Patient;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientMapper implements RowMapper<Patient> {

    @Override
    public Patient mapRow(ResultSet rs, int index) throws SQLException {
        Patient patient = new Patient();
        patient.setId(rs.getLong("person_id"));
        patient.setFirstName(rs.getString("first_name"));
        patient.setLastName(rs.getString("last_name"));
        patient.setDateOfBirth(rs.getDate("day_of_birth").toLocalDate());
        patient.setDentist(rs.getObject("dentist", Dentist.class));
        patient.setAppointmentList((List<Appointment>) rs.getObject("appointmentList", ArrayList.class));
        return patient;
    }

}
