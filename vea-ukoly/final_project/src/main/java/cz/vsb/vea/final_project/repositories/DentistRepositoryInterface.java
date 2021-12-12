package cz.vsb.vea.final_project.repositories;

import cz.vsb.vea.final_project.entities.Dentist;
import cz.vsb.vea.final_project.entities.Patient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DentistRepositoryInterface extends CrudRepository<Dentist, Long> {
    Optional<Dentist> findDentistById(Long id);
    Optional<Dentist> findDentistByPatientListContains(Patient patient);
}
