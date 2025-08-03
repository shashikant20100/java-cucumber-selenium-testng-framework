package common;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.ElementClickInterceptedException;
import drivers.DriverFactory;
import config.ConfigReader;
import utils.LogUtil;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class CommonMethods {

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final int RETRY_DELAY_MS = 500;

    // Enhanced retry mechanism for stale element handling
    /**
     * Author: shashi
     * Description: Retries the provided action on a WebDriver instance up to MAX_RETRY_ATTEMPTS times
     * if a StaleElementReferenceException occurs. Useful for handling stale element issues in Selenium tests.
     */
    public static <T> T retryOnStaleElement(Function<WebDriver, T> action) {
        WebDriver driver = getDriver();
        Exception lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                return action.apply(driver);
            } catch (StaleElementReferenceException e) {
                lastException = e;
                LogUtil.warn("Stale element reference on attempt " + attempt + ", retrying...");
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during retry", ie);
                }
            }
        }
        throw new RuntimeException("Failed after " + MAX_RETRY_ATTEMPTS + " attempts", lastException);
    }

    // Dynamic locator builder
    public static class LocatorBuilder {

        /**
         * Author: shashi
         * Description: Builds a dynamic Selenium By locator from a pattern and values.
         */
        public static By buildDynamicLocator(String locatorPattern, Object... values) {
            String finalLocator = String.format(locatorPattern, values);

            if (locatorPattern.startsWith("//") || locatorPattern.startsWith("(")) {
                return By.xpath(finalLocator);
            } else if (locatorPattern.startsWith("#")) {
                return By.cssSelector(finalLocator);
            } else if (locatorPattern.contains("[") && locatorPattern.contains("]")) {
                return By.cssSelector(finalLocator);
            } else {
                return By.xpath(finalLocator);
            }
        }

        /**
         * Author: shashi
         * Description: Returns a Selenium By.id locator for the given id.
         */
        public static By id(String id) {
            return By.id(id);
        }

        /**
         * Author: shashi
         * Description: Returns a Selenium By.xpath locator for the given xpath.
         */
        public static By xpath(String xpath) {
            return By.xpath(xpath);
        }

        /**
         * Author: shashi
         * Description: Returns a Selenium By.cssSelector locator for the given CSS selector.
         */
        public static By css(String css) {
            return By.cssSelector(css);
        }

        /**
         * Author: shashi
         * Description: Returns a Selenium By.name locator for the given name.
         */
        public static By name(String name) {
            return By.name(name);
        }

        /**
         * Author: shashi
         * Description: Returns a Selenium By.className locator for the given class name.
         */
        public static By className(String className) {
            return By.className(className);
        }

        /**
         * Author: shashi
         * Description: Returns a Selenium By.tagName locator for the given tag name.
         */
        public static By tagName(String tagName) {
            return By.tagName(tagName);
        }

        /**
         * Author: shashi
         * Description: Returns a Selenium By.linkText locator for the given link text.
         */
        public static By linkText(String linkText) {
            return By.linkText(linkText);
        }

        /**
         * Author: shashi
         * Description: Returns a Selenium By.partialLinkText locator for the given partial link text.
         */
        public static By partialLinkText(String partialLinkText) {
            return By.partialLinkText(partialLinkText);
        }
    }

    /**
     * Author: shashi
     * Description: Returns the current WebDriver instance from the DriverFactory.
     */
    private static WebDriver getDriver() {
        return DriverFactory.getDriver();
    }

    /**
     * Author: shashi
     * Description: Returns a WebDriverWait instance with the default explicit wait time from ConfigReader.
     */
    private static WebDriverWait getWait() {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(ConfigReader.getExplicitWait()));
    }

    /**
     * Author: shashi
     * Description: Returns a WebDriverWait instance with a custom timeout in seconds.
     */
    private static WebDriverWait getWait(int timeoutSeconds) {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutSeconds));
    }

    // Dynamic Element Finding Methods
    /**
     * Author: shashi
     * Description: Finds a single WebElement using the provided By locator. Logs error if not found.
     */
    public static WebElement findElement(By locator) {
        try {
            return getDriver().findElement(locator);
        } catch (NoSuchElementException e) {
            LogUtil.error("Element not found with locator: " + locator);
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Finds a single WebElement using a dynamic locator pattern and values.
     */
    public static WebElement findElement(String locatorPattern, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        return findElement(locator);
    }

    /**
     * Author: shashi
     * Description: Finds a list of WebElements using the provided By locator. Logs error if not found.
     */
    public static List<WebElement> findElements(By locator) {
        try {
            return getDriver().findElements(locator);
        } catch (Exception e) {
            LogUtil.error("Elements not found with locator: " + locator);
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Finds a list of WebElements using a dynamic locator pattern and values.
     */
    public static List<WebElement> findElements(String locatorPattern, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        return findElements(locator);
    }

    // Smart Element Interaction Methods
    /**
     * Author: shashi
     * Description: Clicks on a WebElement using the provided By locator, with retry logic for stale elements and JS fallback.
     */
    public static void smartClick(By locator) {
        retryOnStaleElement(driver -> {
            try {
                WebElement element = waitForElementToBeClickable(locator);
                element.click();
                LogUtil.info("Clicked on element: " + locator);
                return null;
            } catch (ElementClickInterceptedException e) {
                LogUtil.warn("Element click intercepted, trying JavaScript click: " + locator);
                jsClick(locator);
                return null;
            } catch (Exception e) {
                LogUtil.warn("Standard click failed, trying JavaScript click: " + locator);
                jsClick(locator);
                return null;
            }
        });
    }

    /**
     * Author: shashi
     * Description: Clicks on a WebElement using a dynamic locator pattern and values, with retry logic and JS fallback.
     */
    public static void smartClick(String locatorPattern, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        smartClick(locator);
    }

    /**
     * Author: shashi
     * Description: Sends keys to a WebElement using the provided By locator, after clearing it. Logs actions and errors.
     */
    public static void smartSendKeys(By locator, String text) {
        try {
            WebElement element = waitForElementToBeVisible(locator);
            element.clear();
            element.sendKeys(text);
            LogUtil.info("Entered text '" + text + "' in element: " + locator);
        } catch (Exception e) {
            LogUtil.error("Failed to enter text in element: " + locator + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Sends keys to a WebElement using a dynamic locator pattern and values, after clearing it.
     */
    public static void smartSendKeys(String locatorPattern, String text, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        smartSendKeys(locator, text);
    }

    /**
     * Author: shashi
     * Description: Gets the text of a WebElement using the provided By locator. Logs actions and errors.
     */
    public static String smartGetText(By locator) {
        try {
            WebElement element = waitForElementToBeVisible(locator);
            String text = element.getText();
            LogUtil.info("Retrieved text '" + text + "' from element: " + locator);
            return text;
        } catch (Exception e) {
            LogUtil.error("Failed to get text from element: " + locator + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Gets the text of a WebElement using a dynamic locator pattern and values.
     */
    public static String smartGetText(String locatorPattern, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        return smartGetText(locator);
    }

    /**
     * Author: shashi
     * Description: Gets the value of an attribute from a WebElement using the provided By locator.
     */
    public static String smartGetAttribute(By locator, String attributeName) {
        try {
            WebElement element = waitForElementToBeVisible(locator);
            String attributeValue = element.getAttribute(attributeName);
            LogUtil.info("Retrieved attribute '" + attributeName + "' = '" + attributeValue + "' from element: " + locator);
            return attributeValue;
        } catch (Exception e) {
            LogUtil.error("Failed to get attribute from element: " + locator + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Gets the value of an attribute from a WebElement using a dynamic locator pattern and values.
     */
    public static String smartGetAttribute(String locatorPattern, String attributeName, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        return smartGetAttribute(locator, attributeName);
    }

    // Smart Waiting Methods
    /**
     * Author: shashi
     * Description: Waits for a WebElement to be visible using the provided By locator and custom timeout.
     */
    public static WebElement waitForElementToBeVisible(By locator, int timeoutSeconds) {
        try {
            return getWait(timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            LogUtil.error("Element not visible within " + timeoutSeconds + " seconds: " + locator);
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Waits for a WebElement to be visible using the provided By locator and default timeout.
     */
    public static WebElement waitForElementToBeVisible(By locator) {
        return waitForElementToBeVisible(locator, ConfigReader.getExplicitWait());
    }

    /**
     * Author: shashi
     * Description: Waits for a WebElement to be visible using a dynamic locator pattern and values.
     */
    public static WebElement waitForElementToBeVisible(String locatorPattern, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        return waitForElementToBeVisible(locator);
    }

    /**
     * Author: shashi
     * Description: Waits for a WebElement to be clickable using the provided By locator and custom timeout.
     */
    public static WebElement waitForElementToBeClickable(By locator, int timeoutSeconds) {
        try {
            return getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            LogUtil.error("Element not clickable within " + timeoutSeconds + " seconds: " + locator);
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Waits for a WebElement to be clickable using the provided By locator and default timeout.
     */
    public static WebElement waitForElementToBeClickable(By locator) {
        return waitForElementToBeClickable(locator, ConfigReader.getExplicitWait());
    }

    /**
     * Author: shashi
     * Description: Waits for a WebElement to be clickable using a dynamic locator pattern and values.
     */
    public static WebElement waitForElementToBeClickable(String locatorPattern, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        return waitForElementToBeClickable(locator);
    }

    /**
     * Author: shashi
     * Description: Waits for a WebElement to be invisible using the provided By locator and custom timeout.
     */
    public static boolean waitForElementToBeInvisible(By locator, int timeoutSeconds) {
        try {
            return getWait(timeoutSeconds).until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            LogUtil.warn("Element still visible after " + timeoutSeconds + " seconds: " + locator);
            return false;
        }
    }

    /**
     * Author: shashi
     * Description: Waits for a WebElement to be invisible using a dynamic locator pattern and values.
     */
    public static boolean waitForElementToBeInvisible(String locatorPattern, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        return waitForElementToBeInvisible(locator, ConfigReader.getExplicitWait());
    }

    // Element State Checking Methods
    /**
     * Author: shashi
     * Description: Checks if a WebElement is present using the provided By locator.
     */
    public static boolean isElementPresent(By locator) {
        try {
            getDriver().findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Author: shashi
     * Description: Checks if a WebElement is present using a dynamic locator pattern and values.
     */
    public static boolean isElementPresent(String locatorPattern, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        return isElementPresent(locator);
    }

    /**
     * Author: shashi
     * Description: Checks if a WebElement is visible using the provided By locator.
     */
    public static boolean isElementVisible(By locator) {
        try {
            WebElement element = getDriver().findElement(locator);
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Author: shashi
     * Description: Checks if a WebElement is visible using a dynamic locator pattern and values.
     */
    public static boolean isElementVisible(String locatorPattern, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        return isElementVisible(locator);
    }

    /**
     * Author: shashi
     * Description: Checks if a WebElement is clickable using the provided By locator.
     */
    public static boolean isElementClickable(By locator) {
        try {
            WebElement element = getDriver().findElement(locator);
            return element.isDisplayed() && element.isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Author: shashi
     * Description: Checks if a WebElement is clickable using a dynamic locator pattern and values.
     */
    public static boolean isElementClickable(String locatorPattern, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        return isElementClickable(locator);
    }

    /**
     * Author: shashi
     * Description: Checks if a WebElement is selected using the provided By locator.
     */
    public static boolean isElementSelected(By locator) {
        try {
            WebElement element = getDriver().findElement(locator);
            return element.isSelected();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Author: shashi
     * Description: Checks if a WebElement is selected using a dynamic locator pattern and values.
     */
    public static boolean isElementSelected(String locatorPattern, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        return isElementSelected(locator);
    }

    // Advanced Interaction Methods
    /**
     * Author: shashi
     * Description: Scrolls to a WebElement using the provided By locator via JavaScript.
     */
    public static void scrollToElement(By locator) {
        try {
            WebElement element = findElement(locator);
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            js.executeScript("arguments[0].scrollIntoView(true);", element);
            LogUtil.info("Scrolled to element: " + locator);
        } catch (Exception e) {
            LogUtil.error("Failed to scroll to element: " + locator + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Scrolls to a WebElement using a dynamic locator pattern and values via JavaScript.
     */
    public static void scrollToElement(String locatorPattern, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        scrollToElement(locator);
    }

    /**
     * Author: shashi
     * Description: Clicks a WebElement using JavaScript and the provided By locator.
     */
    public static void jsClick(By locator) {
        try {
            WebElement element = findElement(locator);
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            js.executeScript("arguments[0].click();", element);
            LogUtil.info("JavaScript clicked on element: " + locator);
        } catch (Exception e) {
            LogUtil.error("Failed to JavaScript click element: " + locator + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Clicks a WebElement using JavaScript and a dynamic locator pattern and values.
     */
    public static void jsClick(String locatorPattern, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        jsClick(locator);
    }

    /**
     * Author: shashi
     * Description: Hovers over a WebElement using the provided By locator.
     */
    public static void hoverOver(By locator) {
        try {
            WebElement element = waitForElementToBeVisible(locator);
            Actions actions = new Actions(getDriver());
            actions.moveToElement(element).perform();
            LogUtil.info("Hovered over element: " + locator);
        } catch (Exception e) {
            LogUtil.error("Failed to hover over element: " + locator + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Hovers over a WebElement using a dynamic locator pattern and values.
     */
    public static void hoverOver(String locatorPattern, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        hoverOver(locator);
    }

    /**
     * Author: shashi
     * Description: Double-clicks a WebElement using the provided By locator.
     */
    public static void doubleClick(By locator) {
        try {
            WebElement element = waitForElementToBeClickable(locator);
            Actions actions = new Actions(getDriver());
            actions.doubleClick(element).perform();
            LogUtil.info("Double clicked on element: " + locator);
        } catch (Exception e) {
            LogUtil.error("Failed to double click element: " + locator + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Double-clicks a WebElement using a dynamic locator pattern and values.
     */
    public static void doubleClick(String locatorPattern, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        doubleClick(locator);
    }

    /**
     * Author: shashi
     * Description: Right-clicks a WebElement using the provided By locator.
     */
    public static void rightClick(By locator) {
        try {
            WebElement element = waitForElementToBeClickable(locator);
            Actions actions = new Actions(getDriver());
            actions.contextClick(element).perform();
            LogUtil.info("Right clicked on element: " + locator);
        } catch (Exception e) {
            LogUtil.error("Failed to right click element: " + locator + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Right-clicks a WebElement using a dynamic locator pattern and values.
     */
    public static void rightClick(String locatorPattern, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        rightClick(locator);
    }

    // Dropdown/Select Methods
    /**
     * Author: shashi
     * Description: Selects an option by visible text from a dropdown WebElement using the provided By locator.
     */
    public static void selectByText(By locator, String text) {
        try {
            WebElement element = waitForElementToBeVisible(locator);
            Select select = new Select(element);
            select.selectByVisibleText(text);
            LogUtil.info("Selected option by text '" + text + "' from dropdown: " + locator);
        } catch (Exception e) {
            LogUtil.error("Failed to select option by text: " + locator + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Selects an option by visible text from a dropdown WebElement using a dynamic locator pattern and values.
     */
    public static void selectByText(String locatorPattern, String text, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        selectByText(locator, text);
    }

    /**
     * Author: shashi
     * Description: Selects an option by value from a dropdown WebElement using the provided By locator.
     */
    public static void selectByValue(By locator, String value) {
        try {
            WebElement element = waitForElementToBeVisible(locator);
            Select select = new Select(element);
            select.selectByValue(value);
            LogUtil.info("Selected option by value '" + value + "' from dropdown: " + locator);
        } catch (Exception e) {
            LogUtil.error("Failed to select option by value: " + locator + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Selects an option by value from a dropdown WebElement using a dynamic locator pattern and values.
     */
    public static void selectByValue(String locatorPattern, String value, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        selectByValue(locator, value);
    }

    /**
     * Author: shashi
     * Description: Selects an option by index from a dropdown WebElement using the provided By locator.
     */
    public static void selectByIndex(By locator, int index) {
        try {
            WebElement element = waitForElementToBeVisible(locator);
            Select select = new Select(element);
            select.selectByIndex(index);
            LogUtil.info("Selected option by index '" + index + "' from dropdown: " + locator);
        } catch (Exception e) {
            LogUtil.error("Failed to select option by index: " + locator + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Selects an option by index from a dropdown WebElement using a dynamic locator pattern and values.
     */
    public static void selectByIndex(String locatorPattern, int index, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        selectByIndex(locator, index);
    }

    // Collection Methods
    /**
     * Author: shashi
     * Description: Returns the count of WebElements found using the provided By locator.
     */
    public static int getElementCount(By locator) {
        try {
            List<WebElement> elements = findElements(locator);
            LogUtil.info("Found " + elements.size() + " elements with locator: " + locator);
            return elements.size();
        } catch (Exception e) {
            LogUtil.error("Failed to count elements: " + locator + " - " + e.getMessage());
            return 0;
        }
    }

    /**
     * Author: shashi
     * Description: Returns the count of WebElements found using a dynamic locator pattern and values.
     */
    public static int getElementCount(String locatorPattern, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        return getElementCount(locator);
    }

    /**
     * Author: shashi
     * Description: Returns a list of text values from WebElements found using the provided By locator.
     */
    public static List<String> getAllTexts(By locator) {
        try {
            List<WebElement> elements = findElements(locator);
            return elements.stream()
                    .map(WebElement::getText)
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            LogUtil.error("Failed to get all texts: " + locator + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Returns a list of text values from WebElements found using a dynamic locator pattern and values.
     */
    public static List<String> getAllTexts(String locatorPattern, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        return getAllTexts(locator);
    }

    // Utility Methods
    /**
     * Author: shashi
     * Description: Waits for the page to load completely using JavaScript document.readyState.
     */
    public static void waitForPageToLoad() {
        try {
            getWait().until(webDriver ->
                    ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            LogUtil.info("Page loaded completely");
        } catch (Exception e) {
            LogUtil.warn("Page load check timed out: " + e.getMessage());
        }
    }

    /**
     * Author: shashi
     * Description: Switches to a frame using the provided By locator after waiting for it to be visible.
     */
    public static void switchToFrame(By frameLocator) {
        try {
            WebElement frame = waitForElementToBeVisible(frameLocator);
            getDriver().switchTo().frame(frame);
            LogUtil.info("Switched to frame: " + frameLocator);
        } catch (Exception e) {
            LogUtil.error("Failed to switch to frame: " + frameLocator + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Switches to a frame using a dynamic locator pattern and values after waiting for it to be visible.
     */
    public static void switchToFrame(String locatorPattern, Object... values) {
        By locator = LocatorBuilder.buildDynamicLocator(locatorPattern, values);
        switchToFrame(locator);
    }

    /**
     * Author: shashi
     * Description: Switches to the default content of the current WebDriver.
     */
    public static void switchToDefaultContent() {
        try {
            getDriver().switchTo().defaultContent();
            LogUtil.info("Switched to default content");
        } catch (Exception e) {
            LogUtil.error("Failed to switch to default content: " + e.getMessage());
            throw e;
        }
    }

    // Alert Methods
    /**
     * Author: shashi
     * Description: Accepts a browser alert if present.
     */
    public static void acceptAlert() {
        try {
            Alert alert = getWait().until(ExpectedConditions.alertIsPresent());
            alert.accept();
            LogUtil.info("Alert accepted");
        } catch (Exception e) {
            LogUtil.error("Failed to accept alert: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Dismisses a browser alert if present.
     */
    public static void dismissAlert() {
        try {
            Alert alert = getWait().until(ExpectedConditions.alertIsPresent());
            alert.dismiss();
            LogUtil.info("Alert dismissed");
        } catch (Exception e) {
            LogUtil.error("Failed to dismiss alert: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Gets the text from a browser alert if present.
     */
    public static String getAlertText() {
        try {
            Alert alert = getWait().until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            LogUtil.info("Alert text: " + alertText);
            return alertText;
        } catch (Exception e) {
            LogUtil.error("Failed to get alert text: " + e.getMessage());
            throw e;
        }
    }

    // Window Management
    /**
     * Author: shashi
     * Description: Switches to a browser window using the given window handle.
     */
    public static void switchToWindow(String windowHandle) {
        try {
            getDriver().switchTo().window(windowHandle);
            LogUtil.info("Switched to window: " + windowHandle);
        } catch (Exception e) {
            LogUtil.error("Failed to switch to window: " + windowHandle + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Returns all window handles for the current WebDriver session.
     */
    public static Set<String> getAllWindowHandles() {
        return getDriver().getWindowHandles();
    }

    /**
     * Author: shashi
     * Description: Returns the current window handle for the WebDriver session.
     */
    public static String getCurrentWindowHandle() {
        return getDriver().getWindowHandle();
    }

    /**
     * Author: shashi
     * Description: Closes the current browser window.
     */
    public static void closeCurrentWindow() {
        try {
            getDriver().close();
            LogUtil.info("Current window closed");
        } catch (Exception e) {
            LogUtil.error("Failed to close current window: " + e.getMessage());
            throw e;
        }
    }

    // Navigation
    /**
     * Author: shashi
     * Description: Navigates the browser to the specified URL.
     */
    public static void navigateTo(String url) {
        try {
            getDriver().navigate().to(url);
            LogUtil.info("Navigated to: " + url);
        } catch (Exception e) {
            LogUtil.error("Failed to navigate to: " + url + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Navigates the browser back to the previous page.
     */
    public static void navigateBack() {
        try {
            getDriver().navigate().back();
            LogUtil.info("Navigated back");
        } catch (Exception e) {
            LogUtil.error("Failed to navigate back: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Navigates the browser forward to the next page.
     */
    public static void navigateForward() {
        try {
            getDriver().navigate().forward();
            LogUtil.info("Navigated forward");
        } catch (Exception e) {
            LogUtil.error("Failed to navigate forward: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Refreshes the current browser page.
     */
    public static void refreshPage() {
        try {
            getDriver().navigate().refresh();
            LogUtil.info("Page refreshed");
        } catch (Exception e) {
            LogUtil.error("Failed to refresh page: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Returns the current URL of the browser.
     */
    public static String getCurrentUrl() {
        try {
            String url = getDriver().getCurrentUrl();
            LogUtil.info("Current URL: " + url);
            return url;
        } catch (Exception e) {
            LogUtil.error("Failed to get current URL: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Returns the title of the current browser page.
     */
    public static String getPageTitle() {
        try {
            String title = getDriver().getTitle();
            LogUtil.info("Page title: " + title);
            return title;
        } catch (Exception e) {
            LogUtil.error("Failed to get page title: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Author: shashi
     * Description: Updates the value of an input element found by XPath and triggers input/change events using JavaScript.
     */
    public static void updateInputValueByXPath(String xpath, String newValue) {
        WebElement element = getDriver().findElement(By.xpath(xpath));
        String script = "arguments[0].value = arguments[1];" +
                "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));";
        ((JavascriptExecutor) getDriver()).executeScript(script, element, newValue);
    }

    /**
     * Author: shashi
     * Description: Opens a new browser tab with the href of the element found by the given XPath using JavaScript.
     */
    public static void openNewTab(String xpath) {
        WebElement firstProductLink = CommonMethods.findElement(xpath);
        ((JavascriptExecutor) CommonMethods.getDriver()).executeScript("window.open(arguments[0].href, '_blank');", firstProductLink);

    }


}
