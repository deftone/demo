package de.deftone.demo.model;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    @Test
    public void xxx() {
        Event event = new Event(1L, LocalDate.of(2020, 10, 3));
        event.getFormattedDate();
        System.out.println(event.getDate());
        System.out.println(event.getFormattedDate());
    }

}