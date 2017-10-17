package f2g.autotests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.concurrent.TimeUnit;

public class F2GUsersTest {

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
        this.driver.findElement(By.xpath("//a[@href='#external-users']")).click();
    }

    @Test(priority=1)
    private void createUserTest() {

        //Delete user if it is already exist.
        if (F2GTestsLib.checkElementPresence(this.driver, "testuser29 (test user29)")) {
            F2GTestsLib.findElementAndSelectAction(this.driver, "testuser29 (test user29)", "Delete");
            this.driver.navigate().refresh();
        }

        //Create user.
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        driver.findElement(By.xpath("//a[@title='Add external user']")).click();
        driver.findElement(By.xpath("//input[@data-bind='value: Login, autocomplete: $parent.ExternalUsersNames'][@class='tt-input']")).sendKeys("testuser29");
        driver.findElement(By.xpath("//input[@data-bind='value: FirstName']")).sendKeys("test");
        driver.findElement(By.xpath("//input[@data-bind='value: LastName']")).sendKeys("user29");
        driver.findElement(By.xpath("//input[@data-bind='value: Email']")).sendKeys("testuser29@gmail.com");
        driver.findElement(By.xpath("//input[@data-bind='value: Password']")).sendKeys("Password123");
        driver.findElement(By.xpath("//div[@class='overlay overlay-contentscale open']/div[@class='dialog']" +
                "/div[@class='dialog-footer']/button[text()='Save'][@data-bind='click: save']")).click();

        //Check that user exists
        F2GTestsLib.checkIfElementExists(this.driver,"testuser29 (test user29)");
    }

    @Test(priority=2)
    private void editUserTest() {

        //Delete old edited users.
        if(F2GTestsLib.checkElementPresence(this.driver,"testuser29-Edited (test-Edited user29-Edited)")){
            F2GTestsLib.findElementAndSelectAction(this.driver, "testuser29-Edited (test-Edited user29-Edited)", "Delete");
            this.driver.navigate().refresh();
        }

        //Create a user if it does not exist.
        if (!F2GTestsLib.checkElementPresence(this.driver, "testuser29 (test user29)")) {
            //Create user.
            driver.findElement(By.xpath("//a[@title='Add external user']")).click();
            driver.findElement(By.xpath("//input[@data-bind='value: Login, autocomplete: $parent.ExternalUsersNames'][@class='tt-input']")).sendKeys("testuser29");
            driver.findElement(By.xpath("//input[@data-bind='value: FirstName']")).sendKeys("test");
            driver.findElement(By.xpath("//input[@data-bind='value: LastName']")).sendKeys("user29");
            driver.findElement(By.xpath("//input[@data-bind='value: Email']")).sendKeys("testuser29@gmail.com");
            driver.findElement(By.xpath("//input[@data-bind='value: Password']")).sendKeys("Password123");
            driver.findElement(By.xpath("//div[@class='overlay overlay-contentscale open']/div[@class='dialog']" +
                    "/div[@class='dialog-footer']/button[text()='Save'][@data-bind='click: save']")).click();
        }

        //Edit user.
        F2GTestsLib.findElementAndSelectAction(this.driver, "testuser29 (test user29)","Edit");
        String tmpLogin = driver.findElement(By.xpath("//input[@data-bind='value: Login, autocomplete: $parent.ExternalUsersNames'][@class='tt-input']")).getAttribute("value");
        String tmpFirstName = driver.findElement(By.xpath("//input[@data-bind='value: FirstName']")).getAttribute("value");
        String tmpLastName = driver.findElement(By.xpath("//input[@data-bind='value: LastName']")).getAttribute("value");
        tmpLogin = tmpLogin + "-Edited";
        tmpFirstName = tmpFirstName + "-Edited";
        tmpLastName = tmpLastName + "-Edited";
        driver.findElement(By.xpath("//input[@data-bind='value: Login, autocomplete: $parent.ExternalUsersNames'][@class='tt-input']")).clear();
        driver.findElement(By.xpath("//input[@data-bind='value: Login, autocomplete: $parent.ExternalUsersNames'][@class='tt-input']")).sendKeys(tmpLogin);
        driver.findElement(By.xpath("//input[@data-bind='value: FirstName']")).clear();
        driver.findElement(By.xpath("//input[@data-bind='value: FirstName']")).sendKeys(tmpFirstName);
        driver.findElement(By.xpath("//input[@data-bind='value: LastName']")).clear();
        driver.findElement(By.xpath("//input[@data-bind='value: LastName']")).sendKeys(tmpLastName);
        driver.findElement(By.xpath("//div[@class='overlay overlay-contentscale open']/div[@class='dialog']" +
                "/div[@class='dialog-footer']/button[text()='Save'][@data-bind='click: save']")).click();

        //Check that data updated.
        F2GTestsLib.checkIfElementExists(this.driver, "testuser29-Edited (test-Edited user29-Edited)");
     }

   @Test(priority=3)
   private void deleteUserTest() {

       //Create a user if it does not exist.
       if (!F2GTestsLib.checkElementPresence(this.driver, "testuser29 (test user29)")) {
           //Create user.
           driver.findElement(By.xpath("//a[@title='Add external user']")).click();
           driver.findElement(By.xpath("//input[@data-bind='value: Login, autocomplete: $parent.ExternalUsersNames'][@class='tt-input']")).sendKeys("testuser29");
           driver.findElement(By.xpath("//input[@data-bind='value: FirstName']")).sendKeys("test");
           driver.findElement(By.xpath("//input[@data-bind='value: LastName']")).sendKeys("user29");
           driver.findElement(By.xpath("//input[@data-bind='value: Email']")).sendKeys("testuser29@gmail.com");
           driver.findElement(By.xpath("//input[@data-bind='value: Password']")).sendKeys("Password123");
           driver.findElement(By.xpath("//div[@class='overlay overlay-contentscale open']/div[@class='dialog']" +
                   "/div[@class='dialog-footer']/button[text()='Save'][@data-bind='click: save']")).click();
       }

       //Delete user.
       F2GTestsLib.findElementAndSelectAction(this.driver, "testuser29 (test user29)", "Delete");
       this.driver.navigate().refresh();

       //Ensure that user does not exist anymore.
       F2GTestsLib.checkThatElementDoesNotExist(this.driver,"testuser29 (test user29)");
   }


    @AfterClass
    public void AfterClass(){
        driver.quit();
    }

}
