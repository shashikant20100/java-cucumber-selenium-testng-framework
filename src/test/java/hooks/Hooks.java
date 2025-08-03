package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import drivers.DriverFactory;
import utils.LogUtil;
import utils.DirectoryCleanupUtil;
import utils.ScreenshotUtil;
import utils.ExtentManager;
import config.ConfigReader;
import pages.PageObjectManager;
import utils.DownloadManager;

public class Hooks {
    
    /**
     * Clean up files before any test execution starts.
     * This runs once before the entire test suite.
     */
    @BeforeAll
    public static void beforeAll() {
        try {
            // Clean up old download directories and screenshots
            DirectoryCleanupUtil.cleanupOldDownloadDirectories();
            DirectoryCleanupUtil.cleanupOldScreenshots(7); // Keep screenshots for 7 days
            DirectoryCleanupUtil.cleanupOldLogFiles(3); // Keep logs for 3 days

            System.out.println("Pre-test cleanup completed successfully");
        } catch (Exception e) {
            System.err.println("Failed to clean up files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Before
    public void setUp(Scenario scenario) {
        try {
            LogUtil.startTest(scenario.getName());
            LogUtil.info("Setting up test environment");

            // Create ExtentTest for this scenario
            ExtentManager.createTest(scenario.getName(), "Cucumber BDD Test Scenario");
            ExtentManager.info("Starting test: " + scenario.getName());

            // Ensure browser is launched before each scenario
            drivers.DriverFactory.getDriver();
            LogUtil.info("Test setup completed successfully");
            ExtentManager.pass("Test environment setup completed successfully");

            // Navigate to the base URL
            String baseUrl = ConfigReader.getProperty("url");
            if (baseUrl != null && !baseUrl.isEmpty()) {
                drivers.DriverFactory.getDriver().get(baseUrl);
                LogUtil.info("Navigated to URL: " + baseUrl);
            } else {
                LogUtil.warn("Base URL is not configured in config.properties");
            }

            // Initialize page objects through PageObjectManager
            LogUtil.info("Page objects initialized via PageObjectManager");

        } catch (Exception e) {
            LogUtil.error("Failed to setup test environment", e);
            ExtentManager.fail("Failed to setup test environment: " + e.getMessage());
            throw e;
        }
    }
    

    @After
    public void tearDown(Scenario scenario) {
        try {
            LogUtil.info("Starting test cleanup for thread: " + Thread.currentThread().getId());
            ExtentManager.info("Starting test cleanup for: " + scenario.getName());

            if (DriverFactory.isDriverActive()) {
                if (scenario.isFailed() && ConfigReader.getTakeScreenshotOnFailure()) {
                    LogUtil.info("Test failed, capturing screenshot");
                    ExtentManager.fail("Test scenario failed");

                    // Extract feature name from scenario URI or source
                    String featureName = extractFeatureName(scenario);

                    // Capture screenshot as bytes for Cucumber report with feature folder structure
                    byte[] screenshot = ScreenshotUtil.captureScreenshotAsBytes(scenario.getName(), featureName);
                    if (screenshot != null) {
                        scenario.attach(screenshot, "image/png", scenario.getName());
                        LogUtil.info("Screenshot attached to scenario: " + scenario.getName() + " in feature: " + featureName);

                        // Also capture as base64 for ExtentReports
                        String base64Screenshot = ScreenshotUtil.captureScreenshotBase64(scenario.getName(), featureName);
                        if (base64Screenshot != null) {
                            ExtentManager.addScreenshot(base64Screenshot);
                            ExtentManager.info("Screenshot attached to ExtentReports");
                        }
                    }
                } else if (scenario.getStatus().toString().equals("PASSED")) {
                    ExtentManager.pass("Test scenario passed successfully");
                } else if (scenario.getStatus().toString().equals("SKIPPED")) {
                    ExtentManager.skip("Test scenario was skipped");
                }
                
                DriverFactory.quitDriver();
                LogUtil.info("Driver closed successfully for thread: " + Thread.currentThread().getId());
                ExtentManager.info("Browser closed successfully");
            } else {
                LogUtil.info("No active driver found, skipping driver cleanup");
                ExtentManager.info("No active driver found, skipping driver cleanup");
            }
            
            // Clean up download directory for current thread
            DownloadManager.cleanupDownloadDirectory();
            LogUtil.info("Download directory cleaned for thread: " + Thread.currentThread().getId());

            // Clear all page object instances for current thread
            PageObjectManager.clearAllPages();
            LogUtil.info("Page objects cleared for thread: " + Thread.currentThread().getId());

            LogUtil.endTest(scenario.getName());
            ExtentManager.info("Test cleanup completed for: " + scenario.getName());
        } catch (Exception e) {
            LogUtil.error("Error during test cleanup for thread " + Thread.currentThread().getId() + ": " + e.getMessage());
            ExtentManager.fail("Error during test cleanup: " + e.getMessage());
        }
    }

    private String extractFeatureName(Scenario scenario) {
        try {
            // Get feature name from scenario URI
            String uri = scenario.getUri().toString();
            String fileName = uri.substring(uri.lastIndexOf("/") + 1);
            if (fileName.endsWith(".feature")) {
                fileName = fileName.substring(0, fileName.length() - 8); // Remove .feature extension
            }
            return fileName;
        } catch (Exception e) {
            LogUtil.warn("Could not extract feature name from scenario, using default");
            return "UnknownFeature";
        }
    }

    @After
    public void threadCleanup() {
        try {
            // Final cleanup for ThreadLocal resources
            PageObjectManager.removeThreadLocalPages();
            LogUtil.info("ThreadLocal cleanup completed for thread: " + Thread.currentThread().getId());
        } catch (Exception e) {
            LogUtil.warn("Error during ThreadLocal cleanup: " + e.getMessage());
        }
    }
}