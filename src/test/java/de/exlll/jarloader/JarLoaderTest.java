package de.exlll.jarloader;

import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JarLoaderTest {
    private static final String HELLO_WORLD_SHA_512 = "374d794a95cdcfd8b35993185fef9ba368f160d8daf432d08ba9f1ed1e5abe6cc69291e0fa2fe0006a52570ef18c19def4e617c33ce52ef0a6e5fbe318cb0387";
    private static final String HELLO_WORLD_SHA_256 = "dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f";
    private static final String HELLO_WORLD = "Hello, World!";
    private FileSystem fileSystem;
    private Path path;
    private Path resourcePath;
    private String downloadUrl;

    @BeforeEach
    void setUp() throws IOException {
        fileSystem = Jimfs.newFileSystem();
        path = fileSystem.getPath("/a/b/dependency.txt");
        resourcePath = fileSystem.getPath("/a/b/resource.txt");
        Files.createDirectories(resourcePath.getParent());
        Files.write(resourcePath, HELLO_WORLD.getBytes());
        downloadUrl = resourcePath.toUri().toURL().toString();
    }

    @AfterEach
    void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    void downloadDownloadsDependency() throws IOException {
        String downloadUrl = resourcePath.toUri().toURL().toString();
        JarDependency dependency = new JarDependency(downloadUrl, path);
        JarLoader.downloadIfNotExists(dependency);
        String text = new String(Files.readAllBytes(resourcePath));
        assertThat(text, is(HELLO_WORLD));
    }

    @Test
    void downloadChecksDigests() throws IOException {
        JarDependency dependency1 = new JarDependency(downloadUrl, path)
                .addDigest("sha-256", HELLO_WORLD_SHA_256)
                .addDigest("sha-512", HELLO_WORLD_SHA_512);
        JarLoader.downloadIfNotExists(dependency1);

        JarDependency dependency2 = new JarDependency(downloadUrl, path)
                .addDigest("sha-256", HELLO_WORLD_SHA_256)
                .addDigest("sha-512", "abc");

        assertThrows(
                DigestMismatchException.class,
                () -> JarLoader.download(dependency2),
                "Digest mismatch exception:\n" +
                        "Expected: 'abc'\n" +
                        "Actual:   '" + HELLO_WORLD_SHA_512 + "'"
        );
    }

    @Test
    void loadAddsResource() throws IOException {
        JarDependency dependency = new JarDependency(downloadUrl, path);
        JarLoader.downloadIfNotExists(dependency);

        URLClassLoader classLoader = new URLClassLoader(new URL[]{});
        assertThat(classLoader.getURLs().length, is(0));

        JarLoader.load(dependency, classLoader);
        assertThat(classLoader.getURLs().length, is(1));
        assertThat(classLoader.getURLs()[0], is(path.toUri().toURL()));
    }
}