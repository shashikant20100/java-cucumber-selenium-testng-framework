package utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import drivers.DriverFactory;
import config.ConfigReader;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtil {
    
    public static String captureScreenshot(String testName) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver == null) {
                LogUtil.warn("Driver is null, cannot capture screenshot");
                return null;
            }

            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = testName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";

            String screenshotDir = ConfigReader.getScreenshotPath();
            File directory = new File(screenshotDir);
            if (!directory.exists() && !directory.mkdirs()) {
                LogUtil.error("Failed to create screenshot directory: " + screenshotDir);
                return null;
            }

            File destFile = new File(screenshotDir + fileName);
            FileUtils.copyFile(sourceFile, destFile);

            String screenshotPath = destFile.getAbsolutePath();
            LogUtil.info("Screenshot captured: " + screenshotPath);
            return screenshotPath;

        } catch (IOException e) {
            LogUtil.error("Failed to save screenshot file: " + e.getMessage(), e);
        } catch (Exception e) {
            LogUtil.error("Unexpected error while capturing screenshot: " + e.getMessage(), e);
        }
        return null;
    }
    
    public static String captureScreenshot() {
        return captureScreenshot("screenshot");
    }
    
    public static byte[] captureScreenshotAsBytes() {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver == null) {
                LogUtil.warn("Driver is null, cannot capture screenshot");
                return null;
            }
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            byte[] screenshotBytes = takesScreenshot.getScreenshotAs(OutputType.BYTES);

            // Create a run-specific folder based on date and time: DD_MM_YYYY_HH_MM
            String runFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm"));
            String screenshotDir = ConfigReader.getScreenshotPath() + runFolder + File.separator;
            File directory = new File(screenshotDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Use thread name and timestamp for screenshot file name
            String testName = Thread.currentThread().getName();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = testName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";
            File destFile = new File(screenshotDir + fileName);
            FileUtils.writeByteArrayToFile(destFile, screenshotBytes);
            LogUtil.info("Screenshot captured and saved: " + destFile.getAbsolutePath());
            return screenshotBytes;
        } catch (Exception e) {
            LogUtil.error("Failed to capture screenshot as bytes: " + e.getMessage());
            return null;
        }
    }
    
    public static String captureScreenshotBase64() {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver == null) {
                LogUtil.warn("Driver is null, cannot capture screenshot");
                return null;
            }
            
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            String base64Screenshot = takesScreenshot.getScreenshotAs(OutputType.BASE64);
            LogUtil.info("Screenshot captured as Base64");
            return base64Screenshot;
            
        } catch (Exception e) {
            LogUtil.error("Failed to capture screenshot as Base64: " + e.getMessage());
            return null;
        }
    }

    public static byte[] captureScreenshotAsBytes(String scenarioName) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver == null) {
                LogUtil.warn("Driver is null, cannot capture screenshot");
                return null;
            }
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            byte[] screenshotBytes = takesScreenshot.getScreenshotAs(OutputType.BYTES);

            String runFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm"));
            String screenshotDir = ConfigReader.getScreenshotPath() + runFolder + File.separator;
            File directory = new File(screenshotDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = scenarioName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";
            File destFile = new File(screenshotDir + fileName);
            FileUtils.writeByteArrayToFile(destFile, screenshotBytes);
            LogUtil.info("Screenshot captured and saved: " + destFile.getAbsolutePath());
            return screenshotBytes;
        } catch (Exception e) {
            LogUtil.error("Failed to capture screenshot as bytes: " + e.getMessage());
            return null;
        }
    }

    public static String captureScreenshotBase64(String scenarioName) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver == null) {
                LogUtil.warn("Driver is null, cannot capture screenshot");
                return null;
            }
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            String base64Screenshot = takesScreenshot.getScreenshotAs(OutputType.BASE64);
            LogUtil.info("Screenshot captured as Base64 for scenario: " + scenarioName);
            return base64Screenshot;
        } catch (Exception e) {
            LogUtil.error("Failed to capture screenshot as Base64: " + e.getMessage());
            return null;
        }
    }

    public static byte[] captureScreenshotAsBytes(String scenarioName, String featureName) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver == null) {
                LogUtil.warn("Driver is null, cannot capture screenshot");
                return null;
            }
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            byte[] screenshotBytes = takesScreenshot.getScreenshotAs(OutputType.BYTES);

            // Create feature-specific folder structure
            String featureFolder = featureName.replaceAll("[^a-zA-Z0-9]", "_");
            String screenshotDir = ConfigReader.getScreenshotPath() + featureFolder + File.separator;
            File directory = new File(screenshotDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = scenarioName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";
            File destFile = new File(screenshotDir + fileName);
            FileUtils.writeByteArrayToFile(destFile, screenshotBytes);
            LogUtil.info("Screenshot captured and saved: " + destFile.getAbsolutePath());
            return screenshotBytes;
        } catch (Exception e) {
            LogUtil.error("Failed to capture screenshot as bytes: " + e.getMessage());
            return null;
        }
    }

    public static String captureScreenshotBase64(String scenarioName, String featureName) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver == null) {
                LogUtil.warn("Driver is null, cannot capture screenshot");
                return null;
            }
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            String base64Screenshot = takesScreenshot.getScreenshotAs(OutputType.BASE64);
            LogUtil.info("Screenshot captured as Base64 for scenario: " + scenarioName + " in feature: " + featureName);
            return base64Screenshot;
        } catch (Exception e) {
            LogUtil.error("Failed to capture screenshot as Base64: " + e.getMessage());
            return null;
        }
    }

    private static void deleteRecursively(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteRecursively(child);
            }
        }
        file.delete();
    }

    public static void deleteAllScreenshots() {
        String screenshotDir = ConfigReader.getScreenshotPath();
        File directory = new File(screenshotDir);
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                deleteRecursively(file);
            }
        }
    }
}