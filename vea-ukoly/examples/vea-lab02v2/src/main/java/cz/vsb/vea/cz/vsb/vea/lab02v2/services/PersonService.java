package cz.vsb.vea.cz.vsb.vea.lab02v2.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import cz.vsb.vea.cz.vsb.vea.lab02v2.models.Person;

@Service
public class PersonService {

	private List<Person> persons;

	
	public PersonService() {
		persons = new ArrayList<>();
	}
	
	public void addPerson(Person person){
		persons.add(person);
	}
	
	public List<Person> getPersons() {
		return persons;
	}
	
	
}
