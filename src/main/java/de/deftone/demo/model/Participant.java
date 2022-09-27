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

    private String locationName;

    @OneToOne(targetEntity = Event.class)
    private Event event;

    public void setName(String name) {
        this.name = cleanUserInput(name);
    }

    public void setLocationName(String locationName) {
        this.locationName = cleanUserInput(locationName);
    }

    static String cleanUserInput(String expressionToCheck) {
        String ohneBoeseWoerter = expressionToCheck
                .toLowerCase()
                .replace("sleep", "--")
                .replace("delay", "--")
                .replace("drop", "--")
                .replace("select", "--")
                .replace("insert", "--")
                .replace("update", "--")
                .replace("delete", "--");

        if (ohneBoeseWoerter.equals(expressionToCheck.toLowerCase())) {
            return expressionToCheck
                    .replaceAll("[^a-zA-Z0-9.,&()\\s\\u00c4\\u00e4\\u00d6\\u00f6\\u00dc\\u00fc\\u00df]", "-");
        } else {
            return ohneBoeseWoerter
                    .replaceAll("[^a-zA-Z0-9\\s\\u00c4\\u00e4\\u00d6\\u00f6\\u00dc\\u00fc\\u00df]", "-");
        }
    }
}
