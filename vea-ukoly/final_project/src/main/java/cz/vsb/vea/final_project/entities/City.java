package cz.vsb.vea.final_project.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class City {
    @Id
    int id;
    String name;
}
