package de.deftone.demo.repo;

import de.deftone.demo.model.Wort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WortRepo extends JpaRepository<Wort, Long> {
}
