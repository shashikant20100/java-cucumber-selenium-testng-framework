package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Parser for Cucumber JSON reports to extract detailed test statistics
 */
public class CucumberReportParser {

    private static final String CUCUMBER_JSON_PATH = "target/cucumber-reports/json/Cucumber.json";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Detailed test statistics with feature and scenario breakdown
     */
    public static class DetailedTestStatistics {
        private int totalFeatures;
        private int totalScenarios;
        private int totalSteps;
        private int passedScenarios;
        private int failedScenarios;
        private int skippedScenarios;
        private int passedSteps;
        private int failedSteps;
        private int skippedSteps;
        private double scenarioPassPercentage;
        private double stepPassPercentage;
        private String executionTime;
        private String reportPath;
        private List<FeatureResult> featureResults;
        private String executionStartTime;
        private String executionEndTime;
        private long totalExecutionDuration;

        public DetailedTestStatistics() {
            this.featureResults = new ArrayList<>();
        }

        // Getters and setters
        public int getTotalFeatures() { return totalFeatures; }
        public void setTotalFeatures(int totalFeatures) { this.totalFeatures = totalFeatures; }

        public int getTotalScenarios() { return totalScenarios; }
        public void setTotalScenarios(int totalScenarios) { this.totalScenarios = totalScenarios; }

        public int getTotalSteps() { return totalSteps; }
        public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }

        public int getPassedScenarios() { return passedScenarios; }
        public void setPassedScenarios(int passedScenarios) { this.passedScenarios = passedScenarios; }

        public int getFailedScenarios() { return failedScenarios; }
        public void setFailedScenarios(int failedScenarios) { this.failedScenarios = failedScenarios; }

        public int getSkippedScenarios() { return skippedScenarios; }
        public void setSkippedScenarios(int skippedScenarios) { this.skippedScenarios = skippedScenarios; }

        public int getPassedSteps() { return passedSteps; }
        public void setPassedSteps(int passedSteps) { this.passedSteps = passedSteps; }

        public int getFailedSteps() { return failedSteps; }
        public void setFailedSteps(int failedSteps) { this.failedSteps = failedSteps; }

        public int getSkippedSteps() { return skippedSteps; }
        public void setSkippedSteps(int skippedSteps) { this.skippedSteps = skippedSteps; }

        public double getScenarioPassPercentage() { return scenarioPassPercentage; }
        public void setScenarioPassPercentage(double scenarioPassPercentage) { this.scenarioPassPercentage = scenarioPassPercentage; }

        public double getStepPassPercentage() { return stepPassPercentage; }
        public void setStepPassPercentage(double stepPassPercentage) { this.stepPassPercentage = stepPassPercentage; }

        public String getExecutionTime() { return executionTime; }
        public void setExecutionTime(String executionTime) { this.executionTime = executionTime; }

        public String getReportPath() { return reportPath; }
        public void setReportPath(String reportPath) { this.reportPath = reportPath; }

        public List<FeatureResult> getFeatureResults() { return featureResults; }
        public void setFeatureResults(List<FeatureResult> featureResults) { this.featureResults = featureResults; }

        public String getExecutionStartTime() { return executionStartTime; }
        public void setExecutionStartTime(String executionStartTime) { this.executionStartTime = executionStartTime; }

        public String getExecutionEndTime() { return executionEndTime; }
        public void setExecutionEndTime(String executionEndTime) { this.executionEndTime = executionEndTime; }

