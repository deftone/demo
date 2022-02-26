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
import org.springframework.test.web.servlet.ResultActions;
import org.thymeleaf.util.StringUtils;

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

    @DisplayName("teste anmelden nur Pflichtfelder (mit route aus liste) fuer ASL - OK")
    @Test
    void testAddPerson() throws Exception {
        when(locationServiceMock.getLocationNameById(2)).thenReturn(LOC2.getName());

        mockMvc.perform(post("/aktionSaubereLandschaftAddPerson")
                .param("vorUndNachName", "Jonny")
                .param("strasseHausNr", "strasse")
                .param("plzOrt", "12345 O")
                .param("emailAdresse", "a@b.de")
                .param("telefonNr", "12345")
                .param("id", "2") // oder freeLocation
                .param("personenDaten", "true")
        ).andExpect(redirectedUrl("/aktionSaubereLandschaft#mitmacher"));
        // da hier alle services gemocked sind, kann man leider nicht testen,
        // ob der Teilnehmer wirklich auf der DB gespeichert wurde
        // hier kann man nur testen, ob sich die Weboberflaeche richtig verhaelt
    }

    @DisplayName("teste anmelden nur Pflichtfelder (mit selbstgewahltem Ort) fuer ASL - OK")
    @Test
    void testAddPerson2() throws Exception {

        mockMvc.perform(post("/aktionSaubereLandschaftAddPerson")
                .param("vorUndNachName", "Jonny")
                .param("strasseHausNr", "strasse")
                .param("plzOrt", "12345 O")
                .param("emailAdresse", "a@b.de")
                .param("telefonNr", "12345")
                .param("freeLocation", "Eiserne Hand")
                .param("personenDaten", "true")
        ).andExpect(redirectedUrl("/aktionSaubereLandschaft#mitmacher"));
        // da hier alle services gemocked sind, kann man leider nicht testen,
        // ob der Teilnehmer wirklich auf der DB gespeichert wurde
        // hier kann man nur testen, ob sich die Weboberflaeche richtig verhaelt
    }

    @DisplayName("teste anmelden alle Felder fuer ASL - OK")
    @Test
    void testAddPerson3() throws Exception {
        mockMvc.perform(post("/aktionSaubereLandschaftAddPerson")
                .param("vorUndNachName", "Jonny")
                .param("strasseHausNr", "strasse")
                .param("plzOrt", "12345 O")
                .param("emailAdresse", "a@b.de")
                .param("telefonNr", "12345")
                .param("weitereTeilnehmer", "eins, zwei, drei") //freiwillig
                .param("freeLocation", "Eiserne Hand")
                .param("personenDaten", "true")
                .param("fotosMachen", "true") //freiwillig
        ).andExpect(redirectedUrl("/aktionSaubereLandschaft#mitmacher"));
        // da hier alle services gemocked sind, kann man leider nicht testen,
        // ob der Teilnehmer wirklich auf der DB gespeichert wurde
        // hier kann man nur testen, ob sich die Weboberflaeche richtig verhaelt
    }

    @DisplayName("teste anmelden, name fehlt - NOK")
    @Test
    void testAddPerson4() throws Exception {
        ResultActions perform = mockMvc.perform(post("/aktionSaubereLandschaftAddPerson")
//                .param("vorUndNachName", "Jonny")
                        .param("strasseHausNr", "strasse")
                        .param("plzOrt", "12345 O")
                        .param("emailAdresse", "a@b.de")
                        .param("telefonNr", "12345")
                        .param("freeLocation", "Eiserne Hand")
                        .param("personenDaten", "true")
        );
        // hier ja eben kein redirect, wegen model und field errors, diese pruefen
        perform.andExpect(content()
                .string(containsString("Bitte Vor- und Nachname eingeben, z.B. Max Mustermann")));
        //die schon eingegebenen Pflichtfelder sind im model und html noch enthalten:
        perform.andExpect(content().string(containsString("12345 O")));
        perform.andReturn().getModelAndView().getModel().containsValue("12345 O");
        perform.andExpect(content().string(containsString("a@b.de")));
        perform.andReturn().getModelAndView().getModel().containsValue("a@b.de");
        perform.andExpect(content().string(containsString("12345")));
        perform.andReturn().getModelAndView().getModel().containsValue("12345");
        perform.andExpect(content().string(containsString("Eiserne Hand")));
        perform.andReturn().getModelAndView().getModel().containsValue("Eiserne Hand");
    }

    @DisplayName("teste anmelden, strasse fehlt - NOK")
    @Test
    void testAddPerson5() throws Exception {
        ResultActions perform = mockMvc.perform(post("/aktionSaubereLandschaftAddPerson")
                        .param("vorUndNachName", "Jonny")
//                        .param("strasseHausNr", "strasse")
                        .param("plzOrt", "12345 O")
                        .param("emailAdresse", "a@b.de")
                        .param("telefonNr", "12345")
                        .param("freeLocation", "Eiserne Hand")
                        .param("personenDaten", "true")
        );
        // hier ja eben kein redirect, wegen model und field errors, diese pruefen
        perform.andExpect(content()
                .string(containsString("Bitte Straße und Hausnr eingeben, z.B. Darmstädter Str. 1")));
        //die schon eingegebenen Pflichtfelder sind im model und html noch enthalten:
        perform.andExpect(content().string(containsString("Jonny")));
        perform.andReturn().getModelAndView().getModel().containsValue("Jonny");
        perform.andExpect(content().string(containsString("a@b.de")));
        perform.andReturn().getModelAndView().getModel().containsValue("a@b.de");
        perform.andExpect(content().string(containsString("12345")));
        perform.andReturn().getModelAndView().getModel().containsValue("12345");
        perform.andExpect(content().string(containsString("Eiserne Hand")));
        perform.andReturn().getModelAndView().getModel().containsValue("Eiserne Hand");
    }

    @DisplayName("teste anmelden, email fehlt - NOK")
    @Test
    void testAddPerson6() throws Exception {
        ResultActions perform = mockMvc.perform(post("/aktionSaubereLandschaftAddPerson")
                        .param("vorUndNachName", "Jonny")
                        .param("strasseHausNr", "strasse")
                        .param("plzOrt", "12345 O")
//                        .param("emailAdresse", "a@b.de")
                        .param("telefonNr", "12345")
                        .param("freeLocation", "Eiserne Hand")
                        .param("personenDaten", "true")
        );
        // hier ja eben kein redirect, wegen model und field errors, diese pruefen
        perform.andExpect(content()
                .string(containsString("Bitte eine gültige Email-Adresse eingeben, z.B. Max@Mustermann.de")));
        //die schon eingegebenen Pflichtfelder sind im model und html noch enthalten:
        perform.andExpect(content().string(containsString("Jonny")));
        perform.andReturn().getModelAndView().getModel().containsValue("Jonny");
        perform.andExpect(content().string(containsString("12345 O")));
        perform.andReturn().getModelAndView().getModel().containsValue("12345 O");
        perform.andExpect(content().string(containsString("12345")));
        perform.andReturn().getModelAndView().getModel().containsValue("12345");
        perform.andExpect(content().string(containsString("Eiserne Hand")));
        perform.andReturn().getModelAndView().getModel().containsValue("Eiserne Hand");
    }

    @DisplayName("teste anmelden, telefonNr fehlt - NOK")
    @Test
    void testAddPerson6b() throws Exception {
        ResultActions perform = mockMvc.perform(post("/aktionSaubereLandschaftAddPerson")
                        .param("vorUndNachName", "Jonny")
                        .param("strasseHausNr", "strasse")
                        .param("plzOrt", "12345 O")
                        .param("emailAdresse", "a@b.de")
//                        .param("telefonNr", "12345")
                        .param("freeLocation", "Eiserne Hand")
                        .param("personenDaten", "true")
        );
        // hier ja eben kein redirect, wegen model und field errors, diese pruefen
        perform.andExpect(content()
                .string(containsString("Bitte eine Telefonnummer eingeben, z.B. 06154-123456")));
        //die schon eingegebenen Pflichtfelder sind im model und html noch enthalten:
        perform.andExpect(content().string(containsString("Jonny")));
        perform.andReturn().getModelAndView().getModel().containsValue("Jonny");
        perform.andExpect(content().string(containsString("12345 O")));
        perform.andReturn().getModelAndView().getModel().containsValue("12345 O");
        perform.andExpect(content().string(containsString("a@b.de")));
        perform.andReturn().getModelAndView().getModel().containsValue("a@b.de");
        perform.andExpect(content().string(containsString("12345")));
        perform.andReturn().getModelAndView().getModel().containsValue("12345");
    }

    @DisplayName("teste anmelden, location fehlt - NOK")
    @Test
    void testAddPerson7() throws Exception {
        ResultActions perform = mockMvc.perform(post("/aktionSaubereLandschaftAddPerson")
                        .param("vorUndNachName", "Jonny")
                        .param("strasseHausNr", "strasse")
                        .param("plzOrt", "12345 O")
                        .param("emailAdresse", "a@b.de")
                        .param("telefonNr", "12345")
//                        .param("freeLocation", "Eiserne Hand")
                        .param("personenDaten", "true")
        );
        // hier ja eben kein redirect, wegen model und field errors, diese pruefen
        perform.andExpect(content()
                .string(containsString("Bitte eine Route aus der Liste auswählen oder einen selbstgewählten Ort eintragen")));
        //die schon eingegebenen Pflichtfelder sind im model und html noch enthalten:
        perform.andExpect(content().string(containsString("Jonny")));
        perform.andReturn().getModelAndView().getModel().containsValue("Jonny");
        perform.andExpect(content().string(containsString("12345 O")));
        perform.andReturn().getModelAndView().getModel().containsValue("12345 O");
        perform.andExpect(content().string(containsString("a@b.de")));
        perform.andReturn().getModelAndView().getModel().containsValue("a@b.de");
        perform.andExpect(content().string(containsString("12345")));
        perform.andReturn().getModelAndView().getModel().containsValue("12345");
    }

    @DisplayName("teste anmelden, zustimmung fehlt - NOK")
    @Test
    void testAddPerson8() throws Exception {
        ResultActions perform = mockMvc.perform(post("/aktionSaubereLandschaftAddPerson")
                        .param("vorUndNachName", "Jonny")
                        .param("strasseHausNr", "strasse")
                        .param("plzOrt", "12345 O")
                        .param("emailAdresse", "a@b.de")
                        .param("telefonNr", "12345")
                        .param("freeLocation", "Eiserne Hand")
//                        .param("personenDaten", "true")
        );
        // hier ja eben kein redirect, wegen model und field errors, diese pruefen
        perform.andExpect(content()
                .string(containsString("Bitte zustimmen")));
        //die schon eingegebenen Pflichtfelder sind im model und html noch enthalten:
        perform.andExpect(content().string(containsString("Jonny")));
        perform.andReturn().getModelAndView().getModel().containsValue("Jonny");
        perform.andExpect(content().string(containsString("12345 O")));
        perform.andReturn().getModelAndView().getModel().containsValue("12345 O");
        perform.andExpect(content().string(containsString("a@b.de")));
        perform.andReturn().getModelAndView().getModel().containsValue("a@b.de");
        perform.andExpect(content().string(containsString("12345")));
        perform.andReturn().getModelAndView().getModel().containsValue("12345");
        perform.andExpect(content().string(containsString("Eiserne Hand")));
        perform.andReturn().getModelAndView().getModel().containsValue("Eiserne Hand");
    }

    @DisplayName("teste anmelden, name zu lang - NOK")
    @Test
    void testAddPerson9() throws Exception {
        String tooLongString = createLongString(101);
        ResultActions perform = mockMvc.perform(post("/aktionSaubereLandschaftAddPerson")
                .param("vorUndNachName", tooLongString)
                .param("strasseHausNr", "strasse")
                .param("plzOrt", "12345 O")
                .param("emailAdresse", "a@b.de")
                .param("telefonNr", "12345")
                .param("freeLocation", "Eiserne Hand")
                .param("personenDaten", "true")
        );
        // hier ja eben kein redirect, wegen model und field errors, diese pruefen
        perform.andExpect(content()
                .string(containsString("Bitte maximal 100 Zeichen eingeben.")));
        //die schon eingegebenen Pflichtfelder sind im model und html noch enthalten:
        perform.andExpect(content().string(containsString(tooLongString)));
        perform.andReturn().getModelAndView().getModel().containsValue(tooLongString);
        perform.andExpect(content().string(containsString("12345 O")));
        perform.andReturn().getModelAndView().getModel().containsValue("12345 O");
        perform.andExpect(content().string(containsString("a@b.de")));
        perform.andReturn().getModelAndView().getModel().containsValue("a@b.de");
        perform.andExpect(content().string(containsString("12345")));
        perform.andReturn().getModelAndView().getModel().containsValue("12345");
        perform.andExpect(content().string(containsString("Eiserne Hand")));
        perform.andReturn().getModelAndView().getModel().containsValue("Eiserne Hand");
    }

    @DisplayName("teste anmelden, name passt gerade noch - OK")
    @Test
    void testAddPerson10() throws Exception {
        String longString = createLongString(100);
        ResultActions perform = mockMvc.perform(post("/aktionSaubereLandschaftAddPerson")
                .param("vorUndNachName", longString)
                .param("strasseHausNr", "strasse")
                .param("plzOrt", "12345 O")
                .param("emailAdresse", "a@b.de")
                .param("telefonNr", "12345")
                .param("freeLocation", "Eiserne Hand")
                .param("personenDaten", "true")
        );
        // kein fehler
        perform.andExpect(redirectedUrl("/aktionSaubereLandschaft#mitmacher"));
    }

    @DisplayName("teste anmelden, strasse zu lang - NOK")
    @Test
    void testAddPerson11() throws Exception {
        String tooLongString = createLongString(101);
        ResultActions perform = mockMvc.perform(post("/aktionSaubereLandschaftAddPerson")
                .param("vorUndNachName", "Jonny")
                .param("strasseHausNr", tooLongString)
                .param("plzOrt", "12345 O")
                .param("emailAdresse", "a@b.de")
                .param("telefonNr", "12345")
                .param("freeLocation", "Eiserne Hand")
                .param("personenDaten", "true")
        );
        // hier ja eben kein redirect, wegen model und field errors, diese pruefen
        perform.andExpect(content()
                .string(containsString("Bitte maximal 100 Zeichen eingeben.")));
        //die schon eingegebenen Pflichtfelder sind im model und html noch enthalten:
        perform.andExpect(content().string(containsString(tooLongString)));
        perform.andReturn().getModelAndView().getModel().containsValue(tooLongString);
        perform.andExpect(content().string(containsString("12345 O")));
        perform.andReturn().getModelAndView().getModel().containsValue("12345 O");
        perform.andExpect(content().string(containsString("a@b.de")));
        perform.andReturn().getModelAndView().getModel().containsValue("a@b.de");
        perform.andExpect(content().string(containsString("12345")));
        perform.andReturn().getModelAndView().getModel().containsValue("12345");
        perform.andExpect(content().string(containsString("Eiserne Hand")));
        perform.andReturn().getModelAndView().getModel().containsValue("Eiserne Hand");
    }

    @DisplayName("teste anmelden, ort zu lang - NOK")
    @Test
    void testAddPerson12() throws Exception {
        String tooLongString = createLongString(101);
        ResultActions perform = mockMvc.perform(post("/aktionSaubereLandschaftAddPerson")
                .param("vorUndNachName", "Jonny")
                .param("strasseHausNr", "strasse 1")
                .param("plzOrt", tooLongString)
                .param("emailAdresse", "a@b.de")
                .param("telefonNr", "12345")
                .param("freeLocation", "Eiserne Hand")
                .param("personenDaten", "true")
        );
        // hier ja eben kein redirect, wegen model und field errors, diese pruefen
        perform.andExpect(content()
                .string(containsString("Bitte maximal 100 Zeichen eingeben.")));
        //die schon eingegebenen Pflichtfelder sind im model und html noch enthalten:
        perform.andExpect(content().string(containsString(tooLongString)));
        perform.andReturn().getModelAndView().getModel().containsValue(tooLongString);
        perform.andExpect(content().string(containsString("a@b.de")));
        perform.andReturn().getModelAndView().getModel().containsValue("a@b.de");
        perform.andExpect(content().string(containsString("12345")));
        perform.andReturn().getModelAndView().getModel().containsValue("12345");
        perform.andExpect(content().string(containsString("Eiserne Hand")));
        perform.andReturn().getModelAndView().getModel().containsValue("Eiserne Hand");
    }

    @DisplayName("teste anmelden, email zu lang - NOK")
    @Test
    void testAddPerson13() throws Exception {
        String tooLongString = createLongString(101);
        ResultActions perform = mockMvc.perform(post("/aktionSaubereLandschaftAddPerson")
                .param("vorUndNachName", "Jonny")
                .param("strasseHausNr", "strasse 1")
                .param("plzOrt", "12345 ort")
                .param("emailAdresse", tooLongString)
                .param("telefonNr", "12345")
                .param("freeLocation", "Eiserne Hand")
                .param("personenDaten", "true")
        );
        // hier ja eben kein redirect, wegen model und field errors, diese pruefen
        perform.andExpect(content()
                .string(containsString("Bitte maximal 100 Zeichen eingeben.")));
        //die schon eingegebenen Pflichtfelder sind im model und html noch enthalten:
        perform.andExpect(content().string(containsString(tooLongString)));
        perform.andReturn().getModelAndView().getModel().containsValue(tooLongString);
        perform.andExpect(content().string(containsString("12345")));
        perform.andReturn().getModelAndView().getModel().containsValue("12345");
        perform.andExpect(content().string(containsString("Eiserne Hand")));
        perform.andReturn().getModelAndView().getModel().containsValue("Eiserne Hand");
    }

    @DisplayName("teste anmelden, telNr zu lang - NOK")
    @Test
    void testAddPerson14() throws Exception {
        String tooLongString = createLongString(101);
        ResultActions perform = mockMvc.perform(post("/aktionSaubereLandschaftAddPerson")
                .param("vorUndNachName", "Jonny")
                .param("strasseHausNr", "strasse 1")
                .param("plzOrt", "12345 o")
                .param("emailAdresse", "a@b.de")
                .param("telefonNr", tooLongString)
                .param("freeLocation", "Eiserne Hand")
                .param("personenDaten", "true")
        );
        // hier ja eben kein redirect, wegen model und field errors, diese pruefen
        perform.andExpect(content()
                .string(containsString("Bitte maximal 100 Zeichen eingeben.")));
        //die schon eingegebenen Pflichtfelder sind im model und html noch enthalten:
        perform.andExpect(content().string(containsString(tooLongString)));
        perform.andReturn().getModelAndView().getModel().containsValue(tooLongString);
        perform.andExpect(content().string(containsString("a@b.de")));
        perform.andReturn().getModelAndView().getModel().containsValue("a@b.de");
        perform.andExpect(content().string(containsString("Eiserne Hand")));
        perform.andReturn().getModelAndView().getModel().containsValue("Eiserne Hand");
    }

    @DisplayName("teste anmelden, freeLocation zu lang - NOK")
    @Test
    void testAddPerson15() throws Exception {
        String tooLongString = createLongString(101);
        ResultActions perform = mockMvc.perform(post("/aktionSaubereLandschaftAddPerson")
                .param("vorUndNachName", "Jonny")
                .param("strasseHausNr", "strasse 1")
                .param("plzOrt", "12345 o")
                .param("emailAdresse", "a@b.de")
                .param("telefonNr", "12345")
                .param("freeLocation", tooLongString)
                .param("personenDaten", "true")
        );
        // hier ja eben kein redirect, wegen model und field errors, diese pruefen
        perform.andExpect(content()
                .string(containsString("Bitte maximal 100 Zeichen eingeben.")));
        //die schon eingegebenen Pflichtfelder sind im model und html noch enthalten:
        perform.andExpect(content().string(containsString(tooLongString)));
        perform.andReturn().getModelAndView().getModel().containsValue(tooLongString);
        perform.andExpect(content().string(containsString("a@b.de")));
        perform.andReturn().getModelAndView().getModel().containsValue("a@b.de");
    }

    @DisplayName("teste anmelden, weitere Teilnehmer zu lang - NOK")
    @Test
    void testAddPerson16() throws Exception {
        String tooLongString = createLongString(251);
        ResultActions perform = mockMvc.perform(post("/aktionSaubereLandschaftAddPerson")
                .param("vorUndNachName", "Jonny")
                .param("strasseHausNr", "strasse 1")
                .param("plzOrt", "12345 o")
                .param("emailAdresse", "a@b.de")
                .param("telefonNr", "12345")
                .param("freeLocation", "Eiserne Hand")
                .param("personenDaten", "true")
                .param("weitereTeilnehmer", tooLongString)
        );
        // hier ja eben kein redirect, wegen model und field errors, diese pruefen
        perform.andExpect(content()
                .string(containsString("Bitte maximal 250 Zeichen eingeben.")));
        //die schon eingegebenen Pflichtfelder sind im model und html noch enthalten:
        perform.andExpect(content().string(containsString(tooLongString)));
        perform.andReturn().getModelAndView().getModel().containsValue(tooLongString);
        perform.andExpect(content().string(containsString("a@b.de")));
        perform.andReturn().getModelAndView().getModel().containsValue("a@b.de");
        perform.andExpect(content().string(containsString("Eiserne Hand")));
        perform.andReturn().getModelAndView().getModel().containsValue("Eiserne Hand");
    }

    @DisplayName("teste anmelden, weitere Teilnehmer gerade ok - OK")
    @Test
    void testAddPerson17() throws Exception {
        String longString = createLongString(250);
        ResultActions perform = mockMvc.perform(post("/aktionSaubereLandschaftAddPerson")
                .param("vorUndNachName", "Jonny")
                .param("strasseHausNr", "strasse 1")
                .param("plzOrt", "12345 o")
                .param("emailAdresse", "a@b.de")
                .param("telefonNr", "12345")
                .param("freeLocation", "Eiserne Hand")
                .param("personenDaten", "true")
                .param("weitereTeilnehmer", longString)
        );
        // kein fehler
        perform.andExpect(redirectedUrl("/aktionSaubereLandschaft#mitmacher"));
    }

    private String createLongString(int length) {
        return StringUtils.repeat('a', length);
    }

}