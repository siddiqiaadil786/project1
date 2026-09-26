package com.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTest {

    @Test
    public void testGetHtml() {
        String html = App.getHtml();
        assertTrue(html.contains("Scientific Calculator"), "HTML should contain the calculator title");
        assertTrue(html.contains("calculate()"), "HTML should contain the JS calculate function");
        assertTrue(html.contains("class=\"calculator\""), "HTML should contain the calculator container");
    }
}
