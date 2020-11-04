package de.deftone.demo.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Event {

    private long id;
    private LocalDate date;
    private String name;
    private String location;

}
