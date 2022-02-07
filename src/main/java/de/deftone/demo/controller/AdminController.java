package de.deftone.demo.controller;

import de.deftone.demo.model.*;
import de.deftone.demo.service.EventService;
import de.deftone.demo.service.LocationService;
import de.deftone.demo.service.ParticipantService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class AdminController {

    private final LocationService locationService;
    private final EventService eventService;
    private final ParticipantService participantService;

    // ****** Location ******
    //zum erstellen
    @PostMapping(path = "/admin/addLocation", consumes = "application/json")
    public Location addLocation(@RequestBody Location location) {
        return locationService.addLocation(location);
    }

    @PostMapping(path = "/admin/addLocationList", consumes = "application/json")
    public List<Location> addLocationList(@RequestBody List<Location> locations) {
        return locationService.addLocationList(locations);
    }

    @PostMapping(path = "/admin/addASLLocationList", consumes = "application/json")
    public List<LocationASL> addASLLocationList(@RequestBody List<LocationASL> locations) {
        return locationService.addASLLocationList(locations);
    }

    //zum kontrollieren
    @GetMapping("/admin/getLocations")
    public List<Location> getLocations() {
        return locationService.getAllLocations();
    }

    //zum zuruecksetzen
    @GetMapping(path = "/admin/resetAllLocations")
    public List<Location> resetAllLocations() {
        return locationService.resetAllLocations();
    }

    @GetMapping(path = "/admin/resetAllASLLocations")
    public List<LocationASL> resetAllASLLocations() {
        return locationService.resetAllASLLocations();
    }


    //zum loeschen
    @PostMapping(path = "/admin/deleteLocation")
    public void deleteLocation(@RequestParam long id) {
        locationService.deleteLocation(id);
    }

    @PostMapping(path = "/admin/deleteAllLocation")
    public void deleteAllLocation() {
        locationService.deleteAllLocations();
    }

    @PostMapping(path = "/admin/deleteAllASLLocation")
    public void deleteAllASLLocation() {
        locationService.deleteAllASLLocations();
    }


    // ****** Event *******
    //zum erstellen
    @PostMapping(path = "/admin/addEvent")
    public Event addEvent(@RequestParam String datumInYYYY_MM_DD) {
        return eventService.addEvent(datumInYYYY_MM_DD);
    }

    //zum kontrollieren
    @GetMapping("/admin/getEvents")
    public List<Event> getEvents() {
        return eventService.getAllEvents();
    }

    // ***** Participant *******

    @GetMapping("/admin/getAllParticipantsForNextEvent")
    public List<Participant> getAllParticipantsForNextEvent() {
        return participantService.getAllParticipantsForNextEvent();
    }

    @GetMapping("/admin/getAllASLParticipantsForNextEvent")
    public List<ParticipantASL> getAllASLParticipantsForNextEvent() {
        return participantService.getAllASLParticipantsForNextEvent();
    }

    //Teilnehmer loeschen
    @PostMapping(path = "/admin/deleteParticipant")
    public boolean deleteParticipant(@RequestParam long id) {
        return participantService.deleteParticipant(id);
    }

    @PostMapping(path = "/admin/deleteASLParticipant")
    public boolean deleteASLParticipant(@RequestParam long id) {
        return participantService.deleteASLParticipant(id);
    }
}
