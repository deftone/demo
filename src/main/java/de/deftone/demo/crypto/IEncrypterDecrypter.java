package de.deftone.demo.crypto;

public interface IEncrypterDecrypter {

    String encrypt(String strToEncrypt, String secret);
    String decrypt(String strToDecrypt, String secret);
}
