package utils;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Professional email dashboard generator for test reports
 */
public class EmailDashboardGenerator {

    private static final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("#.##");
    private static final DecimalFormat DURATION_FORMAT = new DecimalFormat("#.##");

    /**
     * Generate professional HTML email dashboard
     */
    public static String generateDashboard(CucumberReportParser.DetailedTestStatistics stats) {
        StringBuilder html = new StringBuilder();

        html.append(generateEmailHeader());
        html.append(generateExecutionSummary(stats));
        html.append(generateOverallStatistics(stats));
        html.append(generateFeatureBreakdown(stats));
        html.append(generateFooter());

        return html.toString();
    }

    private static String generateEmailHeader() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>Test Execution Report</title>\n");
        html.append("    <style>\n");
        html.append("        body {\n");
        html.append("            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n");
        html.append("            margin: 0;\n");
        html.append("            padding: 20px;\n");
        html.append("            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n"); // Enhanced gradient background
        html.append("            color: #2c3e50;\n"); // Enhanced dark text
        html.append("            line-height: 1.6;\n");
        html.append("            min-height: 100vh;\n");
        html.append("        }\n");
        html.append("        .container {\n");
        html.append("            max-width: 900px;\n");
        html.append("            margin: 0 auto;\n");
        html.append("            background: linear-gradient(145deg, #ffffff 0%, #f8f9fa 100%);\n"); // Enhanced white gradient
        html.append("            border-radius: 16px;\n"); // More rounded corners
        html.append("            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15), 0 4px 16px rgba(0, 0, 0, 0.1);\n"); // Enhanced shadow
        html.append("            overflow: hidden;\n");
        html.append("            border: 1px solid rgba(255, 255, 255, 0.2);\n");
        html.append("        }\n");
        html.append("        .header {\n");
        html.append("            background: linear-gradient(135deg, #1e3c72 0%, #2a5298 50%, #667eea 100%);\n"); // Enhanced blue gradient
        html.append("            color: #ffffff;\n");
        html.append("            padding: 32px 24px;\n");
        html.append("            text-align: center;\n");
        html.append("            position: relative;\n");
        html.append("            overflow: hidden;\n");
        html.append("        }\n");
        html.append("        .header::before {\n");
        html.append("            content: '';\n");
        html.append("            position: absolute;\n");
        html.append("            top: 0;\n");
        html.append("            left: 0;\n");
        html.append("            right: 0;\n");
        html.append("            bottom: 0;\n");
        html.append("            background: linear-gradient(45deg, rgba(255,255,255,0.1) 0%, transparent 50%);\n");
        html.append("            pointer-events: none;\n");
        html.append("        }\n");
        html.append("        .header h1 {\n");
        html.append("            margin: 0;\n");
        html.append("            font-size: 32px;\n"); // Larger font
        html.append("            font-weight: 700;\n");
        html.append("            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);\n"); // Enhanced text shadow
        html.append("            letter-spacing: 1px;\n");
        html.append("            position: relative;\n");
        html.append("            z-index: 1;\n");
        html.append("        }\n");
        html.append("        .header p {\n");
        html.append("            margin: 12px 0 0 0;\n");
        html.append("            font-size: 16px;\n");
        html.append("            opacity: 0.95;\n");
        html.append("            position: relative;\n");
        html.append("            z-index: 1;\n");
        html.append("        }\n");
        html.append("        .summary-section {\n");
        html.append("            padding: 20px;\n");
        html.append("            background-color: #fafafa;\n");
        html.append("            border-bottom: 1px solid #eee;\n");
        html.append("        }\n");
        html.append("        .compact-section {\n");
        html.append("            padding: 20px;\n"); // Increased padding
        html.append("            background: linear-gradient(145deg, #ffffff 0%, #f8f9fa 100%);\n"); // Enhanced background
        html.append("            border-bottom: 1px solid #e1e8ed;\n");
        html.append("        }\n");
        html.append("        .stats-grid {\n");
        html.append("            display: grid;\n");
        html.append("            grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));\n");
        html.append("            gap: 12px;\n");
        html.append("            margin: 15px 0;\n");
        html.append("        }\n");
        html.append("        .stats-grid-horizontal {\n");
        html.append("            display: flex;\n");
        html.append("            flex-wrap: wrap;\n");
        html.append("            justify-content: space-between;\n");
        html.append("            margin: 15px 0;\n");
        html.append("            gap: 16px;\n"); // Add gap between cards
        html.append("        }\n");
        html.append("        .stat-card {\n");
        html.append("            background: linear-gradient(145deg, #ffffff 0%, #fefefe 100%);\n"); // Enhanced card background
        html.append("            padding: 20px;\n"); // Increased padding
        html.append("            border-radius: 12px;\n"); // More rounded
        html.append("            text-align: center;\n");
        html.append("            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1), 0 2px 6px rgba(0, 0, 0, 0.05);\n"); // Enhanced shadow
        html.append("            border-left: 4px solid;\n"); // Thicker accent border
        html.append("            min-width: 140px;\n");
        html.append("            flex: 1 1 140px;\n");
        html.append("            max-width: 180px;\n");
        html.append("            transition: transform 0.2s ease, box-shadow 0.2s ease;\n");
        html.append("        }\n");
        html.append("        .stat-card:hover {\n");
        html.append("            transform: translateY(-2px);\n");
        html.append("            box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);\n");
        html.append("        }\n");
        html.append("        .stat-card.total { border-left-color: #3498db; }\n");
        html.append("        .stat-card.passed { border-left-color: #2ecc71; }\n");
        html.append("        .stat-card.failed { border-left-color: #e74c3c; }\n");
        html.append("        .stat-card.skipped { border-left-color: #f39c12; }\n");
        html.append("        .stat-number {\n");
        html.append("            font-size: 28px;\n"); // Larger font
        html.append("            font-weight: 700;\n"); // Bolder
        html.append("            margin-bottom: 6px;\n");
        html.append("            color: #1a1a1a;\n"); // Darker text for better contrast
        html.append("        }\n");
        html.append("        .stat-icon {\n");
        html.append("            font-size: 22px;\n"); // Larger icon
        html.append("            margin-bottom: 8px;\n");
        html.append("            opacity: 0.9;\n");
        html.append("        }\n");
        html.append("        .stat-label {\n");
        html.append("            color: #333333;\n"); // Darker for better contrast
        html.append("            font-size: 13px;\n"); // Slightly larger
        html.append("            text-transform: uppercase;\n");
        html.append("            letter-spacing: 0.8px;\n");
        html.append("            margin-bottom: 3px;\n");
        html.append("            font-weight: 700;\n"); // Bolder
        html.append("        }\n");
        html.append("        .stat-subtitle {\n");
        html.append("            color: #666666;\n"); // Darker for better contrast
        html.append("            font-size: 11px;\n");
        html.append("            font-style: italic;\n");
        html.append("            font-weight: 500;\n");
        html.append("        }\n");
        html.append("        .progress-bar {\n");
        html.append("            background-color: #ecf0f1;\n");
        html.append("            border-radius: 8px;\n");
        html.append("            height: 18px;\n");
        html.append("            margin: 10px 0;\n");
        html.append("            overflow: hidden;\n");
        html.append("        }\n");
        html.append("        .progress-fill {\n");
        html.append("            height: 100%;\n");
        html.append("            background: linear-gradient(90deg, #2ecc71 0%, #27ae60 100%);\n");
        html.append("            border-radius: 8px;\n");
        html.append("            display: flex;\n");
        html.append("            align-items: center;\n");
        html.append("            justify-content: center;\n");
        html.append("            color: white;\n");
        html.append("            font-weight: 600;\n");
        html.append("            font-size: 11px;\n");
        html.append("        }\n");
        html.append("        .feature-section {\n");
        html.append("            padding: 20px;\n");
        html.append("        }\n");
        html.append("        .feature-card {\n");
        html.append("            background: white;\n");
        html.append("            border: 1px solid #e1e8ed;\n");
        html.append("            border-radius: 6px;\n");
        html.append("            margin-bottom: 15px;\n");
        html.append("            overflow: hidden;\n");
        html.append("        }\n");
        html.append("        .feature-header {\n");
        html.append("            background-color: #f8f9fa;\n");
        html.append("            padding: 12px 15px;\n");
        html.append("            border-bottom: 1px solid #e1e8ed;\n");
        html.append("        }\n");
        html.append("        .feature-title {\n");
        html.append("            font-size: 16px;\n");
        html.append("            font-weight: 600;\n");
        html.append("            color: #2c3e50;\n");
        html.append("            margin: 0 0 4px 0;\n");
        html.append("        }\n");
        html.append("        .feature-description {\n");
        html.append("            color: #7f8c8d;\n");
        html.append("            font-size: 13px;\n");
        html.append("            margin: 0;\n");
        html.append("        }\n");
        html.append("        .feature-stats {\n");
        html.append("            padding: 12px 15px;\n");
        html.append("        }\n");
        html.append("        .feature-stats-grid {\n");
        html.append("            display: grid;\n");
        html.append("            grid-template-columns: repeat(auto-fit, minmax(80px, 1fr));\n");
        html.append("            gap: 12px;\n");
        html.append("            margin-bottom: 12px;\n");
        html.append("        }\n");
        html.append("        .feature-stat {\n");
        html.append("            text-align: center;\n");
        html.append("        }\n");
        html.append("        .feature-stat-number {\n");
        html.append("            font-size: 18px;\n");
        html.append("            font-weight: bold;\n");
        html.append("            margin-bottom: 2px;\n");
        html.append("        }\n");
        html.append("        .feature-stat-label {\n");
        html.append("            font-size: 11px;\n");
        html.append("            color: #666;\n");
        html.append("            text-transform: uppercase;\n");
        html.append("            font-weight: 600;\n");
        html.append("        }\n");
        html.append("        .scenario-list {\n");
        html.append("            margin-top: 12px;\n");
        html.append("        }\n");
        html.append("        .scenario-item {\n");
        html.append("            display: flex;\n");
        html.append("            justify-content: space-between;\n");
        html.append("            align-items: center;\n");
        html.append("            padding: 6px 0;\n");
        html.append("            border-bottom: 1px solid #f1f3f4;\n");
        html.append("        }\n");
        html.append("        .scenario-item:last-child {\n");
        html.append("            border-bottom: none;\n");
        html.append("        }\n");
        html.append("        .scenario-name {\n");
        html.append("            flex: 1;\n");
        html.append("            font-size: 13px;\n");
        html.append("        }\n");
        html.append("        .scenario-status {\n");
        html.append("            padding: 3px 6px;\n");
        html.append("            border-radius: 3px;\n");
        html.append("            font-size: 10px;\n");
        html.append("            font-weight: bold;\n");
        html.append("            text-transform: uppercase;\n");
        html.append("        }\n");
        html.append("        .status-passed {\n");
        html.append("            background-color: #d4edda;\n");
        html.append("            color: #155724;\n");
        html.append("        }\n");
        html.append("        .status-failed {\n");
        html.append("            background-color: #f8d7da;\n");
        html.append("            color: #721c24;\n");
        html.append("        }\n");
        html.append("        .status-skipped {\n");
        html.append("            background-color: #fff3cd;\n");
        html.append("            color: #856404;\n");
        html.append("        }\n");
        html.append("        .footer {\n");
        html.append("            background-color: #2c3e50;\n");
        html.append("            color: white;\n");
        html.append("            padding: 15px;\n");
        html.append("            text-align: center;\n");
        html.append("            font-size: 12px;\n");
        html.append("        }\n");
        html.append("        .timestamp {\n");
        html.append("            color: #bdc3c7;\n");
        html.append("            font-size: 11px;\n");
        html.append("        }\n");
        html.append("        .section-title {\n");
        html.append("            font-size: 24px;\n"); // Larger font
        html.append("            font-weight: 700;\n"); // Bolder
        html.append("            margin: 0 0 20px 0;\n");
        html.append("            color: #1a1a1a;\n"); // Darker for better contrast
        html.append("            text-align: center;\n");
        html.append("            text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);\n");
        html.append("        }\n");
        html.append("        .duration-info {\n");
        html.append("            background-color: #ecf0f1;\n");
        html.append("            padding: 8px 12px;\n");
        html.append("            border-radius: 4px;\n");
        html.append("            margin: 8px 0;\n");
        html.append("            font-size: 13px;\n");
        html.append("            color: #34495e;\n");
        html.append("        }\n");
        html.append("        .compact-table {\n");
        html.append("            width: 100%;\n");
        html.append("            border-collapse: collapse;\n");
        html.append("            margin: 15px 0;\n");
        html.append("            box-shadow: 0 1px 3px rgba(0,0,0,0.1);\n");
        html.append("            font-size: 13px;\n");
        html.append("        }\n");
        html.append("        .compact-table th {\n");
        html.append("            padding: 10px 8px;\n");
        html.append("            text-align: center;\n");
        html.append("            border: 1px solid #ddd;\n");
        html.append("            font-weight: 600;\n");
        html.append("            font-size: 12px;\n");
        html.append("        }\n");
        html.append("        .compact-table td {\n");
        html.append("            padding: 8px;\n");
        html.append("            border: 1px solid #ddd;\n");
        html.append("            text-align: center;\n");
        html.append("        }\n");
        html.append("        .metrics-container {\n");
        html.append("            display: grid;\n");
        html.append("            grid-template-columns: 1fr 1fr;\n");
        html.append("            gap: 15px;\n");
        html.append("            margin: 15px 0;\n");
        html.append("        }\n");
        html.append("        .metric-card {\n");
        html.append("            background: white;\n");
        html.append("            padding: 12px;\n");
        html.append("            border-radius: 6px;\n");
        html.append("            border: 1px solid #dee2e6;\n");
        html.append("        }\n");
        html.append("        .metric-title {\n");
        html.append("            font-size: 14px;\n");
        html.append("            font-weight: 600;\n");
        html.append("            color: #2c3e50;\n");
        html.append("            margin-bottom: 8px;\n");
        html.append("            text-align: center;\n");
        html.append("        }\n");
        html.append("        table {\n");
        html.append("            width: 100%;\n");
        html.append("            border-collapse: collapse;\n");
        html.append("            margin: 18px 0;\n");
        html.append("        }\n");
        html.append("        th, td {\n");
        html.append("            padding: 10px 14px;\n");
        html.append("            border-bottom: 1px solid #e1e8ed;\n");
        html.append("            text-align: left;\n");
        html.append("        }\n");
        html.append("        th {\n");
        html.append("            background-color: #f4f6fb;\n");
        html.append("            font-weight: 600;\n");
        html.append("            color: #333;\n");
        html.append("        }\n");
        html.append("        tr:nth-child(even) {\n");
        html.append("            background-color: #fafafa;\n");
        html.append("        }\n");
        html.append("        tr:hover {\n");
        html.append("            background-color: #eaf1fb;\n");
        html.append("        }\n");
        html.append("        .btn {\n");
        html.append("            display: inline-block;\n");
        html.append("            padding: 8px 18px;\n");
        html.append("            font-size: 14px;\n");
        html.append("            border-radius: 4px;\n");
        html.append("            background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);\n");
        html.append("            color: #fff;\n");
        html.append("            text-decoration: none;\n");
        html.append("            font-weight: 500;\n");
        html.append("            box-shadow: 0 1px 3px rgba(0,0,0,0.08);\n");
        html.append("            border: none;\n");
        html.append("            cursor: pointer;\n");
        html.append("            transition: background 0.2s;\n");
        html.append("        }\n");
        html.append("        .btn:hover {\n");
        html.append("            background: linear-gradient(90deg, #764ba2 0%, #667eea 100%);\n");
        html.append("        }\n");
        html.append("        @media (max-width: 600px) {\n");
        html.append("            .container {\n");
        html.append("                padding: 8px;\n");
        html.append("            }\n");
        html.append("            .stats-grid {\n");
        html.append("                grid-template-columns: 1fr;\n");
        html.append("            }\n");
        html.append("            .feature-section, .summary-section, .compact-section {\n");
        html.append("                padding: 10px;\n");
        html.append("            }\n");
        html.append("        }\n");
        html.append("        @media (max-width: 900px) {\n");
        html.append("            .stats-grid-horizontal {\n");
        html.append("                flex-direction: column;\n");
        html.append("                gap: 12px;\n");
        html.append("            }\n");
        html.append("            .stat-card {\n");
        html.append("                max-width: 100%;\n");
        html.append("            }\n");
        html.append("        }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <div class=\"container\">\n");

