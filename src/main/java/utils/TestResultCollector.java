package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test result collector without TestNG dependencies
 */
public class TestResultCollector {

    private static final AtomicInteger totalTests = new AtomicInteger(0);
    private static final AtomicInteger passedTests = new AtomicInteger(0);
    private static final AtomicInteger failedTests = new AtomicInteger(0);
    private static final AtomicInteger skippedTests = new AtomicInteger(0);

    private static String testStartTime;
    private static String testEndTime;

    /**
     * Start test collection
     */
    public static void startTestSuite() {
        testStartTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LogUtil.info("Test suite started at: " + testStartTime);

        // Reset counters
        totalTests.set(0);
        passedTests.set(0);
        failedTests.set(0);
        skippedTests.set(0);
    }

    /**
     * End test collection
     */
    public static void endTestSuite() {
        testEndTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LogUtil.info("Test suite finished at: " + testEndTime);

        LogUtil.info("Final test statistics - Total: " + totalTests.get() +
                    ", Passed: " + passedTests.get() +
                    ", Failed: " + failedTests.get() +
                    ", Skipped: " + skippedTests.get());
    }

    /**
     * Set test counts directly
     */
    public static void setTestCounts(int total, int passed, int failed, int skipped) {
        totalTests.set(total);
        passedTests.set(passed);
        failedTests.set(failed);
        skippedTests.set(skipped);
    }

    /**
     * Record a test success
     */
    public static void recordTestSuccess(String testName) {
        passedTests.incrementAndGet();
        LogUtil.info("Test PASSED: " + testName);
    }

    /**
     * Record a test failure
     */
    public static void recordTestFailure(String testName) {
        failedTests.incrementAndGet();
        LogUtil.error("Test FAILED: " + testName);
    }

    /**
     * Record a test skipped
     */
    public static void recordTestSkipped(String testName) {
        skippedTests.incrementAndGet();
        LogUtil.warn("Test SKIPPED: " + testName);
    }

    /**
     * Get current test statistics
     */
    public static EmailUtil.TestStatistics getTestStatistics() {
        String executionTime = testStartTime + " to " +
                              (testEndTime != null ? testEndTime : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String reportPath = ExtentManager.getReportPath();

        return new EmailUtil.TestStatistics(
            totalTests.get(),
            passedTests.get(),
            failedTests.get(),
            skippedTests.get(),
            executionTime,
            reportPath
        );
    }

    /**
     * Send professional dashboard email with Cucumber JSON data
     */
    public static boolean sendProfessionalReport() {
        try {
            LogUtil.info("Sending professional dashboard email with Cucumber JSON data...");
            return EmailUtil.sendProfessionalDashboard();
        } catch (Exception e) {
            LogUtil.error("Failed to send professional dashboard email", e);
            return false;
        }
    }

    /**
     * Reset all counters (useful for multiple test runs)
     */
    public static void resetCounters() {
        totalTests.set(0);
        passedTests.set(0);
        failedTests.set(0);
        skippedTests.set(0);
        testStartTime = null;
        testEndTime = null;
    }
}
