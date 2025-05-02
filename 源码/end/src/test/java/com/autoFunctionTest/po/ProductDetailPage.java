package com.autoFunctionTest.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * 商品详情页面对象类
 */
public class ProductDetailPage extends BasePage {
    // 页面元素定位器
    private By productName = By.cssSelector(".product-name");
    private By productPrice = By.cssSelector(".price");
    private By productImage = By.cssSelector(".gallery-image");
    private By quantityInput = By.cssSelector(".el-input-number input");
    private By quantityPlusButton = By.cssSelector(".el-input-number__increase");
    private By quantityMinusButton = By.cssSelector(".el-input-number__decrease");
    private By addToCartButton = By.xpath("//button[contains(text(),'加入购物车')]");
    private By buyNowButton = By.xpath("//button[contains(text(),'立即购买')]");
    private By stockInfo = By.cssSelector(".stock-info");
    private By tabDetails = By.xpath("//div[contains(@role,'tab') and contains(text(),'商品详情')]");
    private By tabReviews = By.xpath("//div[contains(@role,'tab') and contains(text(),'商品评价')]");
    private By productDescription = By.cssSelector(".detail-section p");
    private By reviewItems = By.cssSelector(".review-item");
    private By relatedOutfits = By.cssSelector(".related-outfits-section .outfit-card");
    private By buyDialogConfirmButton = By.xpath("//span[contains(@class,'dialog-footer')]//button[contains(text(),'确认下单')]");
    private By orderRemarkTextarea = By.cssSelector(".remark-input textarea");
    
    public ProductDetailPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * 验证页面是否加载完成
     * @return 页面加载是否完成
     */
    public boolean isPageLoaded() {
        return isElementPresent(productName) && 
               isElementPresent(productPrice) && 
               isElementPresent(productImage);
    }
    
    /**
     * 获取商品名称
     * @return 商品名称
     */
    public String getProductName() {
        return getText(productName);
    }
    
    /**
     * 获取商品价格
     * @return 商品价格
     */
    public String getProductPrice() {
        return getText(productPrice);
    }
    
    /**
     * 获取库存信息
     * @return 库存信息
     */
    public String getStockInfo() {
        return getText(stockInfo);
    }
    
    /**
     * 设置购买数量
     * @param quantity 购买数量
     * @return ProductDetailPage实例
     */
    public ProductDetailPage setQuantity(int quantity) {
        // 先获取当前数量
        String currentValue = findElement(quantityInput).getAttribute("value");
        int current = Integer.parseInt(currentValue);
        
        // 根据当前数量和目标数量决定是增加还是减少
        if (quantity > current) {
            for (int i = current; i < quantity; i++) {
                click(quantityPlusButton);
            }
        } else if (quantity < current) {
            for (int i = current; i > quantity; i--) {
                click(quantityMinusButton);
            }
        }
        
        return this;
    }
    
    /**
     * 加入购物车
     * @return ProductDetailPage实例
     */
    public ProductDetailPage addToCart() {
        click(addToCartButton);
        // 等待操作完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this;
    }
    
    /**
     * 立即购买
     * @return ProductDetailPage实例
     */
    public ProductDetailPage buyNow() {
        click(buyNowButton);
        // 等待购买对话框显示
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this;
    }
    
    /**
     * 在购买对话框中填写备注
     * @param remark 备注内容
     * @return ProductDetailPage实例
     */
    public ProductDetailPage fillOrderRemark(String remark) {
        type(orderRemarkTextarea, remark);
        return this;
    }
    
    /**
     * 确认下单
     */
    public void confirmOrder() {
        click(buyDialogConfirmButton);
        // 等待操作完成
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 切换到商品详情标签
     * @return ProductDetailPage实例
     */
    public ProductDetailPage switchToDetailsTab() {
        click(tabDetails);
        return this;
    }
    
    /**
     * 切换到商品评价标签
     * @return ProductDetailPage实例
     */
    public ProductDetailPage switchToReviewsTab() {
        click(tabReviews);
        return this;
    }
    
    /**
     * 获取商品描述
     * @return 商品描述文本
     */
    public String getProductDescription() {
        switchToDetailsTab();
        return getText(productDescription);
    }
    
    /**
     * 获取评价数量
     * @return 评价数量
     */
    public int getReviewsCount() {
        switchToReviewsTab();
        return driver.findElements(reviewItems).size();
    }
    
    /**
     * 获取相关穿搭数量
     * @return 相关穿搭数量
     */
    public int getRelatedOutfitsCount() {
        return driver.findElements(relatedOutfits).size();
    }
    
    /**
     * 查看指定索引的相关穿搭
     * @param index 穿搭索引（从0开始）
     */
    public void viewRelatedOutfit(int index) {
        driver.findElements(relatedOutfits).get(index).click();
    }
} 