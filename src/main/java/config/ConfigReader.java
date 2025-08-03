package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import utils.LogUtil;

public class ConfigReader {
    private static Properties properties;
    private static final String CONFIG_FILE_PATH = "src/main/resources/config.properties";
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        try {
            properties = new Properties();
            FileInputStream fileInputStream = new FileInputStream(CONFIG_FILE_PATH);
            properties.load(fileInputStream);
            fileInputStream.close();
            LogUtil.info("Configuration properties loaded successfully");
        } catch (IOException e) {
            LogUtil.error("Failed to load configuration properties: " + e.getMessage());
            throw new RuntimeException("Configuration file not found or cannot be read", e);
        }
    }
    
    public static String getProperty(String key) {
        return getProperty(key, null);
    }
    
    public static String getProperty(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null) {
            value = properties.getProperty(key, defaultValue);
        }
        return value;
    }
    
    public static String getBrowser() {
        return getProperty("browser", "chrome");
    }
    
    public static String getUrl() {
        return getProperty("url", "https://www.google.com");
    }
    
    public static String getEnvironment() {
        return getProperty("environment", "dev");
    }
    
    public static boolean getHeadlessMode() {
        return Boolean.parseBoolean(getProperty("headless", "false"));
    }
    
    public static int getImplicitWait() {
        return Integer.parseInt(getProperty("implicit.wait", "10"));
    }
    
    public static int getExplicitWait() {
        return Integer.parseInt(getProperty("explicit.wait", "20"));
    }
    
    public static int getPageLoadTimeout() {
        return Integer.parseInt(getProperty("page.load.timeout", "30"));
    }
    
    public static String getScreenshotPath() {
        return getProperty("screenshot.path", "output/screenshots/");
    }
    
    public static String getReportPath() {
        return getProperty("report.path", "output/reports/");
    }
    
    public static String getLogPath() {
        return getProperty("log.path", "logs/");
    }
    
    public static boolean getTakeScreenshotOnFailure() {
        return Boolean.parseBoolean(getProperty("screenshot.on.failure", "true"));
    }
    
    public static String getGridUrl() {
        return getProperty("grid.url", "http://localhost:4444/wd/hub");
    }
    
    public static String getRemoteBrowser() {
        return getProperty("remote.browser", "chrome");
    }
    
    public static int getThreadCount() {
        return Integer.parseInt(getProperty("thread.count", "1"));
    }
    
    public static boolean getParallelExecution() {
        return Boolean.parseBoolean(getProperty("parallel.execution", "false"));
    }

    // Email Configuration Methods
    public static boolean isEmailEnabled() {
        return Boolean.parseBoolean(getProperty("email.enabled", "false"));
    }

    public static String getEmailSmtpHost() {
        return getProperty("email.smtp.host", "smtp.gmail.com");
    }

    public static String getEmailSmtpPort() {
        return getProperty("email.smtp.port", "587");
    }

    public static String getEmailFrom() {
        return getProperty("email.from", "");
    }

    public static String getEmailPassword() {
        return getProperty("email.password", "");
    }

    public static String[] getEmailTo() {
        String toEmails = getProperty("email.to", "");
        return toEmails.isEmpty() ? new String[0] : toEmails.split(",");
    }

    public static String[] getEmailCc() {
        String ccEmails = getProperty("email.cc", "");
        return ccEmails.isEmpty() ? new String[0] : ccEmails.split(",");
    }

    public static String getEmailSubjectPrefix() {
        return getProperty("email.subject.prefix", "Automation Test Report");
    }

    public static int getEmailMaxFileSizeMb() {
        return Integer.parseInt(getProperty("email.max.file.size.mb", "10"));
    }
}