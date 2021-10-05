package cz.vsb.vea.cz.vsb.vea.lab02v2.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import cz.vsb.vea.cz.vsb.vea.lab02v2.models.Person;

@Service
@RequestScope
public class PersonService {

	private static int count = 0;
	
	private List<Person> persons;
	private String name;
	
	public PersonService() {
		name = "PS " + count++;
		persons = new ArrayList<>();
		persons.add(new Person("aa", "bb", LocalDate.of(2020, 1, 1)));
		persons.add(new Person("cc", "dd", LocalDate.of(2019, 1, 1)));
		persons.add(new Person("ee", "ff", LocalDate.of(2020, 12, 12)));
		persons.add(new Person("gg", "hh", LocalDate.of(2019, 12, 12)));
	}
	
	public void addPerson(Person person){
		persons.add(person);
	}
	
	public List<Person> getPersons() {
		System.out.println();
		return persons;
	}
	
	
}
