package de.deftone.demo.crypto;

import org.apache.commons.codec.digest.DigestUtils;

public class PasswordHasher {
    public String createMD5Hash(String password ) {
        return DigestUtils.md5Hex(password).toUpperCase();
    }

    public String createMD5Hash(String password, String salt ) {
        return createMD5Hash(password + salt);
    }
}
