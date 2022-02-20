package de.deftone.demo.service;

import de.deftone.demo.model.Secret;
import de.deftone.demo.repo.SecretRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecretService {

    private final SecretRepo secretRepo;
    private final PasswordEncoder passwordEncoder;


    public List<Secret> getAllSecrets() {
        return secretRepo.findAll();
    }

    public Secret addSecret(Secret secret) {
        return secretRepo.save(verschluesselSecret(secret));
    }

    Secret verschluesselSecret(Secret secret) {
        String wordVerschluesselt = passwordEncoder.encode(secret.getName());
        secret.setName(wordVerschluesselt);
        return secret;
    }

    public void deleteSecret(long id) {
        Optional<Secret> byId = secretRepo.findById(id);
        byId.ifPresent(secretRepo::delete);
    }

    public boolean checkSecret(Secret secret) {
        Optional<Secret> byId = secretRepo.findById(secret.getId());
        if (byId.isEmpty()) {
            return false;
        }
        Secret secretFromDB = byId.get();
        return passwordEncoder.matches(secret.getName(), secretFromDB.getName());
    }

    public String getPassphrase() {
        // die passphrase wurde unter id = 1 gespeichert
        // d.h. es ist IMMER der selbe, verschluesselte name vom secret
        Optional<Secret> byId = secretRepo.findById(1L);
        return byId.map(Secret::getName).orElse(null);
    }
}
