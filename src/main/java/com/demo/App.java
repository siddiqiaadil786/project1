package com.demo;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        
        // 1. Scientific Calculator Endpoint ("/")
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if (!exchange.getRequestURI().getPath().equals("/")) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }
                sendResponse(exchange, getScientificHtml());
            }
        });

        // 2. EMI Calculator Endpoint ("/emicalculator")
        server.createContext("/emicalculator", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                sendResponse(exchange, getEmiHtml());
            }
        });

        // 3. Kids Quiz Endpoint ("/quiz")
        server.createContext("/quiz", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                sendResponse(exchange, getQuizHtml());
            }
        });
        
        System.out.println("Starting Multi-App Server on port 8081...");
        server.setExecutor(null); 
        server.start();
    }

    private static void sendResponse(HttpExchange exchange, String html) throws IOException {
        byte[] bytes = html.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private static String getNavHtml(String activePage) {
        String sciClass = activePage.equals("sci") ? "class=\"active\"" : "";
        String emiClass = activePage.equals("emi") ? "class=\"active\"" : "";
        String quizClass = activePage.equals("quiz") ? "class=\"active\"" : "";
        
        return "<div class=\"nav\">\n" +
               "    <a href=\"/\" " + sciClass + ">Scientific Calculator</a>\n" +
               "    <a href=\"/emicalculator\" " + emiClass + ">EMI Calculator</a>\n" +
               "    <a href=\"/quiz\" " + quizClass + ">Kids Quiz 🌟</a>\n" +
               "</div>";
    }

    public static String getScientificHtml() {
        return "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Scientific Calculator</title>" +
               "<style>" +
               "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; display: flex; flex-direction: column; align-items: center; background-color: #2c3e50; margin: 0; padding-top: 30px; }" +
               ".nav { margin-bottom: 30px; display: flex; gap: 10px; justify-content: center; flex-wrap: wrap; }" +
               ".nav a { color: #ecf0f1; text-decoration: none; padding: 12px 20px; background: #34495e; border-radius: 5px; border: 1px solid #7f8c8d; font-weight: bold; transition: 0.2s; }" +
               ".nav a:hover { background: #7f8c8d; }" +
               ".nav a.active { background: #e67e22; border: none; }" +
               ".calculator { background: #34495e; padding: 25px; border-radius: 15px; box-shadow: 0px 10px 30px rgba(0,0,0,0.5); width: 400px; max-width: 90%; }" +
               ".display { background: #ecf0f1; color: #2c3e50; font-size: 2.5em; text-align: right; padding: 15px; border-radius: 8px; margin-bottom: 25px; min-height: 50px; font-family: monospace; overflow-x: auto; white-space: nowrap; }" +
               ".buttons { display: grid; grid-template-columns: repeat(5, 1fr); gap: 12px; }" +
               "button { padding: 18px 10px; font-size: 1.2em; cursor: pointer; border: none; border-radius: 8px; background: #7f8c8d; color: white; transition: 0.2s; font-weight: bold; }" +
               "button:hover { background: #95a5a6; transform: translateY(-2px); }" +
               ".btn-operator { background: #e67e22; }.btn-operator:hover { background: #d35400; }" +
               ".btn-equal { background: #27ae60; grid-column: span 2; }.btn-equal:hover { background: #2ecc71; }" +
               ".btn-clear { background: #e74c3c; }.btn-clear:hover { background: #c0392b; }" +
               "</style></head><body>" +
               getNavHtml("sci") +
               "<div class=\"calculator\"><div class=\"display\" id=\"display\">0</div><div class=\"buttons\">" +
               "<button onclick=\"append('sin(')\">sin</button><button onclick=\"append('cos(')\">cos</button><button onclick=\"append('tan(')\">tan</button><button class=\"btn-clear\" onclick=\"clearDisplay()\">AC</button><button class=\"btn-clear\" onclick=\"backspace()\">⌫</button>" +
               "<button onclick=\"append('log(')\">log</button><button onclick=\"append('sqrt(')\">√</button><button onclick=\"append('(')\">(</button><button onclick=\"append(')')\">)</button><button class=\"btn-operator\" onclick=\"append('÷')\">÷</button>" +
               "<button onclick=\"append('7')\">7</button><button onclick=\"append('8')\">8</button><button onclick=\"append('9')\">9</button><button onclick=\"append('^')\">^</button><button class=\"btn-operator\" onclick=\"append('×')\">×</button>" +
               "<button onclick=\"append('4')\">4</button><button onclick=\"append('5')\">5</button><button onclick=\"append('6')\">6</button><button onclick=\"append('π')\">π</button><button class=\"btn-operator\" onclick=\"append('-')\">-</button>" +
               "<button onclick=\"append('1')\">1</button><button onclick=\"append('2')\">2</button><button onclick=\"append('3')\">3</button><button onclick=\"append('e')\">e</button><button class=\"btn-operator\" onclick=\"append('+')\">+</button>" +
               "<button onclick=\"append('0')\">0</button><button onclick=\"append('.')\">.</button><button class=\"btn-equal\" onclick=\"calculate()\">=</button>" +
               "</div></div>" +
               "<script>" +
               "let display = document.getElementById('display');" +
               "function append(val) { if(display.innerText === '0' || display.innerText === 'Error') display.innerText = val; else display.innerText += val; }" +
               "function clearDisplay() { display.innerText = '0'; }" +
               "function backspace() { if(display.innerText.length > 1 && display.innerText !== 'Error') display.innerText = display.innerText.slice(0, -1); else clearDisplay(); }" +
               "function calculate() { try { let expr = display.innerText.replace(/sin\\(/g, 'Math.sin(').replace(/cos\\(/g, 'Math.cos(').replace(/tan\\(/g, 'Math.tan(').replace(/log\\(/g, 'Math.log10(').replace(/sqrt\\(/g, 'Math.sqrt(').replace(/π/g, 'Math.PI').replace(/e/g, 'Math.E').replace(/÷/g, '/').replace(/×/g, '*').replace(/\\^/g, '**'); let result = eval(expr); if(result === undefined) throw new Error(); display.innerText = Math.round(result * 10000000000) / 10000000000; } catch (e) { display.innerText = 'Error'; } }" +
               "</script></body></html>";
    }

    public static String getEmiHtml() {
        return "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>EMI Calculator</title>" +
               "<style>" +
               "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; display: flex; flex-direction: column; align-items: center; background-color: #2c3e50; margin: 0; padding-top: 30px; color: white; }" +
               ".nav { margin-bottom: 30px; display: flex; gap: 10px; justify-content: center; flex-wrap: wrap; }" +
               ".nav a { color: #ecf0f1; text-decoration: none; padding: 12px 20px; background: #34495e; border-radius: 5px; border: 1px solid #7f8c8d; font-weight: bold; transition: 0.2s; }" +
               ".nav a:hover { background: #7f8c8d; }" +
               ".nav a.active { background: #e67e22; border: none; }" +
               ".calculator { background: #34495e; padding: 30px; border-radius: 15px; box-shadow: 0px 10px 30px rgba(0,0,0,0.5); width: 400px; max-width: 90%; box-sizing: border-box; }" +
               "h2 { text-align: center; margin-top: 0; color: #ecf0f1; margin-bottom: 25px; }" +
               ".form-group { margin-bottom: 20px; }" +
               "label { display: block; margin-bottom: 8px; font-weight: bold; color: #bdc3c7; }" +
               "input { width: 100%; padding: 12px; border-radius: 8px; border: none; box-sizing: border-box; font-size: 1.1em; background: #ecf0f1; color: #2c3e50; }" +
               "button { width: 100%; padding: 15px; font-size: 1.2em; cursor: pointer; border: none; border-radius: 8px; background: #27ae60; color: white; font-weight: bold; transition: 0.2s; }" +
               "button:hover { background: #2ecc71; transform: translateY(-2px); }" +
               ".result { margin-top: 25px; padding: 20px; background: #ecf0f1; color: #2c3e50; border-radius: 8px; text-align: center; font-size: 1.5em; display: none; }" +
               "</style></head><body>" +
               getNavHtml("emi") +
               "<div class=\"calculator\"><h2>EMI Calculator</h2>" +
               "<div class=\"form-group\"><label>Loan Amount (Principal)</label><input type=\"number\" id=\"principal\" placeholder=\"e.g. 500000\"></div>" +
               "<div class=\"form-group\"><label>Interest Rate (Annual %)</label><input type=\"number\" id=\"rate\" placeholder=\"e.g. 8.5\" step=\"0.1\"></div>" +
               "<div class=\"form-group\"><label>Loan Tenure (Years)</label><input type=\"number\" id=\"tenure\" placeholder=\"e.g. 5\"></div>" +
               "<button onclick=\"calculateEMI()\">Calculate EMI</button><div class=\"result\" id=\"result\"></div>" +
               "</div>" +
               "<script>" +
               "function calculateEMI() { let p = parseFloat(document.getElementById('principal').value); let annualRate = parseFloat(document.getElementById('rate').value); let years = parseFloat(document.getElementById('tenure').value); let resDiv = document.getElementById('result'); if (isNaN(p) || isNaN(annualRate) || isNaN(years) || p <= 0 || annualRate <= 0 || years <= 0) { resDiv.style.display = 'block'; resDiv.innerHTML = \"<span style='color:#c0392b;font-size:0.7em;'>Please enter valid numbers!</span>\"; return; } let r = annualRate / 12 / 100; let n = years * 12; let emi = (p * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1); let totalAmount = emi * n; resDiv.style.display = 'block'; resDiv.innerHTML = \"Monthly EMI:<br><strong style='color:#27ae60;font-size:1.3em;'>₹\" + Math.round(emi).toLocaleString() + \"</strong><br><span style='font-size:0.6em;color:#7f8c8d;display:block;margin-top:10px;'>Total Interest: ₹\" + Math.round(totalAmount - p).toLocaleString() + \"</span>\"; }" +
               "</script></body></html>";
    }

    public static String getQuizHtml() {
        return "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Kids Quiz</title>" +
               "<style>" +
               "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; display: flex; flex-direction: column; align-items: center; background-color: #2c3e50; margin: 0; padding-top: 30px; color: white; }" +
               ".nav { margin-bottom: 30px; display: flex; gap: 10px; justify-content: center; flex-wrap: wrap; }" +
               ".nav a { color: #ecf0f1; text-decoration: none; padding: 12px 20px; background: #34495e; border-radius: 5px; border: 1px solid #7f8c8d; font-weight: bold; transition: 0.2s; }" +
               ".nav a:hover { background: #7f8c8d; }" +
               ".nav a.active { background: #e67e22; border: none; }" +
               ".quiz-container { background: #34495e; padding: 30px; border-radius: 15px; box-shadow: 0px 10px 30px rgba(0,0,0,0.5); width: 600px; max-width: 90%; margin-bottom: 50px; }" +
               "h2 { text-align: center; color: #ecf0f1; margin-top: 0; }" +
               ".question-block { margin-bottom: 25px; background: #2c3e50; padding: 15px; border-radius: 8px; border-left: 5px solid #f1c40f; }" +
               ".question-title { font-size: 1.2em; font-weight: bold; margin-bottom: 15px; color: #f1c40f; }" +
               ".options label { display: block; background: #465c71; padding: 12px; margin-bottom: 8px; border-radius: 5px; cursor: pointer; transition: 0.2s; font-size: 1.1em; }" +
               ".options label:hover { background: #5c7791; }" +
               "input[type=\"radio\"] { margin-right: 15px; transform: scale(1.2); }" +
               "button { width: 100%; padding: 15px; font-size: 1.3em; cursor: pointer; border: none; border-radius: 8px; background: #27ae60; color: white; font-weight: bold; margin-top: 10px; transition: 0.2s; }" +
               "button:hover { background: #2ecc71; transform: translateY(-2px); }" +
               "#result-screen { display: none; text-align: center; padding: 40px 0; }" +
               ".score { font-size: 4em; color: #2ecc71; font-weight: bold; margin: 20px 0; }" +
               "</style></head><body>" +
               getNavHtml("quiz") +
               "<div class=\"quiz-container\" id=\"quiz-container\">" +
               "<h2>Fun Quiz for Kids! 🌟</h2>" +
               "<div id=\"questions-list\"></div>" +
               "<button onclick=\"submitQuiz()\">Submit Quiz</button>" +
               "</div>" +
               "<div class=\"quiz-container\" id=\"result-screen\">" +
               "<h2>Quiz Complete! 🎉</h2><div style=\"font-size:1.5em\">You scored:</div>" +
               "<div class=\"score\"><span id=\"score-val\">0</span> / 10</div>" +
               "<button onclick=\"location.reload()\">Play Again!</button>" +
               "</div>" +
               "<script>" +
               "const questions = [" +
               "{ q: \"Math: What is 5 + 7?\", opts: [\"10\", \"12\", \"15\", \"7\"], ans: 1 }," +
               "{ q: \"Science: Which planet is known as the Red Planet?\", opts: [\"Venus\", \"Mars\", \"Jupiter\", \"Saturn\"], ans: 1 }," +
               "{ q: \"English: What is the opposite of 'Hot'?\", opts: [\"Warm\", \"Sun\", \"Cold\", \"Ice\"], ans: 2 }," +
               "{ q: \"Math: What is 10 × 3?\", opts: [\"30\", \"13\", \"103\", \"33\"], ans: 0 }," +
               "{ q: \"Science: What do plants need to grow?\", opts: [\"Juice\", \"Soda\", \"Water\", \"Milk\"], ans: 2 }," +
               "{ q: \"English: Which of these is an animal?\", opts: [\"Table\", \"Apple\", \"Dog\", \"Car\"], ans: 2 }," +
               "{ q: \"Math: How many sides does a triangle have?\", opts: [\"2\", \"3\", \"4\", \"5\"], ans: 1 }," +
               "{ q: \"Science: What falls from clouds when it storms?\", opts: [\"Rocks\", \"Rain\", \"Leaves\", \"Dirt\"], ans: 1 }," +
               "{ q: \"English: Choose the correct spelling:\", opts: [\"Kat\", \"Catt\", \"Ket\", \"Cat\"], ans: 3 }," +
               "{ q: \"Math: What is 20 - 8?\", opts: [\"10\", \"11\", \"12\", \"14\"], ans: 2 }" +
               "];" +
               "const listDiv = document.getElementById('questions-list');" +
               "questions.forEach((q, index) => {" +
               "  let html = `<div class=\"question-block\"><div class=\"question-title\">${index + 1}. ${q.q}</div><div class=\"options\">`;" +
               "  q.opts.forEach((opt, optIndex) => html += `<label><input type=\"radio\" name=\"q${index}\" value=\"${optIndex}\"> ${opt}</label>`);" +
               "  html += `</div></div>`; listDiv.innerHTML += html;" +
               "});" +
               "function submitQuiz() {" +
               "  let score = 0; let allAnswered = true;" +
               "  for(let i=0; i<questions.length; i++) {" +
               "    let selected = document.querySelector(`input[name=\"q${i}\"]:checked`);" +
               "    if(!selected) { allAnswered = false; break; }" +
               "    if(parseInt(selected.value) === questions[i].ans) score++;" +
               "  }" +
               "  if(!allAnswered) { alert('Please answer all 10 questions before submitting!'); return; }" +
               "  document.getElementById('quiz-container').style.display = 'none';" +
               "  document.getElementById('result-screen').style.display = 'block';" +
               "  document.getElementById('score-val').innerText = score;" +
               "}" +
               "</script></body></html>";
    }
}
