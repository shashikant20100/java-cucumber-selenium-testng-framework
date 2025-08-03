# 🎯 Cucumber + Selenium + TestNG Automation Framework

A comprehensive and robust test automation framework built with **Cucumber BDD**, **Selenium WebDriver**, and **TestNG** for end-to-end web application testing with advanced reporting and email notifications.

## 🏗️ Framework Architecture

```
cucumber-selenium-testng-project/
├── src/
│   ├── main/java/
│   │   ├── common/
│   │   │   └── CommonMethods.java           # Reusable utility methods
│   │   ├── config/
│   │   │   └── ConfigReader.java            # Configuration management
│   │   ├── drivers/
│   │   │   └── DriverFactory.java           # WebDriver factory & management
│   │   ├── pages/
│   │   │   ├── BasePage.java                # Base page object class
│   │   │   ├── AmazonHomePageDynamic.java   # Home page objects
│   │   │   ├── AmazonSearchResultsPageDynamic.java # Search results page
│   │   │   ├── AmazonProductPageDynamic.java # Product page objects
│   │   │   └── PageObjectManager.java       # Page object manager
│   │   ├── utils/
│   │   │   ├── LogUtil.java                 # Logging utilities
│   │   │   ├── ScreenshotUtil.java          # Screenshot capture
│   │   │   ├── ExtentManager.java           # Extent report manager
│   │   │   ├── EmailUtil.java               # Email notification utility
│   │   │   ├── EmailDashboardGenerator.java # Professional email dashboard
│   │   │   ├── CucumberReportParser.java    # Cucumber JSON parser
│   │   │   └── TestStatisticsCollector.java # Test statistics collector
│   │   └── xpath/
│   │       └── XPathConstants.java          # Centralized XPath constants
│   ├── main/resources/
│   │   ├── config.properties                # Environment configuration
│   │   └── log4j2.xml                      # Logging configuration
│   └── test/
│       ├── java/
│       │   ├── hooks/
│       │   │   └── Hooks.java               # Before/After scenario hooks
│       │   ├── runners/
│       │   │   └── TestRunner.java          # Cucumber test runner
│       │   └── stepdefinitions/
│       │       └── amazon_smartwatch.java   # Step definitions
│       └── resources/
│           ├── features/
│           │   └── amazon_smartwatch.feature # BDD feature files
│           ├── testng.xml                   # TestNG configuration
│           ├── extent-config.xml            # Extent report configuration
│           └── log4j2-test.xml             # Test logging configuration
├── target/
│   ├── cucumber-reports/                   # Generated reports
│   │   ├── html/                          # HTML reports
│   │   ├── json/                          # JSON reports
│   │   └── xml/                           # XML reports
│   └── output/
│       ├── reports/
│       │   └── ExtentReport.html          # Extent HTML report
│       └── screenshots/                   # Test screenshots
├── logs/                                  # Application logs
├── drivers/                               # WebDriver binaries
└── pom.xml                               # Maven dependencies
```

## 🚀 Key Features

### 🌐 **Multi-Browser Support**
- Chrome, Firefox, Edge with headless options
- Cross-browser compatibility testing
- Automatic WebDriver management

### ⚡ **Parallel Execution**
- TestNG-based parallel test execution
- Thread-safe WebDriver instances
- Configurable thread count

### 📊 **Advanced Reporting**
- **Extent Reports**: Rich HTML reports with screenshots
- **Cucumber Reports**: JSON, XML, HTML formats
- **Professional Email Dashboard**: Automated email notifications with detailed statistics
- **Real-time Logging**: Comprehensive log4j2 integration

### 🎯 **BDD Approach**
- Cucumber integration with Gherkin syntax
- Business-readable test scenarios
- Step definition reusability

### 🏛️ **Design Patterns**
- Page Object Model (POM)
- Page Factory pattern
- Singleton WebDriver instances
- Factory design pattern for drivers

### 📧 **Email Notifications**
- Automated test execution reports via email
- Professional dashboard with feature breakdown
- Configurable recipients and settings
- Rich HTML email templates

### 🔧 **Configuration Management**
- Environment-specific configurations
- External property files
- Runtime parameter support

## 📋 Prerequisites

Before setting up the framework, ensure you have:

- **Java**: JDK 11 or higher
- **Maven**: Version 3.6 or higher
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code
- **Browsers**: Chrome, Firefox, or Edge installed
- **Git**: For version control (optional)

## 🛠️ Installation & Setup

### 1. **Clone or Download the Project**
```bash
# Using Git
git clone <repository-url>
cd cucumber-selenium-testng-project

# Or download and extract the ZIP file
```

### 2. **Install Dependencies**
```bash
# Clean and install all Maven dependencies
mvn clean install -DskipTests

# Verify installation
mvn dependency:tree
```

### 3. **Configure Environment**
Update `src/main/resources/config.properties`:
```properties
# Browser Configuration
browser.default=chrome
browser.headless=false
browser.window.maximize=true
thread.count=1

# Timeouts (in seconds)
implicit.wait=10
explicit.wait=15
page.load.timeout=30

# URLs
base.url=https://www.amazon.com

# Email Configuration
email.enabled=true
email.smtp.host=smtp.gmail.com
email.smtp.port=587
email.username=your-email@gmail.com
email.password=your-app-password
email.to=recipient1@example.com,recipient2@example.com
email.subject.prefix=[TEST AUTOMATION]
email.max.file.size.mb=25

# Report Configuration
report.extent.title=Test Automation Report
report.extent.document.title=Cucumber Selenium TestNG Report
```

