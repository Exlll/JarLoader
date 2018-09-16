package de.exlll.jarloader;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DigestsTest {
    @Test
    void toHexStringReturnsHexString() {
        Random random = new Random();
        byte[] data = new byte[100];
        for (int i = 0; i < 100; i++) {
            int rnd = random.nextInt(255) - 128;
            data[i] = (byte) rnd;
        }
        String hexString = Digests.toHexString(data);
        assertThat(hexString, is(toHexString(data)));
    }

    @Test
    void testEqualThrowsExceptionIfNoEqual() {
        String digest = "65a8e27d8879283831b664bd8b7f0ad4";
        Digests.testEqual("md5", digest, "Hello, World!".getBytes());

        assertThrows(
                DigestMismatchException.class,
                () -> Digests.testEqual("md5", digest, "Hello, World".getBytes()),
                "Digest mismatch exception:\n" +
                        "Expected: " + "65a8e27d8879283831b664bd8b7f0ad4" + "\n" +
                        "Actual:   " + "82bb413746aee42f89dea2b59614f9ef"
        );
    }

    private static String toHexString(byte[] data) {
        StringBuilder builder = new StringBuilder();
        for (byte datum : data) {
            String hex = String.format("%02x", datum);
            builder.append(hex);
        }
        return builder.toString();
    }
}