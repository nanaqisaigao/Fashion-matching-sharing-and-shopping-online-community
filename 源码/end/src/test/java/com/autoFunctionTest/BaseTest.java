package com.autoFunctionTest;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 所有功能测试的基类，管理WebDriver和测试生命周期
 */
public class BaseTest {
    protected WebDriver driver;
    protected Properties config;
    protected String baseUrl;

    @BeforeClass
    public void setUp() {
        try {
            // 加载配置文件
            config = new Properties();
            FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
            config.load(fis);
            fis.close();
            
            // 设置WebDriver路径
            System.setProperty("webdriver.edge.driver", "src/test/resources/msedgedriver.exe");
            
//            // 配置Edge浏览器选项
//            EdgeOptions options = new EdgeOptions();
//            if (Boolean.parseBoolean(config.getProperty("webdriver.headless", "false"))) {
//                options.addArguments("--headless");
//            }
//
            // 初始化WebDriver
            driver = new EdgeDriver();
//
            // 设置超时时间
            driver.manage().timeouts().implicitlyWait(
                Long.parseLong(config.getProperty("webdriver.implicit.wait", "10")), 
                TimeUnit.SECONDS
            );
            driver.manage().timeouts().pageLoadTimeout(
                Long.parseLong(config.getProperty("webdriver.page.load.timeout", "30")), 
                TimeUnit.SECONDS
            );
            
            driver.manage().window().maximize();
            
            // 设置基础URL
            baseUrl = config.getProperty("base.url", "http://localhost:8080");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @AfterMethod
    public void takeScreenshotOnFailure(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            try {
                // 确保截图目录存在
                String screenshotPath = config.getProperty("screenshot.path", "test-output/screenshots");
                Path dirPath = Paths.get(screenshotPath);
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                }
                
                // 生成文件名
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
                String timestamp = dateFormat.format(new Date());
                String fileName = result.getName() + "-" + timestamp + ".png";
                
                // 截图并保存
                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Files.copy(screenshot.toPath(), Paths.get(screenshotPath, fileName));
                
                System.out.println("截图已保存: " + Paths.get(screenshotPath, fileName).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
} 