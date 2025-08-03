package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import drivers.DriverFactory;
import config.ConfigReader;
import utils.LogUtil;
import utils.ExtentManager;

import java.time.Duration;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage() {
        this.driver = DriverFactory.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
        PageFactory.initElements(driver, this);
    }

    // Common page methods
    protected void click(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
            LogUtil.info("Clicked on element: " + element.toString());
            ExtentManager.logAction("Click", getElementDescription(element));
        } catch (Exception e) {
            LogUtil.error("Failed to click element: " + e.getMessage());
            ExtentManager.logError("Failed to click element", e);
            throw e;
        }
    }

    protected void sendKeys(WebElement element, String text) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            element.clear();
            element.sendKeys(text);
            LogUtil.info("Entered text '" + text + "' in element");
            ExtentManager.logAction("Enter text: " + text, getElementDescription(element));
        } catch (Exception e) {
            LogUtil.error("Failed to enter text: " + e.getMessage());
            ExtentManager.logError("Failed to enter text", e);
            throw e;
        }
    }

    protected String getText(WebElement element) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            String text = element.getText();
            LogUtil.info("Retrieved text: " + text);
            ExtentManager.logAction("Get text: " + text, getElementDescription(element));
            return text;
        } catch (Exception e) {
            LogUtil.error("Failed to get text: " + e.getMessage());
            ExtentManager.logError("Failed to get text", e);
            throw e;
        }
    }

    protected boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected void waitForElementToBeVisible(WebElement element) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            ExtentManager.logAction("Wait for element visibility", getElementDescription(element));
        } catch (Exception e) {
            ExtentManager.logError("Element not visible within timeout", e);
            throw e;
        }
    }

    protected void waitForElementToBeClickable(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            ExtentManager.logAction("Wait for element clickable", getElementDescription(element));
        } catch (Exception e) {
            ExtentManager.logError("Element not clickable within timeout", e);
            throw e;
        }
    }

    protected void scrollToElement(WebElement element) {
        try {
            ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView(true);", element);
            Thread.sleep(500);
            ExtentManager.logAction("Scroll to element", getElementDescription(element));
        } catch (Exception e) {
            LogUtil.warn("Failed to scroll to element: " + e.getMessage());
            ExtentManager.logError("Failed to scroll to element", e);
        }
    }

    // Helper method to get element description for better logging
    private String getElementDescription(WebElement element) {
        try {
            String tagName = element.getTagName();
            String id = element.getAttribute("id");
            String className = element.getAttribute("class");
            String text = element.getText();

            StringBuilder description = new StringBuilder(tagName);
            if (id != null && !id.isEmpty()) {
                description.append(" (id: ").append(id).append(")");
            } else if (className != null && !className.isEmpty()) {
                description.append(" (class: ").append(className.split(" ")[0]).append(")");
            } else if (text != null && !text.isEmpty() && text.length() < 20) {
                description.append(" (text: ").append(text).append(")");
            }
            return description.toString();
        } catch (Exception e) {
            return "Element";
        }
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
