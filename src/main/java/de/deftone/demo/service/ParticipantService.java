package de.deftone.demo.service;

import de.deftone.demo.crypto.AESCrypto;
import de.deftone.demo.model.Event;
import de.deftone.demo.model.Participant;
import de.deftone.demo.model.ParticipantASL;
import de.deftone.demo.repo.ParticipantASLRepo;
import de.deftone.demo.repo.ParticipantRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ParticipantService {

    private final ParticipantRepo participantRepo;
    private final ParticipantASLRepo participantASLRepo;
    private final EventService eventService;
    private final AESCrypto aesCrypto;
    private final String passphrase;

    public ParticipantService(ParticipantRepo participantRepo,
                              ParticipantASLRepo participantASLRepo,
                              EventService eventService,
                              SecretService secretService,
                              AESCrypto aesCrypto) {
        this.participantRepo = participantRepo;
        this.participantASLRepo = participantASLRepo;
        this.eventService = eventService;
        this.aesCrypto = aesCrypto;
        this.passphrase = secretService.getPassphrase();
    }

    public Participant addParticipant(Participant participant) {
        return participantRepo.save(participant);
    }

    public List<Participant> getAllParticipantsForNextEvent() {
        Event nextEvent = eventService.getNextEvent();
        return participantRepo.findAll()
                .stream()
                .filter(p -> p.getEvent().getDate().compareTo(nextEvent.getDate()) == 0)
                .collect(Collectors.toList());
    }

    public List<ParticipantASL> getAllASLParticipantsForNextEvent() {
        Event nextEvent = eventService.getNextEvent();
        List<ParticipantASL> encodedParticipants = participantASLRepo.findAll()
                .stream()
                .filter(p -> p.getEvent().getDate().compareTo(nextEvent.getDate()) == 0)
                .collect(Collectors.toList());
        for (ParticipantASL participantASL : encodedParticipants) {
            participantASL.setVorUndNachName(aesCrypto.decrypt(participantASL.getVorUndNachName(), passphrase));
            participantASL.setStrasseHausNr(aesCrypto.decrypt(participantASL.getStrasseHausNr(), passphrase));
            participantASL.setPlzOrt(aesCrypto.decrypt(participantASL.getPlzOrt(), passphrase));
            participantASL.setEmailAdresse(aesCrypto.decrypt(participantASL.getEmailAdresse(), passphrase));
            participantASL.setWeitereTeilnehmer(aesCrypto.decrypt(participantASL.getWeitereTeilnehmer(), passphrase));
        }
        return encodedParticipants;
    }

    public boolean deleteParticipant(long id) {
        Optional<Participant> byId = participantRepo.findById(id);
        if (byId.isPresent()) {
            participantRepo.delete(byId.get());
            return true;
        }
        return false;
    }

    public boolean deleteASLParticipant(long id) {
        Optional<ParticipantASL> byId = participantASLRepo.findById(id);
        if (byId.isPresent()) {
            participantASLRepo.delete(byId.get());
            return true;
        }
        return false;
    }

    public ParticipantASL addParticipantASL(ParticipantASL participant) {
        ParticipantASL participantASL = encodeParticipantASL(participant);
        return participantASLRepo.save(participantASL);
    }

    ParticipantASL encodeParticipantASL(ParticipantASL participant) {
        ParticipantASL verschluesselterParticipant = new ParticipantASL();
        verschluesselterParticipant.setVorUndNachName(aesCrypto.encrypt(participant.getVorUndNachName(), passphrase));
        verschluesselterParticipant.setStrasseHausNr(aesCrypto.encrypt(participant.getStrasseHausNr(), passphrase));
        verschluesselterParticipant.setPlzOrt(aesCrypto.encrypt(participant.getPlzOrt(), passphrase));
        verschluesselterParticipant.setEmailAdresse(aesCrypto.encrypt(participant.getEmailAdresse(), passphrase));
        if (participant.getWeitereTeilnehmer() != null && !participant.getWeitereTeilnehmer().isEmpty()) {
            verschluesselterParticipant.setWeitereTeilnehmer(aesCrypto.encrypt(participant.getWeitereTeilnehmer(), passphrase));
        }
        return verschluesselterParticipant;
    }


}
