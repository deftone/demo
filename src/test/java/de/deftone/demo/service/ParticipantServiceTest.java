package de.deftone.demo.service;

import de.deftone.demo.crypto.AESCrypto;
import de.deftone.demo.model.Event;
import de.deftone.demo.model.Participant;
import de.deftone.demo.model.ParticipantASL;
import de.deftone.demo.repo.ParticipantASLRepo;
import de.deftone.demo.repo.ParticipantRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ParticipantServiceTest {

    @Mock
    private ParticipantRepo participantRepoMock;

    @Mock
    private ParticipantASLRepo participantASLRepoMock;

    @Mock
    private EventService eventServiceMock;

    @Mock
    private SecretService secretServiceMock;

    private ParticipantService service;

    @BeforeEach
    void setUp() {
        AESCrypto aesCrypto = new AESCrypto();

        when(secretServiceMock.getPassphrase())
                .thenReturn("1$s/53CSv4zJfrTnIyC63+rw$+aVNBR8FzuXSxaN9iJ1E44xhNCucUDXVYOHldPqpc08");

        service = new ParticipantService(participantRepoMock,
                participantASLRepoMock,
                eventServiceMock,
                secretServiceMock,
                aesCrypto);
    }

    @DisplayName("hole von mehreren Teilnehmern nur die, die fuers naechste Event angemeldet sind")
    @Test
    void getParticipantsForNextEvent() {
        Event nextEvent = new Event();
        nextEvent.setDate(LocalDate.now().plusDays(5));
        when(eventServiceMock.getNextEvent()).thenReturn(nextEvent);
        when(participantRepoMock.findAll()).thenReturn(createParticipantsForSeveralEvents());
        List<Participant> allParticipantsForNextEvent = service.getAllParticipantsForNextEvent();
        assertEquals(2, allParticipantsForNextEvent.size());
        assertEquals("Donald", allParticipantsForNextEvent.get(0).getName());
        assertEquals("Daisy", allParticipantsForNextEvent.get(1).getName());
    }

    private List<Participant> createParticipantsForSeveralEvents() {
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

    //----------- ASL Tests ---------------------//

    @DisplayName("teste das loeschen eines existierenden ASL Teilnehmers")
    @Test
    void deleteASLParticipant() {
        ParticipantASL participantASL = createASLParticipant();
        when(participantASLRepoMock.findById(1L)).thenReturn(Optional.of(participantASL));

        assertTrue(service.deleteASLParticipant(1L));
    }

    @DisplayName("teste das loeschen eines nicht existierenden ASL Teilnehmers")
    @Test
    void deleteASLParticipant2() {
        when(participantASLRepoMock.findById(1L)).thenReturn(Optional.empty());

        assertFalse(service.deleteASLParticipant(1L));
    }

    @DisplayName("teste das verschluesseln aller Felder mit Personendaten eines ASL Teilnehmers")
    @Test
    void encodeParticipantASL() {
        ParticipantASL participantASL = createASLParticipant();
        ParticipantASL savedParticipantASL = service.encodeParticipantASL(participantASL);
        checkParticipant(createEndcodedASLParticipant(), savedParticipantASL);
    }

//    @DisplayName("todo: der mock funktioniert nicht und savedParti ist null :(")
//    @Test
//    void addASLPartisipant() {
//        ParticipantASL participantASL = createASLParticipant();
//        ParticipantASL expectedParticipant = createEndcodedASLParticipant();
//        when(participantASLRepoMock.save(expectedParticipant)).thenReturn(expectedParticipant);
//
//        ParticipantASL savedParticipant = service.addParticipantASL(participantASL);
//        checkParticipant(expectedParticipant, savedParticipant);
//    }

    @DisplayName("teste das verschluesseln nur der Pflicht-Felder mit Personendaten eines ASL Teilnehmers")
    @Test
    void encodeParticipantASL2() {
        ParticipantASL participantASL = createASLParticipant();
        participantASL.setWeitereTeilnehmer(null);
        ParticipantASL savedParticipantASL = service.encodeParticipantASL(participantASL);
        ParticipantASL endcodedASLParticipant = createEndcodedASLParticipant();
        endcodedASLParticipant.setWeitereTeilnehmer(null);
        checkParticipant(endcodedASLParticipant, savedParticipantASL);
    }


    @DisplayName("teste das entschluesseln eines ASL Teilnehmers")
    @Test
    void getAllASLParticipantsForNextEvent() {
        Event nextEvent = new Event();
        nextEvent.setDate(LocalDate.now().plusDays(5));

        when(eventServiceMock.getNextEvent()).thenReturn(nextEvent);
        when(participantASLRepoMock.findAll()).thenReturn(createEncodedASLParticipants());

        List<ParticipantASL> allASLParticipantsForNextEvent = service.getAllASLParticipantsForNextEvent();

        assertEquals(1, allASLParticipantsForNextEvent.size());
        ParticipantASL participantAusDBentschluesselt = allASLParticipantsForNextEvent.get(0);
        checkParticipant(createASLParticipant(), participantAusDBentschluesselt);
    }

    @DisplayName("teste das entschluesseln eines ASL Teilnehmers - nur Pflichtfelder")
    @Test
    void getAllASLParticipantsForNextEvent2() {
        Event nextEvent = new Event();
        nextEvent.setDate(LocalDate.now().plusDays(5));

        when(eventServiceMock.getNextEvent()).thenReturn(nextEvent);
        List<ParticipantASL> encodedASLParticipants = createEncodedASLParticipants();
        encodedASLParticipants.get(0).setWeitereTeilnehmer(null);
        when(participantASLRepoMock.findAll()).thenReturn(encodedASLParticipants);

        List<ParticipantASL> allASLParticipantsForNextEvent = service.getAllASLParticipantsForNextEvent();

        assertEquals(1, allASLParticipantsForNextEvent.size());
        ParticipantASL participantAusDBentschluesselt = allASLParticipantsForNextEvent.get(0);

        ParticipantASL aslParticipant = createASLParticipant();
        aslParticipant.setWeitereTeilnehmer(null);
        checkParticipant(aslParticipant, participantAusDBentschluesselt);
    }

    private List<ParticipantASL> createEncodedASLParticipants() {
        List<ParticipantASL> encodedParticipants = new ArrayList<>();
        ParticipantASL encodedParticipantASL = createEndcodedASLParticipant();
        Event nextEvent = new Event(2L, LocalDate.now().plusDays(5));
        encodedParticipantASL.setEvent(nextEvent);
        encodedParticipants.add(encodedParticipantASL);
        return encodedParticipants;
    }

    private ParticipantASL createASLParticipant() {
        ParticipantASL participantASL = new ParticipantASL();
        participantASL.setId(1L);
        participantASL.setVorUndNachName("Jean-Luc Picard");
        participantASL.setEmailAdresse("jean-luc@picard.de");
        participantASL.setStrasseHausNr("Darmstädter Str. 33");
        participantASL.setPlzOrt("64380 Roßdorf");
        participantASL.setWeitereTeilnehmer("Will Riker, Deanna Troi, Mister Data");
        participantASL.setEvent(null);
        participantASL.setLocationName(null);
        participantASL.setAngemeldetAm(null);
        return participantASL;
    }

    private ParticipantASL createEndcodedASLParticipant() {
        ParticipantASL participantASL = new ParticipantASL();
        participantASL.setId(0L);
        participantASL.setVorUndNachName("sefTiRlc/nynGJZbiU5S6Q==");
        participantASL.setEmailAdresse("9LnEQLWxawne8L6MjlTkW5BOEfxNdG4iuld+C6wtkRc=");
        participantASL.setStrasseHausNr("qejxctf3LKYJ+pGlnExfI3laW6fvleUDxj7EFcfszCA=");
        participantASL.setPlzOrt("ibYskQ/8BQd446+IuELmVw==");
        participantASL.setWeitereTeilnehmer("6dY9EKFZ1MgKNEPWThV5P5lU+6C+yea8YfW5ZxJXTR+s09qB/MhrLlLC3gVnEjtV");
        participantASL.setEvent(null);
        participantASL.setLocationName(null);
        participantASL.setAngemeldetAm(null);
        return participantASL;
    }

    private void checkParticipant(ParticipantASL sollParticipant, ParticipantASL istParticipant) {
        assertEquals(sollParticipant.getVorUndNachName(), istParticipant.getVorUndNachName());
        assertEquals(sollParticipant.getStrasseHausNr(), istParticipant.getStrasseHausNr());
        assertEquals(sollParticipant.getPlzOrt(), istParticipant.getPlzOrt());
        assertEquals(sollParticipant.getEmailAdresse(), istParticipant.getEmailAdresse());
        assertEquals(sollParticipant.getWeitereTeilnehmer(), istParticipant.getWeitereTeilnehmer());
    }

}