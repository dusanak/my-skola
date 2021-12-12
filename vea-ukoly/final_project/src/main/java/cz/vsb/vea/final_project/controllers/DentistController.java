package cz.vsb.vea.final_project.controllers;

import cz.vsb.vea.final_project.repositories.DentistRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dentist")
public class DentistController {
    @Autowired
    DentistRepositoryInterface dentistRepository;


}
