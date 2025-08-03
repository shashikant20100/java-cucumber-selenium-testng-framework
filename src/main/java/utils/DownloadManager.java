package utils;

import drivers.DriverFactory;
import config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class DownloadManager {

    private static final int DEFAULT_DOWNLOAD_TIMEOUT_SECONDS = 60;
    private static final ThreadLocal<String> downloadDirectory = new ThreadLocal<>();

    /**
     * Creates a thread-safe download directory for the current test execution
     * @return The absolute path of the created download directory
     */
    public static String createDownloadDirectory() {
        try {
            // Create unique download folder for each thread/test run
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String threadId = String.valueOf(Thread.currentThread().getId());
            String downloadFolderName = "downloads_" + timestamp + "_thread_" + threadId;

            File projectDir = new File(System.getProperty("user.dir"));
            File downloadDir = new File(projectDir, "output/downloads/" + downloadFolderName);

            if (!downloadDir.exists()) {
                if (downloadDir.mkdirs()) {
                    LogUtil.info("Created download directory: " + downloadDir.getAbsolutePath());
                    ExtentManager.logAction("Create directory", "Download folder: " + downloadDir.getName());
                } else {
                    throw new RuntimeException("Failed to create download directory: " + downloadDir.getAbsolutePath());
                }
            }

            // Store the directory path in ThreadLocal for this thread
            downloadDirectory.set(downloadDir.getAbsolutePath());
            return downloadDir.getAbsolutePath();

        } catch (Exception e) {
            LogUtil.error("Failed to create download directory: " + e.getMessage());
            ExtentManager.logError("Failed to create download directory", e);
            throw new RuntimeException("Download directory creation failed", e);
        }
    }

    /**
     * Gets the current thread's download directory
     * @return The download directory path for the current thread
     */
    public static String getDownloadDirectory() {
        String dir = downloadDirectory.get();
        if (dir == null) {
            dir = createDownloadDirectory();
        }
        return dir;
    }

    /**
     * Initiates a file download by clicking on a download element
     * @param downloadElement The WebElement that triggers the download
     * @param expectedFileName The expected name of the downloaded file (can be partial)
     * @return The full path of the downloaded file
     */
    public static String downloadFile(WebElement downloadElement, String expectedFileName) {
        return downloadFile(downloadElement, expectedFileName, DEFAULT_DOWNLOAD_TIMEOUT_SECONDS);
    }

    /**
     * Initiates a file download by clicking on a download element with custom timeout
     * @param downloadElement The WebElement that triggers the download
     * @param expectedFileName The expected name of the downloaded file (can be partial)
     * @param timeoutSeconds Maximum time to wait for download completion
     * @return The full path of the downloaded file
     */
    public static String downloadFile(WebElement downloadElement, String expectedFileName, int timeoutSeconds) {
        try {
            LogUtil.info("Starting file download. Expected file name: " + expectedFileName);
            ExtentManager.logStep("Download file: " + expectedFileName);

            String downloadDir = getDownloadDirectory();

            // Get initial file count in download directory
            File[] initialFiles = new File(downloadDir).listFiles();
            int initialFileCount = initialFiles != null ? initialFiles.length : 0;

            LogUtil.info("Download directory: " + downloadDir);
            LogUtil.info("Initial file count: " + initialFileCount);

            // Click the download element
            WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(10));
            wait.until(ExpectedConditions.elementToBeClickable(downloadElement));
            downloadElement.click();

            ExtentManager.logAction("Click download", "Download element clicked");
            LogUtil.info("Download element clicked successfully");

            // Wait for download to complete
            String downloadedFilePath = waitForDownloadToComplete(downloadDir, expectedFileName, timeoutSeconds);

            if (downloadedFilePath != null) {
                LogUtil.info("File downloaded successfully: " + downloadedFilePath);
                ExtentManager.logVerification("File downloaded successfully: " + new File(downloadedFilePath).getName(), true);
                return downloadedFilePath;
            } else {
                String errorMsg = "File download failed or timed out after " + timeoutSeconds + " seconds";
                LogUtil.error(errorMsg);
                ExtentManager.logVerification("File download completed", false);
                throw new RuntimeException(errorMsg);
            }

        } catch (Exception e) {
            LogUtil.error("Error during file download: " + e.getMessage());
            ExtentManager.logError("File download failed", e);
            throw new RuntimeException("File download failed", e);
        }
    }

    /**
     * Downloads a file by navigating to a URL
     * @param downloadUrl The URL to download the file from
     * @param expectedFileName The expected name of the downloaded file
     * @return The full path of the downloaded file
     */
    public static String downloadFileFromUrl(String downloadUrl, String expectedFileName) {
        try {
            LogUtil.info("Downloading file from URL: " + downloadUrl);
            ExtentManager.logStep("Download file from URL: " + downloadUrl);

            WebDriver driver = DriverFactory.getDriver();
            String downloadDir = getDownloadDirectory();

            // Get initial file count
            File[] initialFiles = new File(downloadDir).listFiles();
            int initialFileCount = initialFiles != null ? initialFiles.length : 0;

            // Navigate to download URL
            driver.get(downloadUrl);
            ExtentManager.logAction("Navigate to", downloadUrl);

            // Wait for download to complete
            String downloadedFilePath = waitForDownloadToComplete(downloadDir, expectedFileName, DEFAULT_DOWNLOAD_TIMEOUT_SECONDS);

            if (downloadedFilePath != null) {
                LogUtil.info("File downloaded successfully from URL: " + downloadedFilePath);
                ExtentManager.logVerification("File downloaded from URL", true);
                return downloadedFilePath;
            } else {
                throw new RuntimeException("File download from URL failed or timed out");
            }

        } catch (Exception e) {
            LogUtil.error("Error downloading file from URL: " + e.getMessage());
            ExtentManager.logError("URL download failed", e);
            throw new RuntimeException("URL download failed", e);
        }
    }

    /**
     * Waits for a file download to complete and verifies the file name
     * @param downloadDir The directory to monitor for downloads
     * @param expectedFileName The expected file name (can be partial)
     * @param timeoutSeconds Maximum time to wait
     * @return The full path of the downloaded file, or null if timeout/not found
     */
    private static String waitForDownloadToComplete(String downloadDir, String expectedFileName, int timeoutSeconds) {
        try {
            LogUtil.info("Waiting for download to complete. Timeout: " + timeoutSeconds + " seconds");
            ExtentManager.logAction("Wait for download", "Expected file: " + expectedFileName);

            File downloadDirectory = new File(downloadDir);
            long startTime = System.currentTimeMillis();
            long timeoutMillis = timeoutSeconds * 1000L;

            while ((System.currentTimeMillis() - startTime) < timeoutMillis) {
                File[] files = downloadDirectory.listFiles();

                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && !file.getName().endsWith(".crdownload") && !file.getName().endsWith(".tmp")) {
                            String fileName = file.getName();

                            // Check if file name contains the expected name (case insensitive)
                            if (fileName.toLowerCase().contains(expectedFileName.toLowerCase())) {
                                LogUtil.info("Found matching file: " + fileName);

                                // Verify file is completely downloaded (not still downloading)
                                if (isFileDownloadComplete(file)) {
                                    LogUtil.info("Download completed successfully: " + file.getAbsolutePath());
                                    return file.getAbsolutePath();
                                }
                            }
                        }
                    }
                }

                // Wait 1 second before checking again
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            // Log available files for debugging
            File[] finalFiles = downloadDirectory.listFiles();
            if (finalFiles != null && finalFiles.length > 0) {
                LogUtil.warn("Download timeout. Available files in directory:");
                Arrays.stream(finalFiles).forEach(file -> LogUtil.warn("- " + file.getName()));
            } else {
                LogUtil.warn("Download timeout. No files found in download directory.");
            }

            return null;

        } catch (Exception e) {
            LogUtil.error("Error while waiting for download: " + e.getMessage());
            return null;
        }
    }

    /**
     * Checks if a file download is complete by verifying file stability
     * @param file The file to check
     * @return true if download is complete, false otherwise
     */
    private static boolean isFileDownloadComplete(File file) {
        try {
            // Check file size stability (file size doesn't change for 2 seconds)
            long initialSize = file.length();
            Thread.sleep(2000);
            long finalSize = file.length();

            boolean isComplete = (initialSize == finalSize && finalSize > 0);
            LogUtil.info("File size check - Initial: " + initialSize + ", Final: " + finalSize + ", Complete: " + isComplete);

            return isComplete;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            LogUtil.warn("Error checking file completion: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifies if a downloaded file exists and contains the expected name
     * @param filePath The path of the file to verify
     * @param expectedFileName The expected file name (can be partial)
     * @return true if file exists and name matches, false otherwise
     */
    public static boolean verifyDownloadedFile(String filePath, String expectedFileName) {
        try {
            File file = new File(filePath);

            if (!file.exists()) {
                LogUtil.error("Downloaded file does not exist: " + filePath);
                ExtentManager.logVerification("Downloaded file exists", false);
                return false;
            }

            String fileName = file.getName();
            boolean nameMatches = fileName.toLowerCase().contains(expectedFileName.toLowerCase());

            LogUtil.info("File verification - Path: " + filePath + ", Expected name: " + expectedFileName + ", Actual name: " + fileName + ", Matches: " + nameMatches);
            ExtentManager.logVerification("File name contains '" + expectedFileName + "'", nameMatches);

            return nameMatches;

        } catch (Exception e) {
            LogUtil.error("Error verifying downloaded file: " + e.getMessage());
            ExtentManager.logError("File verification failed", e);
            return false;
        }
    }

    /**
     * Gets the list of files in the current thread's download directory
     * @return Array of files in the download directory
     */
    public static File[] getDownloadedFiles() {
        String downloadDir = getDownloadDirectory();
        File directory = new File(downloadDir);
        return directory.listFiles();
    }

    /**
     * Cleans up the download directory for the current thread
     */
    public static void cleanupDownloadDirectory() {
        try {
            String downloadDir = downloadDirectory.get();
            if (downloadDir != null) {
                File directory = new File(downloadDir);
                if (directory.exists()) {
                    File[] files = directory.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            if (file.delete()) {
                                LogUtil.info("Deleted download file: " + file.getName());
                            }
                        }
                    }
                    if (directory.delete()) {
                        LogUtil.info("Deleted download directory: " + directory.getName());
                    }
                }
                downloadDirectory.remove();
            }
        } catch (Exception e) {
            LogUtil.warn("Error during download cleanup: " + e.getMessage());
        }
    }
}
