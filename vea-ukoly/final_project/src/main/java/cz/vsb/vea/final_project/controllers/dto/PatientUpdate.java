package cz.vsb.vea.final_project.controllers.dto;

import java.time.LocalDate;

public class PatientUpdate {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Long dentist;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Long getDentist() {
        return dentist;
    }

    public void setDentist(Long dentist) {
        this.dentist = dentist;
    }
}
