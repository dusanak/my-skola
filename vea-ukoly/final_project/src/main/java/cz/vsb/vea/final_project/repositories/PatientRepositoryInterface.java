package cz.vsb.vea.final_project.repositories;

import cz.vsb.vea.final_project.entities.Patient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepositoryInterface {
    List<Patient> findAllPatients();
    List<Patient> findAllPatientsByDentist(long id);
    Patient save(Patient patient);
    Optional<Patient> findPatientById(long id);
    void delete(Patient patient);
}