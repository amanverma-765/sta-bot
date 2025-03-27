package com.ark.stabot.utils

import com.ark.stabot.model.Trademark

fun getTrademarkStatusTemplate(totalTrademarks: Int, lastTrademark: Trademark?): String {
    return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Trademark Scraper Status</title>
            <style>
                :root {
                    --primary-color: #4361ee;
                    --secondary-color: #3a0ca3;
                    --accent-color: #4cc9f0;
                    --background: #f8f9fa;
                    --card-bg: #ffffff;
                    --text-primary: #2b2d42;
                    --text-secondary: #6c757d;
                }
                
                body {
                    font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    background-color: var(--background);
                    margin: 0;
                    padding: 0;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    min-height: 100vh;
                    color: var(--text-primary);
                }
                
                .container {
                    max-width: 900px;
                    width: 90%;
                    padding: 2rem;
                }
                
                .dashboard {
                    background-color: var(--card-bg);
                    border-radius: 16px;
                    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
                    padding: 2rem;
                    overflow: hidden;
                    position: relative;
                }
                
                .dashboard::before {
                    content: '';
                    position: absolute;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 6px;
                    background: linear-gradient(90deg, var(--primary-color), var(--accent-color));
                }
                
                h1 {
                    font-size: 1.8rem;
                    margin-bottom: 2rem;
                    text-align: center;
                    color: var(--secondary-color);
                }
                
                .stats-container {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                    gap: 1.5rem;
                    margin-bottom: 2rem;
                }
                
                .stat-card {
                    background: linear-gradient(145deg, #ffffff, #f0f0f0);
                    border-radius: 12px;
                    padding: 1.5rem;
                    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.04);
                    transition: transform 0.3s ease, box-shadow 0.3s ease;
                }
                
                .stat-card:hover {
                    transform: translateY(-5px);
                    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
                }
                
                .stat-icon {
                    display: flex;
                    align-items: center;
                    margin-bottom: 1rem;
                    color: var(--primary-color);
                }
                
                .stat-title {
                    font-size: 0.9rem;
                    color: var(--text-secondary);
                    margin-bottom: 0.3rem;
                    text-transform: uppercase;
                    letter-spacing: 0.05em;
                }
                
                .stat-value {
                    font-size: 2rem;
                    font-weight: 700;
                    color: var(--primary-color);
                }
                
                .subtitle {
                    font-size: 0.9rem;
                    color: var(--text-secondary);
                    margin-top: 0.2rem;
                }
                
                .empty-state {
                    text-align: center;
                    padding: 2rem;
                    color: var(--text-secondary);
                }
                
                .footer {
                    margin-top: 2rem;
                    text-align: center;
                    font-size: 0.8rem;
                    color: var(--text-secondary);
                }
                
                @media (max-width: 600px) {
                    .container {
                        padding: 1rem;
                    }
                    
                    .dashboard {
                        padding: 1.5rem;
                    }
                    
                    .stats-container {
                        grid-template-columns: 1fr;
                    }
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="dashboard">
                    <h1>Trademark Scraper Status</h1>
                    
                    <div class="stats-container">
                        <div class="stat-card">
                            <div class="stat-icon">
                                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                                    <polyline points="9 22 9 12 15 12 15 22"></polyline>
                                </svg>
                            </div>
                            <div class="stat-title">Total Trademarks</div>
                            <div class="stat-value">${totalTrademarks}</div>
                            <div class="subtitle">Scraped from database</div>
                        </div>
                        
                        ${if (lastTrademark != null) """
                        <div class="stat-card">
                            <div class="stat-icon">
                                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <circle cx="12" cy="12" r="10"></circle>
                                    <polyline points="12 6 12 12 16 14"></polyline>
                                </svg>
                            </div>
                           <div class="stat-title">Latest Trademark</div>
                            <div class="stat-value">${lastTrademark.applicationNumber}</div>
                            <div class="subtitle">Last updated: ${lastTrademark.dateOfApplication}</div>
                            <div class="subtitle">Name: ${lastTrademark.tmAppliedFor}</div>
                        </div>
                        """ else """
                        <div class="stat-card">
                            <div class="stat-icon">
                                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <circle cx="12" cy="12" r="10"></circle>
                                    <line x1="12" y1="8" x2="12" y2="12"></line>
                                    <line x1="12" y1="16" x2="12.01" y2="16"></line>
                                </svg>
                            </div>
                            <div class="stat-title">Latest Trademark</div>
                            <div class="stat-value">None</div>
                            <div class="subtitle">No trademarks in database</div>
                        </div>
                        """}
                    </div>
                    
                    ${if (totalTrademarks == 0) """
                    <div class="empty-state">
                        <p>No trademarks have been scraped yet. Please check back later.</p>
                    </div>
                    """ else ""}
                    
                    <div class="footer">
                        <p>© ${java.time.Year.now()} STA-Bot Trademark Scraper | Last Updated: ${java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}</p>
                    </div>
                </div>
            </div>
        </body>
        </html>
    """.trimIndent()
}