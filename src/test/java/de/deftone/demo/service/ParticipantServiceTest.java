package de.deftone.demo.service;

import de.deftone.demo.model.Event;
import de.deftone.demo.model.Participant;
import de.deftone.demo.repo.ParticipantRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class ParticipantServiceTest {

    @Mock
    private ParticipantRepo participantRepoMock;

    @Mock
    private EventService eventServiceMock;

    private ParticipantService service;

    @Before
    public void setUp() {
        service = new ParticipantService(participantRepoMock, eventServiceMock);
    }

    @Test
    public void getParticipantsForNextEvent() {
        Event nextEvent = new Event();
        nextEvent.setDate(LocalDate.now().plusDays(5));
        when(eventServiceMock.getNextEvent()).thenReturn(nextEvent);
        when(participantRepoMock.findAll()).thenReturn(createParticipants());
        List<Participant> allParticipantsForNextEvent = service.getAllParticipantsForNextEvent();
        assertEquals(2, allParticipantsForNextEvent.size());
        assertEquals("Donald", allParticipantsForNextEvent.get(0).getName());
        assertEquals("Daisy", allParticipantsForNextEvent.get(1).getName());
    }

    private List<Participant> createParticipants() {
        List<Participant> list = new ArrayList<>();
        Event veryOldEvent = new Event(1L, LocalDate.now().minusDays(25));
        list.add(new Participant(1L, LocalDate.now().minusMonths(1), "Heinz", "egal", veryOldEvent));
        list.add(new Participant(2L, LocalDate.now().minusMonths(1), "Hans", "egal", veryOldEvent));
        Event oldEvent = new Event(1L, LocalDate.now().minusDays(25));
        list.add(new Participant(3L, LocalDate.now().minusMonths(1), "Hugo", "egal", oldEvent));
        Event nextEvent = new Event(2L, LocalDate.now().plusDays(5));
        list.add(new Participant(4L, LocalDate.now().minusDays(2), "Donald", "egal", nextEvent));
        list.add(new Participant(5L, LocalDate.now().minusDays(1), "Daisy", "egal", nextEvent));
        Event futureEvent = new Event(3L, LocalDate.now().plusMonths(1));
        list.add(new Participant(6L, LocalDate.now().minusDays(1), "Jonny", "egal", futureEvent));
        return list;
    }

}