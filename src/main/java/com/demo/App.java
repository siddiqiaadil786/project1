package com.demo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) throws IOException {
        int port = 8081;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        
        server.createContext("/", new UIHandler());
        
        server.setExecutor(null);
        System.out.println("Samiti Management Server started on port " + port);
        server.start();
    }

    static class UIHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = getAppHtml();
            t.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            t.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public static String getAppHtml() {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Samiti Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://unpkg.com/vue@3/dist/vue.global.js"></script>
    <style>
        body { background-color: #f8f9fa; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .table th { font-size: 0.85rem; vertical-align: middle; }
        .table td { font-size: 0.9rem; vertical-align: middle; }
    </style>
</head>
<body>
    <div id="app" class="container-fluid py-4">
        
        <!-- Login Screen -->
        <div v-if="!user" class="row justify-content-center align-items-center" style="min-height: 80vh;">
            <div class="col-md-4">
                <div class="card shadow-lg border-0 rounded-lg">
                    <div class="card-header bg-primary text-white text-center py-3">
                        <h3 class="mb-0">Samiti Management</h3>
                        <small>Interest-Free Loan & Fund System</small>
                    </div>
                    <div class="card-body p-4">
                        <div class="mb-3">
                            <label class="form-label fw-bold">Mobile No:</label>
                            <input v-model="loginData.mobile" class="form-control" placeholder="Enter Mobile No">
                        </div>
                        <div class="mb-4">
                            <label class="form-label fw-bold">Password:</label>
                            <input v-model="loginData.password" type="password" class="form-control" placeholder="Enter Password" @keyup.enter="login">
                        </div>
                        <button @click="login" class="btn btn-primary w-100 fw-bold">LOGIN</button>
                        
                        <div class="mt-4 alert alert-secondary text-center small mb-0">
                            <strong>Demo Accounts:</strong><br>
                            Admin: <code>admin</code> / <code>admin123</code><br>
                            User: <code>9876543210</code> / <code>user123</code>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Dashboard / Main Interface -->
        <div v-else>
            <!-- Header -->
            <div class="d-flex justify-content-between align-items-center mb-4 bg-white p-3 rounded shadow-sm border">
                <div>
                    <h2 class="mb-0 text-primary fw-bold">Samiti Management <span class="badge bg-secondary fs-6">Meeting #133</span></h2>
                </div>
                <div class="d-flex align-items-center">
                    <span class="me-4 fw-bold text-muted">Profile: {{ user.name }} ({{ user.role.toUpperCase() }})</span>
                    <button @click="user = null" class="btn btn-outline-danger btn-sm fw-bold">Logout</button>
                </div>
            </div>

            <!-- User Specific Profile Summary -->
            <div v-if="user.role === 'user'" class="alert alert-info shadow-sm mb-4 border-info">
                <h4 class="alert-heading fw-bold">My Account Status (As of Last Meeting)</h4>
                <hr>
                <div class="row">
                    <div class="col-md-3"><strong>Total Jama Till Now:</strong> ₹39,600</div>
                    <div class="col-md-3"><strong>Active Loan:</strong> ₹1,200 (Issued 2 months ago)</div>
                    <div class="col-md-3"><strong>Current EMI:</strong> ₹400/mo</div>
                    <div class="col-md-3"><strong>Pending (Bakaya):</strong> ₹0</div>
                </div>
            </div>

            <!-- Main Ledger -->
            <div class="card shadow-sm mb-4 border-0">
                <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center py-3">
                    <h5 class="mb-0">Current Meeting Collection & Distribution Sheet</h5>
                    <div v-if="user.role === 'admin'">
                        <button class="btn btn-sm btn-light text-dark fw-bold me-2">+ Add Member Entry</button>
                        <button class="btn btn-sm btn-warning fw-bold">Upload Last 130 Meetings Data</button>
                    </div>
                </div>
                <div class="card-body table-responsive p-0">
                    <table class="table table-hover table-bordered text-center mb-0">
                        <thead class="table-secondary">
                            <tr>
                                <th>Sr No</th>
                                <th>Name</th>
                                <th>Kist No</th>
                                <th>Loan Issue Date</th>
                                <th>Loan Amount</th>
                                <th>Total Jama (Till Now)</th>
                                <th>Share / Monthly Jama</th>
                                <th>EMI (Kist)</th>
                                <th>Last Mth Advance</th>
                                <th>Total Collect This Month</th>
                                <th>Total Bakaya</th>
                                <th>Advance Given</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="row in ledger" :key="row.id">
                                <td>{{ row.srNo }}</td>
                                <td class="fw-bold">{{ row.name }}</td>
                                <td>{{ row.kistNo }}</td>
                                <td>{{ row.loanDate }}</td>
                                <td><span v-if="row.loanAmount > 0" class="text-danger fw-bold">₹{{ row.loanAmount }}</span><span v-else>-</span></td>
                                <td>₹{{ row.totalJama }}</td>
                                <td>₹{{ row.monthlyJama }}</td>
                                <td><span v-if="row.emi > 0" class="text-primary fw-bold">₹{{ row.emi }}</span><span v-else>-</span></td>
                                <td><span v-if="row.lastAdvance > 0" class="text-success">₹{{ row.lastAdvance }}</span><span v-else>-</span></td>
                                <td class="fw-bold bg-light">₹{{ row.totalCollect }}</td>
                                <td><span v-if="row.bakaya > 0" class="text-danger fw-bold">₹{{ row.bakaya }}</span><span v-else class="text-muted">₹0</span></td>
                                <td><span v-if="row.advanceGiven > 0" class="text-success fw-bold">₹{{ row.advanceGiven }}</span><span v-else>-</span></td>
                                <td><span :class="row.status === 'Online' ? 'badge bg-success' : 'badge bg-warning text-dark'">{{ row.status }}</span></td>
                            </tr>
                        </tbody>
                        <tfoot class="table-light fw-bold">
                            <tr>
                                <td colspan="9" class="text-end">Total Collection:</td>
                                <td class="text-primary fs-5">₹4,300</td>
                                <td colspan="3"></td>
                            </tr>
                        </tfoot>
                    </table>
                </div>
            </div>

            <!-- Bottom Summary -->
            <div class="row">
                <div class="col-md-12">
                    <div class="card shadow-sm border-0 border-start border-4 border-primary">
                        <div class="card-body bg-light">
                            <h5 class="fw-bold text-primary mb-3">Meeting Summary & Distribution</h5>
                            <div class="row">
                                <div class="col-md-3">
                                    <p class="mb-1 text-muted">Loans Issued This Month</p>
                                    <h6 class="fw-bold">Person B (₹1200)</h6>
                                    <h6 class="fw-bold">Person C (₹1200)</h6>
                                </div>
                                <div class="col-md-3">
                                    <p class="mb-1 text-muted">Advances Given To</p>
                                    <h6 class="fw-bold">Person A (₹200)</h6>
                                </div>
                                <div class="col-md-3">
                                    <p class="mb-1 text-muted">Old Advances Recovered</p>
                                    <h6 class="fw-bold">₹300 (From Person D & E)</h6>
                                </div>
                                <div class="col-md-3">
                                    <p class="mb-1 text-muted">Other Funds (Donations)</p>
                                    <h6 class="fw-bold text-success">₹150 (From Person B)</h6>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
        </div>
    </div>

    <script>
        const { createApp } = Vue
        createApp({
            data() {
                return {
                    user: null,
                    loginData: { mobile: 'admin', password: 'admin123' },
                    
                    // Complex example data reflecting your exact scenario
                    ledger: [
                        { id:1, srNo:1, name:'Person A', kistNo:133, loanDate:'-', loanAmount:0, totalJama:39600, monthlyJama:300, emi:0, lastAdvance:0, totalCollect:300, bakaya:0, advanceGiven:200, status:'Online' },
                        { id:2, srNo:2, name:'Person B', kistNo:133, loanDate:'2026-09-27', loanAmount:1200, totalJama:39600, monthlyJama:300, emi:0, lastAdvance:0, totalCollect:450, bakaya:0, advanceGiven:0, status:'Cash' },
                        { id:3, srNo:3, name:'Person C', kistNo:133, loanDate:'2026-09-27', loanAmount:1200, totalJama:39600, monthlyJama:300, emi:0, lastAdvance:0, totalCollect:300, bakaya:0, advanceGiven:0, status:'Online' },
                        { id:4, srNo:4, name:'Person D (Old Loan)', kistNo:133, loanDate:'2026-07-15', loanAmount:0, totalJama:39600, monthlyJama:300, emi:400, lastAdvance:200, totalCollect:900, bakaya:0, advanceGiven:0, status:'Online' },
                        { id:5, srNo:5, name:'Person E (Missed Last)', kistNo:133, loanDate:'-', loanAmount:0, totalJama:39300, monthlyJama:300, emi:0, lastAdvance:0, totalCollect:600, bakaya:300, advanceGiven:0, status:'Cash' },
                        { id:6, srNo:6, name:'Person F (New Joinee)', kistNo:1, loanDate:'-', loanAmount:0, totalJama:0, monthlyJama:300, emi:0, lastAdvance:0, totalCollect:1500, bakaya:0, advanceGiven:0, status:'Online' } // Paid past dues 4 months (4*300) + 300 current
                    ]
                }
            },
            methods: {
                login() {
                    if (this.loginData.mobile === 'admin' && this.loginData.password === 'admin123') {
                        this.user = { name: 'Admin Manager', role: 'admin' }
                    } else if (this.loginData.mobile === '9876543210' && this.loginData.password === 'user123') {
                        this.user = { name: 'Regular Member', role: 'user' }
                    } else {
                        alert('Invalid credentials! Please use admin/admin123 or 9876543210/user123')
                    }
                }
            }
        }).mount('#app')
    </script>
</body>
</html>
""";
    }
}
