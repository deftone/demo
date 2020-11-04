package de.deftone.demo.controller;

import de.deftone.demo.model.Event;
import de.deftone.demo.model.Location;
import de.deftone.demo.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class WebController {

    private List<Event> list = new ArrayList<>();
    private List<Location> allLocations;

    @Autowired
    private LocationService locationService;

    @GetMapping("/index")
    public java.lang.String showTemplate(Model model) {
        list = createEvents();
        allLocations = locationService.getAllLocations();
        model.addAttribute("events", list);
        model.addAttribute("locations", allLocations);
        return "index";
    }

    @PostMapping("/addPerson")
    public java.lang.String addPerson(@RequestParam java.lang.String name,
                                      @RequestParam Long id,
                                      Model model) {
        if (name != null && !name.isEmpty() && id != null) {
            list.add(createEvent(name, id));
        }
        model.addAttribute("events", list);
        model.addAttribute("locations", allLocations);
        return "redirect:/index";
    }

    private Event createEvent(java.lang.String name, String location) {
        Event event = new Event();
        event.setName(name);
        event.setLocation(location);
        event.setDate(LocalDate.now());
        return event;
    }

    private Event createEvent(java.lang.String name, long id) {
        Event event = new Event();
        event.setName(name);
        event.setLocation(getSector(id));
        event.setDate(LocalDate.now());
        return event;
    }

    private String getSector(long id) {
        Optional<Location> first = allLocations.stream().filter(l -> l.getId() == id).findFirst();
        if (first.isPresent()) {
            return first.get().getName();
        } else {
            return "";
        }
    }

    private List<Event> createEvents() {
        List<Event> list = new ArrayList<>();
        list.add(createEvent("Heinz", "Rathaus"));
        list.add(createEvent("Jean-Luc", "Stetteriz"));
        list.add(createEvent("Kirk", "Rehberg"));
        return list;
    }

}