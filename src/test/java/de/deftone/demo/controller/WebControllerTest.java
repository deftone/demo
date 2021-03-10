package de.deftone.demo.controller;

import de.deftone.demo.model.Event;
import de.deftone.demo.model.Location;
import de.deftone.demo.model.Participant;
import de.deftone.demo.service.EventService;
import de.deftone.demo.service.LocationService;
import de.deftone.demo.service.ParticipantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebController.class)
class WebControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private LocationService locationServiceMock;

    @MockBean
    private EventService eventServiceMock;

    @MockBean
    private ParticipantService participantServiceMock;

    private final List<Location> ALL_LOCATIONS = new ArrayList<>();
    private final List<Location> FREE_LOCATIONS = new ArrayList<>();
    private final List<Participant> ALL_PARTICIPANTS = new ArrayList<>();

    private final Location LOC1 = new Location(1L, "R1", "Route 1", true);
    private final Location LOC2 = new Location(2L, "R2", "Route 2", false);
    private final Location LOC3 = new Location(3L, "R3", "Route 3", true);

    private final LocalDate NEXT_DATE = LocalDate.of(2021, 1, 1);
    private final Event EVENT = new Event(1L, NEXT_DATE);
    private final LocalDate NOW = LocalDate.now();
    private final Participant PARTICIPANT1 = new Participant(1L, NOW, "Heinz", "Route 2", EVENT);
    private final Participant PARTICIPANT2 = new Participant(2L, NOW, "Kirk", "free Route", EVENT);

    @BeforeEach
    void setUp() {
        ALL_LOCATIONS.add(LOC1);
        ALL_LOCATIONS.add(LOC2);
        ALL_LOCATIONS.add(LOC3);

        FREE_LOCATIONS.add(LOC1);
        FREE_LOCATIONS.add(LOC3);

        ALL_PARTICIPANTS.add(PARTICIPANT1);
        ALL_PARTICIPANTS.add(PARTICIPANT2);

        when(eventServiceMock.getNextEvent()).thenReturn(EVENT);
        when(locationServiceMock.getAllLocations()).thenReturn(ALL_LOCATIONS);
        when(locationServiceMock.getFreeLocations()).thenReturn(FREE_LOCATIONS);
        when(participantServiceMock.getAllParticipantsForNextEvent()).thenReturn(ALL_PARTICIPANTS);
    }


    @Test
    void testHome() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("locations", hasItems(ALL_LOCATIONS.toArray())))
                .andExpect(model().attribute("freeLocations", hasItems(FREE_LOCATIONS.toArray())))
                .andExpect(model().attribute("participants", hasItems(ALL_PARTICIPANTS.toArray())))
                .andExpect(content().string(containsString("Die nächste Aktion ist am Sonntag, den <span>" + EVENT.getFormattedDate() + "</span>.")));
    }

    @Test
    void testAddPerson() throws Exception {
        when(locationServiceMock.getLocationNameById(2)).thenReturn(LOC2.getName());

        mockMvc.perform(post("/addPerson")
                .param("name", "Jonny")
                .param("id", "2"))
                .andExpect(redirectedUrl("/#mitmacher"));
    }
}