
package com.ultrasafe;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

public class CryptoUtils {
    public static final byte[] MAGIC = "USAFE1\0".getBytes(StandardCharsets.US_ASCII);
    private static final int SALT_LEN = 16;
    private static final int IV_LEN = 12;
    private static final int TAG_BITS = 128;
    private static final int ITERATIONS = 200_000; // reasonable default

    public static byte[] encrypt(char[] password, byte[] plaintext) throws Exception {
        byte[] salt = new byte[SALT_LEN];
        byte[] iv = new byte[IV_LEN];
        SecureRandom rnd = new SecureRandom();
        rnd.nextBytes(salt);
        rnd.nextBytes(iv);

        SecretKey key = deriveKey(password, salt, 32); // 256-bit
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
        byte[] cipherText = cipher.doFinal(plaintext);

        ByteBuffer bb = ByteBuffer.allocate(MAGIC.length + 4 + SALT_LEN + IV_LEN + 4 + cipherText.length);
        bb.put(MAGIC);
        bb.putInt(ITERATIONS);
        bb.put(salt);
        bb.put(iv);
        bb.putInt(cipherText.length);
        bb.put(cipherText);
        return bb.array();
    }

    public static byte[] decrypt(char[] password, byte[] blob) throws Exception {
        ByteBuffer bb = ByteBuffer.wrap(blob);
        byte[] magic = new byte[MAGIC.length];
        bb.get(magic);
        if (!Arrays.equals(magic, MAGIC)) throw new IllegalArgumentException("Bad file format");
        int iterations = bb.getInt();
        byte[] salt = new byte[SALT_LEN];
        bb.get(salt);
        byte[] iv = new byte[IV_LEN];
        bb.get(iv);
        int len = bb.getInt();
        byte[] cipherText = new byte[len];
        bb.get(cipherText);

        SecretKey key = deriveKey(password, salt, 32, iterations);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
        return cipher.doFinal(cipherText);
    }

    private static SecretKey deriveKey(char[] password, byte[] salt, int len) throws Exception {
        return deriveKey(password, salt, len, ITERATIONS);
    }
    private static SecretKey deriveKey(char[] password, byte[] salt, int len, int iterations) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, len * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = skf.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }
}
