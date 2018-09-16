package de.exlll.jarloader;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represent a downloadable jar file.
 */
public final class JarDependency {
    private final URL downloadUrl;
    private final Path path;
    private final Map<String, String> digestsByAlgorithm;

    /**
     * Constructs a new {@code JarDependency} with the given {@code downloadUrl}
     * and {@code path}.
     *
     * @param downloadUrl location of the jar file
     * @param path        path of target file
     * @throws IllegalArgumentException if the {@code downloadUrl} is malformed
     * @throws NullPointerException     if any argument is null
     */
    public JarDependency(String downloadUrl, Path path) {
        this.downloadUrl = toUrl(downloadUrl);
        this.path = Objects.requireNonNull(path);
        this.digestsByAlgorithm = new HashMap<>();
    }

    private static URL toUrl(String downloadUrl) {
        try {
            return new URL(Objects.requireNonNull(downloadUrl));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Adds a digest to be tested. Digests are tested before a dependency
     * is saved.
     *
     * @param algorithm      the digest algorithm (e.g. MD5, SHA-256)
     * @param expectedDigest the expected digest
     * @return this {@code JarDependency}
     * @throws IllegalArgumentException if the {@code algorithm} is not available
     * @throws NullPointerException     if any argument is null
     */
    public JarDependency addDigest(String algorithm, String expectedDigest) {
        try {
            MessageDigest.getInstance(algorithm);
            digestsByAlgorithm.put(
                    algorithm, Objects.requireNonNull(expectedDigest)
            );
            return this;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    URL getDownloadUrl() {
        return downloadUrl;
    }

    Path getPath() {
        return path;
    }

    Map<String, String> getDigestsByAlgorithm() {
        return digestsByAlgorithm;
    }
}
