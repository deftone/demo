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
        model.addAttribute("events", eventService.getNextEvent());
        model.addAttribute("locations", locationService.getAllLocations());
        return "index";
    }

    @PostMapping("/addPerson")
    public java.lang.String addPerson(@RequestParam java.lang.String name,
                                      @RequestParam Long id,
                                      Model model) {
        if (name != null && !name.isEmpty() && id != null) {
            Participant participant = new Participant();
            participant.setName(name);
            participant.setAngemeldetAm(LocalDate.now());
            participant.setEvent(eventService.getNextEvent());
            participantService.addParticipant(participant);
        }
        model.addAttribute("participants", participantService.getAllParticipants());
//        model.addAttribute("locations", l); hat sichnicht geaendert, muss nicht uebergeben werden??
        return "redirect:/index";
    }
}