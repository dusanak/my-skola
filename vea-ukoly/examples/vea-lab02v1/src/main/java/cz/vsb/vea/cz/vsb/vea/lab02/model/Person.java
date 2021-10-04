package cz.vsb.vea.cz.vsb.vea.lab02.model;

import java.time.LocalDate;
import java.time.Period;

public class Person {

	private String firstName;
	private String lastName;
	private LocalDate dayOfBirth;

	public Person() {
		System.out.println("person constructor");
	}

	public Person(String firstName, String lastName, LocalDate dayOfBirth) {
		System.out.println("person constructor with params");
		this.firstName = firstName;
		this.lastName = lastName;
		this.dayOfBirth = dayOfBirth;
	}

	public int getAge() {
		Period period = Period.between(dayOfBirth, LocalDate.now());
		return period.getYears();
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

}
