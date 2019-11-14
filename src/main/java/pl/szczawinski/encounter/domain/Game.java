package pl.szczawinski.encounter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private @Version
    @JsonIgnore
    Long version;

    private @ManyToOne Manager manager;



    public Game(final String name) {
        this.name = name;
    }

    public Game(final String name, Manager manager) {
        this.name = name;
        this.manager = manager;
    }

    // private List<Team> participants;
}
