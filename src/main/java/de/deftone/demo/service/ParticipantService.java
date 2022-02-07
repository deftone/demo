package de.deftone.demo.service;

import de.deftone.demo.model.Event;
import de.deftone.demo.model.Participant;
import de.deftone.demo.model.ParticipantASL;
import de.deftone.demo.repo.ParticipantASLRepo;
import de.deftone.demo.repo.ParticipantRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ParticipantService {

    private final ParticipantRepo participantRepo;
    private final ParticipantASLRepo participantASLRepo;
    private final EventService eventService;

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
        return participantASLRepo.findAll()
                .stream()
                .filter(p -> p.getEvent().getDate().compareTo(nextEvent.getDate()) == 0)
                .collect(Collectors.toList());
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
        return participantASLRepo.save(participant);
    }
}
