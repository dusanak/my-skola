package cz.vsb.vea.final_project.services;

import cz.vsb.vea.final_project.controllers.dto.PatientAdd;
import cz.vsb.vea.final_project.controllers.dto.PatientUpdate;
import cz.vsb.vea.final_project.entities.Dentist;
import cz.vsb.vea.final_project.entities.Patient;
import cz.vsb.vea.final_project.repositories.DentistRepositoryInterface;
import cz.vsb.vea.final_project.repositories.PatientRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    DentistRepositoryInterface dentistRepositoryInterface;

    @Autowired
    PatientRepositoryInterface patientRepositoryInterface;

    public PatientService() {}

    @PostConstruct
    public void init() {
//		personRepository.save(new Person(0, "aa", "bb", LocalDate.of(2020, 1, 1)));
//		personRepository.save(new Person(0, "cc", "dd", LocalDate.of(2019, 1, 1)));
//		personRepository.save(new Person(0, "ee", "ff", LocalDate.of(2020, 12, 12)));
//		personRepository.save(new Person(0, "gg", "hh", LocalDate.of(2019, 12, 12)));
    }

    public Optional<Patient> findPatient(Long id) {
        return patientRepositoryInterface.findPatientById(id);
    }

    public List<Patient> findAllPatients() {
        return patientRepositoryInterface.findAllPatients();
    }

    public Optional<List<Patient>> findAllPatientsByDentist(Long id) {
        Optional<Dentist> dentist = dentistRepositoryInterface.findDentistById(id);
        if (dentist.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(patientRepositoryInterface.findAllPatientsByDentist(id));
    }

    public Optional<Patient> addPatient(PatientAdd patientAdd){
        Optional<Dentist> dentist = dentistRepositoryInterface.findDentistById(patientAdd.getDentistId());
        if (dentist.isEmpty()) {
            return Optional.empty();
        }

        Patient patient = new Patient();
        patient.setId(0L);
        patient.setFirstName(patientAdd.getFirstName());
        patient.setLastName(patientAdd.getLastName());
        patient.setDateOfBirth(patientAdd.getDateOfBirth());
        patient.setDentist(dentist.get());

        Patient result = patientRepositoryInterface.save(patient);

        dentist.get().getPatientList().add(result);
        dentistRepositoryInterface.save(dentist.get());

        return Optional.of(patient);
    }

    public Optional<Patient> updatePatient(PatientUpdate patientUpdate) {
        Optional<Patient> patientOptional = patientRepositoryInterface.findPatientById(patientUpdate.getId());
        if (patientOptional.isEmpty()) {
            return Optional.empty();
        }
        Patient patient = patientOptional.get();

        Optional<Dentist> dentistOptional = dentistRepositoryInterface.findDentistById(patientUpdate.getDentist());
        if (dentistOptional.isEmpty()) {
            return Optional.empty();
        }
        Dentist dentist = dentistOptional.get();

        patient.setFirstName(patientUpdate.getFirstName());
        patient.setLastName(patientUpdate.getLastName());
        patient.setDateOfBirth(patientUpdate.getDateOfBirth());

        Dentist originalDentist = patient.getDentist();
        if (!originalDentist.getId().equals(dentist.getId())) {
            originalDentist.getPatientList().remove(patient);
            dentistRepositoryInterface.save(originalDentist);

            patient.setDentist(dentist);
        }

        Patient result = patientRepositoryInterface.save(patient);

        if (!originalDentist.getId().equals(dentist.getId())) {
            dentist.getPatientList().add(patient);
            dentistRepositoryInterface.save(dentist);
        }

        return Optional.of(patient);
    }

    public boolean deletePatient(Long id) {
        Optional<Patient> patientOptional = patientRepositoryInterface.findPatientById(id);
        if (patientOptional.isEmpty()) {
            return false;
        }

        patientRepositoryInterface.delete(patientOptional.get());

        return true;
    }
}
