package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.ITestResult;
import org.testng.ITestListener;
import utils.ExtentManager;
import utils.LogUtil;
import utils.EmailUtil;
import utils.TestStatisticsCollector;
import utils.CucumberReportParser;
import drivers.DriverFactory;
import config.ConfigReader;

@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"stepdefinitions", "hooks"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/html",
        "json:target/cucumber-reports/json/Cucumber.json",
        "junit:target/cucumber-reports/xml/Cucumber.xml"
    },
    monochrome = true,
    publish = true
)
public class TestRunner extends AbstractTestNGCucumberTests implements ITestListener {

    @BeforeSuite
    public void setUp() {
        // Comprehensive logs directory management - delete and recreate fresh
        utils.LogCleanupUtil.prepareLogsDirectoryForAutomation();

        // Create automation log header to initialize log files
        utils.LogCleanupUtil.createAutomationLogHeader();

        // Initialize test statistics collection
        TestStatisticsCollector.initializeTestRun();

        LogUtil.info("Starting Cucumber Test Execution");

        // Configure thread count from config.properties
        int threadCount = ConfigReader.getThreadCount();
        System.setProperty("dataproviderthreadcount", String.valueOf(threadCount));
        LogUtil.info("Test execution configured with thread count: " + threadCount);

        // Initialize ExtentReports
        ExtentManager.createInstance();
        LogUtil.info("ExtentReports initialized successfully");

        // Smart cleanup: only delete screenshots older than 7 days, preserve recent ones
        utils.ScreenshotUtil.deleteAllScreenshots();
        LogUtil.info("Smart screenshot cleanup completed before test suite execution");
    }

    @AfterSuite
    public void tearDown() {
        // Finalize test statistics collection
        TestStatisticsCollector.finalizeTestRun();

        // Flush ExtentReports
        ExtentManager.flush();
        LogUtil.info("ExtentReports flushed successfully");

        // Get actual test execution statistics from CucumberReportParser
        CucumberReportParser.DetailedTestStatistics detailedStats = null;
        try {
            detailedStats = utils.CucumberReportParser.parseReport();

            // Set actual test statistics from parsed results
            TestStatisticsCollector.setTestStatistics(
                detailedStats.getTotalScenarios(),
                detailedStats.getPassedScenarios(),
                detailedStats.getFailedScenarios(),
                detailedStats.getSkippedScenarios()
            );

            LogUtil.info("Test statistics collected from actual execution results");
        } catch (Exception e) {
            LogUtil.warn("Could not parse cucumber report for statistics, using fallback method: " + e.getMessage());

            // Fallback: Try to get statistics from TestNG results if available
            // For now, set to 0 until actual results are available
            TestStatisticsCollector.setTestStatistics(0, 0, 0, 0);
        }

        // Get test execution statistics
        EmailUtil.TestStatistics testStats = TestStatisticsCollector.getTestStatistics();

        // Log final test statistics
        LogUtil.info("================================================================================");
        LogUtil.info("                    FINAL TEST EXECUTION STATISTICS");
        LogUtil.info("================================================================================");
        LogUtil.info("Total Test Cases: " + testStats.getTotalTestCases());
        LogUtil.info("Passed Test Cases: " + testStats.getPassedTestCases());
        LogUtil.info("Failed Test Cases: " + testStats.getFailedTestCases());
        LogUtil.info("Skipped Test Cases: " + testStats.getSkippedTestCases());
        LogUtil.info("Pass Percentage: " + String.format("%.1f%%", testStats.getPassPercentage()));
        LogUtil.info("================================================================================");

        // Send professional dashboard email with feature breakdown table if enabled
        if (ConfigReader.isEmailEnabled()) {
            LogUtil.info("Email reporting is enabled. Attempting to send professional dashboard report...");

            if (detailedStats != null) {
                // Send professional dashboard with feature breakdown table
                boolean emailSent = EmailUtil.sendProfessionalDashboard();
                if (emailSent) {
                    LogUtil.info("Professional dashboard email with feature breakdown sent successfully");
                } else {
                    LogUtil.warn("Failed to send professional dashboard email");
                }
            } else {
                // Fallback to basic email if detailed stats are not available
                boolean emailSent = EmailUtil.sendExtentReport(testStats);
                if (emailSent) {
                    LogUtil.info("Basic extent report email sent successfully");
                } else {
                    LogUtil.warn("Failed to send extent report email");
                }
            }
        } else {
            LogUtil.info("Email reporting is disabled in configuration");
        }

        // Close all drivers
        DriverFactory.quitAllDrivers();
        LogUtil.info("Successfully closed all browser instances");
        LogUtil.info("Completed Cucumber Test Execution");

        // Print report location
        System.out.println("========================================");
        System.out.println("ExtentReports generated at: " + ExtentManager.getReportPath());
        System.out.println("========================================");
    }

    // TestNG listener methods to track test execution
    @Override
    public void onTestStart(ITestResult result) {
        // Test started - this will be called for each scenario
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // Test passed - increment passed counter if needed
    }

    @Override
    public void onTestFailure(ITestResult result) {
        // Test failed - increment failed counter if needed
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        // Test skipped - increment skipped counter if needed
    }

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        // Always set the thread count from config, regardless of parallel.execution setting
        int threadCount = ConfigReader.getThreadCount();
        System.setProperty("dataproviderthreadcount", String.valueOf(threadCount));

        LogUtil.info("Configured thread count: " + threadCount);
        LogUtil.info("Parallel execution: " + ConfigReader.getParallelExecution());

        return super.scenarios();
    }
}