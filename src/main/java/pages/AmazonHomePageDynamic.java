package pages;

import utils.LogUtil;
import common.CommonMethods;
import xpath.AmazonHomePageXpath;

public class AmazonHomePageDynamic extends BasePage implements AmazonHomePageXpath {

    // Constructor - No PageFactory needed!
    public AmazonHomePageDynamic() {
        // No PageFactory.initElements() required
    }

    // Dynamic Element Access Methods
    public void waitForHomepageToLoad() {
        try {
            LogUtil.info("Waiting for Amazon homepage to load");

            if(CommonMethods.isElementVisible(CommonMethods.LocatorBuilder.xpath(CONTINUE_SHOPPING))){
                CommonMethods.smartClick(CommonMethods.LocatorBuilder.xpath(CONTINUE_SHOPPING));
            }
            CommonMethods.waitForElementToBeVisible(CommonMethods.LocatorBuilder.id(SEARCH_BOX));
            CommonMethods.waitForElementToBeVisible(CommonMethods.LocatorBuilder.id(AMAZON_LOGO));
            LogUtil.info("Amazon homepage loaded successfully");
        } catch (Exception e) {
            LogUtil.error("Failed to wait for homepage to load: " + e.getMessage());
            throw e;
        }
    }

    public void enterSearchTerm(String searchTerm) {
        try {
            LogUtil.info("Entering search term: " + searchTerm);
            CommonMethods.smartSendKeys(CommonMethods.LocatorBuilder.id(SEARCH_BOX), searchTerm);
            LogUtil.info("Search term entered successfully");
        } catch (Exception e) {
            LogUtil.error("Failed to enter search term: " + e.getMessage());
            throw e;
        }
    }

    public AmazonSearchResultsPageDynamic clickSearchButton() {
        try {
            LogUtil.info("Clicking search button");
            CommonMethods.smartClick(CommonMethods.LocatorBuilder.id(SEARCH_BUTTON));
            LogUtil.info("Search button clicked successfully");
            return new AmazonSearchResultsPageDynamic();
        } catch (Exception e) {
            LogUtil.error("Failed to click search button: " + e.getMessage());
            throw e;
        }
    }

    public AmazonSearchResultsPageDynamic searchForProduct(String searchTerm) {
        enterSearchTerm(searchTerm);
        return clickSearchButton();
    }

    public boolean isHomepageDisplayed() {
        return CommonMethods.isElementVisible(CommonMethods.LocatorBuilder.id(SEARCH_BOX)) &&
               CommonMethods.isElementVisible(CommonMethods.LocatorBuilder.id(AMAZON_LOGO));
    }

    public boolean doesPageTitleContain(String expectedText) {
        String actualTitle = CommonMethods.getPageTitle();
        boolean contains = actualTitle.contains(expectedText);
        LogUtil.info("Page title '" + actualTitle + "' contains '" + expectedText + "': " + contains);
        return contains;
    }
}
