package cz.vsb.vea.final_project.services;

import cz.vsb.vea.final_project.controllers.dto.DentistAdd;
import cz.vsb.vea.final_project.controllers.dto.DentistUpdate;
import cz.vsb.vea.final_project.controllers.dto.PatientAdd;
import cz.vsb.vea.final_project.controllers.dto.PatientUpdate;
import cz.vsb.vea.final_project.entities.Dentist;
import cz.vsb.vea.final_project.entities.Patient;
import cz.vsb.vea.final_project.repositories.DentistRepositoryInterface;
import cz.vsb.vea.final_project.repositories.PatientRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DentistService {

    @Autowired
    DentistRepositoryInterface dentistRepositoryInterface;

    @Autowired
    PatientRepositoryInterface patientRepositoryInterface;

    public DentistService() {}

    @PostConstruct
    public void init() {
//		personRepository.save(new Person(0, "aa", "bb", LocalDate.of(2020, 1, 1)));
//		personRepository.save(new Person(0, "cc", "dd", LocalDate.of(2019, 1, 1)));
//		personRepository.save(new Person(0, "ee", "ff", LocalDate.of(2020, 12, 12)));
//		personRepository.save(new Person(0, "gg", "hh", LocalDate.of(2019, 12, 12)));
    }

    public Optional<Dentist> findDentist(Long id) {
        return dentistRepositoryInterface.findDentistById(id);
    }

    public List<Dentist> findAllDentists() {
        return dentistRepositoryInterface.findAll();
    }

    public Optional<Dentist> findDentistByPatient(Long id) {
        Optional<Patient> patient = patientRepositoryInterface.findPatientById(id);
        if (patient.isEmpty()) {
            return Optional.empty();
        }

        return dentistRepositoryInterface.findDentistByPatientListContains(patient.get());
    }

    public Optional<Dentist> addDentist(DentistAdd dentistAdd){
        Dentist dentist = new Dentist();
        dentist.setId(0L);
        dentist.setFirstName(dentistAdd.getFirstName());
        dentist.setLastName(dentistAdd.getLastName());
        dentist.setDateOfBirth(dentistAdd.getDateOfBirth());
        dentist.setPatientList(new ArrayList<>());
        dentist.setAppointmentList(new ArrayList<>());

        Dentist result = dentistRepositoryInterface.save(dentist);

        return Optional.of(result);
    }

    public Optional<Dentist> updateDentist(DentistUpdate dentistUpdate) {
        Optional<Dentist> dentistOptional = dentistRepositoryInterface.findDentistById(dentistUpdate.getId());
        if (dentistOptional.isEmpty()) {
            return Optional.empty();
        }
        Dentist dentist = dentistOptional.get();

        dentist.setFirstName(dentistUpdate.getFirstName());
        dentist.setLastName(dentistUpdate.getLastName());
        dentist.setDateOfBirth(dentistUpdate.getDateOfBirth());

        Dentist result = dentistRepositoryInterface.save(dentist);

        return Optional.of(dentist);
    }

    public boolean deleteDentist(Long id) {
        Optional<Dentist> dentistOptional = dentistRepositoryInterface.findDentistById(id);
        if (dentistOptional.isEmpty()) {
            return false;
        }

        dentistRepositoryInterface.delete(dentistOptional.get());

        return true;
    }
}
