package de.deftone.demo.controller;

import de.deftone.demo.model.Event;
import de.deftone.demo.model.LocationASL;
import de.deftone.demo.service.EventService;
import de.deftone.demo.service.LocationService;
import de.deftone.demo.service.ParticipantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class WebControllerASLTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private LocationService locationServiceMock;

    @MockBean
    private EventService eventServiceMock;

    @MockBean
    private ParticipantService participantServiceMock;

    private final List<LocationASL> ALL_LOCATIONS = new ArrayList<>();
    private final List<LocationASL> FREE_LOCATIONS = new ArrayList<>();

    private final LocationASL LOC1 = new LocationASL(1L, "R1", "Route 1", true);
    private final LocationASL LOC2 = new LocationASL(2L, "R2", "Route 2", false);
    private final LocationASL LOC3 = new LocationASL(3L, "R3", "Route 3", true);

    private final LocalDate NEXT_DATE = LocalDate.of(2021, 1, 1);
    private final Event EVENT = new Event(1L, NEXT_DATE);

    @BeforeEach
    void setUp() {
        ALL_LOCATIONS.add(LOC1);
        ALL_LOCATIONS.add(LOC2);
        ALL_LOCATIONS.add(LOC3);

        FREE_LOCATIONS.add(LOC1);
        FREE_LOCATIONS.add(LOC3);

        when(eventServiceMock.getNextEvent()).thenReturn(EVENT);
        when(locationServiceMock.getAllASLLocations()).thenReturn(ALL_LOCATIONS);
        when(locationServiceMock.getFreeASLLocations()).thenReturn(FREE_LOCATIONS);

        //todo: liste mit allen vergebenen Orten
    }

    @DisplayName("teste anzeige der locations auf home fuer ASL")
    @Test
    void testHome() throws Exception {
        mockMvc.perform(get("/aktionSaubereLandschaft"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("locations", hasItems(ALL_LOCATIONS.toArray())))
                .andExpect(content().string(containsString("In diesem April ist es anders. Gemeinsam mit der Gemeinde")));
    }

    @DisplayName("teste anzeige der anmeldeseite fuer ASL")
    @Test
    void testAnmeldeSeite() throws Exception {
        mockMvc.perform(get("/aktionSaubereLandschaftAnmelden"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("freeLocationsASL", hasItems(FREE_LOCATIONS.toArray())))
                .andExpect(content().string(containsString("Aktion Saubere Landschaft - Anmelden")));
    }

    @DisplayName("teste anmelden der locations auf home fuer ASL")
    @Test
    void testAnmelden() throws Exception {
        mockMvc.perform(get("/aktionSaubereLandschaft"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("locations", hasItems(ALL_LOCATIONS.toArray())))
                .andExpect(content().string(containsString("In diesem April ist es anders. Gemeinsam mit der Gemeinde")));
    }

    @DisplayName("teste anmelden mit route aus checkbox fuer ASL - OK")
    @Test
    void testAddPerson() throws Exception {
        when(locationServiceMock.getLocationNameById(2)).thenReturn(LOC2.getName());

        mockMvc.perform(post("/aktionSaubereLandschaftAddPerson")
                        .param("vorUndNachName", "Jonny")
                        .param("strasseHausNr", "strasse")
                        .param("plzOrt", "12345 O")
                        .param("emailAdresse", "a@b.de")
                        .param("weitereTeilnehmer", "eins, zwei, drei")
                        .param("id", "2") // oder freeLocation
                        .param("personenDaten", "true")
                //optional: fotosMachen
        ).andExpect(redirectedUrl("/aktionSaubereLandschaft#mitmacher"));
    }

    //todo: noch mehr tests, mit freeLocation und mit allen moeglichen fehleingaben
}