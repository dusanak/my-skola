package cz.vsb.vea.final_project.repositories;

import cz.vsb.vea.final_project.entities.Patient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(
        value="accessType",
        havingValue = "jpa",
        matchIfMissing = true)
public interface PatientRepositoryJPA extends PatientRepositoryInterface, CrudRepository<Patient, Long> {
    List<Patient> findAll();
    Optional<Patient> findPatientById(long id);
}
