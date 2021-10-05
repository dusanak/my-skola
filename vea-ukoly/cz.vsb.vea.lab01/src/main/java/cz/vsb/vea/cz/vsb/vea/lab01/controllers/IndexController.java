package cz.vsb.vea.cz.vsb.vea.lab01.controllers;

import cz.vsb.vea.cz.vsb.vea.lab01.model.Person;
import cz.vsb.vea.cz.vsb.vea.lab01.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@Controller
public class IndexController {

    @Autowired
    private PersonService personService;

    public IndexController() {
        System.out.println("Index controller constructor");
    }

    @PostConstruct
    public void init() {
//        personService.addPerson(new Person("Alpha", "Alpha", LocalDate.of(2020, 9, 20)));
//        personService.addPerson(new Person("Beta", "Beta", LocalDate.of(2019, 9, 20)));
//        personService.addPerson(new Person("Gamma", "Gamma", LocalDate.of(2020, 9, 21)));
//        personService.addPerson(new Person("Delta", "Delta", LocalDate.of(2019, 9, 21)));
    }

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("message", "Click");
        return "index";
    }

    @RequestMapping("/getPersonList")
    public String personList(Model model) {
        model.addAttribute("persons", personService.getPersonList());
        return "personList";
    }

    @RequestMapping("/editPerson")
    public String personEdit(int index, Model model) {
        model.addAttribute("person", personService.getPersonList().get(index));
        return "personEdit";
    }

    @RequestMapping("/editPerson/{index}")
    public String personEdit2(@PathVariable int index, Model model) {
        model.addAttribute("person", personService.getPersonList().get(index));
        return "personEdit";
    }

    @ModelAttribute(name = "firstPerson")
    public Person getFirst() {
        if (!personService.getPersonList().isEmpty()) {
            return personService.getPersonList().get(0);
        }

        return null;
    }

    @RequestMapping("/save")
    public String save(@ModelAttribute @Validated Person person, BindingResult personError, Model model) {
        if(personError.hasErrors()) {
            model.addAttribute("person", person);
            return "personEdit";
        }
        System.out.println(person);
        model.addAttribute("persons", personService.getPersonList());
        return "personList";
    }
}
