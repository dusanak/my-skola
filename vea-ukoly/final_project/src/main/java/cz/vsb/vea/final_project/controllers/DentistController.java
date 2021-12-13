package cz.vsb.vea.final_project.controllers;

import cz.vsb.vea.final_project.controllers.dto.DentistAdd;
import cz.vsb.vea.final_project.controllers.dto.DentistUpdate;
import cz.vsb.vea.final_project.entities.Dentist;
import cz.vsb.vea.final_project.services.DentistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dentist")
public class DentistController {
    @Autowired
    DentistService dentistService;

    @GetMapping(value = "/get", params = "id")
    public ResponseEntity<Dentist> getDentist(Long id) {
        Optional<Dentist> dentist = dentistService.findDentist(id);
        if (dentist.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.of(dentist);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Dentist>> getAllDentists() {
        return ResponseEntity.ok(dentistService.findAllDentists());
    }

    @GetMapping(value="/getDentistByPatient", params="patientId")
    public ResponseEntity<Dentist> getDentistByPatient(Long patientId) {
        Optional<Dentist> dentist = dentistService.findDentistByPatient(patientId);

        if (dentist.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dentist.get());
    }

    @PostMapping(value="/add")
    public ResponseEntity<Dentist> addDentist(@RequestBody DentistAdd dentistAdd) {
        Optional<Dentist> dentist = dentistService.addDentist(dentistAdd);

        if (dentist.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.of(dentist);
    }


    @PutMapping(value="/update", params = "dentistUpdate")
    public ResponseEntity<Dentist> updateDentist(@RequestBody DentistUpdate dentistUpdate) {
        Optional<Dentist> dentist = dentistService.updateDentist(dentistUpdate);

        if (dentist.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.of(dentist);
    }

    @DeleteMapping(value="/delete", params = "dentistId")
    public ResponseEntity<?> deleteDentist(Long dentistId) {
        if (!dentistService.deleteDentist(dentistId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }
}
