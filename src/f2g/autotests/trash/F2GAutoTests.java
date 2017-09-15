package f2g.autotests.trash;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class F2GAutoTests {

    public static void main(String[] args) {

        /*//Switch to files tab.
    //this.driver.findElement(By.xpath("//a[@href='#folder-files-to-add-tab']")).click();
    this.driver.findElement(By.xpath("//a[@href='#folder-files-to-add-tab']")).sendKeys(Keys.ENTER);

    //Select all files and save PR.
    List<WebElement> checkboxesList= this.driver.findElements(By.xpath("//div[@class='right-checkbox-cell']"));
    for (WebElement el:checkboxesList){
        el.click();
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
    }
    this.driver.findElement(By.xpath("//section[@id='folder-files-to-add-tab']/div[@class='dialog-footer']/button[text()='Add']")).click();*/
////////////////////////////////////////////////////////
        //DesiredCapabilities caps = DesiredCapabilities.firefox();
        //DesiredCapabilities caps = DesiredCapabilities.chrome();

        /*caps.setCapability("acceptInsecureCerts", true);
        caps.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
        caps.setCapability(CapabilityType.SUPPORTS_ALERTS, false);*/

        /*System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")+"\\BrowserDrivers\\geckodriver.exe");
        FirefoxDriver driver = new FirefoxDriver(caps);*/

        /*System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\BrowserDrivers\\chromedriver.exe");
        WebDriver driver = new ChromeDriver(caps);*/

        System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"\\BrowserDrivers\\IEDriverServer.exe");
        WebDriver driver = new InternetExplorerDriver();

        driver.get("http://localhost/webclient");
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.findElement(By.partialLinkText("Continue to this")).click();

        try {
            Thread.sleep(2000);
            Runtime.getRuntime().exec("C:\\AutoitScript\\ieauth2.exe");

            //Runtime.getRuntime().exec("C:\\AutoitScript\\chromeauth.exe");

            Thread.sleep(10000);
        } catch(Exception ex){
            ex.printStackTrace();
        }
        //driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
        try {
            /*System.out.println("driver=" + driver);
            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='top']/div[@class='buttons-panel float-left']/a[@title='Create folder']/i[1]")));*/
            driver.findElement(By.xpath("//div[@class='top']/div[@class='buttons-panel float-left']/a[@title='Create folder']/i[1]")).click();
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
            driver.findElement(By.xpath("//div[@class='overlay overlay-contentscale open']/div[@class='dialog']/div[@class='dialog-footer']/button[text()='Close']")).click();
        } catch (UnhandledAlertException ex){
            System.out.println("OPA unhandled alert exception!!");

           /* WebDriverWait wait = new WebDriverWait(driver, 3);
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            alert.accept();
            driver.switchTo().defaultContent();*/
        }
    }
}