## 🚀 Running Tests

### **Basic Execution**
```bash
# Run all tests with default configuration
mvn clean test

# Run with specific browser
mvn clean test -Dbrowser=chrome
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=edge

# Run in headless mode
mvn clean test -Dbrowser=chrome-headless
mvn clean test -Dbrowser=firefox-headless
```

### **Parallel Execution**
```bash
# Run tests in parallel (configure thread count in config.properties)
mvn clean test -Dthread.count=3

# Run with TestNG profiles
mvn clean test -P parallel
mvn clean test -P headless
```

### **Advanced Options**
```bash
# Run specific scenarios by tags
mvn clean test -Dcucumber.filter.tags="@smoke"
mvn clean test -Dcucumber.filter.tags="@regression and not @slow"

# Run with custom configuration
mvn clean test -Dconfig.file=config-staging.properties

# Generate reports only
mvn clean test -DskipTests=false -Dmaven.test.skip=false
```

## 📊 Reports & Outputs

### **Generated Reports**
After test execution, find reports in:

1. **Extent Reports**: `target/output/reports/ExtentReport.html`
2. **Cucumber HTML**: `target/cucumber-reports/html/index.html`
3. **Cucumber JSON**: `target/cucumber-reports/json/Cucumber.json`
4. **Screenshots**: `target/output/screenshots/`
5. **Logs**: `logs/automation.log`

### **Email Dashboard**
Automatically sent after test execution with:
- Executive summary with KPI cards
- Feature breakdown table
- Pass/Fail statistics
- Execution time details
- Professional HTML formatting

## 🔧 Configuration Options

### **Browser Configuration**
```properties
# Supported browsers
browser.default=chrome|firefox|edge
browser.headless=true|false
browser.window.maximize=true|false
browser.incognito=true|false
```

### **Email Configuration**
```properties
# Enable/disable email notifications
email.enabled=true|false

# SMTP settings for Gmail
email.smtp.host=smtp.gmail.com
email.smtp.port=587
email.smtp.auth=true
email.smtp.starttls.enable=true

# Authentication
email.username=your-email@gmail.com
email.password=your-app-password  # Use App Password for Gmail

# Recipients (comma-separated)
email.to=user1@example.com,user2@example.com
email.cc=manager@example.com
email.bcc=archive@example.com
```

### **Parallel Execution**
```properties
# Thread configuration
thread.count=3
data.provider.thread.count=2
```

## 🏗️ Writing Tests

### **1. Create Feature File**
```gherkin
# src/test/resources/features/sample.feature
Feature: Sample Test Feature
  As a user
  I want to test application functionality
  So that I can ensure quality

  @smoke
  Scenario: Sample test scenario
    Given I navigate to the application
    When I perform some action
    Then I should see expected result
```

### **2. Implement Step Definitions**
```java
// src/test/java/stepdefinitions/SampleSteps.java
public class SampleSteps {
    private WebDriver driver;
    private BasePage basePage;
    
    @Given("I navigate to the application")
    public void i_navigate_to_application() {
        driver = DriverFactory.getDriver();
        basePage = new BasePage(driver);
        basePage.navigateToUrl(ConfigReader.getBaseUrl());
    }
    
    @When("I perform some action")
    public void i_perform_some_action() {
        // Implementation
    }
    
    @Then("I should see expected result")
    public void i_should_see_expected_result() {
        // Assertions
    }
}
```

### **3. Create Page Objects**
```java
// src/main/java/pages/SamplePage.java
public class SamplePage extends BasePage {
    
    @FindBy(id = "element-id")
    private WebElement sampleElement;
    
    public SamplePage(WebDriver driver) {
        super(driver);
    }
    
    public void performAction() {
        waitForElementToBeClickable(sampleElement).click();
    }
}
```

## 🐛 Debugging & Troubleshooting

### **Common Issues**

1. **WebDriver Issues**
   ```bash
   # Update WebDriver binaries
   mvn clean install -U
   ```

2. **Port Already in Use**
   ```bash
   # Check running processes
   lsof -i :4444
   # Kill specific process
   kill -9 <PID>
   ```

3. **Browser Not Found**
   - Ensure browsers are installed
   - Check browser versions compatibility
   - Update WebDriver binaries

### **Debugging Options**
```bash
# Run with debug logging
mvn clean test -Dlog.level=DEBUG

# Run single test for debugging
mvn clean test -Dcucumber.filter.tags="@debug"

# Skip email notifications during debugging
mvn clean test -Demail.enabled=false
```

## 📈 Best Practices

### **Test Design**
- Keep scenarios simple and focused
- Use descriptive scenario names
- Implement proper wait strategies
- Follow Page Object Model principles

### **Maintenance**
- Regular dependency updates
- Browser compatibility checks
- Log file rotation and cleanup
- Screenshot management

### **CI/CD Integration**
```yaml
# Example Jenkins pipeline step
steps:
  - name: Run Tests
    run: |
      mvn clean test -Dbrowser=chrome-headless -Demail.enabled=true
      
  - name: Publish Reports
    uses: actions/upload-artifact@v2
    with:
      name: test-reports
      path: target/cucumber-reports/
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Implement changes with tests
4. Update documentation
5. Submit pull request

## 📞 Support

For issues or questions:
- Check logs in `logs/automation.log`
- Review generated reports
- Contact: **Shashi Kant Sharma**

---

**© 2025 Shashi Kant Sharma. All rights reserved.**

*Built with ❤️ using Cucumber + Selenium + TestNG*
"# java-cucumber-selenium-testng-framework" 
