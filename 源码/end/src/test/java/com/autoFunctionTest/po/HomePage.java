package com.autoFunctionTest.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * 用户首页页面对象类
 */
public class HomePage extends BasePage {
    // 页面元素定位器
    private By carouselContainer = By.cssSelector(".el-carousel");
    private By outfitItems = By.cssSelector(".outfit-card");
    private By productItems = By.cssSelector(".product-card");
    private By searchInput = By.cssSelector(".search-input input");
    private By searchButton = By.cssSelector(".search-button");
    private By addToCartButtons = By.cssSelector(".product-card .add-to-cart-btn");
    private By viewDetailsButtons = By.cssSelector(".product-card .view-details-btn");
    private By cartIcon = By.cssSelector(".el-icon-shopping-cart-2");
    
    public HomePage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * 打开首页
     * @return HomePage实例
     */
    public HomePage open() {
        navigateTo("/#/user/home");
        return this;
    }
    
    /**
     * 验证页面是否加载完成
     * @return 页面加载是否完成
     */
    public boolean isPageLoaded() {
        return isElementPresent(carouselContainer) && 
               isElementPresent(outfitItems) && 
               isElementPresent(productItems);
    }
    
    /**
     * 获取推荐商品数量
     * @return 推荐商品数量
     */
    public int getRecommendedProductsCount() {
        List<WebElement> products = driver.findElements(productItems);
        return products.size();
    }
    
    /**
     * 获取穿搭分享数量
     * @return 穿搭分享数量
     */
    public int getOutfitsCount() {
        List<WebElement> outfits = driver.findElements(outfitItems);
        return outfits.size();
    }
    
    /**
     * 搜索商品
     * @param keyword 搜索关键词
     * @return ShopPage实例
     */
    public ShopPage searchProduct(String keyword) {
        type(searchInput, keyword);
        click(searchButton);
        return new ShopPage(driver);
    }
    
    /**
     * 将指定索引的商品添加到购物车
     * @param index 商品索引（从0开始）
     * @return HomePage实例
     */
    public HomePage addProductToCart(int index) {
        List<WebElement> buttons = driver.findElements(addToCartButtons);
        if (index < buttons.size()) {
            buttons.get(index).click();
            // 等待添加成功提示
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this;
    }
    
    /**
     * 查看指定索引的商品详情
     * @param index 商品索引（从0开始）
     * @return ProductDetailPage实例
     */
    public ProductDetailPage viewProductDetails(int index) {
        List<WebElement> buttons = driver.findElements(viewDetailsButtons);
        if (index < buttons.size()) {
            buttons.get(index).click();
            return new ProductDetailPage(driver);
        }
        throw new IndexOutOfBoundsException("商品索引超出范围");
    }
    
    /**
     * 查看购物车
     * @return CartPage实例
     */
    public Object viewCart() {
        click(cartIcon);
        // 此处应返回CartPage实例，但暂未创建该类
        return null;
    }
    
    /**
     * 查看轮播图数量
     * @return 轮播图数量
     */
    public int getCarouselItemsCount() {
        By carouselItems = By.cssSelector(".el-carousel__item");
        List<WebElement> items = driver.findElements(carouselItems);
        return items.size();
    }
} 