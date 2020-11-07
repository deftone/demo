package de.deftone.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDate angemeldetAm;
    private String name;
    @OneToOne(targetEntity=Location.class)
    private Location location;
    @OneToOne(targetEntity=Event.class)
    private Event event;
}
