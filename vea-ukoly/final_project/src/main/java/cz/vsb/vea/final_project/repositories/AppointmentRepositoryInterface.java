package cz.vsb.vea.final_project.repositories;


import cz.vsb.vea.final_project.entities.Appointment;
import cz.vsb.vea.final_project.entities.Dentist;
import cz.vsb.vea.final_project.entities.Patient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepositoryInterface extends CrudRepository<Appointment, Long> {
    List<Appointment> findAllByDentist(Dentist dentist);
    List<Appointment> findAllByPatient(Patient patient);
}
