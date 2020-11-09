package de.deftone.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Event implements Comparable<Event> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;  //eingabe mit postman bekomme ich nicht hin

    @Override
    public int compareTo(Event o) {
        //zum sortieren nach Datum sortieren
        return this.getDate().compareTo(o.date);
    }

    //todo: equals und hashcode ueberschreiben
}
