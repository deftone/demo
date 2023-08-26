package de.deftone.demo.service;

import de.deftone.demo.model.Event;
import de.deftone.demo.repo.EventRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepo eventRepo;

    //todo: bessere exceptions!
    public Event addEvent(String dateString) {
        //zuerst das pattern pruefen
        try {
            //default, ISO_LOCAL_DATE
            LocalDate localDate = LocalDate.parse(dateString);
            Event event = new Event();
            event.setDate(localDate);

            //pruefen ob es das event schon gibt (kein doppeltes datum!)
            long count = eventRepo.findAll().stream()
                    .filter(e -> e.getDate().equals(localDate))
                    .count();
            if (count == 0) {
                return eventRepo.save(event);
            } else {
                throw new RuntimeException("Event an diesem Event existiert schon!");
            }
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Falsches Datumformat! " +
                    "Bitte Datum so eingeben: YYYY-MM-DD. zB 2021-01-15");
        }

    }

    //todo: bessere Exception!
    public Event getNextEvent() {
        LocalDate today = LocalDate.now();

        long count = eventRepo.findAll()
                .stream()
                .filter(e -> e.getDate().compareTo(today) >= 0)
                .count();
        if (count == 0) {
            throw new RuntimeException("no upcoming Event found!");
        }

        // falls es mehrere gibt, dann wird durch das Sortieren das gefunden,
        // was am naechsten dran ist
        List<Event> nextEvents = eventRepo.findAll()
                .stream()
                .filter(e -> e.getDate().compareTo(today) >= 0)
                .sorted()
                .collect(Collectors.toList());

        return nextEvents.get(0);
    }

    public List<Event> getAllEvents() {
        return eventRepo.findAll();
    }

    public Optional<Event> getEventById(long id) {
        return eventRepo.findById(id);
    }

    public void deleteEventById(long id) {
        Event event = eventRepo.findById(id)
                //todo: bessere exception
                .orElseThrow(() -> new RuntimeException("Kein Event mit id " + id));
        eventRepo.delete(event);
    }
}
