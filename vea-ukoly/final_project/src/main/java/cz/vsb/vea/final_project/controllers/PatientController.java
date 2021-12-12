package cz.vsb.vea.final_project.controllers;

import cz.vsb.vea.final_project.controllers.dto.PatientAdd;
import cz.vsb.vea.final_project.controllers.dto.PatientUpdate;
import cz.vsb.vea.final_project.entities.Dentist;
import cz.vsb.vea.final_project.entities.Patient;
import cz.vsb.vea.final_project.repositories.DentistRepositoryInterface;
import cz.vsb.vea.final_project.repositories.PatientRepositoryInterface;
import cz.vsb.vea.final_project.services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    PatientService patientService;

    @GetMapping("/get")
    public ResponseEntity<Patient> getPatient(Long id) {
        Optional<Patient> patient = patientService.findPatient(id);
        if (patient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.of(patient);
    }

    @GetMapping("/getAll")
    public List<Patient> getAllPatients() {
        return patientService.findAllPatients();
    }

    @GetMapping(value="/getAllFromDentist", params="dentistId")
    public ResponseEntity<List<Patient>> getAllPatientsFromDentist(Long dentistId) {
        Optional<List<Patient>> patients = patientService.findAllPatientsByDentist(dentistId);

        if (patients.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(patients.get());
    }

    @PutMapping(value="/add")
    public ResponseEntity<Patient> addPatient(PatientAdd patientAdd) {
        Optional<Patient> patient = patientService.addPatient(patientAdd);

        if (patient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.of(patient);
    }


    @PostMapping(value="/update")
    public ResponseEntity<Patient> updatePatient(PatientUpdate patientUpdate) {
        Optional<Patient> patient = patientService.updatePatient(patientUpdate);

        if (patient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.of(patient);
    }

    @DeleteMapping(value="/delete", params = "patientId")
    public ResponseEntity<?> deletePatient(Long patientId) {
        if (!patientService.deletePatient(patientId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }
}
