package com.demo;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) throws IOException {
        // Start a simple web server on port 8081
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                App app = new App();
                String response = "<h1>" + app.getMessage() + "</h1><p>Successfully deployed via Jenkins and Docker!</p>";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });
        
        System.out.println("Starting web server on port 8081...");
        server.setExecutor(null); 
        server.start();
    }

    public String getMessage() {
        return "Hello, Jenkins Pipeline Web Server!";
    }
}
