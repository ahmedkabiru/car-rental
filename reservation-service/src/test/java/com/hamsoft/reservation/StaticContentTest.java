package com.hamsoft.reservation;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class StaticContentTest {

    @TestHTTPResource("index.html")
    URL url;

    @Test
    void testStaticContent() throws IOException {
        try (InputStream in = url.openStream()) {
            var contents = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(contents.contains("<title>Testing Guide</title>"));
        }
    }
}
