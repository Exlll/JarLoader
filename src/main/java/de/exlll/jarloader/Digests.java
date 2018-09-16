package de.exlll.jarloader;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

final class Digests {
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    static void testEqual(String algorithm, String expectedDigest, byte[] data) {
        try {
            MessageDigest instance = MessageDigest.getInstance(algorithm);
            byte[] digest = instance.digest(data);
            String actualDigest = toHexString(digest);

            String expectedLower = expectedDigest.toLowerCase();
            if (!actualDigest.equals(expectedLower)) {
                throw new DigestMismatchException(expectedLower, actualDigest);
            }
        } catch (NoSuchAlgorithmException e) {
            /* Should not happen at this point because JarDependency.addDigest()
             * checks if the algorithm exists. */
            throw new RuntimeException(e);
        }
    }

    static String toHexString(byte[] bytes) {
        char[] hex = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i] & 0xff;
            hex[i * 2] = HEX_CHARS[b >>> 4];
            hex[i * 2 + 1] = HEX_CHARS[b & 0xf];
        }
        return new String(hex);
    }
}
