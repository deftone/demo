package de.deftone.demo.controller;

import de.deftone.demo.model.*;
import de.deftone.demo.service.EventService;
import de.deftone.demo.service.LocationService;
import de.deftone.demo.service.ParticipantService;
import de.deftone.demo.service.SecretService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
    @PostMapping(path = "/admin/addLocationList", consumes = "application/json")
    public List<Location> addLocationList(@RequestBody Wrapper locationWithSecret) {
        Secret secret = locationWithSecret.getSecret();
        List<Location> locations = locationWithSecret.getLocationList();
        if (secretService.checkSecret(secret)) {
            return locationService.addLocationList(locations);
        }
        return Collections.emptyList();
    }

    @Getter
    @Setter
    static class Wrapper {
        private Secret secret;
        private List<Location> locationList;
    }

    @PostMapping(path = "/admin/addASLLocationList", consumes = "application/json")
    public List<LocationASL> addASLLocationList(@RequestBody WrapperASL locationWithSecret) {
        Secret secret = locationWithSecret.getSecret();
        List<LocationASL> locations = locationWithSecret.getLocationASLList();
        if (secretService.checkSecret(secret)) {
            return locationService.addASLLocationList(locations);
        }
        return Collections.emptyList();
    }

    @Getter
    @Setter
    static class WrapperASL {
        private Secret secret;
        private List<LocationASL> locationASLList;
    }

    //zum kontrollieren
    @PostMapping("/admin/getLocations")
    public List<Location> getLocations(@RequestBody Secret secret) {
        if (secretService.checkSecret(secret)) {
            return locationService.getAllLocations();
        } else {
            return Collections.emptyList();
        }

    }

    //zum zuruecksetzen
    @PostMapping(path = "/admin/resetAllLocations")
    public List<Location> resetAllLocations(@RequestBody Secret secret) {
        if (secretService.checkSecret(secret)) {
            return locationService.resetAllLocations();
        } else {
            return Collections.emptyList();
        }
    }

    @PostMapping(path = "/admin/resetAllASLLocations")
    public List<LocationASL> resetAllASLLocations(@RequestBody Secret secret) {
        if (secretService.checkSecret(secret)) {
            return locationService.resetAllASLLocations();
        }
        return Collections.emptyList();
    }


    @PostMapping(path = "/admin/setASLLocation")
    public LocationASL setASLLocations(@RequestParam long id, @RequestParam boolean free, @RequestBody Secret secret) {
        if (secretService.checkSecret(secret)) {
            return locationService.setLocation(id, free);
        }
        return new LocationASL();
    }

    @PostMapping(path = "/admin/setLocationToBooked")
    public void setLocationToBooked(@RequestParam long id, @RequestBody Secret secret) {
        if (secretService.checkSecret(secret)) {
            locationService.setLocationToBooked(id);
        }
    }


    //zum loeschen
    @PostMapping(path = "/admin/deleteLocation")
    public void deleteLocation(@RequestParam long id, @RequestBody Secret secret) {
        if (secretService.checkSecret(secret)) {
            locationService.deleteLocation(id);
        }
    }

    @PostMapping(path = "/admin/deleteAllLocation")
    public void deleteAllLocation(@RequestBody Secret secret) {
        if (secretService.checkSecret(secret)) {
            locationService.deleteAllLocations();
        }
    }

    @PostMapping(path = "/admin/deleteAllASLLocation")
    public boolean deleteAllASLLocation(@RequestBody Secret secret) {
        if (secretService.checkSecret(secret)) {
            locationService.deleteAllASLLocations();
            return true;
        }
        return false;
    }


    // ****** Event *******
    //zum erstellen
    @PostMapping(path = "/admin/addEvent")
    public Event addEvent(@RequestParam String datumInYYYY_MM_DD, @RequestBody Secret secret) {
        if (secretService.checkSecret(secret)) {
            return eventService.addEvent(datumInYYYY_MM_DD);
        } else {
            return null;
        }
    }

    //zum kontrollieren
    @PostMapping("/admin/getEvents")
    public List<Event> getEvents(@RequestBody Secret secret) {
        if (secretService.checkSecret(secret)) {
            return eventService.getAllEvents();
        } else {
            return Collections.emptyList();
        }
    }

    //zum loeschen
    @PostMapping(path = "/admin/deleteEvent")
    public void deleteEvent(@RequestParam long id, @RequestBody Secret secret) {
        if (secretService.checkSecret(secret)) {
            eventService.deleteEventById(id);
        }
    }

    // ***** Participant *******

    @PostMapping("/admin/getAllParticipantsForNextEvent")
    public List<Participant> getAllParticipantsForNextEvent(@RequestBody Secret secret) {
        if (secretService.checkSecret(secret)) {
            return participantService.getAllParticipantsForNextEvent();
        } else {
            return Collections.emptyList();
        }
    }

//    @GetMapping("/admin/getSecrets")
//    public List<Secret> getSecrets() {
//        return secretService.getAllSecrets();
//    }

//    @PostMapping("/admin/addSecret")
//    public Secret setSecret(@RequestBody Secret secret) {
//        return secretService.addSecret(secret);
//    }

//    @PostMapping("/admin/deleteSecret")
//    public void setSecret(@RequestParam long id) {
//        secretService.deleteSecret(id);
//    }

    @PostMapping("/admin/getAllASLParticipantsForNextEvent")
    public List<ParticipantASL> getAllASLParticipantsForNextEvent(@RequestBody Secret secret) {
        if (secretService.checkSecret(secret)) {
            return participantService.getAllASLParticipantsForNextEvent();
        } else {
            return null;
        }
    }

    @PostMapping("/admin/getAllASLParticipantsForEventWithId")
    public List<ParticipantASL> getAllASLParticipantsForEvent(@RequestParam long id, @RequestBody Secret secret) {
        if (secretService.checkSecret(secret)) {
            return participantService.getAllASLParticipantsForEvent(id);
        } else {
            return null;
        }
    }

    //Teilnehmer loeschen
    @PostMapping(path = "/admin/deleteParticipant")
    public boolean deleteParticipant(@RequestParam long id, @RequestBody Secret secret) {
        if (secretService.checkSecret(secret)) {
            return participantService.deleteParticipant(id);
        } else {
            return false;
        }
    }

    @PostMapping(path = "/admin/deleteASLParticipant")
    public boolean deleteASLParticipant(@RequestParam long id, @RequestBody Secret secret) {
        if (secretService.checkSecret(secret)) {
            return participantService.deleteASLParticipant(id);
        } else {
            return false;
        }
    }

    //Personendaten von Teilnehmer loeschen
    @PostMapping(path = "/admin/deletePersonenDataFromASLParticipant")
    public boolean deletePersonenDataFromASLParticipant(@RequestParam long id, @RequestBody Secret secret) {
        if (secretService.checkSecret(secret)) {
            return participantService.deletePersonenDataFromASLParticipant(id);
        } else {
            return false;
        }
    }
}
