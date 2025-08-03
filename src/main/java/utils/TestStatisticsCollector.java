package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple test statistics collector that doesn't depend on TestNG listeners
 */
public class TestStatisticsCollector {

    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;
    private static int skippedTests = 0;
    private static String testStartTime;
    private static String testEndTime;

    /**
     * Initialize test statistics collection
     */
    public static void initializeTestRun() {
        testStartTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        totalTests = 0;
        passedTests = 0;
        failedTests = 0;
        skippedTests = 0;
        LogUtil.info("Test statistics collection initialized at: " + testStartTime);
    }

    /**
     * Finalize test statistics collection
     */
    public static void finalizeTestRun() {
        testEndTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LogUtil.info("Test statistics collection finalized at: " + testEndTime);
    }

    /**
     * Manually set test statistics (can be called from TestRunner)
     */
    public static void setTestStatistics(int total, int passed, int failed, int skipped) {
        totalTests = total;
        passedTests = passed;
        failedTests = failed;
        skippedTests = skipped;

        LogUtil.info("Test statistics updated - Total: " + total +
                    ", Passed: " + passed +
                    ", Failed: " + failed +
                    ", Skipped: " + skipped);
    }

    /**
     * Get current test statistics for email reporting
     */
    public static EmailUtil.TestStatistics getTestStatistics() {
        String executionTime = testStartTime + " to " +
                              (testEndTime != null ? testEndTime : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String reportPath = ExtentManager.getReportPath();

        return new EmailUtil.TestStatistics(
            totalTests,
            passedTests,
            failedTests,
            skippedTests,
            executionTime,
            reportPath
        );
    }

    /**
     * Reset all counters
     */
    public static void resetCounters() {
        totalTests = 0;
        passedTests = 0;
        failedTests = 0;
        skippedTests = 0;
        testStartTime = null;
        testEndTime = null;
    }
}
