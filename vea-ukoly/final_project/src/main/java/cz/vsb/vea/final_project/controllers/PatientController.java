package cz.vsb.vea.final_project.controllers;

import cz.vsb.vea.final_project.repositories.PatientRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;

public class PatientController {
    @Autowired
    PatientRepositoryInterface patientRepositoryInterface;


}
