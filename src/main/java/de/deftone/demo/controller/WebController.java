package de.deftone.demo.controller;

import de.deftone.demo.model.GivenLocation;
import de.deftone.demo.model.FreeLocation;
import de.deftone.demo.model.Participant;
import de.deftone.demo.service.EventService;
import de.deftone.demo.service.LocationService;
import de.deftone.demo.service.ParticipantService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@Controller
@AllArgsConstructor
public class WebController {

    private final LocationService locationService;
    private final EventService eventService;
    private final ParticipantService participantService;

    @GetMapping("/")
    public String showTemplate(Model model) {
        model.addAttribute("nextEvent", eventService.getNextEvent().getFormattedDate());
        model.addAttribute("locations", locationService.getAllLocations());
        model.addAttribute("freeLocations", locationService.getFreeLocations());
        model.addAttribute("participants", participantService.getAllParticipantsForNextEvent());
        model.addAttribute("freeLocation", new FreeLocation());
        model.addAttribute("givenLocation", new GivenLocation());
        return "index";
    }

    @PostMapping("/addPerson")
    public String addPerson(@Valid @ModelAttribute GivenLocation givenLocation,
                            BindingResult bindingResult,
                            Model model) {

        // das hier klappt, aber der fehlerrahmen erscheint nicht :(
        if (bindingResult.hasErrors()) {
            //todo: ein pop up? dass beides angegeben werden muss?
            // oder etwas ins html hinzufuegen?
            return "redirect:/#anmelden";
        }

        Participant participant = new Participant();
        participant.setName(givenLocation.getName());
        participant.setAngemeldetAm(LocalDate.now());
        participant.setEvent(eventService.getNextEvent());
        participant.setLocationName(locationService.getLocationNameById(givenLocation.getIdFromString()));
        participantService.addParticipant(participant);

        //und anschliessend als gebucht setzen, damit aus auswahlbox verschwindet
        locationService.setLocationToBooked(givenLocation.getIdFromString());

        // alle geaenderten Attribute neu holen
        model.addAttribute("participants", participantService.getAllParticipantsForNextEvent());
        model.addAttribute("locations", locationService.getAllLocations());
        model.addAttribute("freeLocations", locationService.getFreeLocations());

        return "redirect:/#mitmacher";
    }

    @PostMapping("/addPersonNewRoute")
    public String addPersonNewRoute(@Valid @ModelAttribute FreeLocation freeLocation,
                                    BindingResult bindingResult,
                                    Model model) {
        // das hier klappt, aber der fehlerrahmen erscheint nicht :(
        if (bindingResult.hasErrors()) {
            //todo: ein pop up? dass beides angegeben werden muss?
            // oder etwas ins html hinzufuegen?
            return "redirect:/#anmelden";
        }

        if (freeLocation != null
                && !freeLocation.getName().isEmpty()
                && !freeLocation.getLocation().isEmpty()) {
            Participant participant = new Participant();
            participant.setName(freeLocation.getName());
            participant.setAngemeldetAm(LocalDate.now());
            participant.setEvent(eventService.getNextEvent());
            participant.setLocationName(freeLocation.getLocation());
            participantService.addParticipant(participant);

            // alle geaenderten Attribute neu holen
            model.addAttribute("participants", participantService.getAllParticipantsForNextEvent());

        }
        return "redirect:/#mitmacher";
    }
}