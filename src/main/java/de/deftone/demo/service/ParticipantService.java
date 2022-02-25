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
    private final SecretService secretService;
    private final AESCrypto aesCrypto;
    private String passphrase;

    public ParticipantService(ParticipantRepo participantRepo,
                              ParticipantASLRepo participantASLRepo,
                              EventService eventService,
                              SecretService secretService,
                              AESCrypto aesCrypto) {
        this.participantRepo = participantRepo;
        this.participantASLRepo = participantASLRepo;
        this.eventService = eventService;
        this.secretService = secretService;
        this.aesCrypto = aesCrypto;
    }

    private String getPassphrase() {
        if (passphrase == null) {
            this.passphrase = secretService.getPassphrase();
            return passphrase;
        }
        return passphrase;
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
        String passphrase = getPassphrase();
        List<ParticipantASL> encodedParticipants = participantASLRepo.findAll()
                .stream()
                .filter(p -> p.getEvent().getDate().compareTo(nextEvent.getDate()) == 0)
                .collect(Collectors.toList());
        for (ParticipantASL participantASL : encodedParticipants) {
            participantASL.setVorUndNachName(aesCrypto.decrypt(participantASL.getVorUndNachName(), passphrase));
            participantASL.setStrasseHausNr(aesCrypto.decrypt(participantASL.getStrasseHausNr(), passphrase));
            participantASL.setPlzOrt(aesCrypto.decrypt(participantASL.getPlzOrt(), passphrase));
            participantASL.setEmailAdresse(aesCrypto.decrypt(participantASL.getEmailAdresse(), passphrase));
            participantASL.setTelefonNr(aesCrypto.decrypt(participantASL.getTelefonNr(), passphrase));
            if (participantASL.getWeitereTeilnehmer() != null) {
                participantASL.setWeitereTeilnehmer(aesCrypto.decrypt(participantASL.getWeitereTeilnehmer(), passphrase));
            }
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
        ParticipantASL encodedParticipantASL = encodeParticipantASL(participant);
        return participantASLRepo.save(encodedParticipantASL);
    }

    ParticipantASL encodeParticipantASL(ParticipantASL participant) {
        String passphrase = getPassphrase();
        ParticipantASL verschluesselterParticipant = new ParticipantASL();
        verschluesselterParticipant.setVorUndNachName(aesCrypto.encrypt(participant.getVorUndNachName(), passphrase));
        verschluesselterParticipant.setStrasseHausNr(aesCrypto.encrypt(participant.getStrasseHausNr(), passphrase));
        verschluesselterParticipant.setPlzOrt(aesCrypto.encrypt(participant.getPlzOrt(), passphrase));
        verschluesselterParticipant.setEmailAdresse(aesCrypto.encrypt(participant.getEmailAdresse(), passphrase));
        verschluesselterParticipant.setTelefonNr(aesCrypto.encrypt(participant.getTelefonNr(), passphrase));
        if (participant.getWeitereTeilnehmer() != null && !participant.getWeitereTeilnehmer().isEmpty()) {
            verschluesselterParticipant.setWeitereTeilnehmer(aesCrypto.encrypt(participant.getWeitereTeilnehmer(), passphrase));
        }
        //der Rest ist nicht verschluesselt:
        verschluesselterParticipant.setAngemeldetAm(participant.getAngemeldetAm());
        verschluesselterParticipant.setEvent(participant.getEvent());
        verschluesselterParticipant.setLocationName(participant.getLocationName());
        verschluesselterParticipant.setFotosMachen(participant.isFotosMachen());
        return verschluesselterParticipant;
    }

    public boolean deletePersonenDataFromASLParticipant(long id) {
        Optional<ParticipantASL> byId = participantASLRepo.findById(id);
        if (byId.isPresent()) {
            ParticipantASL participantASL = byId.get();
            participantASL.setVorUndNachName(null);
            participantASL.setStrasseHausNr(null);
            participantASL.setEmailAdresse(null);
            participantASL.setTelefonNr(null);
            participantASL.setWeitereTeilnehmer(null);
            participantASLRepo.save(participantASL);
            return true;
        }
        return false;
    }
}