package de.deftone.demo.controller;

import de.deftone.demo.model.*;
import de.deftone.demo.service.EventService;
import de.deftone.demo.service.LocationService;
import de.deftone.demo.service.ParticipantService;
import de.deftone.demo.service.SecretService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class AdminController {

    private final LocationService locationService;
    private final EventService eventService;
    private final ParticipantService participantService;
    private final SecretService secretService;

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

    @GetMapping("/admin/getSecrets")
    public List<Secret> getSecrets() {
        return secretService.getAllSecrets();
    }

    @PostMapping("/admin/addSecret")
    public Secret setSecret(@RequestBody Secret secret) {
        return secretService.addSecret(secret);
    }

    @PostMapping("/admin/deleteSecret")
    public void setSecret(@RequestParam long id) {
        secretService.deleteSecret(id);
    }

    @PostMapping("/admin/getAllASLParticipantsForNextEvent")
    public List<ParticipantASL> getAllASLParticipantsForNextEvent(@RequestBody Secret secret) {
        if (secretService.checkSecret(secret)) {
            return participantService.getAllASLParticipantsForNextEvent();
        } else {
            return null;
        }
    }

    //Teilnehmer loeschen
    @PostMapping(path = "/admin/deleteParticipant")
    public boolean deleteParticipant(@RequestParam long id) {
        return participantService.deleteParticipant(id);
    }

    @PostMapping(path = "/admin/deleteASLParticipant")
    public boolean deleteASLParticipant(@RequestParam long id, @RequestBody Secret secret) {
        if (secretService.checkSecret(secret)) {
            return participantService.deleteASLParticipant(id);
        } else {
            return false;
        }

    }
}
