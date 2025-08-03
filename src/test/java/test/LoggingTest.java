package test;

import utils.LogUtil;

/**
 * Simple test class to verify log file generation
 */
public class LoggingTest {

    public static void main(String[] args) {
        System.out.println("=== Starting Log File Generation Test ===");

        // Force log initialization
        LogUtil.info("Test log message - INFO level");
        LogUtil.error("Test log message - ERROR level");
        LogUtil.warn("Test log message - WARN level");
        LogUtil.debug("Test log message - DEBUG level");

        LogUtil.startTest("Log File Generation Test");
        LogUtil.step("Step 1: Testing log file creation");
        LogUtil.step("Step 2: Verifying file appenders");
        LogUtil.endTest("Log File Generation Test");

        System.out.println("=== Log File Generation Test Completed ===");
        System.out.println("Check logs/ directory for automation.log and automation-error.log files");
    }
}
