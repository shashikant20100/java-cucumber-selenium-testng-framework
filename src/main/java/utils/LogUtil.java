package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogUtil {
    private static final Logger logger = LogManager.getLogger(LogUtil.class);
    
    public static void info(String message) {
        logger.info(message);
        System.out.println("[INFO] " + message);
    }
    
    public static void error(String message) {
        logger.error(message);
        System.err.println("[ERROR] " + message);
    }
    
    public static void error(String message, Throwable throwable) {
        logger.error(message, throwable);
        System.err.println("[ERROR] " + message + " - " + throwable.getMessage());
    }
    
    public static void warn(String message) {
        logger.warn(message);
        System.out.println("[WARN] " + message);
    }
    
    public static void debug(String message) {
        logger.debug(message);
        System.out.println("[DEBUG] " + message);
    }
    
    public static void trace(String message) {
        logger.trace(message);
        System.out.println("[TRACE] " + message);
    }
    
    public static void startTest(String testName) {
        String message = "========== Starting Test: " + testName + " ==========";
        info(message);
    }
    
    public static void endTest(String testName) {
        String message = "========== Ending Test: " + testName + " ==========";
        info(message);
    }
    
    public static void step(String stepDescription) {
        String message = "STEP: " + stepDescription;
        info(message);
    }
}