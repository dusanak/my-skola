package cz.vsb.vea.cz.vsb.vea.lab02.controllers;

import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cz.vsb.vea.cz.vsb.vea.lab02.model.Person;
import cz.vsb.vea.cz.vsb.vea.lab02.services.PersonService;

@Controller
public class IndexController {

	@Autowired
	private PersonService personService;

	public IndexController() {
		System.out.println("index controler constructor");
	}
	
	@PostConstruct
	public void init() {
		personService.addPerson(new Person("aaa", "bbb", LocalDate.of(2020, 1, 1)));
		personService.addPerson(new Person("cc", "ddd", LocalDate.of(2019, 1, 1)));
		personService.addPerson(new Person("ee", "ff", LocalDate.of(2020, 12, 12)));
		personService.addPerson(new Person("ggg", "hhh", LocalDate.of(2019, 12, 12)));
		
//		IndexController indexController = new IndexController();
//		indexController.init();
		
	}
	
	@RequestMapping("/")
	public String index(Model model) {
		model.addAttribute("message", "Jak se mas?");
		return "index";
	}

	@RequestMapping("/list")
	public String personList(Model model) {
		model.addAttribute("persons", personService.getPersons());
		return "personList";
	}
	@RequestMapping("/edit")
	public String editPerson(int index, Model model) {
		model.addAttribute("person", personService.getPersons().get(index));
		return "personEdit";
	}
	@RequestMapping("/edit/{index}")
	public String personList(@PathVariable int index, Model model) {
		model.addAttribute("person", personService.getPersons().get(index));
		return "personEdit";
	}

	@RequestMapping("/save")
	public String personList(@ModelAttribute Person person, Model model) {
		
		model.addAttribute("persons", personService.getPersons());
		return "personList";
	}

	@ModelAttribute(name = "firstPerson")
	public Person getFirst() {
		if (personService.getPersons() != null && !personService.getPersons().isEmpty()) {
			return personService.getPersons().get(0);
		}
		return null;
	}

}
