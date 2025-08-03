package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import config.ConfigReader;

import java.io.File;

public class ExtentManager {
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    public static ExtentReports createInstance() {
        if (extent != null) {
            extent.flush();
            extent = null;
        }

        // Ensure only report files are managed in the reports directory
        File reportsDir = new File("output/reports/");
        if (reportsDir.exists() && reportsDir.isDirectory()) {
            File[] files = reportsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".html")) {
                        if (!file.delete()) {
                            LogUtil.warn("Failed to delete file: " + file.getAbsolutePath());
                        }
                    }
                }
            }
        }

        // Create the ExtentReport.html file directly in the reports directory
        String reportFile = "output/reports/ExtentReport.html";
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFile);

        // Configure reporter
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("Cucumber Selenium TestNG Automation Report");
        sparkReporter.config().setReportName("Test Execution Report");
        sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        // Set system information
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("Browser", ConfigReader.getBrowser());
        extent.setSystemInfo("Environment", ConfigReader.getProperty("environment", "test"));
        extent.setSystemInfo("User", System.getProperty("user.name"));
        extent.setSystemInfo("Project", "Cucumber Selenium TestNG Project");

        LogUtil.info("ExtentReports initialized: " + reportFile);
        return extent;
    }

    public static ExtentTest createTest(String testName, String description) {
        if (extent == null) {
            extent = createInstance();
        }
        ExtentTest extentTest = extent.createTest(testName, description);
        test.set(extentTest);
        return extentTest;
    }

    public static ExtentTest getTest() {
        return test.get();
    }

    public static void log(Status status, String message) {
        if (getTest() != null) {
            getTest().log(status, message);
        }
    }

    public static void info(String message) {
        log(Status.INFO, message);
    }

    public static void pass(String message) {
        log(Status.PASS, message);
    }

    public static void fail(String message) {
        log(Status.FAIL, message);
    }

    public static void skip(String message) {
        log(Status.SKIP, message);
    }

    public static void addScreenshot(String base64Screenshot) {
        if (getTest() != null && base64Screenshot != null) {
            getTest().addScreenCaptureFromBase64String(base64Screenshot, "Screenshot");
        }
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
            LogUtil.info("ExtentReports flushed successfully");
        }
    }

    public static String getReportPath() {
        return "output/reports/ExtentReport.html";
    }

    // Enhanced logging methods for step-level details
    public static void logStep(String stepName) {
        if (getTest() != null) {
            getTest().info("<b>STEP:</b> " + stepName);
        }
    }

    public static void logStepWithParams(String stepName, String... params) {
        if (getTest() != null) {
            StringBuilder stepInfo = new StringBuilder("<b>STEP:</b> " + stepName);
            if (params.length > 0) {
                stepInfo.append("<br><b>Parameters:</b> ");
                for (int i = 0; i < params.length; i++) {
                    stepInfo.append(params[i]);
                    if (i < params.length - 1) {
                        stepInfo.append(", ");
                    }
                }
            }
            getTest().info(stepInfo.toString());
        }
    }

    public static void logAction(String action, String element) {
        if (getTest() != null) {
            getTest().info("🔷 <b>Action:</b> " + action + " on <i>" + element + "</i>");
        }
    }

    public static void logVerification(String verification, boolean result) {
        if (getTest() != null) {
            String status = result ? "✅ PASSED" : "❌ FAILED";
            String message = "🔍 <b>Verification:</b> " + verification + " - " + status;
            if (result) {
                getTest().pass(message);
            } else {
                getTest().fail(message);
            }
        }
    }

    public static void logPageNavigation(String fromPage, String toPage) {
        if (getTest() != null) {
            getTest().info("🚀 <b>Navigation:</b> " + fromPage + " → " + toPage);
        }
    }

    public static void logError(String error, Exception e) {
        if (getTest() != null) {
            getTest().fail("❌ <b>Error:</b> " + error + "<br><b>Exception:</b> " + e.getMessage());
        }
    }
}
