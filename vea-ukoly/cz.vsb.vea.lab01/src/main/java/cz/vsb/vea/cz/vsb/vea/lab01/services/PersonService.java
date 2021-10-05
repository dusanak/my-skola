package cz.vsb.vea.cz.vsb.vea.lab01.services;

import cz.vsb.vea.cz.vsb.vea.lab01.model.Person;
import cz.vsb.vea.cz.vsb.vea.lab01.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PersonService {

    @Autowired
    PersonRepository personRepository;

    private static int count = 0;

    private List<Person> personList;
    private String name;

    public PersonService() {
        name = "PS " + count++;
    }

    @PostConstruct
    public void init() {
        addPerson(new Person(1, "Alpha", "Alpha", LocalDate.of(2020, 9, 20)));
        addPerson(new Person(2, "Beta", "Beta", LocalDate.of(2019, 9, 20)));
        addPerson(new Person(3, "Gamma", "Gamma", LocalDate.of(2020, 9, 21)));
        addPerson(new Person(4, "Delta", "Delta", LocalDate.of(2019, 9, 21)));
    }

    public void addPerson(Person person) {
        personRepository.save(person);
    }

    public List<Person> getPersonList() {
        return personRepository.getPersonList();
    }
}
