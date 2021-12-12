package cz.vsb.vea.final_project.repositories;

import cz.vsb.vea.final_project.entities.Patient;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepositoryJPA extends PatientRepositoryInterface, CrudRepository<Patient, Long> {
    List<Patient> findAllPatients();
    Optional<Patient> findPatientById(long id);
}
