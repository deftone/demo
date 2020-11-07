package de.deftone.demo.service;

import de.deftone.demo.model.Participant;
import de.deftone.demo.repo.ParticipantRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipantService {

    private final ParticipantRepo participantRepo;

    public ParticipantService(ParticipantRepo participantRepo) {
        this.participantRepo = participantRepo;
    }

    public Participant addParticipant(Participant participant) {
        return participantRepo.save(participant);
    }

    public List<Participant> getAllParticipants() {
        //todo: einschraenkung auf naechsten event
        return participantRepo.findAll();
    }

    public boolean deleteParticipant(long id){
        Optional<Participant> byId = participantRepo.findById(id);
        if (byId.isPresent()){
            participantRepo.delete(byId.get());
            return true;
        }
        return false;
    }
}
