package de.deftone.demo.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class EventTest {

    @Test
    void xxx() {
        Event event = new Event(1L, LocalDate.of(2020, 10, 3));
        event.getFormattedDate();
        System.out.println(event.getDate());
        System.out.println(event.getFormattedDate());
    }

}