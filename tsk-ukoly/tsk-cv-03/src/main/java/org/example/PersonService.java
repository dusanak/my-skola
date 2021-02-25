package org.example;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class PersonService {
    /**
    @Autowire
    @Inject
     injected by framework
    **/
    private PersonRepository personRepository;

    public List<Person> getPersonBornInSaturday() {
        List<Person> result = new ArrayList<>();
        for (Person person: personRepository.getAll()) {
            // FIXME
            if (person.getBirthday().getDayOfWeek() == DayOfWeek.SATURDAY) {
                result.add(person);
            }
        }
        return result;
    }
}
