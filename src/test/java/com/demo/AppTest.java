package com.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTest {

    @Test
    public void testGetScientificHtml() {
        String html = App.getScientificHtml();
        assertTrue(html.contains("Scientific Calculator"), "HTML should contain the scientific calculator title");
        assertTrue(html.contains("href=\"/emicalculator\""), "HTML should contain link to EMI Calculator");
        assertTrue(html.contains("href=\"/quiz\""), "HTML should contain link to Quiz");
    }

    @Test
    public void testGetEmiHtml() {
        String html = App.getEmiHtml();
        assertTrue(html.contains("EMI Calculator"), "HTML should contain the EMI calculator title");
        assertTrue(html.contains("href=\"/\""), "HTML should contain link to Scientific Calculator");
        assertTrue(html.contains("href=\"/quiz\""), "HTML should contain link to Quiz");
    }

    @Test
    public void testGetQuizHtml() {
        String html = App.getQuizHtml();
        assertTrue(html.contains("Kids Quiz"), "HTML should contain the Quiz title");
        assertTrue(html.contains("submitQuiz()"), "HTML should contain the submit logic");
    }
}
