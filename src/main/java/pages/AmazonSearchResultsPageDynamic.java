package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import utils.LogUtil;
import xpath.AmazonSearchResultsPageXPath;
import common.CommonMethods;

import java.util.List;
import java.util.Set;

public class AmazonSearchResultsPageDynamic extends BasePage implements AmazonSearchResultsPageXPath {

    // Constructor - No PageFactory needed!
    public AmazonSearchResultsPageDynamic() {
        // No PageFactory.initElements() required
    }

    // Dynamic Element Access Methods
    public boolean areSearchResultsDisplayed() {
        try {
            LogUtil.info("Checking if search results are displayed");

            // Wait for search results to be present
            CommonMethods.waitForElementToBeVisible(SEARCH_RESULT);

            // Get count of search results dynamically
            int resultCount = CommonMethods.getElementCount(CommonMethods.LocatorBuilder.css(SEARCH_RESULT));
            boolean resultsDisplayed = resultCount > 0;

            LogUtil.info("Search results displayed: " + resultsDisplayed + " (Found " + resultCount + " products)");
            return resultsDisplayed;
        } catch (Exception e) {
            LogUtil.error("Failed to verify search results: " + e.getMessage());
            return false;
        }
    }

    public void applyBrandFilter(String brandName) {
        try {
            LogUtil.info("Applying brand filter for: " + brandName);

            // Scroll to filters section dynamically
            CommonMethods.scrollToElement(CommonMethods.LocatorBuilder.id(FILTERS_SECTION));

            // Click brand checkbox using dynamic locator
            String brandCheckboxXPath = String.format(BRAND_CHECKBOX_PATTERN, brandName);

           if (!CommonMethods.isElementSelected(brandCheckboxXPath)) {
                CommonMethods.smartClick(brandCheckboxXPath);
            }

            waitForResults();
            LogUtil.info("Brand filter applied successfully for: " + brandName);
        } catch (Exception e) {
            LogUtil.error("Failed to apply brand filter: " + e.getMessage());
            throw e;
        }
    }

    public void setPriceFilter(String minPrice, String maxPrice) throws InterruptedException {
        try {
            LogUtil.info("Setting price filter: " + minPrice + " - " + maxPrice);

            // Enter minimum price dynamically
            CommonMethods.updateInputValueByXPath(MIN_PRICE_INPUT,minPrice);

            // Enter maximum price dynamically
            CommonMethods.updateInputValueByXPath(MAX_PRICE_INPUT, maxPrice);

            // Click Go button
            CommonMethods.smartClick(PRICE_GO_BUTTON);

            CommonMethods.refreshPage();

            waitForResults();
            LogUtil.info("Price filter set successfully");
        } catch (Exception e) {
            LogUtil.error("Failed to set price filter: " + e.getMessage());
            throw e;
        }
    }

    public boolean verifyAllProductsWithinPriceRange(String minPrice, String maxPrice) {
        try {
            LogUtil.info("Verifying all products are within price range: " + minPrice + " - " + maxPrice);

            // Get all price texts dynamically
            List<String> priceTexts = CommonMethods.getAllTexts(CommonMethods.LocatorBuilder.css(PRICE_ELEMENTS));

            boolean allWithinRange = true;
            int checkedProducts = 0;

            for (String priceText : priceTexts) {
                try {
                    String cleanPrice = priceText.replaceAll("[^0-9]", "");
                    if (!cleanPrice.isEmpty()) {
                        int price = Integer.parseInt(cleanPrice);
                        if (price < Integer.parseInt(minPrice) || price > Integer.parseInt(maxPrice)) {
                            LogUtil.warn("Product price " + price + " is outside range " + minPrice + "-" + maxPrice);
                            allWithinRange = false;
                        }
                        checkedProducts++;
                    }
                } catch (NumberFormatException e) {
                    LogUtil.warn("Could not parse price: " + priceText);
                }

                if (checkedProducts >= 10) break; // Check first 10 products
            }

            LogUtil.info("Price range verification completed. Checked " + checkedProducts + " products. All within range: " + allWithinRange);
            return allWithinRange;
        } catch (Exception e) {
            LogUtil.error("Failed to verify price range: " + e.getMessage());
            return false;
        }
    }

    public void sortByPriceHighToLow() {
        try {
            LogUtil.info("Sorting by price high to low");

            // Click sort dropdown
            CommonMethods.smartClick(SORT_DROPDOWN);

            // Select "Price: High to Low" option
            CommonMethods.smartClick(PRICE_HIGH_TO_LOW_OPTION);

            waitForResults();
            LogUtil.info("Sorted by price high to low successfully");
        } catch (Exception e) {
            LogUtil.error("Failed to sort by price: " + e.getMessage());
            throw e;
        }
    }

    public AmazonProductPageDynamic clickOnHighestPriceProduct() {
        try {
            LogUtil.info("Clicking on the highest price product");

            CommonMethods.openNewTab(FIRST_PRODUCT_LINK);

            LogUtil.info("Clicked on highest price product successfully");
            return new AmazonProductPageDynamic();
        } catch (Exception e) {
            LogUtil.error("Failed to click on highest price product: " + e.getMessage());
            throw e;
        }
    }

