package de.exlll.jarloader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;

public final class JarLoader {
    /**
     * Downloads a {@code JarDependency} if the file specified by the dependency
     * does not exist. If the file exists exists, the dependency is not downloaded
     * and, therefore, not overridden.
     *
     * @param jarDependency dependency that is downloaded
     * @throws DigestMismatchException if a digest mismatch occurs
     * @throws IOException             if an I/O error occurs
     * @throws NullPointerException    if {@code dependency} is null
     */
    public static void downloadIfNotExists(JarDependency jarDependency)
            throws IOException {
        if (!Files.exists(jarDependency.getPath())) {
            downloadDependency(jarDependency);
        }
    }

    /**
     * Downloads a {@code JarDependency}. If the file specified by the dependency
     * exists, it is overridden.
     *
     * @param jarDependency dependency that is downloaded
     * @throws DigestMismatchException if a digest mismatch occurs
     * @throws IOException             if an I/O error occurs
     * @throws NullPointerException    if {@code dependency} is null
     */
    public static void download(JarDependency jarDependency)
            throws IOException {
        downloadDependency(jarDependency);
    }

    /**
     * Appends the jar specified by the {@code jarDependency} to the list of
     * jars to search for classes and resources.
     *
     * @param jarDependency dependency whose classes should be loaded
     * @param classLoader   classloader which loads the classes
     * @throws NullPointerException if any argument is null
     */
    public static void load(JarDependency jarDependency, URLClassLoader classLoader) {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(classLoader, jarDependency.getPath().toUri().toURL());
        } catch (NoSuchMethodException | IllegalAccessException |
                InvocationTargetException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void downloadDependency(JarDependency jarDependency) throws IOException {
        URL url = jarDependency.getDownloadUrl();
        try (InputStream in = new BufferedInputStream(url.openStream());
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int read;
            byte[] data = new byte[1 << 14];
            while ((read = in.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, read);
            }
            byte[] bytes = buffer.toByteArray();
            checkDigests(jarDependency, bytes);
            Files.write(jarDependency.getPath(), bytes);
        }
    }

    private static void checkDigests(JarDependency jarDependency, byte[] bytes) {
        jarDependency.getDigestsByAlgorithm().forEach(
                (algorithm, digest) -> Digests.testEqual(algorithm, digest, bytes)
        );
    }
}
