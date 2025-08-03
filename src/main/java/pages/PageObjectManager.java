package pages;

import drivers.DriverFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class PageObjectManager {
    // Thread-safe storage for page instances per thread
    private static final ThreadLocal<Map<Class<? extends BasePage>, BasePage>> pageInstances =
        ThreadLocal.withInitial(ConcurrentHashMap::new);

    // Generic method to get or create any page instance (Thread-Safe)
    @SuppressWarnings("unchecked")
    public static <T extends BasePage> T getPage(Class<T> pageClass) {
        try {
            Map<Class<? extends BasePage>, BasePage> threadPageInstances = pageInstances.get();

            // Check if instance already exists for this thread
            T pageInstance = (T) threadPageInstances.get(pageClass);

            if (pageInstance == null) {
                // Create new instance using reflection
                pageInstance = pageClass.getDeclaredConstructor().newInstance();
                threadPageInstances.put(pageClass, pageInstance);
                utils.LogUtil.info("Created new page instance for thread " + Thread.currentThread().getId() + ": " + pageClass.getSimpleName());
            }

            return pageInstance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize page: " + pageClass.getSimpleName() + " for thread: " + Thread.currentThread().getId(), e);
        }
    }

    // Convenience methods for commonly used pages (Thread-Safe) - Updated to use Dynamic classes
    public static AmazonHomePageDynamic getAmazonHomePage() {
        return getPage(AmazonHomePageDynamic.class);
    }

    public static AmazonSearchResultsPageDynamic getAmazonSearchResultsPage() {
        return getPage(AmazonSearchResultsPageDynamic.class);
    }

    public static AmazonProductPageDynamic getAmazonProductPage() {
        return getPage(AmazonProductPageDynamic.class);
    }

    // Method to clear all page instances for current thread (Thread-Safe)
    public static void clearAllPages() {
        Map<Class<? extends BasePage>, BasePage> threadPageInstances = pageInstances.get();
        threadPageInstances.clear();
        utils.LogUtil.info("Cleared all page instances for thread: " + Thread.currentThread().getId());
    }

    // Method to clear specific page instance for current thread
    public static void clearPage(Class<? extends BasePage> pageClass) {
        Map<Class<? extends BasePage>, BasePage> threadPageInstances = pageInstances.get();
        BasePage removed = threadPageInstances.remove(pageClass);
        if (removed != null) {
            utils.LogUtil.info("Cleared page instance for thread " + Thread.currentThread().getId() + ": " + pageClass.getSimpleName());
        }
    }

    // Method to check if page instance exists for current thread
    public static boolean hasPageInstance(Class<? extends BasePage> pageClass) {
        Map<Class<? extends BasePage>, BasePage> threadPageInstances = pageInstances.get();
        return threadPageInstances.containsKey(pageClass);
    }

    // Method to get number of initialized pages for current thread
    public static int getInitializedPageCount() {
        Map<Class<? extends BasePage>, BasePage> threadPageInstances = pageInstances.get();
        return threadPageInstances.size();
    }

    // Method to completely remove ThreadLocal for current thread (important for cleanup)
    public static void removeThreadLocalPages() {
        pageInstances.remove();
        utils.LogUtil.info("Removed ThreadLocal page instances for thread: " + Thread.currentThread().getId());
    }
}
