package cz.vsb.vea.final_project.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "patient")
public class Patient extends Person {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dentist_id", nullable = false)
    Dentist dentist;

    public Patient() {
    }

    public Patient(Long id, String firstName, String lastName, LocalDate dateOfBirth, Dentist dentist) {
        super(id, firstName, lastName, dateOfBirth);
        this.dentist = dentist;
    }

    public Dentist getDentist() {
        return dentist;
    }

    public void setDentist(Dentist dentist) {
        this.dentist = dentist;
    }
}
