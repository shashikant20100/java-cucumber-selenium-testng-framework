package utils;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import config.ConfigReader;

/**
 * Utility class for sending Extent reports via email with test execution statistics
 */
public class EmailUtil {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Test execution statistics data class
     */
    public static class TestStatistics {
        private int totalTestCases;
        private int passedTestCases;
        private int failedTestCases;
        private int skippedTestCases;
        private double passPercentage;
        private String executionTime;
        private String reportPath;

        public TestStatistics(int total, int passed, int failed, int skipped, String execTime, String path) {
            this.totalTestCases = total;
            this.passedTestCases = passed;
            this.failedTestCases = failed;
            this.skippedTestCases = skipped;
            this.passPercentage = total > 0 ? (double) passed / total * 100 : 0;
            this.executionTime = execTime;
            this.reportPath = path;
        }

        // Getters
        public int getTotalTestCases() { return totalTestCases; }
        public int getPassedTestCases() { return passedTestCases; }
        public int getFailedTestCases() { return failedTestCases; }
        public int getSkippedTestCases() { return skippedTestCases; }
        public double getPassPercentage() { return passPercentage; }
        public String getExecutionTime() { return executionTime; }
        public String getReportPath() { return reportPath; }
    }

    /**
     * Send Extent report via email if file size is less than configured limit
     */
    public static boolean sendExtentReport(TestStatistics stats) {
        try {
            // Check if email is enabled
            if (!ConfigReader.isEmailEnabled()) {
                LogUtil.info("Email reporting is disabled in configuration");
                return false;
            }

            String[] toEmails = ConfigReader.getEmailTo();
            if (toEmails.length == 0) {
                LogUtil.warn("No recipient email addresses configured");
                return false;
            }

            LogUtil.info("Starting email report sending process");

            File reportFile = new File(stats.getReportPath());

            // Check if report file exists
            if (!reportFile.exists()) {
                LogUtil.error("Extent report file not found: " + stats.getReportPath());
                return false;
            }

            // Check file size
            long fileSize = reportFile.length();
            long maxFileSize = ConfigReader.getEmailMaxFileSizeMb() * 1024 * 1024; // Convert MB to bytes
            LogUtil.info("Report file size: " + formatFileSize(fileSize));

            if (fileSize > maxFileSize) {
                LogUtil.warn("Report file size (" + formatFileSize(fileSize) + ") exceeds " +
                           ConfigReader.getEmailMaxFileSizeMb() + "MB limit. Email not sent.");
                sendFileSizeExceededNotification(toEmails, stats, fileSize);
                return false;
            }

            // Send email with attachment
            return sendEmailWithAttachment(toEmails, stats, reportFile);

        } catch (Exception e) {
            LogUtil.error("Failed to send email report", e);
            return false;
        }
    }

