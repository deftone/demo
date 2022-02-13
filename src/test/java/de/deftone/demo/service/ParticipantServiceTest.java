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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    private final Event veryOldEvent = new Event(0L, LocalDate.now().minusDays(50));
    private final Event oldEvent = new Event(1L, LocalDate.now().minusDays(25));
    private final Event nextEvent = new Event(2L, LocalDate.now().plusDays(5));
    private final Event futureEvent = new Event(3L, LocalDate.now().plusMonths(1));


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
        when(eventServiceMock.getNextEvent()).thenReturn(nextEvent);
        when(participantRepoMock.findAll()).thenReturn(createParticipantsForSeveralEvents());
        List<Participant> allParticipantsForNextEvent = service.getAllParticipantsForNextEvent();
        assertEquals(2, allParticipantsForNextEvent.size());
        assertEquals("Donald", allParticipantsForNextEvent.get(0).getName());
        assertEquals("Daisy", allParticipantsForNextEvent.get(1).getName());
    }

    private List<Participant> createParticipantsForSeveralEvents() {
        List<Participant> list = new ArrayList<>();

        list.add(new Participant(1L, LocalDate.now().minusMonths(1), "Heinz", "egal", veryOldEvent));
        list.add(new Participant(2L, LocalDate.now().minusMonths(1), "Hans", "egal", veryOldEvent));

        list.add(new Participant(3L, LocalDate.now().minusMonths(1), "Hugo", "egal", oldEvent));

        list.add(new Participant(4L, LocalDate.now().minusDays(2), "Donald", "egal", nextEvent));
        list.add(new Participant(5L, LocalDate.now().minusDays(1), "Daisy", "egal", nextEvent));

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

    @DisplayName("teste, dass die passphrase geholt wird und beim 2. mal bekannt ist")
    @Test
    void encodeParticipantASLDoppelt() {
        ParticipantASL participantASL = createASLParticipant();
        ParticipantASL savedParticipantASL = service.encodeParticipantASL(participantASL);
        checkParticipant(createEndcodedASLParticipant(), savedParticipantASL);
        // zweites mal, jetzt ist die Passphrase bekannt
        ParticipantASL savedParticipantASL2 = service.encodeParticipantASL(participantASL);
        checkParticipant(createEndcodedASLParticipant(), savedParticipantASL2);
        // deshalb wird dieser mock nur einmal (beim erstenmal) aufgerufen
        verify(secretServiceMock, times(1)).getPassphrase();
    }

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

    @DisplayName("unzureichender test des hinzufuegens inkl encoden eines neuen Teilnehmers")
    @Test
    void addASLPartisipant() {
        ParticipantASL participantASL = createASLParticipant();

        ParticipantASL expectedParticipant = createEndcodedASLParticipant();
        //leider kann man es hier nicht genauer testen
        when(participantASLRepoMock.save(any(ParticipantASL.class))).thenReturn(expectedParticipant);
        //gut waere:
        //when(participantASLRepoMock.save(expectedParticipant)).thenReturn(expectedParticipant);
        // aber das geht nicht, weil es nicht wirklich die gleichen objekte sind
        // in der methode wird ein neues erzeugt, auch wenn es die selben felder hat
        ParticipantASL savedParticipant = service.addParticipantASL(participantASL);
        checkParticipant(expectedParticipant, savedParticipant);
    }

    @DisplayName("teste das entschluesseln eines ASL Teilnehmers")
    @Test
    void getAllASLParticipantsForNextEvent() {
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
        encodedParticipantASL.setEvent(nextEvent);
        encodedParticipants.add(encodedParticipantASL);
        return encodedParticipants;
    }

    private ParticipantASL createASLParticipant() {
        ParticipantASL participantASL = new ParticipantASL();
        participantASL.setVorUndNachName("Jean-Luc Picard");
        participantASL.setEmailAdresse("jean-luc@picard.de");
        participantASL.setStrasseHausNr("Darmstädter Str. 33");
        participantASL.setPlzOrt("64380 Roßdorf");
        participantASL.setWeitereTeilnehmer("Will Riker, Deanna Troi, Mister Data");
        participantASL.setEvent(nextEvent);
        participantASL.setLocationName("Enterprise NCC 1701-D");
        participantASL.setAngemeldetAm(LocalDate.now());
        participantASL.setFotosMachen(true);
        return participantASL;
    }

    private ParticipantASL createEndcodedASLParticipant() {
        ParticipantASL participantASL = new ParticipantASL();
        participantASL.setId(1L);
        participantASL.setVorUndNachName("sefTiRlc/nynGJZbiU5S6Q==");
        participantASL.setEmailAdresse("9LnEQLWxawne8L6MjlTkW5BOEfxNdG4iuld+C6wtkRc=");
        participantASL.setStrasseHausNr("qejxctf3LKYJ+pGlnExfI3laW6fvleUDxj7EFcfszCA=");
        participantASL.setPlzOrt("ibYskQ/8BQd446+IuELmVw==");
        participantASL.setWeitereTeilnehmer("6dY9EKFZ1MgKNEPWThV5P5lU+6C+yea8YfW5ZxJXTR+s09qB/MhrLlLC3gVnEjtV");
        participantASL.setEvent(nextEvent);
        participantASL.setLocationName("Enterprise NCC 1701-D");
        participantASL.setAngemeldetAm(LocalDate.now());
        participantASL.setFotosMachen(true);
        return participantASL;
    }

    private void checkParticipant(ParticipantASL sollParticipant, ParticipantASL istParticipant) {
        assertEquals(sollParticipant.getVorUndNachName(), istParticipant.getVorUndNachName());
        assertEquals(sollParticipant.getStrasseHausNr(), istParticipant.getStrasseHausNr());
        assertEquals(sollParticipant.getPlzOrt(), istParticipant.getPlzOrt());
        assertEquals(sollParticipant.getEmailAdresse(), istParticipant.getEmailAdresse());
        assertEquals(sollParticipant.getWeitereTeilnehmer(), istParticipant.getWeitereTeilnehmer());
        assertEquals(sollParticipant.getLocationName(), istParticipant.getLocationName());
        assertEquals(sollParticipant.getAngemeldetAm(), istParticipant.getAngemeldetAm());
        assertEquals(sollParticipant.isFotosMachen(), istParticipant.isFotosMachen());
        checkEvent(sollParticipant.getEvent(), istParticipant.getEvent());
    }

    private void checkEvent(Event soll, Event ist) {
        assertEquals(soll.getId(), ist.getId());
        assertEquals(soll.getFormattedDate(), ist.getFormattedDate());
    }

}