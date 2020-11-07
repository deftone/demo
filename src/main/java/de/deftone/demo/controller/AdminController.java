package de.deftone.demo.controller;

import de.deftone.demo.model.Event;
import de.deftone.demo.model.Location;
import de.deftone.demo.service.EventService;
import de.deftone.demo.service.LocationService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class AdminController {

    private final LocationService locationService;
    private final EventService eventService;

    public AdminController(LocationService locationService,
                           EventService eventService) {
        this.locationService = locationService;
        this.eventService = eventService;
    }


    //zum erstellen
    @PostMapping(path = "/admin/addLocation", consumes = "application/json")
    public Location addLocation(@RequestBody Location location) {
        return locationService.addLocation(location);
    }

    //zum kontrollieren
    @GetMapping("/admin/getLocations")
    public List<Location> getLocations() {
        return locationService.getAllLocations();
    }

    //zum zuruecksetzen
    @GetMapping(path = "/admin/resetAllLocations")
    public List<Location> resetAllLocations() {
        return locationService.resetAllLocation();
    }

    //todo: loeschen

    //zum erstellen
    @PostMapping(path = "/admin/addEvent")
    public Event addEvent(@RequestParam String datumInDDMMYYYY) {
        Event event = new Event();
        //todo: datum nutzen
        event.setDate(LocalDate.now());
        return eventService.addEvent(event);
    }

    //zum kontrollieren
    @GetMapping("/admin/getEvents")
    public List<Event> getEvents() {
        return eventService.getAllEvents();
    }

    //todo: loeschen
}
