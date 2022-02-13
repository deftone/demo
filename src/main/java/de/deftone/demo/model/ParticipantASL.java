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

    private String vorUndNachName;
    private String strasseHausNr;
    private String plzOrt;
    private String emailAdresse;
    private String weitereTeilnehmer;
    private String locationName;
    private boolean fotosMachen;

    @OneToOne(targetEntity = Event.class)
    private Event event;
}
