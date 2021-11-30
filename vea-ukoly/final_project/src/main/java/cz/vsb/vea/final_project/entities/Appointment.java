package cz.vsb.vea.final_project.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "appointment")
public class Appointment {
    @EmbeddedId
    private AppointmentKey id;

    @ManyToOne
    @MapsId("dentistId")
    @JoinColumn(name = "dentist_id")
    private Dentist dentist;

    @ManyToOne
    @MapsId("patientId")
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "price")
    private Double price;

    @Column(name = "description")
    private String description;

    public Appointment() {
    }

    public Appointment(AppointmentKey id, Dentist dentist, Patient patient, LocalDate date, Double price, String description) {
        this.id = id;
        this.dentist = dentist;
        this.patient = patient;
        this.date = date;
        this.price = price;
        this.description = description;
    }

    public AppointmentKey getId() {
        return id;
    }

    public void setId(AppointmentKey id) {
        this.id = id;
    }

    public Dentist getDentist() {
        return dentist;
    }

    public void setDentist(Dentist dentist) {
        this.dentist = dentist;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