    /**
     * Send email with Extent report attachment
     */
    private static boolean sendEmailWithAttachment(String[] toEmails, TestStatistics stats, File reportFile) {
        try {
            // Configure email properties
            Properties props = getEmailProperties();

            // Create email session
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(ConfigReader.getEmailFrom(), ConfigReader.getEmailPassword());
                }
            });

            // Create email message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(ConfigReader.getEmailFrom()));

            // Add recipients
            for (String email : toEmails) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(email.trim()));
            }

            // Add CC recipients if configured
            String[] ccEmails = ConfigReader.getEmailCc();
            for (String email : ccEmails) {
                if (!email.trim().isEmpty()) {
                    message.addRecipient(Message.RecipientType.CC, new InternetAddress(email.trim()));
                }
            }

            // Set subject
            String subject = String.format("%s - %s (%.1f%% Pass Rate)",
                    ConfigReader.getEmailSubjectPrefix(),
                    stats.getExecutionTime().split(" ")[0],
                    stats.getPassPercentage());
            message.setSubject(subject);

            // Create multipart message
            Multipart multipart = new MimeMultipart();

            // Add email body
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(createEmailBody(stats), "text/html; charset=utf-8");
            multipart.addBodyPart(textPart);

            // Add attachment
            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource(reportFile);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName("ExtentReport.html");
            multipart.addBodyPart(attachmentPart);

            // Set message content
            message.setContent(multipart);

            // Send email
            Transport.send(message);

            LogUtil.info("Email sent successfully to: " + String.join(", ", toEmails));
            LogUtil.info("Report file attached: " + reportFile.getName() + " (" + formatFileSize(reportFile.length()) + ")");

            return true;

        } catch (Exception e) {
            LogUtil.error("Failed to send email with attachment", e);
            return false;
        }
    }

    /**
     * Send notification when file size exceeds limit
     */
    private static void sendFileSizeExceededNotification(String[] toEmails, TestStatistics stats, long fileSize) {
        try {
            Properties props = getEmailProperties();
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(ConfigReader.getEmailFrom(), ConfigReader.getEmailPassword());
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(ConfigReader.getEmailFrom()));

            for (String email : toEmails) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            }

            message.setSubject("Automation Test Report - File Size Exceeded");

            String body = createFileSizeExceededBody(stats, fileSize);
            message.setContent(body, "text/html; charset=utf-8");

            Transport.send(message);
            LogUtil.info("File size exceeded notification sent successfully");

        } catch (Exception e) {
            LogUtil.error("Failed to send file size exceeded notification", e);
        }
    }

    /**
     * Send professional dashboard email with Cucumber JSON report data
     */
    public static boolean sendProfessionalDashboard() {
        try {
            // Check if email is enabled
            if (!ConfigReader.isEmailEnabled()) {
                LogUtil.info("Email reporting is disabled in configuration");
                return false;
            }

            String[] toEmails = ConfigReader.getEmailTo();
            if (toEmails.length == 0) {
                LogUtil.warn("No recipient email addresses configured");
                return false;
            }

            LogUtil.info("Starting professional dashboard email sending process");

            // Parse Cucumber JSON report
            CucumberReportParser.DetailedTestStatistics detailedStats = CucumberReportParser.parseReport();

            if (detailedStats.getTotalFeatures() == 0) {
                LogUtil.warn("No test data found in Cucumber JSON report");
                return false;
            }

            File reportFile = new File(detailedStats.getReportPath());

            // Check if report file exists
            if (!reportFile.exists()) {
                LogUtil.error("Extent report file not found: " + detailedStats.getReportPath());
                return false;
            }

            // Check file size
            long fileSize = reportFile.length();
            long maxFileSize = ConfigReader.getEmailMaxFileSizeMb() * 1024 * 1024; // Convert MB to bytes
            LogUtil.info("Report file size: " + formatFileSize(fileSize));

            if (fileSize > maxFileSize) {
                LogUtil.warn("Report file size (" + formatFileSize(fileSize) + ") exceeds " +
                           ConfigReader.getEmailMaxFileSizeMb() + "MB limit. Sending dashboard without attachment.");
                return sendDashboardWithoutAttachment(toEmails, detailedStats);
            }

            // Send email with professional dashboard and attachment
            return sendDashboardWithAttachment(toEmails, detailedStats, reportFile);

        } catch (Exception e) {
            LogUtil.error("Failed to send professional dashboard email", e);
            return false;
        }
    }

    /**
     * Send professional dashboard email with attachment
     */
    private static boolean sendDashboardWithAttachment(String[] toEmails, CucumberReportParser.DetailedTestStatistics stats, File reportFile) {
        try {
            // Configure email properties
            Properties props = getEmailProperties();

            // Create email session
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(ConfigReader.getEmailFrom(), ConfigReader.getEmailPassword());
                }
            });

            // Create email message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(ConfigReader.getEmailFrom()));

            // Add recipients
            for (String email : toEmails) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(email.trim()));
            }

            // Add CC recipients if configured
            String[] ccEmails = ConfigReader.getEmailCc();
            for (String email : ccEmails) {
                if (!email.trim().isEmpty()) {
                    message.addRecipient(Message.RecipientType.CC, new InternetAddress(email.trim()));
                }
            }

            // Set subject with detailed statistics
            String subject = String.format("🚀 Test Report - %d Features, %d Scenarios (%.1f%% Pass Rate) - %s",
                    stats.getTotalFeatures(),
                    stats.getTotalScenarios(),
                    stats.getScenarioPassPercentage(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            message.setSubject(subject);

            // Create multipart message
            Multipart multipart = new MimeMultipart();

            // Add professional dashboard email body
            MimeBodyPart htmlPart = new MimeBodyPart();
            String dashboardHtml = EmailDashboardGenerator.generateDashboard(stats);
            htmlPart.setContent(dashboardHtml, "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);

            // Add attachment
            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource(reportFile);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName("ExtentReport.html");
            multipart.addBodyPart(attachmentPart);

            // Set message content
            message.setContent(multipart);

            // Send email
            Transport.send(message);

            LogUtil.info("Professional dashboard email sent successfully to: " + String.join(", ", toEmails));
            LogUtil.info("Features: " + stats.getTotalFeatures() +
                        ", Scenarios: " + stats.getTotalScenarios() +
                        ", Pass Rate: " + String.format("%.1f%%", stats.getScenarioPassPercentage()));
            LogUtil.info("Report file attached: " + reportFile.getName() + " (" + formatFileSize(reportFile.length()) + ")");

            return true;

        } catch (Exception e) {
            LogUtil.error("Failed to send professional dashboard email with attachment", e);
            return false;
        }
    }

    /**
     * Send professional dashboard email without attachment (when file size exceeds limit)
     */
    private static boolean sendDashboardWithoutAttachment(String[] toEmails, CucumberReportParser.DetailedTestStatistics stats) {
        try {
            Properties props = getEmailProperties();
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(ConfigReader.getEmailFrom(), ConfigReader.getEmailPassword());
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(ConfigReader.getEmailFrom()));

            for (String email : toEmails) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(email.trim()));
            }

            // Add CC recipients if configured
            String[] ccEmails = ConfigReader.getEmailCc();
            for (String email : ccEmails) {
                if (!email.trim().isEmpty()) {
                    message.addRecipient(Message.RecipientType.CC, new InternetAddress(email.trim()));
                }
            }

            String subject = String.format("🚀 Test Report Dashboard - %d Features, %d Scenarios (%.1f%% Pass Rate)",
                    stats.getTotalFeatures(),
                    stats.getTotalScenarios(),
                    stats.getScenarioPassPercentage());
            message.setSubject(subject);

            // Generate dashboard with note about large file
            String dashboardHtml = EmailDashboardGenerator.generateDashboard(stats);
            String noteHtml = "<div style=\"background-color: #fff3cd; border: 1px solid #ffeaa7; color: #856404; " +
                             "padding: 15px; margin: 20px; border-radius: 5px; text-align: center;\">" +
                             "<strong>📎 Note:</strong> The detailed Extent report file was too large to attach. " +
                             "Please check the reports directory on the server for the complete HTML report." +
                             "</div>";

            // Insert note before the closing body tag
            String finalHtml = dashboardHtml.replace("</body>", noteHtml + "</body>");

            message.setContent(finalHtml, "text/html; charset=utf-8");

            Transport.send(message);
            LogUtil.info("Professional dashboard email sent successfully (without attachment due to file size)");

            return true;

        } catch (Exception e) {
            LogUtil.error("Failed to send professional dashboard email without attachment", e);
            return false;
        }
    }

    /**
     * Create email properties for SMTP configuration
     */
    private static Properties getEmailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", ConfigReader.getEmailSmtpHost());
        props.put("mail.smtp.port", ConfigReader.getEmailSmtpPort());
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        return props;
    }

    /**
     * Create HTML email body with test statistics
     */
    private static String createEmailBody(TestStatistics stats) {
        StringBuilder body = new StringBuilder();

        body.append("<!DOCTYPE html>");
        body.append("<html><head>");
        body.append("<style>");
        body.append("body { font-family: Arial, sans-serif; margin: 20px; }");
        body.append(".header { background-color: #4CAF50; color: white; padding: 15px; text-align: center; border-radius: 5px; }");
        body.append(".stats-container { margin: 20px 0; }");
        body.append(".stat-item { display: inline-block; margin: 10px; padding: 15px; border-radius: 5px; text-align: center; min-width: 120px; }");
        body.append(".total { background-color: #2196F3; color: white; }");
        body.append(".passed { background-color: #4CAF50; color: white; }");
        body.append(".failed { background-color: #f44336; color: white; }");
        body.append(".skipped { background-color: #FF9800; color: white; }");
        body.append(".percentage { background-color: #9C27B0; color: white; }");
        body.append(".footer { margin-top: 20px; padding: 15px; background-color: #f1f1f1; border-radius: 5px; }");
        body.append("</style>");
        body.append("</head><body>");

        // Header
        body.append("<div class='header'>");
        body.append("<h2>🚀 Automation Test Execution Report</h2>");
        body.append("<p>Execution Time: ").append(stats.getExecutionTime()).append("</p>");
        body.append("</div>");

        // Statistics
        body.append("<div class='stats-container'>");
        body.append("<h3>📊 Test Execution Statistics</h3>");

        body.append("<div class='stat-item total'>");
        body.append("<h3>").append(stats.getTotalTestCases()).append("</h3>");
        body.append("<p>Total Test Cases</p>");
        body.append("</div>");

        body.append("<div class='stat-item passed'>");
        body.append("<h3>").append(stats.getPassedTestCases()).append("</h3>");
        body.append("<p>Passed Test Cases</p>");
        body.append("</div>");

        body.append("<div class='stat-item failed'>");
        body.append("<h3>").append(stats.getFailedTestCases()).append("</h3>");
        body.append("<p>Failed Test Cases</p>");
        body.append("</div>");

        body.append("<div class='stat-item skipped'>");
        body.append("<h3>").append(stats.getSkippedTestCases()).append("</h3>");
        body.append("<p>Skipped Test Cases</p>");
        body.append("</div>");

        body.append("<div class='stat-item percentage'>");
        body.append("<h3>").append(String.format("%.1f%%", stats.getPassPercentage())).append("</h3>");
        body.append("<p>Pass Percentage</p>");
        body.append("</div>");

        body.append("</div>");

        // Footer
        body.append("<div class='footer'>");
        body.append("<h4>📎 Report Details</h4>");
        body.append("<p><strong>Report File:</strong> ExtentReport.html (attached)</p>");
        body.append("<p><strong>Generated:</strong> ").append(LocalDateTime.now().format(DATE_FORMAT)).append("</p>");
        body.append("<p><strong>Framework:</strong> Cucumber + Selenium + TestNG</p>");
        body.append("<br>");
        body.append("<p><em>This is an automated email from the Test Automation Framework.</em></p>");
        body.append("</div>");

        body.append("</body></html>");

        return body.toString();
    }

    /**
     * Create email body for file size exceeded notification
     */
    private static String createFileSizeExceededBody(TestStatistics stats, long fileSize) {
        StringBuilder body = new StringBuilder();

        body.append("<!DOCTYPE html>");
        body.append("<html><head>");
        body.append("<style>");
        body.append("body { font-family: Arial, sans-serif; margin: 20px; }");
        body.append(".header { background-color: #FF9800; color: white; padding: 15px; text-align: center; border-radius: 5px; }");
        body.append(".warning { background-color: #FFF3E0; border: 1px solid #FFB74D; padding: 15px; border-radius: 5px; margin: 20px 0; }");
        body.append("</style>");
        body.append("</head><body>");

        body.append("<div class='header'>");
        body.append("<h2>⚠️ Test Report - File Size Exceeded</h2>");
        body.append("</div>");

        body.append("<div class='warning'>");
        body.append("<h3>Report file size exceeds email limit</h3>");
        body.append("<p><strong>File Size:</strong> ").append(formatFileSize(fileSize)).append("</p>");
        body.append("<p><strong>Limit:</strong> 10 MB</p>");
        body.append("<p>The report has been generated successfully but cannot be attached due to size restrictions.</p>");
        body.append("<p>Please check the report locally at: <code>").append(stats.getReportPath()).append("</code></p>");
        body.append("</div>");

        // Add statistics summary
        body.append("<h3>📊 Test Execution Summary</h3>");
        body.append("<ul>");
        body.append("<li><strong>Total Test Cases:</strong> ").append(stats.getTotalTestCases()).append("</li>");
        body.append("<li><strong>Passed:</strong> ").append(stats.getPassedTestCases()).append("</li>");
        body.append("<li><strong>Failed:</strong> ").append(stats.getFailedTestCases()).append("</li>");
        body.append("<li><strong>Skipped:</strong> ").append(stats.getSkippedTestCases()).append("</li>");
        body.append("<li><strong>Pass Percentage:</strong> ").append(String.format("%.1f%%", stats.getPassPercentage())).append("</li>");
        body.append("</ul>");

        body.append("</body></html>");

        return body.toString();
    }

    /**
     * Format file size in human readable format
     */
    private static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
