package de.deftone.demo.controller;

import de.deftone.demo.model.Participant;
import de.deftone.demo.service.EventService;
import de.deftone.demo.service.LocationService;
import de.deftone.demo.service.ParticipantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class WebController {

    private final LocationService locationService;
    private final EventService eventService;
    private final ParticipantService participantService;

    public WebController(LocationService locationService,
                         EventService eventService,
                         ParticipantService participantService) {
        this.locationService = locationService;
        this.eventService = eventService;
        this.participantService = participantService;
    }

    @GetMapping("/index")
    public java.lang.String showTemplate(Model model) {
        model.addAttribute("nextEvent", eventService.getNextEvent().getFormattedDate());
        model.addAttribute("locations", locationService.getAllLocations());
        model.addAttribute("freeLocations", locationService.getFreeLocations());
        model.addAttribute("participants", participantService.getAllParticipantsForNextEvent());
        return "index";
    }

    @PostMapping("/addPerson")
    public java.lang.String addPerson(@RequestParam String name,
                                      @RequestParam Integer id,
                                      Model model) {
        // was ist die id bei auswaehlen?? null?
        if (name != null && !name.isEmpty() && id != null) {
            Participant participant = new Participant();
            participant.setName(name);
            participant.setAngemeldetAm(LocalDate.now());
            participant.setEvent(eventService.getNextEvent());
            participant.setLocationName(locationService.getLocationNameById(id));
            participantService.addParticipant(participant);

            //und anschliessend als gebucht setzen, damit aus auswahlbox verschwindet
            locationService.setLocationToBooked(id);

            // alle geaenderten Attribute neu holen
            model.addAttribute("participants", participantService.getAllParticipantsForNextEvent());
            model.addAttribute("locations", locationService.getAllLocations());
            model.addAttribute("freeLocations", locationService.getFreeLocations());
        }
        return "redirect:/index#mitmacher";
    }
}