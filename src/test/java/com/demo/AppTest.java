package com.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTest {

    @Test
    public void testGetScientificHtml() {
        String html = App.getScientificHtml();
        assertTrue(html.contains("Scientific Calculator"), "HTML should contain the scientific calculator title");
        assertTrue(html.contains("href=\"/emicalculator\""), "HTML should contain link to EMI Calculator");
    }

    @Test
    public void testGetEmiHtml() {
        String html = App.getEmiHtml();
        assertTrue(html.contains("EMI Calculator"), "HTML should contain the EMI calculator title");
        assertTrue(html.contains("href=\"/\""), "HTML should contain link to Scientific Calculator");
    }
}
