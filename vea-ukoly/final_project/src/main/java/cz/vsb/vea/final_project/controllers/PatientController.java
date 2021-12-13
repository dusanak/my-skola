package cz.vsb.vea.final_project.controllers;

import cz.vsb.vea.final_project.controllers.dto.PatientAdd;
import cz.vsb.vea.final_project.controllers.dto.PatientUpdate;
import cz.vsb.vea.final_project.entities.Patient;
import cz.vsb.vea.final_project.services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    PatientService patientService;

    @GetMapping(value = "/get", params = "id")
    public ResponseEntity<Patient> getPatient(Long id) {
        Optional<Patient> patient = patientService.findPatient(id);
        if (patient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.of(patient);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.findAllPatients());
    }

    @GetMapping(value="/getAllFromDentist", params="dentistId")
    public ResponseEntity<List<Patient>> getAllPatientsFromDentist(Long dentistId) {
        Optional<List<Patient>> patients = patientService.findAllPatientsByDentist(dentistId);

        if (patients.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(patients.get());
    }

    @PostMapping(value="/add")
    public ResponseEntity<Patient> addPatient(@RequestBody PatientAdd patientAdd) {
        Optional<Patient> patient = patientService.addPatient(patientAdd);

        if (patient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.of(patient);
    }


    @PutMapping(value="/update")
    public ResponseEntity<Patient> updatePatient(@RequestBody PatientUpdate patientUpdate) {
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
