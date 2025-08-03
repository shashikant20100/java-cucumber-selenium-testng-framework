package utils;

/**
 * Quick test to send professional dashboard and verify calculations
 */
public class QuickEmailTest {

    public static void main(String[] args) {
        System.out.println("=== Testing Professional Dashboard Email ===");

        try {
            // First, let's parse and display the statistics
            CucumberReportParser.DetailedTestStatistics stats = CucumberReportParser.parseReport();

            System.out.println("Features: " + stats.getTotalFeatures());
            System.out.println("Scenarios: " + stats.getTotalScenarios());
            System.out.println("Passed Scenarios: " + stats.getPassedScenarios());
            System.out.println("Failed Scenarios: " + stats.getFailedScenarios());
            System.out.println("Pass Rate: " + String.format("%.1f%%", stats.getScenarioPassPercentage()));

            System.out.println("\nStep Details:");
            System.out.println("Total Steps: " + stats.getTotalSteps());
            System.out.println("Passed Steps: " + stats.getPassedSteps());
            System.out.println("Failed Steps: " + stats.getFailedSteps());
            System.out.println("Step Pass Rate: " + String.format("%.1f%%", stats.getStepPassPercentage()));

            // Show feature breakdown
            System.out.println("\nFeature Details:");
            for (CucumberReportParser.FeatureResult feature : stats.getFeatureResults()) {
                System.out.println("  Feature: " + feature.getFeatureName());
                System.out.println("  Total Scenarios: " + feature.getTotalScenarios());
                System.out.println("  Passed Scenarios: " + feature.getPassedScenarios());
                System.out.println("  Pass Rate: " + String.format("%.1f%%", feature.getPassPercentage()));

                for (CucumberReportParser.ScenarioResult scenario : feature.getScenarios()) {
                    System.out.println("    Scenario: " + scenario.getScenarioName());
                    System.out.println("    Status: " + scenario.getStatus().toUpperCase());
                    System.out.println("    Steps: " + scenario.getPassedSteps() + "/" + scenario.getTotalSteps() + " passed");
                }
            }

            // Try to send the professional dashboard
            System.out.println("\n=== Sending Email ===");
            boolean emailSent = EmailUtil.sendProfessionalDashboard();
            System.out.println("Email sent successfully: " + emailSent);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
