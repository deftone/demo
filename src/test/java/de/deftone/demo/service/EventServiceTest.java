package de.deftone.demo.service;

import de.deftone.demo.model.Event;
import de.deftone.demo.repo.EventRepo;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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