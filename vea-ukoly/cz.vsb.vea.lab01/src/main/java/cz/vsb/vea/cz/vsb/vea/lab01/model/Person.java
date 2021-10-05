package cz.vsb.vea.cz.vsb.vea.lab01.model;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Person {
    private int id;
    @NotEmpty(message = "Must not be empty")
    private String firstName;
    @NotEmpty
    private String lastName;
    //@NotInFuture(message = "Has not been born yet")
    private LocalDate dayOfBirth;

    public Person() {
        System.out.println("Person constructor w/o parameters");
    }

    public Person(int id, String firstName, String lastName, LocalDate dayOfBirth) {
        System.out.println("Person constructor w/ parameters");

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dayOfBirth = dayOfBirth;
    }

    public int getAge() {
        long diff = ChronoUnit.YEARS.between(dayOfBirth, LocalDate.now());
        return (int) diff;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDayOfBirth() {
        return dayOfBirth;
    }

    public void setDayOfBirth(LocalDate dayOfBirth) {
        this.dayOfBirth = dayOfBirth;
    }

    @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dayOfBirth=" + dayOfBirth +
                '}';
    }
}
