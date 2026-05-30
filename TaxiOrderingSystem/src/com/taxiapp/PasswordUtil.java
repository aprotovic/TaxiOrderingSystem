package com.taxiapp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Lightweight password utility using SHA-256 hashing.
 *
 * IMPORTANT: For a real production system, replace this with BCrypt
 * (e.g., org.mindrot:jbcrypt) which includes automatic salting and
 * is resistant to rainbow-table attacks. SHA-256 alone is used here
 * to avoid adding external dependencies while still not storing
 * passwords in plain text.
 */
public final class PasswordUtil {

    private PasswordUtil() {}

    /**
     * Returns the SHA-256 hex string of the given plain-text password.
     * Returns the original string on failure (should never happen on any JVM).
     */
    public static String hash(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(plainText.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is mandated by the Java spec — this will never throw
            return plainText;
        }
    }

    /** Returns true if the plain-text password matches the stored hash. */
    public static boolean verify(String plainText, String storedHash) {
        return hash(plainText).equals(storedHash);
    }
}
