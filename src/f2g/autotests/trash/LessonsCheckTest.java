package f2g.autotests.trash;

import f2g.autotests.F2GIEAuth;
import org.junit.After;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.junit.Assert;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class LessonsCheckTest {
    private WebDriver driver;

    @BeforeClass
    public void BeforeClass(){
        //Chrome
        /*DesiredCapabilities caps = DesiredCapabilities.chrome();
        caps.setCapability("acceptInsecureCerts", true);
        caps.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
        caps.setCapability(CapabilityType.SUPPORTS_ALERTS, false);
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\BrowserDrivers\\chromedriver.exe");
        WebDriver wd = new ChromeDriver(caps);
        this.driver = wd;
        F2GChromeAuth.authenticate(this.driver);*/

        //IE
        System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"\\BrowserDrivers\\IEDriverServer.exe");
        WebDriver wd = new InternetExplorerDriver();
        this.driver = wd;
        F2GIEAuth.authenticate(this.driver);
    }

    @Test
    //Test1. Using a web element title in XPATH.
    private void test1() {
        try {
            this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
            this.driver.findElement(By.xpath("//div[@class='top']/div[@class='buttons-panel float-left']/a[@title='Create folder']/i[1]")).click();
            this.driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
            this.driver.findElement(By.xpath("//div[@class='overlay overlay-contentscale open']/div[@class='dialog']/div[@class='dialog-footer']/button[text()='Close']")).click();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Test
    //Test2.Using link in XPATH.
    private void test2(){
        this.driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//a[@href='#my-work-space']")).click();
        this.driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//a[@href='#server']")).click();
    }

    @Test
    //Test3.Creating web element.
    private void test3(){
        this.driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        WebElement searchElement = this.driver.findElement(By.xpath("//div[@class='search float-right']/input"));
        searchElement.sendKeys("12");
        WebElement searchButton = this.driver.findElement(By.xpath("//div[@class='search float-right']/span"));
        searchButton.click();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        searchElement.clear();
        searchButton.click();
    }


    @AfterClass
    public void AfterClass(){
        driver.quit();
    }
}
