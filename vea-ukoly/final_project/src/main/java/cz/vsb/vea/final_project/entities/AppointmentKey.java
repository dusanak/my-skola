package cz.vsb.vea.final_project.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class AppointmentKey implements Serializable {
    @Column(name = "dentist_id")
    Long dentistId;

    @Column(name = "patient_id")
    Long patientId;

    public AppointmentKey() {
    }

    public AppointmentKey(Long dentistId, Long patientId) {
        this.dentistId = dentistId;
        this.patientId = patientId;
    }

    public Long getDentistId() {
        return dentistId;
    }

    public void setDentistId(Long dentistId) {
        this.dentistId = dentistId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppointmentKey that = (AppointmentKey) o;

        if (!dentistId.equals(that.dentistId)) return false;
        return patientId.equals(that.patientId);
    }

    @Override
    public int hashCode() {
        int result = dentistId.hashCode();
        result = 31 * result + patientId.hashCode();
        return result;
    }
}
