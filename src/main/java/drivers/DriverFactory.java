package drivers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import io.github.bonigarcia.wdm.WebDriverManager;

import config.ConfigReader;
import utils.LogUtil;

import java.net.URL;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DriverFactory {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final Set<WebDriver> allDrivers = Collections.synchronizedSet(new HashSet<>());
    private static final int IMPLICIT_WAIT_SECONDS = 10;
    private static final int PAGE_LOAD_TIMEOUT_SECONDS = 30;

    private DriverFactory() {}

    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            String browserName = ConfigReader.getBrowser();
            WebDriver driver = createDriver(browserName);
            driverThreadLocal.set(driver);
            allDrivers.add(driver);
        }
        return driverThreadLocal.get();
    }

    @Deprecated
    public static WebDriver getDriver(String browserName) {
        return getDriver();
    }

    private static WebDriver createDriver(String browserName) {
        WebDriver driver;
        String browser = browserName.toLowerCase().trim();
        boolean headless = ConfigReader.getHeadlessMode();
        try {
            LogUtil.info("Initializing driver for browser: " + browser + ", headless: " + headless);
            switch (browser) {
                case "chrome":
                    driver = createChromeDriver(headless);
                    break;
                case "firefox":
                    driver = createFirefoxDriver(headless);
                    break;
                case "edge":
                    driver = createEdgeDriver(headless);
                    break;
                case "safari":
                    driver = createSafariDriver();
                    break;
                case "remote":
                    driver = createRemoteDriver();
                    break;
                default:
                    LogUtil.warn("Browser '" + browser + "' not recognized. Using Chrome as default.");
                    driver = createChromeDriver(headless);
                    break;
            }
            LogUtil.info("Driver initialized successfully for browser: " + browser);
            configureDriver(driver);
            return driver;

        } catch (Exception e) {
            LogUtil.error("Failed to initialize driver: " + e.getMessage());
            throw new RuntimeException("Driver initialization failed", e);
        }
    }

    private static WebDriver createChromeDriver(boolean headless) {
        try {
            LogUtil.info("Setting up ChromeDriver with headless=" + headless);

            WebDriverManager.chromedriver().setup();
            LogUtil.info("ChromeDriver setup completed");

            ChromeOptions options = new ChromeOptions();

            String downloadPath = createDownloadDirectory();
            Map<String, Object> prefs = new java.util.HashMap<>();
            prefs.put("profile.default_content_settings.popups", 0);
            prefs.put("download.default_directory", downloadPath);
            prefs.put("download.prompt_for_download", false);
            prefs.put("download.directory_upgrade", true);
            prefs.put("safebrowsing.enabled", true);
            options.setExperimentalOption("prefs", prefs);

            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-web-security");
            options.addArguments("--allow-running-insecure-content");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--disable-blink-features=AutomationControlled");

            if (headless) {
                options.addArguments("--headless=new");
                LogUtil.info("Chrome running in headless mode");
            }

            options.setExperimentalOption("useAutomationExtension", false);
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

            LogUtil.info("Creating Chrome driver with options. Download directory: " + downloadPath);
            return new ChromeDriver(options);

        } catch (Exception e) {
            LogUtil.error("Failed to create Chrome driver: " + e.getMessage());
            throw new RuntimeException("Chrome driver creation failed", e);
        }
    }

    private static String createDownloadDirectory() {
        try {
            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String threadId = String.valueOf(Thread.currentThread().getId());
            String downloadFolderName = "downloads_" + timestamp + "_thread_" + threadId;
            java.io.File projectDir = new java.io.File(System.getProperty("user.dir"));
            java.io.File downloadDir = new java.io.File(projectDir, "output/downloads/" + downloadFolderName);

            if (!downloadDir.exists() && !downloadDir.mkdirs()) {
                throw new RuntimeException("Failed to create download directory: " + downloadDir.getAbsolutePath());
            }

            return downloadDir.getAbsolutePath();
        } catch (Exception e) {
            LogUtil.error("Failed to create download directory: " + e.getMessage());
            throw new RuntimeException("Download directory creation failed", e);
        }
    }

    private static WebDriver createFirefoxDriver(boolean headless) {
        try {
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions options = new FirefoxOptions();

            if (headless) {
                options.addArguments("--headless");
                LogUtil.info("Firefox running in headless mode");
            }

            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
            options.addPreference("dom.webnotifications.enabled", false);
            options.addPreference("media.volume_scale", "0.0");

            return new FirefoxDriver(options);

        } catch (Exception e) {
            LogUtil.error("Failed to create Firefox driver: " + e.getMessage());
            throw new RuntimeException("Firefox driver creation failed", e);
        }
    }

    private static WebDriver createEdgeDriver(boolean headless) {
        try {
            WebDriverManager.edgedriver().setup();
            EdgeOptions options = new EdgeOptions();

            if (headless) {
                options.addArguments("--headless");
                LogUtil.info("Edge running in headless mode");
            }

            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");

            return new EdgeDriver(options);

        } catch (Exception e) {
            LogUtil.error("Failed to create Edge driver: " + e.getMessage());
            throw new RuntimeException("Edge driver creation failed", e);
        }
    }

    private static WebDriver createSafariDriver() {
        try {
            killSafariProcesses();
            Thread.sleep(2000);
            return createSafariDriverWithRetry();
        } catch (Exception e) {
            LogUtil.warn("Safari failed, falling back to Chrome");
            return createChromeDriver(false);
        }
    }

    private static WebDriver createSafariDriverWithRetry() throws Exception {
        int maxRetries = 3;
        Exception lastException = null;

        for (int i = 0; i < maxRetries; i++) {
            try {
                return new SafariDriver();
            } catch (Exception e) {
                lastException = e;
                killSafariProcesses();
                Thread.sleep(3000);
            }
        }

        throw new RuntimeException("Failed to create Safari driver after retries", lastException);
    }

    private static void killSafariProcesses() {
        try {
            Runtime.getRuntime().exec("pkill -f Safari");
            Runtime.getRuntime().exec("pkill -f safaridriver");
            Runtime.getRuntime().exec("pkill -f 'Safari.*WebDriver'");
        } catch (Exception e) {
            LogUtil.warn("Failed to kill Safari processes: " + e.getMessage());
        }
    }

    private static WebDriver createRemoteDriver() {
        try {
            String hubUrl = ConfigReader.getProperty("grid.url", "http://localhost:4444/wd/hub");
            String browser = ConfigReader.getProperty("remote.browser", "chrome");
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setBrowserName(browser);
            return new RemoteWebDriver(new URL(hubUrl), capabilities);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create remote driver", e);
        }
    }

    private static void configureDriver(WebDriver driver) {
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT_SECONDS));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(PAGE_LOAD_TIMEOUT_SECONDS));
            driver.manage().window().maximize();
        } catch (Exception e) {
            LogUtil.warn("Failed to configure driver: " + e.getMessage());
        }
    }

    public static void quitAllDrivers() {
        synchronized (allDrivers) {
            allDrivers.forEach(driver -> {
                try {
                    if (driver != null) driver.quit();
                } catch (Exception e) {
                    LogUtil.error("Error closing browser: " + e.getMessage());
                }
            });
            allDrivers.clear();
            driverThreadLocal.remove();
        }
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                LogUtil.error("Error while quitting driver: " + e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }

    public static void removeDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            allDrivers.remove(driver);
            driverThreadLocal.remove();
        }
    }

    public static boolean isDriverActive() {
        return driverThreadLocal.get() != null;
    }
}