        public long getTotalExecutionDuration() { return totalExecutionDuration; }
        public void setTotalExecutionDuration(long totalExecutionDuration) { this.totalExecutionDuration = totalExecutionDuration; }
    }

    /**
     * Feature-level test results
     */
    public static class FeatureResult {
        private String featureName;
        private String featureDescription;
        private int totalScenarios;
        private int passedScenarios;
        private int failedScenarios;
        private int skippedScenarios;
        private double passPercentage;
        private List<ScenarioResult> scenarios;
        private long featureDuration;

        public FeatureResult() {
            this.scenarios = new ArrayList<>();
        }

        // Getters and setters
        public String getFeatureName() { return featureName; }
        public void setFeatureName(String featureName) { this.featureName = featureName; }

        public String getFeatureDescription() { return featureDescription; }
        public void setFeatureDescription(String featureDescription) { this.featureDescription = featureDescription; }

        public int getTotalScenarios() { return totalScenarios; }
        public void setTotalScenarios(int totalScenarios) { this.totalScenarios = totalScenarios; }

        public int getPassedScenarios() { return passedScenarios; }
        public void setPassedScenarios(int passedScenarios) { this.passedScenarios = passedScenarios; }

        public int getFailedScenarios() { return failedScenarios; }
        public void setFailedScenarios(int failedScenarios) { this.failedScenarios = failedScenarios; }

        public int getSkippedScenarios() { return skippedScenarios; }
        public void setSkippedScenarios(int skippedScenarios) { this.skippedScenarios = skippedScenarios; }

        public double getPassPercentage() { return passPercentage; }
        public void setPassPercentage(double passPercentage) { this.passPercentage = passPercentage; }

        public List<ScenarioResult> getScenarios() { return scenarios; }
        public void setScenarios(List<ScenarioResult> scenarios) { this.scenarios = scenarios; }

        public long getFeatureDuration() { return featureDuration; }
        public void setFeatureDuration(long featureDuration) { this.featureDuration = featureDuration; }
    }

    /**
     * Scenario-level test results
     */
    public static class ScenarioResult {
        private String scenarioName;
        private String status;
        private long duration;
        private int totalSteps;
        private int passedSteps;
        private int failedSteps;
        private int skippedSteps;

        // Getters and setters
        public String getScenarioName() { return scenarioName; }
        public void setScenarioName(String scenarioName) { this.scenarioName = scenarioName; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public long getDuration() { return duration; }
        public void setDuration(long duration) { this.duration = duration; }

        public int getTotalSteps() { return totalSteps; }
        public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }

        public int getPassedSteps() { return passedSteps; }
        public void setPassedSteps(int passedSteps) { this.passedSteps = passedSteps; }

        public int getFailedSteps() { return failedSteps; }
        public void setFailedSteps(int failedSteps) { this.failedSteps = failedSteps; }

        public int getSkippedSteps() { return skippedSteps; }
        public void setSkippedSteps(int skippedSteps) { this.skippedSteps = skippedSteps; }
    }

    /**
     * Parse Cucumber JSON report and extract detailed statistics
     */
    public static DetailedTestStatistics parseReport() {
        DetailedTestStatistics stats = new DetailedTestStatistics();

        try {
            File jsonFile = new File(CUCUMBER_JSON_PATH);
            if (!jsonFile.exists()) {
                LogUtil.warn("Cucumber JSON report not found at: " + CUCUMBER_JSON_PATH);
                return stats;
            }

            LogUtil.info("Parsing Cucumber JSON report from: " + CUCUMBER_JSON_PATH);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonFile);

            if (rootNode.isArray() && rootNode.size() > 0) {
                parseFeatures(rootNode, stats);
                calculateOverallStatistics(stats);
                stats.setReportPath(ExtentManager.getReportPath());

                // Log parsed statistics for debugging
                LogUtil.info("Parsed statistics - Features: " + stats.getTotalFeatures() +
                           ", Scenarios: " + stats.getTotalScenarios() +
                           ", Passed: " + stats.getPassedScenarios() +
                           ", Failed: " + stats.getFailedScenarios() +
                           ", Pass Rate: " + stats.getScenarioPassPercentage() + "%");
            }

        } catch (IOException e) {
            LogUtil.error("Error parsing Cucumber JSON report", e);
        }

        return stats;
    }

    private static void parseFeatures(JsonNode featuresNode, DetailedTestStatistics stats) {
        String earliestStartTime = null;
        String latestEndTime = null;
        long totalDurationNanos = 0;

        LogUtil.info("Starting to parse features from JSON. Found " + featuresNode.size() + " features in the report.");

        for (JsonNode featureNode : featuresNode) {
            FeatureResult featureResult = new FeatureResult();

            // Parse feature details
            String featureName = featureNode.path("name").asText("Unknown Feature");
            featureResult.setFeatureName(featureName);
            featureResult.setFeatureDescription(featureNode.path("description").asText(""));

            LogUtil.info("Processing feature: " + featureName);

            JsonNode elementsNode = featureNode.path("elements");
            if (elementsNode.isArray()) {
                LogUtil.info("Feature '" + featureName + "' has " + elementsNode.size() + " elements");
                parseScenarios(elementsNode, featureResult);

                // Extract actual execution times from JSON timestamps
                for (JsonNode elementNode : elementsNode) {
                    String timestamp = elementNode.path("start_timestamp").asText("");
                    if (!timestamp.isEmpty() && earliestStartTime == null) {
                        earliestStartTime = timestamp;
                    }
                }

                // Calculate total duration from actual scenario durations
                for (CucumberReportParser.ScenarioResult scenario : featureResult.getScenarios()) {
                    totalDurationNanos += scenario.getDuration();
                }
            } else {
                LogUtil.warn("Feature '" + featureName + "' has no elements or elements is not an array");
            }

            // Add feature even if it has no scenarios for debugging
            calculateFeatureStatistics(featureResult);
            stats.getFeatureResults().add(featureResult);
            LogUtil.info("Added feature '" + featureResult.getFeatureName() + "' - " +
                       featureResult.getTotalScenarios() + " scenarios, " +
                       featureResult.getPassedScenarios() + " passed (" +
                       String.format("%.1f%%", featureResult.getPassPercentage()) + ")");
        }

        LogUtil.info("Total features processed: " + stats.getFeatureResults().size());

        // Set actual execution times from JSON data
        if (earliestStartTime != null) {
            try {
                // Parse the ISO timestamp from Cucumber JSON
                java.time.Instant startInstant = java.time.Instant.parse(earliestStartTime);
                java.time.Instant endInstant = startInstant.plus(totalDurationNanos, java.time.temporal.ChronoUnit.NANOS);

                stats.setExecutionStartTime(LocalDateTime.ofInstant(startInstant, java.time.ZoneId.systemDefault()).format(DATE_FORMAT));
                stats.setExecutionEndTime(LocalDateTime.ofInstant(endInstant, java.time.ZoneId.systemDefault()).format(DATE_FORMAT));
                stats.setTotalExecutionDuration(totalDurationNanos / 1000000); // Convert to milliseconds
            } catch (Exception e) {
                LogUtil.warn("Failed to parse timestamp from Cucumber JSON, using fallback times");
                setFallbackTimes(stats);
            }
        } else {
            setFallbackTimes(stats);
        }
    }

    private static void setFallbackTimes(DetailedTestStatistics stats) {
        LocalDateTime now = LocalDateTime.now();
        stats.setExecutionStartTime(now.minusMinutes(5).format(DATE_FORMAT));
        stats.setExecutionEndTime(now.format(DATE_FORMAT));
        stats.setTotalExecutionDuration(5 * 60 * 1000); // 5 minutes in milliseconds
    }

    private static void parseScenarios(JsonNode elementsNode, FeatureResult featureResult) {
        for (JsonNode elementNode : elementsNode) {
            String type = elementNode.path("type").asText("");
            String keyword = elementNode.path("keyword").asText("");

            // Skip background elements completely - only process actual test scenarios
            if ("background".equals(type) || "Background".equals(keyword)) {
                continue;
            }

            // Only process actual scenarios
            if ("scenario".equals(type) || "Scenario Outline".equals(keyword) || "Scenario".equals(keyword)) {
                ScenarioResult scenarioResult = new ScenarioResult();
                String scenarioName = elementNode.path("name").asText("Unknown Scenario");

                // If scenario name is empty, try to get it from the feature file or use default
                if (scenarioName.trim().isEmpty()) {
                    scenarioName = "Scenario " + (featureResult.getScenarios().size() + 1);
                }

                scenarioResult.setScenarioName(scenarioName);

                JsonNode stepsNode = elementNode.path("steps");
                if (stepsNode.isArray() && stepsNode.size() > 0) {
                    parseSteps(stepsNode, scenarioResult);

                    // Only add scenarios that actually have steps
                    if (scenarioResult.getTotalSteps() > 0) {
                        // Determine scenario status
                        determineScenarioStatus(scenarioResult);

                        featureResult.getScenarios().add(scenarioResult);
                        updateFeatureCounts(featureResult, scenarioResult);

                        LogUtil.info("Added Scenario: '" + scenarioResult.getScenarioName() + "' - Status: " +
                                   scenarioResult.getStatus() + " (" + scenarioResult.getPassedSteps() +
                                   "/" + scenarioResult.getTotalSteps() + " steps passed)");
                    }
                }
            }
        }
    }

    private static void parseSteps(JsonNode stepsNode, ScenarioResult scenarioResult) {
        long totalDuration = 0;
        int passed = 0, failed = 0, skipped = 0;

        for (JsonNode stepNode : stepsNode) {
            JsonNode resultNode = stepNode.path("result");
            String status = resultNode.path("status").asText("unknown");
            long duration = resultNode.path("duration").asLong(0);

            totalDuration += duration;

            switch (status.toLowerCase()) {
                case "passed":
                    passed++;
                    break;
                case "failed":
                    failed++;
                    break;
                case "skipped":
                case "undefined":
                case "pending":
                    skipped++;
                    break;
            }
        }

        scenarioResult.setDuration(totalDuration);
        scenarioResult.setTotalSteps(stepsNode.size());
        scenarioResult.setPassedSteps(passed);
        scenarioResult.setFailedSteps(failed);
        scenarioResult.setSkippedSteps(skipped);
    }

    private static void determineScenarioStatus(ScenarioResult scenarioResult) {
        // Determine scenario status based on Cucumber's standard logic:
        // - If ANY step failed -> scenario failed
        // - If no failures but has skipped steps -> scenario skipped
        // - If all steps passed (no failures, no skips) -> scenario passed

        if (scenarioResult.getFailedSteps() > 0) {
            scenarioResult.setStatus("failed");
        } else if (scenarioResult.getSkippedSteps() > 0) {
            scenarioResult.setStatus("skipped");
        } else if (scenarioResult.getPassedSteps() == scenarioResult.getTotalSteps() && scenarioResult.getTotalSteps() > 0) {
            // All steps passed - scenario is PASSED
            scenarioResult.setStatus("passed");
        } else {
            scenarioResult.setStatus("unknown");
        }

        LogUtil.info("Scenario '" + scenarioResult.getScenarioName() + "' final status: " + scenarioResult.getStatus().toUpperCase() +
                    " (Steps - Total: " + scenarioResult.getTotalSteps() +
                    ", Passed: " + scenarioResult.getPassedSteps() +
                    ", Failed: " + scenarioResult.getFailedSteps() +
                    ", Skipped: " + scenarioResult.getSkippedSteps() + ")");
    }

    private static void updateFeatureCounts(FeatureResult featureResult, ScenarioResult scenarioResult) {
        featureResult.setTotalScenarios(featureResult.getTotalScenarios() + 1);

        switch (scenarioResult.getStatus()) {
            case "passed":
                featureResult.setPassedScenarios(featureResult.getPassedScenarios() + 1);
                break;
            case "failed":
                featureResult.setFailedScenarios(featureResult.getFailedScenarios() + 1);
                break;
            case "skipped":
                featureResult.setSkippedScenarios(featureResult.getSkippedScenarios() + 1);
                break;
        }

        featureResult.setFeatureDuration(featureResult.getFeatureDuration() + scenarioResult.getDuration());
    }

    private static void calculateFeatureStatistics(FeatureResult featureResult) {
        if (featureResult.getTotalScenarios() > 0) {
            featureResult.setPassPercentage(
                (double) featureResult.getPassedScenarios() / featureResult.getTotalScenarios() * 100
            );
        }
    }

    private static void calculateOverallStatistics(DetailedTestStatistics stats) {
        int totalFeatures = stats.getFeatureResults().size();
        int totalScenarios = 0, passedScenarios = 0, failedScenarios = 0, skippedScenarios = 0;
        int totalSteps = 0, passedSteps = 0, failedSteps = 0, skippedSteps = 0;

        for (FeatureResult feature : stats.getFeatureResults()) {
            totalScenarios += feature.getTotalScenarios();
            passedScenarios += feature.getPassedScenarios();
            failedScenarios += feature.getFailedScenarios();
            skippedScenarios += feature.getSkippedScenarios();

            for (ScenarioResult scenario : feature.getScenarios()) {
                totalSteps += scenario.getTotalSteps();
                passedSteps += scenario.getPassedSteps();
                failedSteps += scenario.getFailedSteps();
                skippedSteps += scenario.getSkippedSteps();
            }
        }

        stats.setTotalFeatures(totalFeatures);
        stats.setTotalScenarios(totalScenarios);
        stats.setPassedScenarios(passedScenarios);
        stats.setFailedScenarios(failedScenarios);
        stats.setSkippedScenarios(skippedScenarios);
        stats.setTotalSteps(totalSteps);
        stats.setPassedSteps(passedSteps);
        stats.setFailedSteps(failedSteps);
        stats.setSkippedSteps(skippedSteps);

        // Calculate percentages
        if (totalScenarios > 0) {
            stats.setScenarioPassPercentage((double) passedScenarios / totalScenarios * 100);
        }
        if (totalSteps > 0) {
            stats.setStepPassPercentage((double) passedSteps / totalSteps * 100);
        }

        // Set execution time string
        if (stats.getExecutionStartTime() != null && stats.getExecutionEndTime() != null) {
            stats.setExecutionTime(stats.getExecutionStartTime() + " to " + stats.getExecutionEndTime());
        } else {
            stats.setExecutionTime(LocalDateTime.now().format(DATE_FORMAT));
        }
    }
}

