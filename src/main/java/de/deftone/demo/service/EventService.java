package de.deftone.demo.service;

import de.deftone.demo.model.Event;
import de.deftone.demo.repo.EventRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepo eventRepo;

    public Event addEvent(Event event) {
        return eventRepo.save(event);
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
}
