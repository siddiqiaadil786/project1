package com.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTest {

    @Test
    public void testGetAppHtml() {
        String html = App.getAppHtml();
        
        // Assert the core UI structure exists
        assertTrue(html.contains("Samiti Management"), "HTML should contain the main Samiti Management title");
        
        // Assert ledger columns exactly as CSV
        assertTrue(html.contains("Loan Emi No"), "HTML should contain Loan Emi No column");
        assertTrue(html.contains("Total Cr by each till now"), "HTML should contain Total Cr by each till now column");
        assertTrue(html.contains("This month Share"), "HTML should contain This month Share column");
        assertTrue(html.contains("Total Advance this month (pending dues)"), "HTML should contain pending advance column");
        
        // Assert next month generation feature
        assertTrue(html.contains("Create Next Month Record"), "HTML should contain Next Month button");
        assertTrue(html.contains("generateNextMonth"), "JS function for next month should exist");
    }
}
