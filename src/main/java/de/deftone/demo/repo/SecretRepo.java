package de.deftone.demo.repo;

import de.deftone.demo.model.Secret;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecretRepo extends JpaRepository<Secret, Long> {

}
