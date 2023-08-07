package de.deftone.demo.controller;

import de.deftone.demo.crypto.EncodingException;
import de.deftone.demo.model.*;
import de.deftone.demo.service.EventService;
import de.deftone.demo.service.LocationService;
import de.deftone.demo.service.ParticipantService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDate;

@Controller
@AllArgsConstructor
public class WebASLController {

    private final LocationService locationService;
    private final EventService eventService;
    private final ParticipantService participantService;

    @GetMapping("/anmeldungErfolg")
    public String showErfolgTemplate() {
        return "anmeldungErfolg";
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
        model.addAttribute("locations", locationService.getAllASLLocations());
        model.addAttribute("givenLocationASL", new GivenLocationASL());
        return "anmeldenAktionSaubereLandschaft";
    }

    @PostMapping("/aktionSaubereLandschaftAddPerson")
    public String addPersonAktionSaubereLandschaft(@Valid @ModelAttribute GivenLocationASL givenLocationASL,
                                                   BindingResult bindingResult,
                                                   Model model) {
        model.addAttribute("freeLocationsASL", locationService.getFreeASLLocations());
        model.addAttribute("locations", locationService.getAllASLLocations());
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
        participant.setTelefonNr(givenLocationASL.getTelefonNr());
        participant.setWeitereTeilnehmer(givenLocationASL.getWeitereTeilnehmer());
        participant.setAngemeldetAm(LocalDate.now());
        participant.setEvent(eventService.getNextEvent());
        // check was gefuellt ist
        if (givenLocationASL.getIdFromString() != -1L) {
            String locationNameById = locationService.getASLLocationNameById(givenLocationASL.getIdFromString());
            participant.setLocationName(locationNameById);
            // und anschliessend als gebucht setzen, damit aus auswahlbox verschwindet
            // nein, nicht machen, ich setze die routen auf "gebucht"
            // locationService.setASLLocationToBooked(givenLocationASL.getIdFromString());
        } else {
            participant.setLocationName(givenLocationASL.getFreeLocation());
        }
        if (givenLocationASL.getFotosMachen() != null) {
            participant.setFotosMachen(true);
        }

        try {
            participantService.addParticipantASL(participant);
        } catch (EncodingException e) {
            //Falls beim encoding ein Fehler passiert: HTML Seite anzeigen, kein Stacktrace!
            return "redirect:/error";
        }

        return "redirect:/anmeldungErfolg";
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