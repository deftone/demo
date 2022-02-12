package de.deftone.demo.crypto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AESCryptoTest {
    IEncrypterDecrypter encrypterDecrypter = new AESCrypto();

    @Test
    void shouldEncryptAndDecryptToSameString() {
        Assertions.assertEquals("Hallo",
                encrypterDecrypter.decrypt(
                        encrypterDecrypter.encrypt("Hallo", "Geheimnis"), "Geheimnis"));
    }

    @Test
    void shouldEncryptAndDecryptToSameStringWithHashedSecret() {
        String hashedSecret = new PasswordHasher().createMD5Hash("Passwort");
        System.out.println("Passwort-Hash: " + hashedSecret);
        Assertions.assertEquals("Hallo",
                encrypterDecrypter.decrypt(
                        encrypterDecrypter.encrypt("Hallo", hashedSecret), hashedSecret));
    }

    @Test
    void encryptedStringShouldBeDifferent() {
        String unverschluesselt = "Wichtiger String";
        String verschluesselt = encrypterDecrypter.encrypt(unverschluesselt, "Geheimnis");

        Assertions.assertNotEquals(unverschluesselt, verschluesselt);
        Assertions.assertNotNull(verschluesselt);
        Assertions.assertNotEquals("", verschluesselt);
    }

    @Test
    void encryptedStringsWithDifferentSecretsShouldBeDifferent() {
        String unverschluesselt = "Wichtiger String";
        String verschluesselt1 = encrypterDecrypter.encrypt(unverschluesselt, "Geheimnis1");
        String verschluesselt2 = encrypterDecrypter.encrypt(unverschluesselt, "Geheimnis2");

        System.out.println(unverschluesselt);
        System.out.println(verschluesselt1);
        System.out.println(verschluesselt2);

        Assertions.assertNotEquals(verschluesselt1, verschluesselt2);
        Assertions.assertNotNull(verschluesselt1);
        Assertions.assertNotEquals("", verschluesselt1);
        Assertions.assertNotNull(verschluesselt2);
        Assertions.assertNotEquals("", verschluesselt2);
    }
}