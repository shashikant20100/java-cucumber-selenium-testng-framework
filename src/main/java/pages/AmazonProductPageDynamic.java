package pages;

import utils.LogUtil;
import utils.ExtentManager;
import xpath.AmazonProductPageXPath;
import common.CommonMethods;

import java.util.Set;

public class AmazonProductPageDynamic extends BasePage implements AmazonProductPageXPath {

    // Constructor - No PageFactory needed!
    public AmazonProductPageDynamic() {
        // No PageFactory.initElements() required
    }

    // Dynamic Element Access Methods
    public boolean areProductDetailsDisplayed() {
        try {
            LogUtil.info("Checking if product details are displayed");
            CommonMethods.waitForElementToBeVisible(CommonMethods.LocatorBuilder.id(PRODUCT_TITLE));

            boolean titleDisplayed = CommonMethods.isElementVisible(CommonMethods.LocatorBuilder.id(PRODUCT_TITLE));
            boolean priceDisplayed = CommonMethods.isElementVisible(CommonMethods.LocatorBuilder.css(PRODUCT_PRICE));
            boolean detailsDisplayed = titleDisplayed && priceDisplayed;

            LogUtil.info("Product details displayed: " + detailsDisplayed);
            return detailsDisplayed;
        } catch (Exception e) {
            LogUtil.error("Failed to verify product details: " + e.getMessage());
            return false;
        }
    }

    public String getProductTitle() {
        try {
            return CommonMethods.smartGetText(CommonMethods.LocatorBuilder.id(PRODUCT_TITLE));
        } catch (Exception e) {
            LogUtil.error("Failed to get product title: " + e.getMessage());
            return "";
        }
    }

    public String getProductPrice() {
        try {
            return CommonMethods.smartGetText(CommonMethods.LocatorBuilder.css(PRODUCT_PRICE));
        } catch (Exception e) {
            LogUtil.error("Failed to get product price: " + e.getMessage());
            return "";
        }
    }

    public void addProductToCart() {
        try {
            LogUtil.info("Adding product to cart");
            CommonMethods.scrollToElement(CommonMethods.LocatorBuilder.css(ADD_TO_CART_BUTTON));
            CommonMethods.smartClick(CommonMethods.LocatorBuilder.css(ADD_TO_CART_BUTTON));
            LogUtil.info("Product added to cart successfully");
        } catch (Exception e) {

            LogUtil.error("Failed to add product to cart: " + e.getMessage());
            throw e;
        }
    }

    public boolean isProductAddedToCartConfirmationDisplayed() {
            LogUtil.info("Checking for cart confirmation");
            try {
                return isCartCountUpdated();
            } catch (Exception e) {
            LogUtil.error("Failed to verify cart confirmation: " + e.getMessage());
            return false;
        }
    }

    public boolean isCartCountUpdated() {
        try {
            LogUtil.info("Checking if cart count is updated");
            String countText = CommonMethods.smartGetText(CommonMethods.LocatorBuilder.id(CART_COUNT));
            int count = Integer.parseInt(countText);

            boolean countUpdated = count > 0;
            LogUtil.info("Cart count: " + count + ", Updated: " + countUpdated);
            return countUpdated;
        } catch (Exception e) {
            LogUtil.error("Failed to check cart count: " + e.getMessage());
            return false;
        }
    }

    public int getCartCount() {
        try {
            String countText = CommonMethods.smartGetText(CommonMethods.LocatorBuilder.id(CART_COUNT));
            return Integer.parseInt(countText);
        } catch (Exception e) {
            LogUtil.warn("Could not get cart count: " + e.getMessage());
            return 0;
        }
    }

    public boolean isAddToCartButtonDisplayed() {
        return CommonMethods.isElementVisible(CommonMethods.LocatorBuilder.id(ADD_TO_CART_BUTTON));
    }

    public void closeCurrentWindowAndSwitchToMain() {
        try {
            LogUtil.info("Closing current window and switching to main window");

            // Get all window handles
            Set<String> allWindows = CommonMethods.getAllWindowHandles();
            String currentWindow = CommonMethods.getCurrentWindowHandle();

            // Close current window
            CommonMethods.closeCurrentWindow();
            ExtentManager.logAction("Close window", "Current product window closed");

            // Switch to the remaining window (main window)
            for (String windowHandle : allWindows) {
                if (!windowHandle.equals(currentWindow)) {
                    CommonMethods.switchToWindow(windowHandle);
                    ExtentManager.logAction("Switch to window", "Main search results window");
                    break;
                }
            }

            LogUtil.info("Successfully closed product window and returned to main window");

        } catch (Exception e) {
            LogUtil.error("Failed to close window and return to main: " + e.getMessage());
            ExtentManager.logError("Failed to close window", e);
            throw new RuntimeException("Window management failed", e);
        }
    }

