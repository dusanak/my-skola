package cz.vsb.vea.cz.vsb.vea.lab01.services;

import cz.vsb.vea.cz.vsb.vea.lab01.Person;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PersonService {
    private List<Person> personList;

    public PersonService() {
        personList = new ArrayList<>();
    }

    public void addPerson(Person person) {
        personList.add(person);
    }

    public List<Person> getPersonList() {
        return personList;
    }
}
