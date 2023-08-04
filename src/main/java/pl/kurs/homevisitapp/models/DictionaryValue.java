package pl.kurs.homevisitapp.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class DictionaryValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_value")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "dictionary_id", nullable = false)
    private Dictionary dictionary;

    public DictionaryValue(String name, Dictionary dictionary) {
        this.name = name;
        this.dictionary = dictionary;
    }

    @Override
    public String toString() {
        return '\'' + name + '\'';
    }
}
