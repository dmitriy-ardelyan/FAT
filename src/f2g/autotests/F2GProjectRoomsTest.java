package f2g.autotests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class F2GProjectRoomsTest {
    private WebDriver driver;

    @BeforeClass
    public void BeforeClass(){
        //IE
        System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"\\BrowserDrivers\\IEDriverServer32.exe");
        DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
        capabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
        WebDriver wd = new InternetExplorerDriver(capabilities);
        this.driver = wd;
        F2GIEAuth.authenticate(this.driver);
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//a[@href='#my-work-space']")).click();
    }

    @Test(priority=1)
    private void createProjectRoomOnTabTest() {

        //Delete PR if it exists due to previous tests.
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        if (F2GTestsLib.checkElementPresence(this.driver,"TestPR1")){
            F2GTestsLib.findElementAndSelectAction(this.driver,"TestPR1", "Delete");
            this.driver.navigate().refresh();
        }

        //Initiate PR creation form.
        this.driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//a[@title='Create project room']")).click();

        //Fill PR name field. Somehow here sendKeys function fails from time to time. So i had to create special STAB. See SendKeysCondition class.
        String prNameInputLocator = "//div[@class='overlay overlay-contentscale open']/div[@class='dialog share-dialog']" +
                "/div[@class='tabs tabs-style-bar']/div[@class='content-wrap']/section[@id='folder-settings-tab']/div[@class='dialog-body']" +
                "/div[@class='form middle-form']/div[1]/span/input[@class='tt-input']";
        this.driver.findElement(By.xpath(prNameInputLocator)).sendKeys("TestPR1");
        SendKeysCondition condition = new SendKeysCondition(prNameInputLocator);
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(condition);

        //Finish creation process.
        this.driver.findElement(By.xpath("//div[@class='dialog-footer']/button[text()='Create'][@data-bind='visible: !UserSettingsVisible(),click: save']")).click();
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//div[@class='overlay overlay-contentscale open']/div[@class='dialog']" +
                "/div[@class='dialog-body']/div[@class='middle-form']/div[4]/button")).click();

        System.out.println("opa");
    }


    @AfterClass
    public void AfterClass(){
        driver.quit();
    }


}
