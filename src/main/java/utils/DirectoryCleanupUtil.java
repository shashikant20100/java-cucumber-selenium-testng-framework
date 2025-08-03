package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Utility class for managing download directories and cleanup operations.
 */
public class DirectoryCleanupUtil {

    private static final String DOWNLOADS_BASE_PATH = "output/downloads";
    private static final int MAX_DOWNLOAD_DIRECTORIES = 5; // Keep only last 5 directories

    /**
     * Clean up old download directories, keeping only the most recent ones
     */
    public static void cleanupOldDownloadDirectories() {
        try {
            Path downloadsPath = Paths.get(DOWNLOADS_BASE_PATH);

            if (!Files.exists(downloadsPath)) {
                LogUtil.info("Downloads directory does not exist: " + DOWNLOADS_BASE_PATH);
                return;
            }

            // Get all download directories
            File[] directories = downloadsPath.toFile().listFiles(File::isDirectory);

            if (directories == null || directories.length <= MAX_DOWNLOAD_DIRECTORIES) {
                LogUtil.info("No cleanup needed. Current directories: " + (directories != null ? directories.length : 0));
                return;
            }

            // Sort directories by last modified time (newest first)
            Arrays.sort(directories, Comparator.comparingLong(File::lastModified).reversed());

            // Delete old directories (keep only MAX_DOWNLOAD_DIRECTORIES)
            int deletedCount = 0;
            for (int i = MAX_DOWNLOAD_DIRECTORIES; i < directories.length; i++) {
                if (deleteDirectory(directories[i].toPath())) {
                    deletedCount++;
                    LogUtil.info("Deleted old download directory: " + directories[i].getName());
                }
            }

            LogUtil.info("Cleanup completed. Deleted " + deletedCount + " old download directories");

        } catch (Exception e) {
            LogUtil.error("Error during download directory cleanup", e);
        }
    }

    /**
     * Delete a directory and all its contents
     */
    private static boolean deleteDirectory(Path directory) {
        try {
            Files.walk(directory)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            return true;
        } catch (IOException e) {
            LogUtil.error("Failed to delete directory: " + directory, e);
            return false;
        }
    }

    /**
     * Clean up log files older than specified days
     */
    public static void cleanupOldLogFiles(int daysToKeep) {
        try {
            Path logsPath = Paths.get("logs");

            if (!Files.exists(logsPath)) {
                return;
            }

            long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24L * 60L * 60L * 1000L);

            Files.list(logsPath)
                    .filter(path -> path.toString().endsWith(".log"))
                    .filter(path -> {
                        try {
                            return Files.getLastModifiedTime(path).toMillis() < cutoffTime;
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            LogUtil.info("Deleted old log file: " + path.getFileName());
                        } catch (IOException e) {
                            LogUtil.error("Failed to delete log file: " + path.getFileName(), e);
                        }
                    });

        } catch (Exception e) {
            LogUtil.error("Error during log file cleanup", e);
        }
    }

    /**
     * Clean up screenshot files older than specified days
     */
    public static void cleanupOldScreenshots(int daysToKeep) {
        try {
            Path screenshotsPath = Paths.get("output/screenshots");

            if (!Files.exists(screenshotsPath)) {
                return;
            }

            long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24L * 60L * 60L * 1000L);

            Files.walk(screenshotsPath)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().matches(".*\\.(png|jpg|jpeg)$"))
                    .filter(path -> {
                        try {
                            return Files.getLastModifiedTime(path).toMillis() < cutoffTime;
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            LogUtil.info("Deleted old screenshot: " + path.getFileName());
                        } catch (IOException e) {
                            LogUtil.error("Failed to delete screenshot: " + path.getFileName(), e);
                        }
                    });

        } catch (Exception e) {
            LogUtil.error("Error during screenshot cleanup", e);
        }
    }
}
