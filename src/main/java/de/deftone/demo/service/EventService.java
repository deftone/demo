package de.deftone.demo.service;

import de.deftone.demo.model.Event;
import de.deftone.demo.repo.EventRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepo eventRepo;

    public EventService(EventRepo eventRepo) {
        this.eventRepo = eventRepo;
    }

    public Event addEvent(Event event) {
        return eventRepo.save(event);
    }

    public Event getNextEvent() {
        //todo!! noch richtig implementieren, nach datum suchen, was in der zukunft liegt
        // am naehesten in der zukunft
        return eventRepo.findById(1L).get();
    }

    public List<Event> getAllEvents() {
        return eventRepo.findAll();
    }
}
