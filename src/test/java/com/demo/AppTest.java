package com.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTest {

    @Test
    public void testGetAppHtml() {
        String html = App.getAppHtml();
        
        // Assert the core UI structure exists
        assertTrue(html.contains("Samiti Management"), "HTML should contain the main Samiti Management title");
        
        // Assert login fields exist
        assertTrue(html.contains("Mobile No:"), "HTML should contain Mobile No login field");
        assertTrue(html.contains("Password:"), "HTML should contain Password login field");
        
        // Assert ledger columns exist
        assertTrue(html.contains("Kist No"), "HTML should contain Kist No column");
        assertTrue(html.contains("Total Jama (Till Now)"), "HTML should contain Total Jama column");
        assertTrue(html.contains("Advance Given"), "HTML should contain Advance column");
        assertTrue(html.contains("Total Bakaya"), "HTML should contain Bakaya column");
        
        // Assert bottom summary text exists
        assertTrue(html.contains("Loans Issued This Month"), "HTML should contain bottom summary for loans issued");
        assertTrue(html.contains("Other Funds (Donations)"), "HTML should contain other funds tracking");
    }
}
