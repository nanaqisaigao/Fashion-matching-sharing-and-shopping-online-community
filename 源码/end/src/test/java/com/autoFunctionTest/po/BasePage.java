package com.autoFunctionTest.po;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

/**
 * 页面对象基类，提供所有页面通用的操作方法
 */
public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Properties config;
    protected String baseUrl;
    
    public BasePage(WebDriver driver) {
        this.driver = driver;
        try {
            // 加载配置文件
            config = new Properties();
            FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
            config.load(fis);
            fis.close();
            
            // 设置显式等待时间
            int waitTime = Integer.parseInt(config.getProperty("webdriver.explicit.wait", "15"));
            wait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
            
            // 获取基础URL
            baseUrl = config.getProperty("base.url", "http://localhost:8080");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 导航到指定页面
     * @param relativeUrl 相对URL
     */
    public void navigateTo(String relativeUrl) {
        driver.get(baseUrl + relativeUrl);
    }
    
    /**
     * 查找元素
     * @param locator 元素定位器
     * @return WebElement对象
     */
    protected WebElement findElement(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    /**
     * 点击元素
     * @param locator 元素定位器
     */
    protected void click(By locator) {
        findElement(locator).click();
    }
    
    /**
     * 输入文本
     * @param locator 元素定位器
     * @param text 要输入的文本
     */
    protected void type(By locator, String text) {
        WebElement element = findElement(locator);
        element.clear();
        element.sendKeys(text);
    }
    
    /**
     * 获取元素文本
     * @param locator 元素定位器
     * @return 元素文本内容
     */
    protected String getText(By locator) {
        return findElement(locator).getText();
    }
    
    /**
     * 判断元素是否存在
     * @param locator 元素定位器
     * @return 是否存在
     */
    protected boolean isElementPresent(By locator) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 等待元素可见
     * @param locator 元素定位器
     */
    protected void waitForVisibility(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    /**
     * 等待元素可点击
     * @param locator 元素定位器
     */
    protected void waitForClickable(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    
    /**
     * 等待URL包含特定字符串
     * @param urlPart URL的部分内容
     * @param timeoutInSeconds 超时时间（秒）
     */
    protected void waitForUrlContains(String urlPart, int timeoutInSeconds) {
        WebDriverWait urlWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        urlWait.until(ExpectedConditions.urlContains(urlPart));
    }
    
    /**
     * 使用JavaScript执行点击操作
     * @param locator 元素定位器
     */
    protected void jsClick(By locator) {
        WebElement element = findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }
    
    /**
     * 页面滚动到元素位置
     * @param locator 元素定位器
     */
    protected void scrollToElement(By locator) {
        WebElement element = findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }
} 