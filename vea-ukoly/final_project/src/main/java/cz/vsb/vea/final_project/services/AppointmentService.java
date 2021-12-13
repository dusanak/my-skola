package cz.vsb.vea.final_project.services;

import cz.vsb.vea.final_project.controllers.dto.AppointmentAdd;
import cz.vsb.vea.final_project.entities.Appointment;
import cz.vsb.vea.final_project.entities.AppointmentKey;
import cz.vsb.vea.final_project.entities.Dentist;
import cz.vsb.vea.final_project.entities.Patient;
import cz.vsb.vea.final_project.repositories.AppointmentRepositoryInterface;
import cz.vsb.vea.final_project.repositories.DentistRepositoryInterface;
import cz.vsb.vea.final_project.repositories.PatientRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {
    @Autowired
    DentistRepositoryInterface dentistRepositoryInterface;

    @Autowired
    PatientRepositoryInterface patientRepositoryInterface;

    @Autowired
    AppointmentRepositoryInterface appointmentRepositoryInterface;

    @PostConstruct
    public void init() {
//		personRepository.save(new Person(0, "aa", "bb", LocalDate.of(2020, 1, 1)));
//		personRepository.save(new Person(0, "cc", "dd", LocalDate.of(2019, 1, 1)));
//		personRepository.save(new Person(0, "ee", "ff", LocalDate.of(2020, 12, 12)));
//		personRepository.save(new Person(0, "gg", "hh", LocalDate.of(2019, 12, 12)));
    }

    public Optional<List<Appointment>> findAllAppointmentsByPatientId(Long id) {
        Optional<Patient> patient = patientRepositoryInterface.findPatientById(id);
        if (patient.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(appointmentRepositoryInterface.findAllByPatient(patient.get()));
    }

    public Optional<List<Appointment>> findAllAppointmentsByDentistId(Long id) {
        Optional<Dentist> dentist = dentistRepositoryInterface.findDentistById(id);
        if (dentist.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(appointmentRepositoryInterface.findAllByDentist(dentist.get()));
    }

    public Optional<Appointment> addAppointment(AppointmentAdd appointmentAdd) {
        Optional<Patient> patient = patientRepositoryInterface.findPatientById(appointmentAdd.getPatientId());
        if (patient.isEmpty()) {
            return Optional.empty();
        }

        Optional<Dentist> dentist = dentistRepositoryInterface.findDentistById(appointmentAdd.getDentistId());
        if (dentist.isEmpty()) {
            return Optional.empty();
        }

        Appointment appointment = new Appointment();
        AppointmentKey appointmentKey = new AppointmentKey();
        appointmentKey.setDentistId(dentist.get().getId());
        appointmentKey.setPatientId(patient.get().getId());
        appointment.setId(appointmentKey);
        appointment.setDate(appointmentAdd.getDate());
        appointment.setDescription(appointmentAdd.getDescription());
        appointment.setPrice(appointmentAdd.getPrice());
        appointment.setDentist(dentist.get());
        appointment.setPatient(patient.get());

        Appointment result = appointmentRepositoryInterface.save(appointment);

        patient.get().getAppointmentList().add(result);
        patientRepositoryInterface.save(patient.get());

        dentist.get().getAppointmentList().add(result);
        dentistRepositoryInterface.save(dentist.get());

        return Optional.of(result);
    }

    public boolean deleteAppointment(Long id) {
        Optional<Appointment> appointmentOptional = appointmentRepositoryInterface.findById(id);
        if (appointmentOptional.isEmpty()) {
            return false;
        }

        appointmentRepositoryInterface.delete(appointmentOptional.get());

        return true;
    }
}