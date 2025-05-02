package com.autoFunctionTest;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;

public class DriverQuickstartTest {

    @Test
    public void testDriver() {
        System.setProperty("webdriver.edge.driver", "D:/学习软件安装/WebDriver/msedgedriver.exe");
        WebDriver driver = new EdgeDriver();
        driver.get("http://localhost:8080/#/user/helloHome");
    }
}