package com.autoFunctionTest.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * 商城页面对象类
 */
public class ShopPage extends BasePage {
    // 页面元素定位器 - 从配置文件获取
    private By searchInput;
    private By searchButton;
    private By productItems;
    private By priceFilterDropdown;
    private By categoryFilterItems;
    private By sortDropdown;
    private By pagination;
    private By addToCartButtons;
    private By productNames;
    private By productPrices;
    
    public ShopPage(WebDriver driver) {
        super(driver);
        
        // 从配置文件中获取选择器
        searchInput = By.cssSelector(config.getProperty("shop.search.input", ".search-input"));
        searchButton = By.cssSelector(config.getProperty("shop.search.button", ".el-icon-search"));
        productItems = By.cssSelector(config.getProperty("shop.product.items", ".goods-item"));
        priceFilterDropdown = By.cssSelector(config.getProperty("shop.price.filter", ".el-select"));
        categoryFilterItems = By.cssSelector(config.getProperty("shop.category.items", ".category-item"));
        sortDropdown = By.cssSelector(config.getProperty("shop.sort.dropdown", ".sort-select .el-input"));
        pagination = By.cssSelector(config.getProperty("shop.pagination", ".el-pagination"));
        addToCartButtons = By.cssSelector(config.getProperty("shop.add.cart.button", ".goods-item .add-cart"));
        productNames = By.cssSelector(config.getProperty("shop.product.name", ".goods-item .goods-title"));
        productPrices = By.cssSelector(config.getProperty("shop.product.price", ".goods-item .goods-price"));
    }
    
    /**
     * 打开商城页面
     * @return ShopPage实例
     */
    public ShopPage open() {
        navigateTo("/user/shop");
        return this;
    }
    
    /**
     * 验证页面是否加载完成
     * @return 页面加载是否完成
     */
    public boolean isPageLoaded() {
        return isElementPresent(searchInput) && 
               isElementPresent(productItems) && 
               isElementPresent(pagination);
    }
    
    /**
     * 搜索商品
     * @param keyword 搜索关键词
     * @return ShopPage实例
     */
    public ShopPage searchProduct(String keyword) {
        type(searchInput, keyword);
        click(searchButton);
        // 等待搜索结果加载
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this;
    }
    
    /**
     * 按类别筛选
     * @param categoryIndex 类别索引（从0开始）
     * @return ShopPage实例
     */
    public ShopPage filterByCategory(int categoryIndex) {
        List<WebElement> categories = driver.findElements(categoryFilterItems);
        if (categoryIndex < categories.size()) {
            categories.get(categoryIndex).click();
            // 等待筛选结果加载
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this;
    }
    
    /**
     * 按价格筛选
     * @param priceRangeIndex 价格范围索引（从0开始）
     * @return ShopPage实例
     */
    public ShopPage filterByPrice(int priceRangeIndex) {
        click(priceFilterDropdown);
        By priceOption = By.xpath("(//ul[contains(@class,'el-select-dropdown__list')]//li)[" + (priceRangeIndex + 1) + "]");
        click(priceOption);
        return this;
    }
    
    /**
     * 按排序方式排序
     * @param sortOptionIndex 排序选项索引（从0开始）
     * @return ShopPage实例
     */
    public ShopPage sortBy(int sortOptionIndex) {
        click(sortDropdown);
        By sortOption = By.xpath("(//ul[contains(@class,'el-select-dropdown__list')]//li)[" + (sortOptionIndex + 1) + "]");
        click(sortOption);
        return this;
    }
    
    /**
     * 获取商品列表数量
     * @return 商品数量
     */
    public int getProductCount() {
        List<WebElement> products = driver.findElements(productItems);
        return products.size();
    }
    
    /**
     * 获取指定索引的商品名称
     * @param index 商品索引（从0开始）
     * @return 商品名称
     */
    public String getProductName(int index) {
        List<WebElement> names = driver.findElements(productNames);
        if (index < names.size()) {
            return names.get(index).getText();
        }
        return "";
    }
    
    /**
     * 获取指定索引的商品价格
     * @param index 商品索引（从0开始）
     * @return 商品价格文本
     */
    public String getProductPrice(int index) {
        List<WebElement> prices = driver.findElements(productPrices);
        if (index < prices.size()) {
            return prices.get(index).getText();
        }
        return "";
    }
    
    /**
     * 将指定索引的商品添加到购物车
     * @param index 商品索引（从0开始）
     * @return ShopPage实例
     */
    public ShopPage addProductToCart(int index) {
        List<WebElement> buttons = driver.findElements(addToCartButtons);
        if (index < buttons.size()) {
            buttons.get(index).click();
            // 等待添加成功提示
            waitForVisibility(By.cssSelector(".el-message--success"));
        }
        return this;
    }
    
    /**
     * 查看指定索引的商品详情
     * @param index 商品索引（从0开始）
     * @return ProductDetailPage实例
     */
    public ProductDetailPage viewProductDetails(int index) {
        List<WebElement> products = driver.findElements(productItems);
        if (index < products.size()) {
            products.get(index).click();
            waitForUrlContains("/user/shop/product/", 10);
            return new ProductDetailPage(driver);
        }
        throw new IndexOutOfBoundsException("商品索引超出范围");
    }
    
    /**
     * 跳转到指定页码
     * @param pageNumber 页码
     * @return ShopPage实例
     */
    public ShopPage goToPage(int pageNumber) {
        By pageButton = By.xpath("//ul[@class='el-pager']/li[text()='" + pageNumber + "']");
        if (isElementPresent(pageButton)) {
            scrollToElement(pageButton);
            click(pageButton);
            // 等待页面加载
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this;
    }
} 