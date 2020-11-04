package de.deftone.demo.controller;

import de.deftone.demo.model.Event;
import de.deftone.demo.model.Location;
import de.deftone.demo.model.Sector;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class webController {

    private List<Event> list = new ArrayList<>();
    private List<Location> allLocations;

    @GetMapping("/index")
    public String showTemplate(Model model) {
        list = createEvents();
        allLocations = getAllLocations();
        model.addAttribute("events", list);
        model.addAttribute("locations", allLocations);
        return "index";
    }

    @PostMapping("/addPerson")
    public String addPerson(@RequestParam String name,
                            @RequestParam Long id,
                            Model model) {
        if (name != null && !name.isEmpty() && id != null) {
            list.add(createEvent(name, id));
        }
        model.addAttribute("events", list);
        model.addAttribute("locations", allLocations);
        //todo: redirekt zu /index
        return "index";
    }

    private Event createEvent(String name, Sector sector) {
        Event event = new Event();
        event.setName(name);
        event.setSector(sector);
        event.setDate(LocalDate.now());
        return event;
    }

    private Event createEvent(String name, long id) {
        Event event = new Event();
        event.setName(name);
        event.setSector(getSector(id));
        event.setDate(LocalDate.now());
        return event;
    }

    private Sector getSector(long id) {
        Optional<Location> first = allLocations.stream().filter(l -> l.getId() == id).findFirst();
        if (first.isPresent()) {
            return first.get().getName();
        } else {
            return Sector.none;
        }
    }

    private List<Event> createEvents() {
        List<Event> list = new ArrayList<>();
        list.add(createEvent("Heinz", Sector.Rathaus));
        list.add(createEvent("Jean-Luc", Sector.Stetteriz));
        list.add(createEvent("Kirk", Sector.Rehberg));
        return list;
    }


    public List<Location> getAllLocations() {
        List<Location> allLocations = new ArrayList<>();
        allLocations.add(new Location(1L, Sector.Rathaus, true));
        allLocations.add(new Location(2L, Sector.Rehberg, true));
        allLocations.add(new Location(3L, Sector.Stetteriz, true));
        allLocations.add(new Location(4L, Sector.JustinSchule, false));
        allLocations.add(new Location(5L, Sector.RehbergSchule, false));
        allLocations.add(new Location(6L, Sector.DarmstaedterStrasse, false));
        allLocations.add(new Location(7L, Sector.Taunusspielplatz, false));
        return allLocations;
    }

}

//https://spring.io/guides/gs/handling-form-submission/
//    @GetMapping("/greeting")
//    public String greetingForm(Model model) {
//        model.addAttribute("greeting", new Greeting());
//        return "greeting";
//    }
//
//    @PostMapping("/greeting")
//    public String greetingSubmit(@ModelAttribute Greeting greeting, Model model) {
//        model.addAttribute("greeting", greeting);
//        return "result";
//    }