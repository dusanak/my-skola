package cz.vsb.vea.final_project.repositories;

import cz.vsb.vea.final_project.entities.Patient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepositoryInterface {
    public List<Patient> getAllPatient();
    public Patient save(Patient patient);
    public Patient find(long id);
}