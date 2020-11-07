package de.deftone.demo.repo;

import de.deftone.demo.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepo extends JpaRepository<Participant, Long> {
}