    // Advanced Dynamic Methods
    public String getProductDescription() {
        try {
            LogUtil.info("Getting product description");
            String descriptionSelector = "#feature-bullets ul, #productDescription, .product-description";
            return CommonMethods.smartGetText(CommonMethods.LocatorBuilder.css(descriptionSelector));
        } catch (Exception e) {
            LogUtil.error("Failed to get product description: " + e.getMessage());
            return "";
        }
    }

    public String getProductRating() {
        try {
            LogUtil.info("Getting product rating");
            String ratingSelector = "[data-hook='average-star-rating'] .a-icon-alt, .a-star-medium .a-icon-alt";
            return CommonMethods.smartGetText(CommonMethods.LocatorBuilder.css(ratingSelector));
        } catch (Exception e) {
            LogUtil.error("Failed to get product rating: " + e.getMessage());
            return "";
        }
    }

    public int getReviewCount() {
        try {
            LogUtil.info("Getting review count");
            String reviewCountSelector = "[data-hook='total-review-count'], .cr-widget-ACR .a-size-base";
            String reviewText = CommonMethods.smartGetText(CommonMethods.LocatorBuilder.css(reviewCountSelector));
            // Extract number from text like "1,234 ratings"
            String numberOnly = reviewText.replaceAll("[^0-9,]", "").replace(",", "");
            return Integer.parseInt(numberOnly);
        } catch (Exception e) {
            LogUtil.error("Failed to get review count: " + e.getMessage());
            return 0;
        }
    }

    public void selectQuantity(int quantity) {
        try {
            LogUtil.info("Selecting quantity: " + quantity);
            String quantitySelector = "#quantity";
            CommonMethods.selectByValue(CommonMethods.LocatorBuilder.css(quantitySelector), String.valueOf(quantity));
            LogUtil.info("Quantity selected: " + quantity);
        } catch (Exception e) {
            LogUtil.error("Failed to select quantity: " + e.getMessage());
            throw e;
        }
    }

    public void addToWishlist() {
        try {
            LogUtil.info("Adding product to wishlist");
            String wishlistSelector = "[data-action='add-to-wishlist'], #wishListMainButton";
            CommonMethods.smartClick(CommonMethods.LocatorBuilder.css(wishlistSelector));
            LogUtil.info("Product added to wishlist");
        } catch (Exception e) {
            LogUtil.error("Failed to add to wishlist: " + e.getMessage());
            throw e;
        }
    }

    public void buyNow() {
        try {
            LogUtil.info("Clicking Buy Now button");
            String buyNowSelector = "#buy-now-button, [name='submit.buy-now']";
            CommonMethods.smartClick(CommonMethods.LocatorBuilder.css(buyNowSelector));
            LogUtil.info("Buy Now button clicked");
        } catch (Exception e) {
            LogUtil.error("Failed to click Buy Now: " + e.getMessage());
            throw e;
        }
    }

    public boolean isProductInStock() {
        try {
            LogUtil.info("Checking if product is in stock");
            // Check for out of stock indicators
            String outOfStockSelectors = "#availability .a-color-state, #availability .a-color-price";

            if (CommonMethods.isElementPresent(CommonMethods.LocatorBuilder.css(outOfStockSelectors))) {
                String availabilityText = CommonMethods.smartGetText(CommonMethods.LocatorBuilder.css(outOfStockSelectors));
                boolean inStock = !availabilityText.toLowerCase().contains("out of stock") &&
                                 !availabilityText.toLowerCase().contains("unavailable");
                LogUtil.info("Product in stock: " + inStock);
                return inStock;
            }

            // If no availability text found, assume in stock if Add to Cart button is present
            boolean inStock = CommonMethods.isElementVisible(CommonMethods.LocatorBuilder.id(ADD_TO_CART_BUTTON));
            LogUtil.info("Product in stock (based on Add to Cart button): " + inStock);
            return inStock;
        } catch (Exception e) {
            LogUtil.error("Failed to check stock status: " + e.getMessage());
            return false;
        }
    }

    public String getDeliveryInfo() {
        try {
            LogUtil.info("Getting delivery information");
            String deliverySelector = "#deliveryBlockMessage, #mir-layout-DELIVERY_BLOCK, .a-text-bold:contains('delivery')";
            return CommonMethods.smartGetText(CommonMethods.LocatorBuilder.css(deliverySelector));
        } catch (Exception e) {
            LogUtil.error("Failed to get delivery info: " + e.getMessage());
            return "";
        }
    }
}
