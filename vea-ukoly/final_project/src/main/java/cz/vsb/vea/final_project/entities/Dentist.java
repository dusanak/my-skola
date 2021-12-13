package cz.vsb.vea.final_project.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "dentist")
public class Dentist extends Person {
    @JsonManagedReference
    @OneToMany(mappedBy = "dentist", cascade = CascadeType.ALL)
    private List<Patient> patientList;

    @JsonManagedReference
    @OneToMany(mappedBy = "dentist", cascade = CascadeType.ALL)
    private List<Appointment> appointmentList;

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

    public List<Appointment> getAppointmentList() {
        return appointmentList;
    }

    public void setAppointmentList(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }
}
