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
@Table(name = "doctors")
public class Doctor extends Person {

    @Id
    @Column(name = "id_doctor", nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "specialization_id")
    private DictionaryValue specialization;

    @Column(nullable = false)
    private String nip;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "doctor")
    private Set<HomeVisit> homeVisits = new HashSet<>();

    public Doctor(String lastName, String firstName, LocalDate birthDate, String pesel, Long id, DictionaryValue specialization, String nip) {
        super(lastName, firstName, birthDate, pesel);
        this.id = id;
        this.specialization = specialization;
        this.nip = nip;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", specialization=" + specialization +
                ", nip='" + nip + '\'' +
                "}";
    }

}
