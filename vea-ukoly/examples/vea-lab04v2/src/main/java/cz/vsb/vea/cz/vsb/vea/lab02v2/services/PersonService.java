package cz.vsb.vea.cz.vsb.vea.lab02v2.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import cz.vsb.vea.cz.vsb.vea.lab02v2.models.Person;
import cz.vsb.vea.cz.vsb.vea.lab02v2.repositories.PersonRepository;

@Service
//@RequestScope
public class PersonService {

	private static int count = 0;
	
	@Autowired
	private PersonService personService;
	
	private String name;
	
	@Autowired
	private PersonRepository personRepository;
	
	public PersonService() {
		name = "PS " + count++;
	}
	
	@PostConstruct
	public void init() {
		personRepository.save(new Person(0, "aa", "bb", LocalDate.of(2020, 1, 1)));
		personRepository.save(new Person(0, "cc", "dd", LocalDate.of(2019, 1, 1)));
		personRepository.save(new Person(0, "ee", "ff", LocalDate.of(2020, 12, 12)));
		personRepository.save(new Person(0, "gg", "hh", LocalDate.of(2019, 12, 12)));
	}
	
	
	public void addPerson(Person person){
		personRepository.save(person);
	}
	
	public List<Person> getPersons() {
		return personRepository.getAllPersons();
	}
	
	public String method1(int a) {
		return personService.method2(a+1000);
	}

	public String method2(int a) {
		return Integer.toString(-a);
	}
}
