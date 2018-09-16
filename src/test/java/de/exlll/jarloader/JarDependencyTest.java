package de.exlll.jarloader;

import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Map;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JarDependencyTest {
    private static final String DOWNLOAD_URL = "http://a@b.c";
    private FileSystem fileSystem;
    private Path path;

    @BeforeEach
    void setUp() {
        fileSystem = Jimfs.newFileSystem();
        path = fileSystem.getPath("/a/b/dependency.jar");
    }

    @AfterEach
    void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    void builderFactoryRequiresNonNullArguments() {
        assertThrows(
                NullPointerException.class,
                () -> new JarDependency(null, path)
        );
        assertThrows(
                NullPointerException.class,
                () -> new JarDependency(DOWNLOAD_URL, null)
        );
    }

    @Test
    void builderFactoryRequiresValidUrl() {
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    try {
                        new JarDependency("xyz", path);
                    } catch (IllegalArgumentException e) {
                        Class<?> causeCls = MalformedURLException.class;
                        assertThat(e.getCause(), instanceOf(causeCls));
                        throw e;
                    }
                }
        );
    }

    @Test
    void builderAddsDigests() {
        JarDependency jarDependency = new JarDependency(DOWNLOAD_URL, path)
                .addDigest("SHA-256", "1")
                .addDigest("SHA-512", "2");
        Map<String, String> map = jarDependency.getDigestsByAlgorithm();
        assertThat(map.get("SHA-256"), is("1"));
        assertThat(map.get("SHA-512"), is("2"));
    }

    @Test
    void addDigestRequiresNonNullArguments() {
        JarDependency jarDependency = new JarDependency(DOWNLOAD_URL, path);
        assertThrows(
                NullPointerException.class,
                () -> jarDependency.addDigest(null, "")
        );
        assertThrows(
                NullPointerException.class,
                () -> jarDependency.addDigest("md5", null)
        );
    }

    @Test
    void builderRequiresValidDigestAlgorithm() {
        JarDependency jarDependency = new JarDependency(DOWNLOAD_URL, path);
        assertThrows(
                IllegalArgumentException.class,
                () -> jarDependency.addDigest("", "")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> jarDependency.addDigest("abc", "")
        );
    }
}