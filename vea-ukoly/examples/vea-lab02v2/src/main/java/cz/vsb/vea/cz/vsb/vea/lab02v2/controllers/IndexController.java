package cz.vsb.vea.cz.vsb.vea.lab02v2.controllers;

import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cz.vsb.vea.cz.vsb.vea.lab02v2.models.Person;
import cz.vsb.vea.cz.vsb.vea.lab02v2.services.PersonService;

@Controller
public class IndexController {

	@Autowired
	private PersonService personService;

	public IndexController(PersonService ps) {
		System.out.println("index controler konstruktor");
		System.out.println(ps);
	}

	@PostConstruct
	public void init() {
		personService.addPerson(new Person("aa", "bb", LocalDate.of(2020, 1, 1)));
		personService.addPerson(new Person("cc", "dd", LocalDate.of(2019, 1, 1)));
		personService.addPerson(new Person("ee", "ff", LocalDate.of(2020, 12, 12)));
		personService.addPerson(new Person("gg", "hh", LocalDate.of(2019, 12, 12)));

//		IndexController indexController = new IndexController();
//		indexController.init();
	}

	@RequestMapping("/")
	public String index(Model model) {
		System.out.println(personService);
		model.addAttribute("message", "Ahoj jak se mas?");
		return "index";
	}

	@RequestMapping("/list")
	public String personList(Model model) {
		model.addAttribute("persons", personService.getPersons());
		return "personList";
	}

	@ModelAttribute(name = "firstPerson")
	public Person getFirst() {
		if (personService.getPersons() != null && 
				!personService.getPersons().isEmpty()) {
			return personService.getPersons().get(0);
		}
		return null;
	}
	@RequestMapping("/edit")
	public String edit1(int index, Model model) {
		model.addAttribute("person", personService.getPersons().get(index));
		return "personEdit";
	}

	@RequestMapping("/edit/{index}")
	public String edit2(@PathVariable int index, Model model) {
		model.addAttribute("person", personService.getPersons().get(index));
		return "personEdit";
	}
	
	@RequestMapping("/save")
	public String save(@ModelAttribute Person person, Model model) {
		System.out.println(person);
		model.addAttribute("persons", personService.getPersons());
		return "personList";
	}
	
}
