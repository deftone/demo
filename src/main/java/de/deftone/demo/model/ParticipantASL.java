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
public class ParticipantASL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDate angemeldetAm;

    @Column(length = 300)
    private String vorUndNachName;
    @Column(length = 300)
    private String strasseHausNr;
    @Column(length = 300)
    private String plzOrt;
    @Column(length = 300)
    private String telefonNr;
    @Column(length = 300)
    private String emailAdresse;
    @Column(length = 1000)
    private String weitereTeilnehmer;
    @Column(length = 300)
    private String locationName;
    private boolean fotosMachen;

    @OneToOne(targetEntity = Event.class)
    private Event event;
}
