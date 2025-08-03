package xpath;

public interface AmazonSearchResultsPageXPath {
    String SEARCH_RESULT = "[data-component-type='s-search-result']";
    String FILTERS_SECTION = "brandsRefinements";
    String SORT_DROPDOWN = "//span[contains(@class,'a-dropdown-label')]";
    String FIRST_PRODUCT_LINK = "(//div[@data-component-type='s-search-result']//a/h2)[1]/parent::a";
    String PRICE_ELEMENTS = ".a-price-whole, .a-price .a-offscreen";
    String PRODUCT_TITLES = "h2 a span";
    String MIN_PRICE_INPUT = "//input[@name='low-price']";
    String MAX_PRICE_INPUT = "//input[@name='high-price']";
    String PRICE_GO_BUTTON = "//input[@aria-label='Go - Submit price range']";
    String PRICE_HIGH_TO_LOW_OPTION = "//a[contains(text(),'Price: High to Low') or contains(text(),'high to low')]";
    // Dynamic locator pattern for brand checkbox
    String BRAND_CHECKBOX_PATTERN = "//div[@id='brandsRefinements']//span[text()='%s']";

}
