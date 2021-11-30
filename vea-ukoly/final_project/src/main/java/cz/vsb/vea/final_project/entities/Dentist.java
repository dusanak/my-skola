package cz.vsb.vea.final_project.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "dentist")
public class Dentist extends Person {
    @OneToMany(mappedBy = "dentist", cascade = CascadeType.ALL)
    private List<Patient> patientList;

    public Dentist() {
    }

    public Dentist(Long id, String firstName, String lastName, LocalDate dateOfBirth, List<Patient> patientList) {
        super(id, firstName, lastName, dateOfBirth);
        this.patientList = patientList;
    }

    public List<Patient> getPatientList() {
        return patientList;
    }

    public void setPatientList(List<Patient> patientList) {
        this.patientList = patientList;
    }
}
