package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for comprehensive logs directory management and automation log file generation.
 */
public class LogCleanupUtil {

    private static final String LOG_DIR = "logs";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    /**
     * Check if logs directory exists, delete it completely, and recreate fresh for automation suite.
     * This ensures a clean start for each automation run.
     */
    public static void prepareLogsDirectoryForAutomation() {
        try {
            Path logPath = Paths.get(LOG_DIR);

            // Check if logs directory exists
            if (Files.exists(logPath)) {
                System.out.println("Found existing logs directory at: " + logPath.toAbsolutePath());

                // Delete the entire logs directory and all its contents
                deleteDirectoryRecursively(logPath);
                System.out.println("Successfully deleted existing logs directory");
            } else {
                System.out.println("No existing logs directory found");
            }

            // Create fresh logs directory
            Files.createDirectories(logPath);
            System.out.println("Created fresh logs directory at: " + logPath.toAbsolutePath());

            // Create initial log marker to verify directory is working
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            Path markerFile = logPath.resolve("automation-suite-started-" + timestamp + ".marker");
            Files.createFile(markerFile);
            System.out.println("Created automation start marker: " + markerFile.getFileName());

            // Verify directory is ready for Log4j2
            System.out.println("Logs directory is ready for automation suite execution");

        } catch (Exception e) {
            System.err.println("Error preparing logs directory for automation: " + e.getMessage());
            throw new RuntimeException("Failed to prepare logs directory", e);
        }
    }

    /**
     * Recursively delete a directory and all its contents
     */
    private static void deleteDirectoryRecursively(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                    .sorted((a, b) -> b.getNameCount() - a.getNameCount()) // Delete files before directories
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            System.out.println("Deleted: " + path.getFileName());
                        } catch (IOException e) {
                            System.err.println("Failed to delete: " + path + " - " + e.getMessage());
                        }
                    });
        }
    }

    /**
     * Verify that logs directory exists and is writable
     */
    public static boolean isLogsDirectoryReady() {
        Path logPath = Paths.get(LOG_DIR);
        return Files.exists(logPath) && Files.isDirectory(logPath) && Files.isWritable(logPath);
    }

    /**
     * Create automation log file header with suite information
     */
    public static void createAutomationLogHeader() {
        try {
            // This will trigger Log4j2 to create the actual log files
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            LogUtil.info("================================================================================");
            LogUtil.info("                    AUTOMATION SUITE EXECUTION STARTED");
            LogUtil.info("================================================================================");
            LogUtil.info("Execution Time: " + timestamp);
            LogUtil.info("Working Directory: " + System.getProperty("user.dir"));
            LogUtil.info("Java Version: " + System.getProperty("java.version"));
            LogUtil.info("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
            LogUtil.info("Logs Directory: " + Paths.get(LOG_DIR).toAbsolutePath());
            LogUtil.info("================================================================================");

        } catch (Exception e) {
            System.err.println("Error creating automation log header: " + e.getMessage());
        }
    }
}
