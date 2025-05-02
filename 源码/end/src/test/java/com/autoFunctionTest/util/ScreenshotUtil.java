package com.autoFunctionTest.util;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 截图工具类
 */
public class ScreenshotUtil {
    private static final String DEFAULT_SCREENSHOT_DIR = "test-output/screenshots";
    private static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    
    /**
     * 截取当前页面的屏幕截图
     */
    public static void takeScreenshot(WebDriver driver, String testName) {
        if (driver == null) {
            System.err.println("无法截图：WebDriver为null");
            return;
        }
        
        if (!(driver instanceof TakesScreenshot)) {
            System.err.println("当前WebDriver不支持截图功能");
            return;
        }
        
        // 获取截图目录
        String screenshotDir = ConfigLoader.getProperty("screenshot.path", DEFAULT_SCREENSHOT_DIR);
        
        // 创建目录（如果不存在）
        Path dirPath = Paths.get(screenshotDir);
        try {
            Files.createDirectories(dirPath);
        } catch (IOException e) {
            System.err.println("无法创建截图目录: " + screenshotDir);
            e.printStackTrace();
            return;
        }
        
        // 生成文件名 (testName_timestamp.png)
        String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        String fileName = String.format("%s_%s.png", testName, timestamp);
        
        // 截图并保存
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path destination = Paths.get(screenshotDir, fileName);
            Files.copy(screenshot.toPath(), destination);
            System.out.println("截图已保存: " + destination.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("保存截图失败: " + fileName);
            e.printStackTrace();
        }
    }
} 