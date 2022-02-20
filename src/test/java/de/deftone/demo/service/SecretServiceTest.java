package de.deftone.demo.service;

import de.deftone.demo.model.Secret;
import de.deftone.demo.repo.SecretRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
//fuegt diesen Encoder als bean dem appl. context hinzu:
@Import(Argon2PasswordEncoder.class)
class SecretServiceTest {

    @Mock
    private SecretRepo secretRepoMock;

    @Autowired
    private Argon2PasswordEncoder passwordEncoder;

    private SecretService secretService;

    private final Secret secret = new Secret();
    private final Secret secretUnverschl = new Secret();

    @BeforeEach
    void setUp() {
        secretService = new SecretService(secretRepoMock, passwordEncoder);
        secret.setId(33L);
        secret.setName("geheim");
        secretUnverschl.setId(33L);
        secretUnverschl.setName("geheim");
    }

    @DisplayName("teste ob verschluesseln klappt")
    @Test
    void verschluesselSecret() {
        Secret secret = secretService.verschluesselSecret(this.secret);
        assertEquals(33L, secret.getId());
        //den p=Teil kann man leider nicht pruefen, da dieser immer anders ist
        assertThat(secret.getName()).contains("$argon2id$v=19$m=4096,t=3,p=");
    }

    @DisplayName("secret wird nicht in DB gefunden")
    @Test
    void checkSecretNOK() {
        when(secretRepoMock.findById(secret.getId())).thenReturn(Optional.empty());
        assertFalse(secretService.checkSecret(this.secret));
    }

    @DisplayName("secret wird in DB gefunden")
    @Test
    void checkSecretOK() {
        //nach diesem Aufruf ist das secret verschluesselt, daher doppelt anlegen zum Testen
        Secret verschluesseltesSecret = secretService.verschluesselSecret(secret);
        when(secretRepoMock.findById(secretUnverschl.getId())).thenReturn(Optional.of(verschluesseltesSecret));
        assertTrue(secretService.checkSecret(this.secretUnverschl));
    }

    @DisplayName("keine Passphrase in der DB")
    @Test
    void getPassphraseNOK() {
        when(secretRepoMock.findById(1L)).thenReturn(Optional.empty());
        assertThat(secretService.getPassphrase()).isNull();
    }

    @DisplayName("Passphrase in der DB wird gefunden")
    @Test
    void getPassphraseOK() {
        Secret verschluesseltesSecret = secretService.verschluesselSecret(secret);
        when(secretRepoMock.findById(1L)).thenReturn(Optional.of(verschluesseltesSecret));
        assertThat(secretService.getPassphrase()).isEqualTo(secret.getName());
    }
}