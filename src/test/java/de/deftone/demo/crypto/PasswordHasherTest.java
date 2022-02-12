package de.deftone.demo.crypto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PasswordHasherTest {

    @Test
    void testPasswordIsHashed() {
        String pwd = "GeheimesPasswort";
        String hash = new PasswordHasher().createMD5Hash(pwd);
        Assertions.assertNotNull(hash);
        Assertions.assertNotEquals("", hash);
        Assertions.assertNotEquals(pwd, hash);
    }

    @Test
    void testSameHashForSamePwd() {
        String hash1 = new PasswordHasher().createMD5Hash("GeheimesPasswort");
        String hash2 = new PasswordHasher().createMD5Hash("GeheimesPasswort");

        System.out.println(hash1);
        System.out.println(hash2);

        Assertions.assertNotNull(hash1);
        Assertions.assertNotNull(hash2);
        Assertions.assertNotEquals("", hash1);
        Assertions.assertNotEquals("", hash2);
        Assertions.assertEquals(hash1, hash2);
    }

    @Test
    void testDifferentHashForDifferentPwd() {
        String hash1 = new PasswordHasher().createMD5Hash("GeheimesPasswort");
        String hash2 = new PasswordHasher().createMD5Hash("NochGeheimeresPasswort");

        System.out.println(hash1);
        System.out.println(hash2);

        Assertions.assertNotNull(hash1);
        Assertions.assertNotNull(hash2);
        Assertions.assertNotEquals("", hash1);
        Assertions.assertNotEquals("", hash2);
        Assertions.assertNotEquals(hash1, hash2);
    }

    @Test
    void testDifferentHashForDifferentSalt() {
        String pwd = "GeheimesPasswort";
        String hash1 = new PasswordHasher().createMD5Hash(pwd, "Salz");
        String hash2 = new PasswordHasher().createMD5Hash(pwd, "Pfeffer");
        Assertions.assertNotEquals(hash1, hash2);
    }
}