        return html.toString();
    }

    private static String generateExecutionSummary(CucumberReportParser.DetailedTestStatistics stats) {
        // Removed Execution Summary section as requested
        return "";
    }

    private static String generateOverallStatistics(CucumberReportParser.DetailedTestStatistics stats) {
        StringBuilder html = new StringBuilder();

        html.append("        <div class=\"compact-section\">\n");
        html.append("            <h2 class=\"section-title\">\uD83D\uDCCA Executive Summary</h2>\n");
        html.append("            <!-- Horizontal KPI Cards -->\n");
        html.append("            <div class=\"stats-grid-horizontal\">\n");
        html.append("                <div class=\"stat-card total\">\n");
        html.append("                    <div class=\"stat-icon\">\uD83C\uDFED</div>\n");
        html.append("                    <div class=\"stat-number\">" + stats.getTotalFeatures() + "</div>\n");
        html.append("                    <div class=\"stat-label\">Features</div>\n");
        html.append("                    <div class=\"stat-subtitle\">Components</div>\n");
        html.append("                </div>\n");
        html.append("                <div class=\"stat-card total\">\n");
        html.append("                    <div class=\"stat-icon\">\uD83D\uDCDD</div>\n");
        html.append("                    <div class=\"stat-number\">" + stats.getTotalScenarios() + "</div>\n");
        html.append("                    <div class=\"stat-label\">Scenarios</div>\n");
        html.append("                    <div class=\"stat-subtitle\">Coverage</div>\n");
        html.append("                </div>\n");
        html.append("                <div class=\"stat-card passed\">\n");
        html.append("                    <div class=\"stat-icon\">\u2705</div>\n");
        html.append("                    <div class=\"stat-number\">" + stats.getPassedScenarios() + "</div>\n");
        html.append("                    <div class=\"stat-label\">Passed</div>\n");
        html.append("                    <div class=\"stat-subtitle\">Quality OK</div>\n");
        html.append("                </div>\n");
        html.append("                <div class=\"stat-card failed\">\n");
        html.append("                    <div class=\"stat-icon\">\u274C</div>\n");
        html.append("                    <div class=\"stat-number\">" + stats.getFailedScenarios() + "</div>\n");
        html.append("                    <div class=\"stat-label\">Failed</div>\n");
        html.append("                    <div class=\"stat-subtitle\">Attention</div>\n");
        html.append("                </div>\n");
        html.append("                <div class=\"stat-card skipped\">\n");
        html.append("                    <div class=\"stat-icon\">\u23ED\uFE0F</div>\n");
        html.append("                    <div class=\"stat-number\">" + stats.getSkippedScenarios() + "</div>\n");
        html.append("                    <div class=\"stat-label\">Skipped</div>\n");
        html.append("                    <div class=\"stat-subtitle\">Review</div>\n");
        html.append("                </div>\n");
        html.append("            </div>\n");
        html.append("        </div>\n");

        return html.toString();
    }

    private static String generateFeatureBreakdown(CucumberReportParser.DetailedTestStatistics stats) {
        StringBuilder html = new StringBuilder();

        html.append("        <div class=\"feature-section\">\n");
        html.append("            <h2 class=\"section-title\">\uD83C\uDFED Feature Breakdown</h2>\n");

        // Add the requested summary table
        html.append(generateFeatureSummaryTable(stats));

        html.append("        </div>\n");
        return html.toString();
    }

    private static String generateFeatureSummaryTable(CucumberReportParser.DetailedTestStatistics stats) {
        StringBuilder html = new StringBuilder();

        html.append("            <div style=\"margin-bottom: 30px;\">\n");
        html.append("                <h3 style=\"text-align: center; margin-bottom: 20px; color: #1a1a1a; font-weight: 700;\">📋 Feature Summary Table</h3>\n");
        html.append("                <table style=\"width: 100%; border-collapse: collapse; margin: 20px 0; box-shadow: 0 4px 12px rgba(0,0,0,0.12); border-radius: 8px; overflow: hidden;\">\n");
        html.append("                    <thead>\n");
        html.append("                        <tr style=\"background: linear-gradient(135deg, #2c3e50 0%, #34495e 50%, #4a6741 100%); color: #ffffff;\">\n");
        html.append("                            <th style=\"padding: 18px 15px; text-align: left; border: none; font-weight: 700; font-size: 14px; text-shadow: 1px 1px 2px rgba(0,0,0,0.3);\">📁 Feature Name</th>\n");
        html.append("                            <th style=\"padding: 18px 15px; text-align: center; border: none; font-weight: 700; font-size: 14px; text-shadow: 1px 1px 2px rgba(0,0,0,0.3);\">📊 Total Scenarios</th>\n");
        html.append("                            <th style=\"padding: 18px 15px; text-align: center; border: none; font-weight: 700; font-size: 14px; color: #a8e6cf; text-shadow: 1px 1px 2px rgba(0,0,0,0.3);\">✅ Pass</th>\n");
        html.append("                            <th style=\"padding: 18px 15px; text-align: center; border: none; font-weight: 700; font-size: 14px; color: #ffb3ba; text-shadow: 1px 1px 2px rgba(0,0,0,0.3);\">❌ Fail</th>\n");
        html.append("                            <th style=\"padding: 18px 15px; text-align: center; border: none; font-weight: 700; font-size: 14px; color: #ffd93d; text-shadow: 1px 1px 2px rgba(0,0,0,0.3);\">⏭️ Skip</th>\n");
        html.append("                            <th style=\"padding: 18px 15px; text-align: center; border: none; font-weight: 700; font-size: 14px; color: #dda0dd; text-shadow: 1px 1px 2px rgba(0,0,0,0.3);\">📈 Pass %</th>\n");
        html.append("                            <th style=\"padding: 18px 15px; text-align: center; border: none; font-weight: 700; font-size: 14px; color: #87ceeb; text-shadow: 1px 1px 2px rgba(0,0,0,0.3);\">⏱️ Feature Time</th>\n");
        html.append("                        </tr>\n");
        html.append("                    </thead>\n");
        html.append("                    <tbody>\n");

        for (CucumberReportParser.FeatureResult feature : stats.getFeatureResults()) {
            html.append("                        <tr style=\"background: linear-gradient(145deg, #ffffff 0%, #f8f9fa 100%); border-bottom: 2px solid #e9ecef; transition: all 0.3s ease;\" onmouseover=\"this.style.background='linear-gradient(145deg, #e3f2fd 0%, #f1f8e9 100%)'; this.style.transform='scale(1.01)';\" onmouseout=\"this.style.background='linear-gradient(145deg, #ffffff 0%, #f8f9fa 100%)'; this.style.transform='scale(1)';\">");
            html.append("                            <td style=\"padding: 16px 15px; border: none; font-weight: 600; color: #1a1a1a; font-size: 15px;\">").append(feature.getFeatureName()).append("</td>\n");
            html.append("                            <td style=\"padding: 16px 15px; text-align: center; border: none; font-weight: 700; color: #2980b9; font-size: 16px; background: linear-gradient(135deg, #ebf3fd 0%, #d6eaf8 100%); border-radius: 6px; margin: 2px;\">").append(feature.getTotalScenarios()).append("</td>\n");
            html.append("                            <td style=\"padding: 16px 15px; text-align: center; border: none; font-weight: 700; color: #27ae60; font-size: 16px; background: linear-gradient(135deg, #eafaf1 0%, #d5f4e6 100%); border-radius: 6px; margin: 2px;\">").append(feature.getPassedScenarios()).append("</td>\n");
            html.append("                            <td style=\"padding: 16px 15px; text-align: center; border: none; font-weight: 700; color: #e74c3c; font-size: 16px; background: linear-gradient(135deg, #fdeaea 0%, #fadbd8 100%); border-radius: 6px; margin: 2px;\">").append(feature.getFailedScenarios()).append("</td>\n");
            html.append("                            <td style=\"padding: 16px 15px; text-align: center; border: none; font-weight: 700; color: #f39c12; font-size: 16px; background: linear-gradient(135deg, #fef9e7 0%, #fcf3cf 100%); border-radius: 6px; margin: 2px;\">").append(feature.getSkippedScenarios()).append("</td>\n");
            html.append("                            <td style=\"padding: 16px 15px; text-align: center; border: none; font-weight: 700; color: #8e44ad; font-size: 16px; background: linear-gradient(135deg, #f4ecf7 0%, #e8daef 100%); border-radius: 6px; margin: 2px;\">").append(PERCENTAGE_FORMAT.format(feature.getPassPercentage())).append("%</td>\n");
            html.append("                            <td style=\"padding: 16px 15px; text-align: center; border: none; font-weight: 600; color: #34495e; font-size: 15px; background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); border-radius: 6px; margin: 2px;\">");
            // Display total feature execution time
            long totalFeatureTime = 0L;
            for (CucumberReportParser.ScenarioResult scenario : feature.getScenarios()) {
                totalFeatureTime += scenario.getDuration();
            }
            html.append(formatDuration(totalFeatureTime));
            html.append("</td>\n");
            html.append("                        </tr>\n");
        }

        html.append("                    </tbody>\n");
        html.append("                </table>\n");
        html.append("            </div>\n");

        return html.toString();
    }


    private static String formatDuration(long nanoseconds) {
        if (nanoseconds == 0) return "0ms";

        double seconds = nanoseconds / 1_000_000_000.0;

        if (seconds < 1) {
            return DURATION_FORMAT.format(nanoseconds / 1_000_000.0) + "ms";
        } else if (seconds < 60) {
            return DURATION_FORMAT.format(seconds) + "s";
        } else {
            int minutes = (int) (seconds / 60);
            double remainingSeconds = seconds % 60;
            return minutes + "m " + DURATION_FORMAT.format(remainingSeconds) + "s";
        }
    }

    private static String generateFooter() {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm:ss"));

        StringBuilder html = new StringBuilder();
        html.append("        <div class=\"footer\">\n");
        html.append("            <p>\uD83E\uDD16 Generated by Cucumber Selenium TestNG Automation Framework</p>\n");
        html.append("            <p style=\"margin: 8px 0; font-size: 13px; opacity: 0.9;\">© 2025 Shashi Kant Sharma. All rights reserved.</p>\n");
        html.append("            <p class=\"timestamp\">Report generated on ").append(currentTime).append("</p>\n");
        html.append("            <p style=\"margin-top: 10px; font-size: 12px; opacity: 0.8;\">\n");
        html.append("                For more details, please check the attached Extent Report\n");
        html.append("            </p>\n");
        html.append("        </div>\n");
        html.append("    </div>\n");
        html.append("</body>\n");
        html.append("</html>\n");

        return html.toString();
    }
}
