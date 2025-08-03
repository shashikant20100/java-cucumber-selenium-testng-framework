package stepdefinitions;

import io.cucumber.java.en.*;
import pages.PageObjectManager;
import pages.AmazonHomePageDynamic;
import pages.AmazonSearchResultsPageDynamic;
import pages.AmazonProductPageDynamic;
import drivers.DriverFactory;
import utils.LogUtil;
import utils.ExtentManager;
import static org.testng.Assert.*;

public class amazon_smartwatch {
    
    // Page object instances - Updated to use Dynamic classes
    private final AmazonHomePageDynamic homePage;
    private final AmazonSearchResultsPageDynamic searchPage;
    private final AmazonProductPageDynamic productPage;

    // Constructor to initialize page objects
    public amazon_smartwatch() {
        this.homePage = PageObjectManager.getAmazonHomePage();
        this.searchPage = PageObjectManager.getAmazonSearchResultsPage();
        this.productPage = PageObjectManager.getAmazonProductPage();
    }

    @Given("I launch Chrome browser and navigate to Amazon India")
    public void i_launch_chrome_and_navigate_to_amazon_india() {
        LogUtil.info("STEP: Launch Chrome browser and navigate to Amazon India");
        ExtentManager.logStep("Launch Chrome browser and navigate to Amazon India");

        DriverFactory.getDriver("chrome");
        String amazonUrl = "https://www.amazon.in/";
        DriverFactory.getDriver().get(amazonUrl);

        ExtentManager.logAction("Navigate to", amazonUrl);
        LogUtil.info("Navigated to Amazon India: " + amazonUrl);
    }

    @When("I wait for the Amazon homepage to load completely")
    public void i_wait_for_amazon_homepage_to_load() {
        LogUtil.info("STEP: Wait for Amazon homepage to load completely");
        ExtentManager.logStep("Wait for Amazon homepage to load completely");

        homePage.waitForHomepageToLoad();
        ExtentManager.logAction("Wait for page load", "Amazon Homepage");
    }

    @Then("I should see the Amazon page title contains {string}")
    public void i_should_see_amazon_page_title_contains(String expectedTitleText) {
        LogUtil.info("STEP: Verify Amazon page title contains: " + expectedTitleText);
        ExtentManager.logStepWithParams("Verify Amazon page title contains", expectedTitleText);

        boolean result = homePage.doesPageTitleContain(expectedTitleText);

        ExtentManager.logVerification("Page title contains '" + expectedTitleText + "'", result);
        assertTrue(result, "Expected title to contain '" + expectedTitleText + "' but actual title was: " + homePage.getPageTitle());
        LogUtil.info("Page title verification successful");
    }

    @When("I search for {string} in the search box")
    public void i_search_for_in_search_box(String searchTerm) {
        LogUtil.info("STEP: Enter search term in search box: " + searchTerm);
        ExtentManager.logStepWithParams("Enter search term in search box", searchTerm);

        homePage.enterSearchTerm(searchTerm);
        ExtentManager.logAction("Enter text", "Search Box: " + searchTerm);
    }

    @And("I click on the search button")
    public void i_click_on_search_button() {
        LogUtil.info("STEP: Click on the search button");
        ExtentManager.logStep("Click on the search button");

        // Fixed: Now properly handling the returned page object
        AmazonSearchResultsPageDynamic searchResultsPage = homePage.clickSearchButton();
        ExtentManager.logPageNavigation("Home Page", "Search Results Page");
        ExtentManager.logAction("Click", "Search Button");
    }

    @Then("I should see search results for smartwatches")
    public void i_should_see_search_results_for_smartwatches() {
        LogUtil.info("STEP: Verify search results are displayed");
        ExtentManager.logStep("Verify search results are displayed");

        boolean result = searchPage.areSearchResultsDisplayed();
        ExtentManager.logVerification("Search results are displayed", result);
        assertTrue(result, "Search results should be displayed");
        LogUtil.info("Search results verification successful");
    }

    @When("I apply brand filter for {string}")
    public void i_apply_brand_filter_for(String brandName) {
        LogUtil.info("STEP: Apply brand filter for: " + brandName);
        ExtentManager.logStepWithParams("Apply brand filter", brandName);
        searchPage.applyBrandFilter(brandName);
        ExtentManager.logAction("Apply filter", "Brand: " + brandName);
    }

    @And("I set price filter with minimum price {string} and maximum price {string}")
    public void i_set_price_filter_with_min_and_max(String minPrice, String maxPrice) throws InterruptedException {
        LogUtil.info("STEP: Set price filter - Min: " + minPrice + ", Max: " + maxPrice);
        ExtentManager.logStepWithParams("Set price filter", "Min: " + minPrice, "Max: " + maxPrice);

        searchPage.setPriceFilter(minPrice, maxPrice);
        ExtentManager.logAction("Set price filter", minPrice + " - " + maxPrice);
    }

