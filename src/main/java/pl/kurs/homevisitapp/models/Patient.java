package pl.kurs.homevisitapp.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "patients")
public class Patient extends Person {

    @Id
    @Column(name = "id_patient", nullable = false, unique = true)
    private Long id;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "patient")
    private Set<HomeVisit> homeVisits = new HashSet<>();

    public Patient(String lastName, String firstName, LocalDate birthDate, String pesel, Long id) {
        super(lastName, firstName, birthDate, pesel);
        this.id = id;
    }

    @Override
    public String toString() {
        return super.toString() + '}';
    }

}
