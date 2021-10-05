package cz.vsb.vea.cz.vsb.vea.lab02v2.models;

import java.time.LocalDate;
import java.time.Period;

import javax.validation.constraints.NotEmpty;

import cz.vsb.vea.cz.vsb.vea.lab02v2.NotInFuture;

public class Person {

	@NotEmpty(message = "nesmi byt prazdne")
	private String firstName;
	@NotEmpty
	private String lastName;
	@NotInFuture(message = "aaaaa")
	private LocalDate dayOfBirth;

	public Person() {
		System.out.println("person constroctor");
	}

	public Person(String firstName, String lastName, LocalDate dayOfBirth) {
		System.out.println("person constroctor with parameters");
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

	@Override
	public String toString() {
		return "Person [firstName=" + firstName + ", lastName=" + lastName + ", dayOfBirth=" + dayOfBirth + "]";
	}
	
}
