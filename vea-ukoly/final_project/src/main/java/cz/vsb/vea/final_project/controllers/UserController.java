package cz.vsb.vea.final_project.controllers;

import cz.vsb.vea.final_project.entities.Person;
import cz.vsb.vea.final_project.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    PersonRepository personRepository;

    @GetMapping("/get")
    public Person getUser(long id) {
        return personRepository.getPerson(id);
    }

    @PutMapping("/add")
    public void addUser(Person person) {
        personRepository.save(person);
    }

    @GetMapping("/getAll")
    public List<Person> getAllUsers() {
        return personRepository.getAllPersons();
    }

}