    @Then("all displayed smartwatches should have prices between {string} and {string} rupees")
    public void all_displayed_smartwatches_should_have_prices_between(String minPrice, String maxPrice) {
        LogUtil.info("STEP: Verify all products are within price range: " + minPrice + " - " + maxPrice);
        ExtentManager.logStepWithParams("Verify products within price range", minPrice + " - " + maxPrice + " rupees");

        boolean result = searchPage.verifyAllProductsWithinPriceRange(minPrice, maxPrice);
        ExtentManager.logVerification("All products within price range " + minPrice + " - " + maxPrice, result);
        assertTrue(result, "All products should be within price range " + minPrice + " - " + maxPrice);
        LogUtil.info("Price range verification successful");
    }

    @When("I sort the search results by price from high to low")
    public void i_sort_search_results_by_price_high_to_low() {
        LogUtil.info("STEP: Sort search results by price high to low");
        ExtentManager.logStep("Sort search results by price from high to low");

        searchPage.sortByPriceHighToLow();
        ExtentManager.logAction("Apply sorting", "Price: High to Low");
    }

    @And("I click on the product with the highest price")
    public void i_click_on_highest_price_product() {
        LogUtil.info("STEP: Click on the product with highest price");
        ExtentManager.logStep("Click on the product with highest price");

        AmazonProductPageDynamic productPageResult = searchPage.clickOnHighestPriceProduct();
        ExtentManager.logAction("Click", "Highest Price Product");
    }

    @Then("the product details page should open in a new window")
    public void product_details_page_should_open_in_new_window() {
        LogUtil.info("STEP: Verify product details page opens in new window");
        ExtentManager.logStep("Verify product details page opens in new window");

        boolean result = searchPage.isNewWindowOpened();
        ExtentManager.logVerification("Product details page opens in new window", result);
        assertTrue(result, "Product details should open in new window");
        LogUtil.info("New window verification successful");
    }

    @When("I switch to the new product window")
    public void i_switch_to_new_product_window() {
        LogUtil.info("STEP: Switch to new product window");
        ExtentManager.logStep("Switch to new product window");

        AmazonProductPageDynamic productPageResult = searchPage.switchToNewWindow();
        ExtentManager.logPageNavigation("Search Results Page", "Product Details Page");
        ExtentManager.logAction("Switch to", "New Window");
    }

    @And("I verify the product details are displayed")
    public void i_verify_product_details_are_displayed() {
        LogUtil.info("STEP: Verify product details are displayed");
        ExtentManager.logStep("Verify product details are displayed");

        boolean result = productPage.areProductDetailsDisplayed();
        ExtentManager.logVerification("Product details are displayed", result);
        assertTrue(result, "Product details should be displayed");
        LogUtil.info("Product details verification successful");
    }

    @And("I add the product to my cart")
    public void i_add_product_to_my_cart() {
        LogUtil.info("STEP: Add product to cart");
        ExtentManager.logStep("Add product to cart");

        productPage.addProductToCart();
        ExtentManager.logAction("Click", "Add to Cart Button");
    }


    @And("the cart count should be updated")
    public void cart_count_should_be_updated() {
        LogUtil.info("STEP: Verify cart count is updated");
        ExtentManager.logStep("Verify cart count is updated");

        boolean result = productPage.isCartCountUpdated();
        ExtentManager.logVerification("Cart count is updated", result);
        assertTrue(result, "Cart count should be updated");
        LogUtil.info("Cart count update verification successful");
    }

    @And("the search results should display relevant smartwatch products")
    public void search_results_should_display_relevant_smartwatch_products() {
        LogUtil.info("STEP: Verify search results contain relevant smartwatch products");
        ExtentManager.logStep("Verify search results contain relevant smartwatch products");

        boolean result = searchPage.areSearchResultsRelevant("smartwatch");
        ExtentManager.logVerification("Search results contain relevant smartwatch products", result);
        assertTrue(result, "Search results should contain relevant smartwatch products");
        LogUtil.info("Search results relevance verification successful");
    }

    @When("I close the new window and return to main window")
    public void i_close_new_window_and_return_to_main() {
        LogUtil.info("STEP: Close new window and return to main window");
        ExtentManager.logStep("Close new window and return to main window");

        productPage.closeCurrentWindowAndSwitchToMain();
        ExtentManager.logPageNavigation("Product Details Page", "Search Results Page");
        ExtentManager.logAction("Close window", "Product details window closed");
    }

    @Then("I should be back on the search results page")
    public void i_should_be_back_on_search_results_page() {
        LogUtil.info("STEP: Verify back on search results page");
        ExtentManager.logStep("Verify back on search results page");

        boolean result = searchPage.areSearchResultsDisplayed();
        ExtentManager.logVerification("Back on search results page", result);
        assertTrue(result, "Should be back on search results page");
        LogUtil.info("Successfully returned to search results page");
    }
}
