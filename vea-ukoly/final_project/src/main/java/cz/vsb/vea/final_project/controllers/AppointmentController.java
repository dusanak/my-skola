package cz.vsb.vea.final_project.controllers;

import cz.vsb.vea.final_project.controllers.dto.AppointmentAdd;
import cz.vsb.vea.final_project.controllers.dto.DentistAdd;
import cz.vsb.vea.final_project.controllers.dto.DentistUpdate;
import cz.vsb.vea.final_project.entities.Appointment;
import cz.vsb.vea.final_project.entities.Dentist;
import cz.vsb.vea.final_project.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    AppointmentService appointmentService;

    @GetMapping(value="/getAppointmentsByPatient", params="patientId")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatient(Long patientId) {
        Optional<List<Appointment>> appointments = appointmentService.findAllAppointmentsByPatientId(patientId);

        if (appointments.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(appointments.get());
    }

    @GetMapping(value="/getAppointmentsByDentist", params="dentistId")
    public ResponseEntity<List<Appointment>> getAppointmentsByDentist(Long dentistId) {
        Optional<List<Appointment>> appointments = appointmentService.findAllAppointmentsByDentistId(dentistId);

        if (appointments.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(appointments.get());
    }

    @PostMapping(value="/add")
    public ResponseEntity<Appointment> addAppointment(@RequestBody AppointmentAdd appointmentAdd) {
        Optional<Appointment> appointment = appointmentService.addAppointment(appointmentAdd);

        if (appointment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.of(appointment);
    }

    @DeleteMapping(value="/delete", params = "appointmentId")
    public ResponseEntity<?> deleteAppointment(Long appointmentId) {
        if (!appointmentService.deleteAppointment(appointmentId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }
}


