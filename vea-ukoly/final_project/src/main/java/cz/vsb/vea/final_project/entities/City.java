package cz.vsb.vea.final_project.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "city")
public class City {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    @Column (name = "city_id")
    private int id;

    @Column (name = "name")
    private String name;

    @OneToMany (mappedBy = "city", cascade = CascadeType.ALL)
    private List<Person> personList;
}
