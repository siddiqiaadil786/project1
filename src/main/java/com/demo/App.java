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
        .table th { font-size: 0.75rem; vertical-align: middle; padding: 0.5rem; }
        .table td { font-size: 0.85rem; vertical-align: middle; padding: 0.3rem; }
        .summary-card h6 { margin-bottom: 0.2rem; }
        
        .edit-input { 
            width: 100%; border: 1px solid #ced4da; padding: 2px 4px; 
            font-size: 0.85rem; border-radius: 3px; background-color: #fff; text-align: center;
        }
        .edit-input:focus { outline: 2px solid #0d6efd; border-color: transparent; background-color: #e9ecef;}
        .edit-input.text-start { text-align: left; }
    </style>
</head>
<body>
    <div id="app" class="container-fluid py-3">
        
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
                    </div>
                </div>
            </div>
        </div>

        <!-- Dashboard / Main Interface -->
        <div v-else>
            <!-- Header -->
            <div class="d-flex justify-content-between align-items-center mb-3 bg-white p-3 rounded shadow-sm border border-secondary border-opacity-25">
                <div>
                    <h3 class="mb-0 text-primary fw-bold">Samiti Management</h3>
                    <div class="text-muted fw-bold mt-1 d-flex align-items-center">
                        <span class="me-2">Month:</span> 
                        <input v-if="user.role === 'admin' && isLatestMeeting" v-model="currentMeeting.meetingMonth" class="edit-input w-auto me-3 fw-bold">
                        <span v-else class="text-dark me-3">{{ currentMeeting.meetingMonth }}</span>
                        
                        <span class="me-2">Date:</span>
                        <input v-if="user.role === 'admin' && isLatestMeeting" v-model="currentMeeting.meetingDate" class="edit-input w-auto me-3 fw-bold">
                        <span v-else class="text-dark me-3">{{ currentMeeting.meetingDate }}</span>
                        
                        <span>Samiti No. <span class="badge bg-danger fs-6">{{ currentMeeting.meetingNo }}</span></span>
                    </div>
                </div>
                
                <!-- History Selector -->
                <div class="d-flex align-items-center bg-light p-2 rounded border">
                    <label class="me-2 fw-bold text-secondary">View Record:</label>
                    <select v-model="activeMeetingIndex" class="form-select w-auto fw-bold">
                        <option v-for="(m, index) in meetings" :value="index">Samiti No. {{ m.meetingNo }} ({{ m.meetingMonth }})</option>
                    </select>
                </div>

                <div class="d-flex align-items-center">
                    <span class="me-4 fw-bold text-muted">Profile: {{ user.name }} ({{ user.role.toUpperCase() }})</span>
                    <button @click="user = null" class="btn btn-outline-danger btn-sm fw-bold">Logout</button>
                </div>
            </div>

            <!-- Enhanced User Profile Section -->
            <div v-if="user.role === 'user' && userProfileData" class="card shadow-sm mb-4 border-primary">
                <div class="card-header bg-primary text-white fw-bold fs-5">
                    👤 My Personal Profile & Loan Status
                </div>
                <div class="card-body bg-light">
                    <div class="row mb-3">
                        <div class="col-md-3"><strong>Name:</strong> <span class="text-dark">{{ user.name }}</span></div>
                        <div class="col-md-3"><strong>Mobile No:</strong> <span class="text-dark">{{ user.mobile }}</span></div>
                        <div class="col-md-3"><strong>Total Jama Till Now:</strong> <span class="text-success fw-bold">₹{{ userProfileData.totalCrTillNow }}</span></div>
                        <div class="col-md-3"><strong>Active Loan:</strong> <span class="text-danger fw-bold">₹{{ userProfileData.loanAmount > 0 ? userProfileData.loanAmount : '0' }}</span></div>
                    </div>
                    <div class="row">
                        <div class="col-md-3"><strong>Current EMI:</strong> <span class="text-primary fw-bold">₹{{ userProfileData.emiAmount > 0 ? userProfileData.emiAmount : '0' }}</span></div>
                        <div class="col-md-3"><strong>Next Kist No:</strong> <span class="text-dark fw-bold">{{ userProfileData.loanEmiNo }}</span></div>
                        <div class="col-md-3"><strong>Pending Dues (Bakaya):</strong> 
                            <span v-if="userProfileData.totalAdvanceThisMonth > 0" class="badge bg-danger fs-6">₹{{ userProfileData.totalAdvanceThisMonth }}</span>
                            <span v-else class="badge bg-success fs-6">₹0 (Clear)</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Main Ledger -->
            <div class="card shadow-sm mb-3 border-0">
                <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center py-2">
                    <h5 class="mb-0">
                        Collection & Distribution Sheet 
                        <span v-if="user.role==='admin' && isLatestMeeting" class="badge bg-warning text-dark ms-2">EDIT MODE ENABLED</span>
                        <span v-if="!isLatestMeeting" class="badge bg-danger ms-2">HISTORICAL RECORD (READ-ONLY)</span>
                    </h5>
                    <div v-if="user.role === 'admin' && isLatestMeeting">
                        <button @click="generateNextMonth" class="btn btn-sm btn-warning fw-bold text-dark me-2">Create Next Month Record ➡️</button>
                    </div>
                </div>
                <div class="card-body table-responsive p-0">
                    <table class="table table-hover table-bordered text-center mb-0">
                        <thead class="table-secondary">
                            <tr>
                                <th>Sr</th>
                                <th>Name</th>
                                <th>Loan Emi No</th>
                                <th>Loan Issue Date</th>
                                <th>Loan Amount</th>
                                <th>Total Cr by each till now</th>
                                <th>This month Share</th>
                                <th>Emi amount</th>
                                <th>Last month Advance</th>
                                <th>Total Cr this month</th>
                                <th>Status received</th>
                                <th>This month not received (goes into advance)</th>
                                <th>Total Advance this month (pending dues)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="row in currentMeeting.ledger" :key="row.id">
                                <td>{{ row.srNo }}</td>
                                
                                <td class="fw-bold text-start text-nowrap">
                                    <input v-if="canEdit" v-model="row.name" class="edit-input text-start fw-bold">
                                    <span v-else>{{ row.name }}</span>
                                </td>
                                
                                <td>
                                    <input v-if="canEdit" v-model="row.loanEmiNo" class="edit-input">
                                    <span v-else>{{ row.loanEmiNo }}</span>
                                </td>
                                
                                <td>
                                    <input v-if="canEdit" v-model="row.loanIssueDate" class="edit-input">
                                    <span v-else>{{ row.loanIssueDate }}</span>
                                </td>
                                
                                <td>
                                    <input v-if="canEdit" v-model="row.loanAmount" class="edit-input text-danger fw-bold">
                                    <span v-else-if="row.loanAmount > 0" class="text-danger fw-bold">{{ row.loanAmount }}</span>
                                </td>
                                
                                <td>
                                    <input v-if="canEdit" v-model="row.totalCrTillNow" class="edit-input">
                                    <span v-else>{{ row.totalCrTillNow }}</span>
                                </td>
                                
                                <td>
                                    <input v-if="canEdit" v-model="row.thisMonthShare" @input="recalcRow(row)" class="edit-input">
                                    <span v-else>{{ row.thisMonthShare }}</span>
                                </td>
                                
                                <td>
                                    <input v-if="canEdit" v-model="row.emiAmount" @input="recalcRow(row)" class="edit-input text-primary fw-bold">
                                    <span v-else-if="row.emiAmount > 0" class="text-primary fw-bold">{{ row.emiAmount }}</span><span v-else-if="row.emiAmount==='N/A'">N/A</span>
                                </td>
                                
                                <td>
                                    <input v-if="canEdit" v-model="row.lastMonthAdvance" @input="recalcRow(row)" class="edit-input text-success">
                                    <span v-else-if="row.lastMonthAdvance > 0" class="text-success">{{ row.lastMonthAdvance }}</span>
                                </td>
                                
                                <td class="bg-light">
                                    <input v-if="canEdit" v-model="row.totalCrThisMonth" class="edit-input fw-bold bg-white text-dark fs-6">
                                    <span v-else class="fw-bold text-dark fs-6">{{ row.totalCrThisMonth }}</span>
                                </td>
                                
                                <td>
                                    <select v-if="canEdit" v-model="row.status" class="edit-input">
                                        <option value="online">Online</option>
                                        <option value="offline">Offline</option>
                                        <option value="Pending">Pending</option>
                                    </select>
                                    <span v-else :class="row.status === 'online' ? 'badge bg-success' : (row.status === 'offline' ? 'badge bg-primary' : 'badge bg-secondary')">{{ row.status }}</span>
                                </td>
                                
                                <td>
                                    <input v-if="canEdit" v-model="row.notReceivedThisMonth" @input="recalcRow(row)" class="edit-input text-danger fw-bold">
                                    <span v-else-if="row.notReceivedThisMonth > 0" class="text-danger fw-bold">{{ row.notReceivedThisMonth }}</span>
                                </td>
                                
                                <td>
                                    <input v-if="canEdit" v-model="row.totalAdvanceThisMonth" class="edit-input text-danger fw-bold">
                                    <span v-else-if="row.totalAdvanceThisMonth > 0" class="text-danger fw-bold">{{ row.totalAdvanceThisMonth }}</span>
                                </td>
                            </tr>
                        </tbody>
                        <tfoot class="table-dark fw-bold">
                            <tr>
                                <td colspan="5" class="text-end">Total:</td>
                                <td class="text-warning">{{ totalCrTillNowSum }}</td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td class="text-warning fs-5">{{ totalCollectSum }}</td>
                                <td colspan="3"></td>
                            </tr>
                        </tfoot>
                    </table>
                </div>
            </div>

            <!-- Bottom Summary -->
            <div class="row">
                <div class="col-md-12">
                    <div class="card shadow-sm border-0 border-start border-4 border-warning summary-card">
                        <div class="card-header bg-light py-2 d-flex justify-content-between align-items-center">
                            <h6 class="fw-bold text-primary mb-0">Meeting Notes & Summary</h6>
                        </div>
                        <div class="card-body bg-white py-3">
                            <div class="row">
                                <div class="col-md-5 border-end">
                                    <p class="mb-1 text-muted fw-bold">Note 1: Loan Issued This Month</p>
                                    <textarea v-if="canEdit" v-model="currentMeeting.summary.loanIssued" class="form-control form-control-sm" rows="3"></textarea>
                                    <h6 v-else class="fw-bold">{{ currentMeeting.summary.loanIssued }}</h6>
                                </div>
                                <div class="col-md-7">
                                    <p class="mb-1 text-muted fw-bold">Note 2: Advance Given</p>
                                    <textarea v-if="canEdit" v-model="currentMeeting.summary.advancesText" class="form-control form-control-sm mb-2" rows="2"></textarea>
                                    <h6 v-else class="fw-bold" style="white-space: pre-wrap;">{{ currentMeeting.summary.advancesText }}</h6>
                                    
                                    <hr class="my-2">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <span class="fw-bold">Total Advance Given:</span>
                                        <input v-if="canEdit" v-model="currentMeeting.summary.totalAdvance" class="edit-input w-25 fw-bold text-end">
                                        <h6 v-else class="fw-bold mb-0">₹{{ currentMeeting.summary.totalAdvance }}</h6>
                                    </div>
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
                    
                    activeMeetingIndex: 0,
                    meetings: [
                        {
                            meetingNo: 148,
                            meetingMonth: 'Sep 2026',
                            meetingDate: '15/09/2026',
                            ledger: [
                                { id:1, srNo:1, name:'User 1', loanEmiNo:'3', loanIssueDate:'2026-06-12', loanAmount:80000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:3700, lastMonthAdvance:0, totalCrThisMonth:4000, status:'online', notReceivedThisMonth:0, totalAdvanceThisMonth:0 },
                                { id:2, srNo:2, name:'User 2', loanEmiNo:'2', loanIssueDate:'2026-07-12', loanAmount:80000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:3700, lastMonthAdvance:5000, totalCrThisMonth:9000, status:'offline', notReceivedThisMonth:0, totalAdvanceThisMonth:0 },
                                { id:3, srNo:3, name:'User 3', loanEmiNo:'1', loanIssueDate:'2026-08-12', loanAmount:80000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:3700, lastMonthAdvance:0, totalCrThisMonth:4000, status:'online', notReceivedThisMonth:0, totalAdvanceThisMonth:0 },
                                { id:4, srNo:4, name:'User 4', loanEmiNo:'N/A', loanIssueDate:'2026-09-15', loanAmount:80000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:'N/A', lastMonthAdvance:0, totalCrThisMonth:300, status:'online', notReceivedThisMonth:0, totalAdvanceThisMonth:0 },
                                { id:5, srNo:5, name:'User 5', loanEmiNo:'N/A', loanIssueDate:'', loanAmount:0, totalCrTillNow:35000, thisMonthShare:300, emiAmount:'N/A', lastMonthAdvance:0, totalCrThisMonth:300, status:'online', notReceivedThisMonth:0, totalAdvanceThisMonth:0 },
                                { id:6, srNo:6, name:'User 6', loanEmiNo:'20', loanIssueDate:'2025-01-12', loanAmount:60000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:3000, lastMonthAdvance:0, totalCrThisMonth:3300, status:'online', notReceivedThisMonth:0, totalAdvanceThisMonth:0 },
                                { id:7, srNo:7, name:'User 7', loanEmiNo:'19', loanIssueDate:'2025-02-12', loanAmount:60000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:3000, lastMonthAdvance:0, totalCrThisMonth:3300, status:'online', notReceivedThisMonth:0, totalAdvanceThisMonth:0 },
                                { id:8, srNo:8, name:'User 8', loanEmiNo:'18', loanIssueDate:'2025-03-12', loanAmount:60000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:3000, lastMonthAdvance:5400, totalCrThisMonth:8700, status:'online', notReceivedThisMonth:0, totalAdvanceThisMonth:0 },
                                { id:9, srNo:9, name:'User 9', loanEmiNo:'17', loanIssueDate:'2025-04-12', loanAmount:60000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:3000, lastMonthAdvance:5000, totalCrThisMonth:8300, status:'online', notReceivedThisMonth:0, totalAdvanceThisMonth:0 },
                                { id:10, srNo:10, name:'User 10', loanEmiNo:'16', loanIssueDate:'2025-05-12', loanAmount:60000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:3000, lastMonthAdvance:0, totalCrThisMonth:3300, status:'online', notReceivedThisMonth:0, totalAdvanceThisMonth:0 },
                                { id:11, srNo:11, name:'User 11', loanEmiNo:'15', loanIssueDate:'2025-06-12', loanAmount:60000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:3000, lastMonthAdvance:13300, totalCrThisMonth:16600, status:'online', notReceivedThisMonth:0, totalAdvanceThisMonth:0 },
                                { id:12, srNo:12, name:'User 12', loanEmiNo:'14', loanIssueDate:'2025-07-12', loanAmount:60000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:3000, lastMonthAdvance:0, totalCrThisMonth:3300, status:'online', notReceivedThisMonth:0, totalAdvanceThisMonth:0 },
                                { id:13, srNo:13, name:'User 13', loanEmiNo:'13', loanIssueDate:'2025-08-12', loanAmount:60000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:3000, lastMonthAdvance:0, totalCrThisMonth:3300, status:'online', notReceivedThisMonth:0, totalAdvanceThisMonth:0 },
                                { id:14, srNo:14, name:'User 14', loanEmiNo:'12', loanIssueDate:'2025-09-12', loanAmount:60000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:3000, lastMonthAdvance:10000, totalCrThisMonth:13300, status:'online', notReceivedThisMonth:0, totalAdvanceThisMonth:0 },
                                { id:15, srNo:15, name:'User 15', loanEmiNo:'11', loanIssueDate:'2025-10-12', loanAmount:60000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:3000, lastMonthAdvance:0, totalCrThisMonth:3300, status:'online', notReceivedThisMonth:0, totalAdvanceThisMonth:0 },
                                { id:16, srNo:16, name:'User 16', loanEmiNo:'10', loanIssueDate:'2025-11-12', loanAmount:60000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:3000, lastMonthAdvance:0, totalCrThisMonth:3300, status:'online', notReceivedThisMonth:15000, totalAdvanceThisMonth:15000 },
                                { id:17, srNo:17, name:'User 17', loanEmiNo:'9', loanIssueDate:'2025-12-12', loanAmount:60000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:3000, lastMonthAdvance:0, totalCrThisMonth:3300, status:'online', notReceivedThisMonth:0, totalAdvanceThisMonth:0 },
                                { id:18, srNo:18, name:'User 18', loanEmiNo:'8', loanIssueDate:'2026-01-12', loanAmount:60000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:3000, lastMonthAdvance:0, totalCrThisMonth:3300, status:'online', notReceivedThisMonth:0, totalAdvanceThisMonth:0 },
                                { id:19, srNo:19, name:'User 19', loanEmiNo:'7', loanIssueDate:'2026-02-12', loanAmount:60000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:3000, lastMonthAdvance:0, totalCrThisMonth:3300, status:'online', notReceivedThisMonth:0, totalAdvanceThisMonth:0 },
                                { id:20, srNo:20, name:'User 20', loanEmiNo:'6', loanIssueDate:'2026-03-12', loanAmount:60000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:3000, lastMonthAdvance:0, totalCrThisMonth:3300, status:'online', notReceivedThisMonth:0, totalAdvanceThisMonth:0 },
                                { id:21, srNo:21, name:'User 21', loanEmiNo:'5', loanIssueDate:'2026-04-12', loanAmount:60000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:3000, lastMonthAdvance:0, totalCrThisMonth:3300, status:'online', notReceivedThisMonth:0, totalAdvanceThisMonth:0 },
                                { id:22, srNo:22, name:'User 22', loanEmiNo:'4', loanIssueDate:'2026-05-12', loanAmount:60000, totalCrTillNow:35000, thisMonthShare:300, emiAmount:3000, lastMonthAdvance:0, totalCrThisMonth:3300, status:'online', notReceivedThisMonth:0, totalAdvanceThisMonth:0 }
                            ],
                            summary: {
                                loanIssued: 'User 4 and amount 80000 (amount provided 30000 online + 50000 offline)',
                                advancesText: 'User 7: 13000\\nUser 9: 5000\\nUser 11: 15000\\nUser 17: 9400',
                                totalAdvance: 42400
                            }
                        }
                    ]
                }
            },
            computed: {
                currentMeeting() {
                    return this.meetings[this.activeMeetingIndex];
                },
                isLatestMeeting() {
                    return this.activeMeetingIndex === this.meetings.length - 1;
                },
                canEdit() {
                    return this.user.role === 'admin' && this.isLatestMeeting;
                },
                totalCollectSum() {
                    return this.currentMeeting.ledger.reduce((sum, row) => sum + Number(row.totalCrThisMonth || 0), 0);
                },
                totalCrTillNowSum() {
                    return this.currentMeeting.ledger.reduce((sum, row) => sum + Number(row.totalCrTillNow || 0), 0);
                },
                userProfileData() {
                    if (this.user && this.user.role === 'user') {
                        // Find the user's row in the currently viewed meeting
                        return this.currentMeeting.ledger.find(r => r.name === this.user.name);
                    }
                    return null;
                }
            },
            methods: {
                login() {
                    if (this.loginData.mobile === 'admin' && this.loginData.password === 'admin123') {
                        this.user = { name: 'Admin Account', mobile: 'admin', role: 'admin' }
                    } else if (this.loginData.mobile === '9876543210' && this.loginData.password === 'user123') {
                        // For demo purposes, map this specific phone number to 'User 4' so they can see a rich profile
                        this.user = { name: 'User 4', mobile: '9876543210', role: 'user' }
                    } else {
                        alert('Invalid! Try admin/admin123 or 9876543210/user123')
                    }
                },
                recalcRow(row) {
                    let share = Number(row.thisMonthShare) || 0;
                    let emi = row.emiAmount === 'N/A' ? 0 : (Number(row.emiAmount) || 0);
                    let advance = Number(row.lastMonthAdvance) || 0;
                    let notRecv = Number(row.notReceivedThisMonth) || 0;
                    row.totalCrThisMonth = (share + emi + advance) - notRecv;
                },
                generateNextMonth() {
                    if(!confirm("Finalize this sheet and generate the next month's sheet? Previous sheets will be saved to history.")) return;
                    
                    let currentM = this.meetings[this.meetings.length - 1];
                    // Deep copy the ledger so history isn't overwritten
                    let newLedger = JSON.parse(JSON.stringify(currentM.ledger));
                    
                    newLedger = newLedger.map(row => {
                        let nextEmi = row.loanEmiNo;
                        let emiAmt = row.emiAmount;
                        
                        if (nextEmi !== 'N/A' && nextEmi !== '') {
                            nextEmi = parseInt(nextEmi) + 1;
                        }
                        
                        let pendingDues = row.totalAdvanceThisMonth;
                        let newShare = parseInt(row.thisMonthShare) || 0;
                        
                        return {
                            ...row,
                            loanEmiNo: nextEmi,
                            emiAmount: emiAmt,
                            totalCrTillNow: parseInt(row.totalCrTillNow) + newShare,
                            lastMonthAdvance: pendingDues, 
                            
                            totalCrThisMonth: newShare + (parseInt(emiAmt) || 0) + parseInt(pendingDues || 0),
                            notReceivedThisMonth: 0,
                            totalAdvanceThisMonth: 0,
                            status: 'Pending'
                        }
                    });
                    
                    this.meetings.push({
                        meetingNo: currentM.meetingNo + 1,
                        meetingMonth: 'Enter Month Here',
                        meetingDate: 'Enter Date Here',
                        ledger: newLedger,
                        summary: { loanIssued: '', advancesText: '', totalAdvance: 0 }
                    });
                    
                    // Automatically switch view to the newly generated meeting
                    this.activeMeetingIndex = this.meetings.length - 1;
                    alert("Next month's record created successfully! It is now the active editable sheet.");
                }
            }
        }).mount('#app')
    </script>
</body>
</html>
""";
    }
}
