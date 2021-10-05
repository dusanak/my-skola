package cz.vsb.vea.cz.vsb.vea.lab02.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import cz.vsb.vea.cz.vsb.vea.lab02.model.Person;

@Service
public class PersonService {
	private static int count = 0;

	private List<Person> persons;

	private String name;

	public PersonService() {
		name = "PS " + count++;
		persons = new ArrayList<>();
		persons.add(new Person("aaa", "bbb", LocalDate.of(2020, 1, 1)));
		persons.add(new Person("cc", "ddd", LocalDate.of(2019, 1, 1)));
		persons.add(new Person("ee", "ff", LocalDate.of(2020, 12, 12)));
		persons.add(new Person("ggg", "hhh", LocalDate.of(2019, 12, 12)));
		System.out.println(name);
	}

	public void addPerson(Person person) {
		persons.add(person);
	}

	@Transactional
	public List<Person> getPersons() {
		System.out.println(name);
		return persons;
	}

}