    public boolean areSearchResultsRelevant(String searchTerm) {
        try {
            LogUtil.info("Checking if search results are relevant to: " + searchTerm);

            // Get all product titles dynamically
            List<String> productTitles = CommonMethods.getAllTexts(CommonMethods.LocatorBuilder.css(PRODUCT_TITLES));

            int relevantProducts = 0;
            int totalChecked = Math.min(productTitles.size(), 5); // Check first 5 products

            for (int i = 0; i < totalChecked; i++) {
                String title = productTitles.get(i).toLowerCase();
                if (title.contains(searchTerm.toLowerCase())) {
                    relevantProducts++;
                }
            }

            boolean resultsRelevant = (relevantProducts >= totalChecked / 2); // At least 50% relevant
            LogUtil.info("Relevant products: " + relevantProducts + "/" + totalChecked + ", Results relevant: " + resultsRelevant);
            return resultsRelevant;
        } catch (Exception e) {
            LogUtil.error("Failed to check search results relevance: " + e.getMessage());
            return false;
        }
    }

    public boolean isNewWindowOpened() {
        try {
            LogUtil.info("Checking if new window is opened");
            Set<String> windowHandles = CommonMethods.getAllWindowHandles();
            boolean newWindowOpened = windowHandles.size() > 1;
            LogUtil.info("New window opened: " + newWindowOpened + " (Total windows: " + windowHandles.size() + ")");
            return newWindowOpened;
        } catch (Exception e) {
            LogUtil.error("Failed to check new window: " + e.getMessage());
            return false;
        }
    }

    public AmazonProductPageDynamic switchToNewWindow() {
        try {
            LogUtil.info("Switching to new window");
            Set<String> windowHandles = CommonMethods.getAllWindowHandles();
            String currentWindow = CommonMethods.getCurrentWindowHandle();

            for (String windowHandle : windowHandles) {
                if (!windowHandle.equals(currentWindow)) {
                    CommonMethods.switchToWindow(windowHandle);
                    break;
                }
            }

            LogUtil.info("Switched to new window successfully");
            return new AmazonProductPageDynamic();
        } catch (Exception e) {
            LogUtil.error("Failed to switch to new window: " + e.getMessage());
            throw e;
        }
    }

    // Advanced Dynamic Methods
    public void filterByBrandAndPriceRange(String brandName, String minPrice, String maxPrice) throws InterruptedException {
        try {
            LogUtil.info("Applying combined filter - Brand: " + brandName + ", Price: " + minPrice + "-" + maxPrice);

            // Apply brand filter
            applyBrandFilter(brandName);

            // Apply price filter
            setPriceFilter(minPrice, maxPrice);

            LogUtil.info("Combined filter applied successfully");
        } catch (Exception e) {
            LogUtil.error("Failed to apply combined filter: " + e.getMessage());
            throw e;
        }
    }

    public List<String> getAllProductTitles() {
        try {
            LogUtil.info("Getting all product titles");
            return CommonMethods.getAllTexts(CommonMethods.LocatorBuilder.css(PRODUCT_TITLES));
        } catch (Exception e) {
            LogUtil.error("Failed to get product titles: " + e.getMessage());
            throw e;
        }
    }

    public List<String> getAllProductPrices() {
        try {
            LogUtil.info("Getting all product prices");
            return CommonMethods.getAllTexts(CommonMethods.LocatorBuilder.css(PRICE_ELEMENTS));
        } catch (Exception e) {
            LogUtil.error("Failed to get product prices: " + e.getMessage());
            throw e;
        }
    }

    public void clickProductByIndex(int index) {
        try {
            LogUtil.info("Clicking on product at index: " + index);

            // Dynamic locator for nth product
            String productLocator = String.format("[data-component-type='s-search-result']:nth-child(%d) h2 a", index + 1);
            CommonMethods.smartClick(productLocator);

            LogUtil.info("Clicked on product at index " + index + " successfully");
        } catch (Exception e) {
            LogUtil.error("Failed to click on product at index " + index + ": " + e.getMessage());
            throw e;
        }
    }

    public void clickProductByTitle(String productTitle) {
        try {
            LogUtil.info("Clicking on product with title containing: " + productTitle);

            // Dynamic XPath to find product by title
            String productLocator = String.format("//h2//span[contains(text(),'%s')]/ancestor::h2/a", productTitle);
            CommonMethods.smartClick(productLocator);

            LogUtil.info("Clicked on product with title: " + productTitle);
        } catch (Exception e) {
            LogUtil.error("Failed to click on product with title '" + productTitle + "': " + e.getMessage());
            throw e;
        }
    }

    public boolean isFilterApplied(String filterName) {
        try {
            LogUtil.info("Checking if filter is applied: " + filterName);

            // Dynamic locator to check if filter is active
            String activeFilterLocator = String.format("//span[contains(@class,'s-filter-applied') and contains(text(),'%s')]", filterName);
            boolean isApplied = CommonMethods.isElementVisible(activeFilterLocator);

            LogUtil.info("Filter '" + filterName + "' is applied: " + isApplied);
            return isApplied;
        } catch (Exception e) {
            LogUtil.error("Failed to check filter status: " + e.getMessage());
            return false;
        }
    }

    public void removeFilter(String filterName) {
        try {
            LogUtil.info("Removing filter: " + filterName);

            // Dynamic locator to find and click remove filter button
            String removeFilterLocator = String.format("//span[contains(text(),'%s')]/following-sibling::*//i[contains(@class,'remove')]", filterName);
            CommonMethods.smartClick(removeFilterLocator);

            waitForResults();
            LogUtil.info("Filter removed successfully: " + filterName);
        } catch (Exception e) {
            LogUtil.error("Failed to remove filter '" + filterName + "': " + e.getMessage());
            throw e;
        }
    }

    // Utility method for waiting after actions
    private void waitForResults() {
        try {
            Thread.sleep(2000); // Wait for 2 seconds for results to update
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LogUtil.warn("Thread interrupted while waiting for results: " + e.getMessage());
        }
    }
}
