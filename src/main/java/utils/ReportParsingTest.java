package utils;

/**
 * Test class to verify Cucumber report parsing calculations
 */
public class ReportParsingTest {

    public static void main(String[] args) {
        System.out.println("=== Testing Cucumber Report Parser ===");

        try {
            CucumberReportParser.DetailedTestStatistics stats = CucumberReportParser.parseReport();

            System.out.println("\n=== DETAILED STATISTICS ===");
            System.out.println("Total Features: " + stats.getTotalFeatures());
            System.out.println("Total Scenarios: " + stats.getTotalScenarios());
            System.out.println("Passed Scenarios: " + stats.getPassedScenarios());
            System.out.println("Failed Scenarios: " + stats.getFailedScenarios());
            System.out.println("Skipped Scenarios: " + stats.getSkippedScenarios());
            System.out.println("Scenario Pass Rate: " + String.format("%.2f%%", stats.getScenarioPassPercentage()));

            System.out.println("\nTotal Steps: " + stats.getTotalSteps());
            System.out.println("Passed Steps: " + stats.getPassedSteps());
            System.out.println("Failed Steps: " + stats.getFailedSteps());
            System.out.println("Skipped Steps: " + stats.getSkippedSteps());
            System.out.println("Step Pass Rate: " + String.format("%.2f%%", stats.getStepPassPercentage()));

            System.out.println("\n=== FEATURE BREAKDOWN ===");
            for (CucumberReportParser.FeatureResult feature : stats.getFeatureResults()) {
                System.out.println("Feature: " + feature.getFeatureName());
                System.out.println("  Total Scenarios: " + feature.getTotalScenarios());
                System.out.println("  Passed: " + feature.getPassedScenarios());
                System.out.println("  Failed: " + feature.getFailedScenarios());
                System.out.println("  Skipped: " + feature.getSkippedScenarios());
                System.out.println("  Pass Rate: " + String.format("%.2f%%", feature.getPassPercentage()));

                System.out.println("  Scenarios:");
                for (CucumberReportParser.ScenarioResult scenario : feature.getScenarios()) {
                    System.out.println("    - " + scenario.getScenarioName() +
                                     " [" + scenario.getStatus() + "] " +
                                     "(" + scenario.getPassedSteps() + "/" + scenario.getTotalSteps() + " steps passed)");
                }
                System.out.println();
            }

        } catch (Exception e) {
            System.err.println("Error testing parser: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
