package com.autoFunctionTest.testcase;

import com.autoFunctionTest.BaseTest;
import com.autoFunctionTest.po.HomePage;
import com.autoFunctionTest.po.LoginPage;
import com.autoFunctionTest.po.ProductDetailPage;
import com.autoFunctionTest.po.ShopPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 商城功能测试类
 */
public class ShopTest extends BaseTest {
    
    /**
     * 每个测试方法前执行登录
     */
    @BeforeMethod
    public void setup() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        
        String username = config.getProperty("test.user.phone");
        String password = config.getProperty("test.user.password");
        
        loginPage.login(username, password);
    }
    
    /**
     * 测试打开商城页面
     */
    @Test
    public void testOpenShopPage() {
        ShopPage shopPage = new ShopPage(driver);
        shopPage.open();
        
        Assert.assertTrue(shopPage.isPageLoaded(), "商城页面未正确加载");
        Assert.assertTrue(shopPage.getProductCount() > 0, "商城页面应该显示商品");
    }
    
    /**
     * 测试搜索商品功能
     */
    @Test
    public void testSearchProduct() {
        ShopPage shopPage = new ShopPage(driver);
        shopPage.open();
        
        // 搜索关键词
        String keyword = "衬衫";
        shopPage.searchProduct(keyword);
        
        // 验证是否有搜索结果
        int productCount = shopPage.getProductCount();
        Assert.assertTrue(productCount > 0, "搜索应该返回结果");
        
        // 验证搜索结果是否包含关键词
        if (productCount > 0) {
            String productName = shopPage.getProductName(0);
            // 不区分大小写搜索匹配
            Assert.assertTrue(
                productName.toLowerCase().contains(keyword.toLowerCase()) || 
                keyword.toLowerCase().contains(productName.toLowerCase()),
                "搜索结果应该与关键词相关: " + productName
            );
        }
    }
    
    /**
     * 测试类别筛选功能
     */
    @Test
    public void testFilterByCategory() {
        ShopPage shopPage = new ShopPage(driver);
        shopPage.open();
        
        int initialCount = shopPage.getProductCount();
        
        // 选择第一个类别筛选
        shopPage.filterByCategory(0);
        
        // 验证筛选后的商品列表
        int filteredCount = shopPage.getProductCount();
        Assert.assertTrue(filteredCount > 0, "筛选后应该有商品显示");
    }
    
    /**
     * 测试价格筛选功能
     */
    @Test
    public void testFilterByPrice() {
        ShopPage shopPage = new ShopPage(driver);
        shopPage.open();
        
        // 选择第一个价格范围
        shopPage.filterByPrice(0);
        
        // 验证筛选后的商品列表
        int filteredCount = shopPage.getProductCount();
        Assert.assertTrue(filteredCount > 0, "筛选后应该有商品显示");
        
        // 获取商品价格并验证是否在筛选范围内
        // 注意：这里需要根据实际筛选范围进行验证
        if (filteredCount > 0) {
            String priceText = shopPage.getProductPrice(0);
            Assert.assertNotNull(priceText, "商品应该有价格");
        }
    }
    
    /**
     * 测试排序功能
     */
    @Test
    public void testSortProducts() {
        ShopPage shopPage = new ShopPage(driver);
        shopPage.open();
        
        // 按销量排序（假设是第1个排序选项）
        shopPage.sortBy(1);
        
        // 验证排序后的商品列表
        int productCount = shopPage.getProductCount();
        Assert.assertTrue(productCount > 0, "排序后应该有商品显示");
    }
    
    /**
     * 测试查看商品详情
     */
    @Test
    public void testViewProductDetail() {
        ShopPage shopPage = new ShopPage(driver);
        shopPage.open();
        
        // 确保有商品可查看
        int productCount = shopPage.getProductCount();
        if (productCount > 0) {
            // 获取第一个商品的名称
            String productName = shopPage.getProductName(0);
            
            // 查看第一个商品详情
            ProductDetailPage detailPage = shopPage.viewProductDetails(0);
            
            // 验证详情页是否加载
            Assert.assertTrue(detailPage.isPageLoaded(), "商品详情页未正确加载");
            
            // 验证商品名称是否一致
            String detailProductName = detailPage.getProductName();
            Assert.assertEquals(detailProductName, productName, "详情页商品名称与列表页不一致");
        } else {
            Assert.fail("没有商品可供测试");
        }
    }
    
    /**
     * 测试分页功能
     */
    @Test
    public void testPagination() {
        ShopPage shopPage = new ShopPage(driver);
        shopPage.open();
        
        // 记录第一页的第一个商品名称
        int productCount = shopPage.getProductCount();
        if (productCount > 0) {
            String firstPageProductName = shopPage.getProductName(0);
            
            // 跳转到第二页
            shopPage.goToPage(2);
            
            // 验证页面已加载
            Assert.assertTrue(shopPage.isPageLoaded(), "第二页未正确加载");
            
            // 获取第二页的第一个商品名称
            String secondPageProductName = shopPage.getProductName(0);
            
            // 验证两页的商品不同
            Assert.assertNotEquals(secondPageProductName, firstPageProductName, "第二页的商品应该与第一页不同");
        }
    }
    
    /**
     * 测试添加商品到购物车
     */
    @Test
    public void testAddToCart() {
        ShopPage shopPage = new ShopPage(driver);
        shopPage.open();
        
        // 确保有商品可添加
        int productCount = shopPage.getProductCount();
        if (productCount > 0) {
            // 添加第一个商品到购物车
            shopPage.addProductToCart(0);
            
            // 验证是否显示添加成功的信息
            // 这里需要根据实际页面上的成功提示来验证
            // 例如可能有一个toast消息或者弹窗
        } else {
            Assert.fail("没有商品可供测试");
        }
    }
} 