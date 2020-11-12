package de.deftone.demo.service;

import de.deftone.demo.model.Event;
import de.deftone.demo.repo.EventRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// ohne diese Annotation @RunWith ist der eventRepoMock null
// diese Zeile macht den Test NICHT langsam
// es wird nicht die Application gestartet!
@RunWith(SpringRunner.class)
public class EventServiceTest {

    @Mock
    private EventRepo eventRepoMock;

    private EventService service;

    @Before
    public void setUp() {
        service = new EventService(eventRepoMock);
    }

    @Test
    public void addEventSuccess() {
        when(eventRepoMock.save(any()))
                .thenReturn(new Event(1L, LocalDate.of(2020, 12, 1)));
        Event event = service.addEvent("2020-12-01");
        assertEquals(LocalDate.of(2020, 12, 1), event.getDate());
    }

    @Test
    public void addEventWrongDatePattern() {
        when(eventRepoMock.save(any()))
                .thenReturn(new Event(1L, LocalDate.of(2020, 12, 1)));
        try {
            service.addEvent("2020.12.01");
            fail();
        } catch (RuntimeException e) {
            assertEquals("Falsches Datumformat! " +
                    "Bitte Datum so eingeben: YYYY-MM-DD. zB 2021-01-15", e.getMessage());
        }
    }

    @Test
    public void addEventDatumExistiertSchon() {
        when(eventRepoMock.findAll())
                .thenReturn(Collections.singletonList(
                        new Event(1L, LocalDate.of(2020, 12, 1))));
        try {
            service.addEvent("2020-12-01");
            fail();
        } catch (RuntimeException e) {
            assertEquals("Event an diesem Event existiert schon!", e.getMessage());
        }
    }

    @Test
    public void testNextEventOhneHeute() {
        when(eventRepoMock.findAll()).thenReturn(createEventsOhneHeute());
        Event nextEvent = service.getNextEvent();
        assertEquals(LocalDate.now().plusDays(5), nextEvent.getDate());
    }

    @Test
    public void testNextEventMitHeute() {
        when(eventRepoMock.findAll()).thenReturn(createEventsMitHeute());
        Event nextEvent = service.getNextEvent();
        assertEquals(LocalDate.now(), nextEvent.getDate());
    }

    private List<Event> createEventsOhneHeute() {
        List<Event> events = new ArrayList<>();
        Event event1 = new Event(1L, LocalDate.now().minusDays(5));
        Event event2 = new Event(2L, LocalDate.now().minusDays(25));
        Event event4 = new Event(4L, LocalDate.now().plusDays(5));
        Event event5 = new Event(5L, LocalDate.now().plusDays(35));
        Event event6 = new Event(6L, LocalDate.now().plusDays(65));
        events.add(event2);
        events.add(event6);
        events.add(event4);
        events.add(event5);
        events.add(event1);
        return events;
    }

    private List<Event> createEventsMitHeute() {
        List<Event> events = createEventsOhneHeute();
        Event event3 = new Event(3L, LocalDate.now());
        events.add(event3);
        return events;
    }

}