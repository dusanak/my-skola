package org.example;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @InjectMocks
    private PersonService personService;

    @Mock
    private PersonRepository repository;

    @Test
    void testGetPersonBornInSaturday() {
        Mockito.when(repository.getAll()).thenReturn(
                Arrays.asList(
                        new Person(0, "a", "b", LocalDate.of(2021, 2, 13)),
                        new Person(1, "a", "b", LocalDate.of(2021, 2, 12))
                )
        );

        List<Person> result = personService.getPersonBornInSaturday();
        Mockito.verify(repository, Mockito.times(1)).getAll();
        assertFalse(result.isEmpty());
    }
}
