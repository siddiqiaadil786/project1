package com.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {

    @Test
    public void testGetMessage() {
        App app = new App();
        assertEquals("Hello, Jenkins Pipeline Web Server!", app.getMessage(), "Message should match expected output.");
    }
}
