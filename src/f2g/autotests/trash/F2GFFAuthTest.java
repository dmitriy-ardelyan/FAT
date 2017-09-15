package f2g.autotests.trash;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 9/5/2017.
 */
public class F2GFFAuthTest {

    public static void main(String[] args) {

        DesiredCapabilities caps = DesiredCapabilities.firefox();
        //DesiredCapabilities caps = DesiredCapabilities.chrome();

        caps.setCapability("acceptInsecureCerts", true);
        caps.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
        caps.setCapability(CapabilityType.SUPPORTS_ALERTS, true);

        System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")+"\\BrowserDrivers\\geckodriver.exe");
        FirefoxDriver driver = new FirefoxDriver(caps);

        /*System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\BrowserDrivers\\chromedriver.exe");
        WebDriver driver = new ChromeDriver(caps);*/

        /*System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"\\BrowserDrivers\\IEDriverServer.exe");
        WebDriver driver = new InternetExplorerDriver();*/

        driver.get("http://localhost/webclient");
        //driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        //driver.findElement(By.partialLinkText("Continue to this")).click();

        try {
            Thread.sleep(2000);
            //Runtime.getRuntime().exec("C:\\AutoitScript\\ieauth2.exe");
            Runtime.getRuntime().exec("C:\\AutoitScript\\authentication.exe");
            //Runtime.getRuntime().exec("C:\\AutoitScript\\chromeauth.exe");

            Thread.sleep(10000);
        } catch(Exception ex){
            ex.printStackTrace();
        }
        driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
        try {
            System.out.println("driver=" + driver);
            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='top']/div[@class='buttons-panel float-left']/a[@title='Create folder']/i[1]")));
            driver.findElement(By.xpath("//div[@class='top']/div[@class='buttons-panel float-left']/a[@title='Create folder']/i[1]")).click();
        } catch (UnhandledAlertException ex){
            System.out.println("OPA unhandled alert exception!!");

            WebDriverWait wait = new WebDriverWait(driver, 3);
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            alert.accept();
            driver.switchTo().defaultContent();
        }
    }

}
