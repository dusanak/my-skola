package cz.vsb.vea.cz.vsb.vea.lab02v2.controllers;

import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cz.vsb.vea.cz.vsb.vea.lab02v2.models.Person;
import cz.vsb.vea.cz.vsb.vea.lab02v2.services.PersonService;

@Controller
public class IndexController {

	@Autowired
	private PersonService personService;

	public IndexController() {
		System.out.println("index controler konstruktor");
//		System.out.println(ps);
	}

	@PostConstruct
	public void init() {

//		IndexController indexController = new IndexController();
//		indexController.init();
	}

	@RequestMapping("/")
	public String index(Model model) {
		System.out.println(personService.method1(10));
		//System.out.println(personService.method2(3));
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
	public String save(@ModelAttribute @Validated Person person, BindingResult personError, Model model) {
		if(personError.hasErrors()) {
			model.addAttribute("person", person);
			return "personEdit";
		}
		System.out.println(person);
		model.addAttribute("persons", personService.getPersons());
		return "personList";
	}
	
}
