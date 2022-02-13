package de.deftone.demo.controller;

import de.deftone.demo.model.*;
import de.deftone.demo.service.EventService;
import de.deftone.demo.service.LocationService;
import de.deftone.demo.service.ParticipantService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
        }
        return "redirect:/#mitmacher";
    }

    @GetMapping({"/presse"})
    public String presse() {
        return "presse";
    }


    // AKTION SAUBERE LANDSCHAFT

    @GetMapping("/aktionSaubereLandschaft")
    public String showTemplateAktionSaubereLandschaft(Model model) {
        model.addAttribute("nextEvent", eventService.getNextEvent().getFormattedDate());
        model.addAttribute("locations", locationService.getAllASLLocations());
        model.addAttribute("participantsASL", participantService.getAllASLParticipantsForNextEvent());
        return "indexAktionSaubereLandschaft";
    }

    @GetMapping("/aktionSaubereLandschaftAnmelden")
    public String anmeldenAktionSaubereLandschaft(Model model) {
        model.addAttribute("freeLocationsASL", locationService.getFreeASLLocations());
        model.addAttribute("givenLocationASL", new GivenLocationASL());
        return "anmeldenAktionSaubereLandschaft";
    }

    @PostMapping("/aktionSaubereLandschaftAddPerson")
    public String addPersonAktionSaubereLandschaft(@Valid @ModelAttribute GivenLocationASL givenLocationASL,
                                                   BindingResult bindingResult,
                                                   Model model) {
        model.addAttribute("freeLocationsASL", locationService.getFreeASLLocations());
        // alle Pflichtfelder muessen gefuellt sein:
        if (bindingResult.hasErrors()
                || keinOrtEingetragen(givenLocationASL)
                || datenCheckboxFehlt(givenLocationASL)
        ) {
            if (keinOrtEingetragen(givenLocationASL)) {
                bindingResult.addError(new FieldError("givenLocationASL",
                        "freeLocation",
                        "Bitte eine Route aus der Liste auswählen oder einen selbstgewählten Ort eintragen"));
            }
            if (datenCheckboxFehlt(givenLocationASL)) {
                bindingResult.addError(new FieldError("givenLocationASL",
                        "personenDaten",
                        "Bitte zustimmen"));
            }
            // damit die fehlermeldungen an den input boxen angezeigt werden, KEIN redirekt sonder das template zurueck geben!!
            // allerdings ist man dann ganz oben, daher evtl eine eigene neue seite, wo man sich anmelden kann
            // oder ich finde heraus, wie man an die stelle #anmelden kommt, hier mit href arbeiten klappt aber nicht
            // macht aber evtl eh sinn, die ganze anmelde formalitaet auf einer eigenen seite zu machen
            return "anmeldenAktionSaubereLandschaft";
        }

        ParticipantASL participant = new ParticipantASL();
        participant.setVorUndNachName(givenLocationASL.getVorUndNachName());
        participant.setStrasseHausNr(givenLocationASL.getStrasseHausNr());
        participant.setPlzOrt(givenLocationASL.getPlzOrt());
        participant.setEmailAdresse(givenLocationASL.getEmailAdresse());
        participant.setWeitereTeilnehmer(givenLocationASL.getWeitereTeilnehmer());
        participant.setAngemeldetAm(LocalDate.now());
        participant.setEvent(eventService.getNextEvent());
        // check was gefuellt ist
        if (givenLocationASL.getIdFromString() != -1L) {
            String locationNameById = locationService.getASLLocationNameById(givenLocationASL.getIdFromString());
            participant.setLocationName(locationNameById);
            //und anschliessend als gebucht setzen, damit aus auswahlbox verschwindet
            locationService.setASLLocationToBooked(givenLocationASL.getIdFromString());
        } else {
            participant.setLocationName(givenLocationASL.getFreeLocation());
        }
        if (givenLocationASL.getFotosMachen() != null) {
            participant.setFotosMachen(true);
        }

        participantService.addParticipantASL(participant);


        return "redirect:/aktionSaubereLandschaft#mitmacher";
    }

    private boolean keinOrtEingetragen(GivenLocationASL givenLocationASL) {
        return givenLocationASL.getIdFromString() == -1L &&
                (givenLocationASL.getFreeLocation() == null
                        || givenLocationASL.getFreeLocation().isBlank()
                        || givenLocationASL.getFreeLocation().isEmpty());
    }

    private boolean datenCheckboxFehlt(GivenLocationASL givenLocationASL) {
        return givenLocationASL.getPersonenDaten() == null;
    }
}