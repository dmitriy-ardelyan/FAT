package f2g.autotests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
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
        F2GTestsLib.deleteProjectRoom(this.driver, "TestPR1");

        //Initiate PR creation form.
        F2GTestsLib.createTestPR1(this.driver);

        //Check that PR exists.
        F2GTestsLib.checkIfElementExists(this.driver, "TestPR1");
    }

    @Test(priority=2)
    private void uploadFileToProjectRoomTest() {

        //Create new blank TestPR1.
        if(!F2GTestsLib.checkElementPresence(this.driver,"TestPR1")){
            F2GTestsLib.createTestPR1(this.driver);
        } else{
            F2GTestsLib.deleteProjectRoom(this.driver, "TestPR1");
            F2GTestsLib.createTestPR1(this.driver);
        }

        //Enter a PR.
        F2GTestsLib.openF2GElement(this.driver, "TestPR1");

        //Initiate upload file form.
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.findElement(By.xpath("//a[@title='Upload']")).click();
        try {
            this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
            this.driver.findElement(By.xpath("//a[@title='Upload']")).click();
            Runtime.getRuntime().exec("C:\\AutoitScript\\fileUpload.exe");
            Thread.sleep(7000);
        } catch (Exception ex){
            ex.printStackTrace();
        }

        //Check that slytherin.jpeg file uploaded.
        F2GTestsLib.checkIfElementExists(this.driver, "slytherin.jpeg");
        this.driver.findElement(By.xpath("//a[@href='#my-work-space']")).click();
    }

   @Test(priority=3)
   private void sendLinkToProjectRoomTest() {

       //Create new blank TestPR1.
       if(!F2GTestsLib.checkElementPresence(this.driver,"TestPR1")){
           F2GTestsLib.createTestPR1(this.driver);
       } else{
           F2GTestsLib.deleteProjectRoom(this.driver, "TestPR1");
           F2GTestsLib.createTestPR1(this.driver);
       }

       //Open PR settings.
       if(!F2GTestsLib.checkElementPresence(this.driver,"TestPR1")){
           F2GTestsLib.createTestPR1(this.driver);
       }
       F2GTestsLib.findElementAndSelectAction(this.driver, "TestPR1", "Settings");

       //Switch to External users tab and initiate Add External User form.
       this.driver.findElement(By.xpath("//div[@class='overlay overlay-contentscale open']/div[@class='dialog share-dialog']" +
               "/div[@class='tabs tabs-style-bar']/nav[@class='dialog-head']/ul/li[2]/a")).click();
       this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
       this.driver.findElement(By.xpath("//section[@id='folder-external-users-tab'][@class='content-current']/div[@class='dialog-footer']" +
               "/button[text()='Add external user']")).click();

       //Select user 'ivanka (ivanovna ivan)' from drop down.
       this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
       this.driver.findElement(By.xpath("//option[text()='ivanka (ivanovna ivan)']/parent::select/preceding-sibling::div/a/span[@class='select2-arrow']")).click();
       this.driver.findElement(By.xpath("//option[text()='ivanka (ivanovna ivan)']")).click();

       //Select read/write permissions from Permissions drop down.
       this.driver.findElement(By.xpath("//option[text()='ivanka (ivanovna ivan)']/parent::select/parent::div" +
               "/following-sibling::div[1]/div[@class='select2-container']/a")).click();
       this.driver.findElement(By.xpath("//div[text()='Read/Write']")).click();

       //Select Valid till option.
       this.driver.findElement(By.xpath("//option[text()='ivanka (ivanovna ivan)']/parent::select/parent::div/following-sibling::div[2]/input")).click();
       WebElement selectedDateEl = this.driver.findElement(By.xpath("//div[@class='datepicker-days']/table/tbody/tr[last()]/td[last()]"));
       selectedDateEl.click();
       String selectedDate = this.driver.findElement(By.xpath("//option[text()='ivanka (ivanovna ivan)']" +
               "/parent::select/parent::div/following-sibling::div[2]/input")).getAttribute("value");

       //Save external user clicking on apply permissions button.
       this.driver.findElement(By.xpath("//section[@id='folder-external-users-tab'][@class='content-current']/div[@class='dialog-footer']" +
               "/button[text()='Apply permissions']")).click();

       //Finish editing process.
       this.driver.findElement(By.xpath("//div[@class='dialog-footer']/button[text()='Save'][@data-bind='visible: !UserSettingsVisible(), click: save']")).click();

       //Here system automatically opens a PR lets return to root.
       this.driver.findElement(By.xpath("//a[@href='#my-work-space']")).click();

       //View Send link menu and ensure that ivanka is in the list.
       F2GTestsLib.findElementAndSelectAction(this.driver, "TestPR1", "Send a link");
       this.driver.findElement(By.xpath("//div[@id='s2id_autogen23']/a/span[@class='select2-arrow']")).click();
       List<WebElement> dropDownOptions = this.driver.findElements(By.xpath("//div[@id='select2-drop']/ul/li/div"));
       Boolean isIvankaInList = false;
       for(WebElement el:dropDownOptions){
           String option = el.getText();
           if (option.contains("ivanka")){
               el.click();
               isIvankaInList = true;
               break;
           }
       }
       Assert.assertTrue(isIvankaInList);

       //Initiate link Send.
       this.driver.findElement(By.xpath("//button[text()='Send']")).click();

       //Close link send pop-up.
       try {
           Runtime.getRuntime().exec("C:\\AutoitScript\\ieSendLinkClose.exe");
           Thread.sleep(2500);
       } catch (Exception ex){
           ex.printStackTrace();
       }
   }

    @Test(priority=4)
    private void editSettingsTest() {

        //Create new blank TestPR1.
        if(!F2GTestsLib.checkElementPresence(this.driver,"TestPR1")){
            F2GTestsLib.createTestPR1(this.driver);
        } else{
            F2GTestsLib.deleteProjectRoom(this.driver, "TestPR1");
            F2GTestsLib.createTestPR1(this.driver);
        }

        //Open PR settings.
        if(!F2GTestsLib.checkElementPresence(this.driver,"TestPR1")){
            F2GTestsLib.createTestPR1(this.driver);
        }
        F2GTestsLib.findElementAndSelectAction(this.driver, "TestPR1", "Settings");

        //Switch to External users tab and initiate Add External User form.
        this.driver.findElement(By.xpath("//div[@class='overlay overlay-contentscale open']/div[@class='dialog share-dialog']" +
                "/div[@class='tabs tabs-style-bar']/nav[@class='dialog-head']/ul/li[2]/a")).click();
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//section[@id='folder-external-users-tab'][@class='content-current']/div[@class='dialog-footer']" +
                "/button[text()='Add external user']")).click();

        //Select user 'ivanka (ivanovna ivan)' from drop down.
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//option[text()='ivanka (ivanovna ivan)']/parent::select/preceding-sibling::div/a/span[@class='select2-arrow']")).click();
        this.driver.findElement(By.xpath("//option[text()='ivanka (ivanovna ivan)']")).click();

        //Select read/write permissions from Permissions drop down.
        this.driver.findElement(By.xpath("//option[text()='ivanka (ivanovna ivan)']/parent::select/parent::div" +
                "/following-sibling::div[1]/div[@class='select2-container']/a")).click();
        this.driver.findElement(By.xpath("//div[text()='Read/Write']")).click();

        //Select Valid till option.
        this.driver.findElement(By.xpath("//option[text()='ivanka (ivanovna ivan)']/parent::select/parent::div/following-sibling::div[2]/input")).click();
        WebElement selectedDateEl = this.driver.findElement(By.xpath("//div[@class='datepicker-days']/table/tbody/tr[last()]/td[last()]"));
        selectedDateEl.click();
        String selectedDate = this.driver.findElement(By.xpath("//option[text()='ivanka (ivanovna ivan)']" +
                "/parent::select/parent::div/following-sibling::div[2]/input")).getAttribute("value");

        //Save external user clicking on apply permissions button.
        this.driver.findElement(By.xpath("//section[@id='folder-external-users-tab'][@class='content-current']/div[@class='dialog-footer']" +
                "/button[text()='Apply permissions']")).click();

        //Check access permissions.
        String permissionsInGrid = this.driver.findElement(By.xpath("//div[@class=' kgCell col6']/div/span")).getText();
        Assert.assertTrue(permissionsInGrid.equals("Read/Write"));

        //Update permissions.
        this.driver.findElement(By.xpath("//div[@class='kgCellText colt7']/a[contains(@data-bind, 'changeExternalAccess')]")).click();
        permissionsInGrid = this.driver.findElement(By.xpath("//div[@class=' kgCell col6']/div/span")).getText();
        Assert.assertTrue(permissionsInGrid.equals("Denied"));

        //Initiate edit permissions form.
        this.driver.findElement(By.xpath("//div[@class='kgCellText colt7']/a[contains(@data-bind, 'editExternalItem')]")).click();

        //Select read permissions from Permissions drop down.
        this.driver.findElement(By.xpath("//option[text()='ivanka (ivanovna ivan)']/parent::select/parent::div" +
                "/following-sibling::div[1]/div[@class='select2-container']/a")).click();
        this.driver.findElement(By.xpath("//div[text()='Read']")).click();

        //Save external user clicking on apply permissions button.
        this.driver.findElement(By.xpath("//section[@id='folder-external-users-tab'][@class='content-current']/div[@class='dialog-footer']" +
                "/button[text()='Apply permissions']")).click();

        //Check access permissions.
        permissionsInGrid = this.driver.findElement(By.xpath("//div[@class=' kgCell col6']/div/span")).getText();
        Assert.assertTrue(permissionsInGrid.equals("Read"));

        //Delete external user.
        this.driver.findElement(By.xpath("//div[@class='kgCellText colt7']/a[contains(@data-bind, 'deleteExternalItem')]")).click();

        //Check that there is not users in a list.
        Boolean isExternalUserPresent = this.driver.findElements(By.xpath("//div[@class='kgCellText colt0']")).size()>0;
        Assert.assertFalse(isExternalUserPresent);

        //Finish editing process.
        this.driver.findElement(By.xpath("//div[@class='dialog-footer']/button[text()='Save'][@data-bind='visible: !UserSettingsVisible(), click: save']")).click();
    }

    @Test(priority=4)
    private void deleteProjectRoomTest() {
        if(!F2GTestsLib.checkElementPresence(this.driver,"TestPR1")){
            F2GTestsLib.createTestPR1(this.driver);
        }
        F2GTestsLib.deleteProjectRoom(this.driver, "TestPR1");
        F2GTestsLib.checkThatElementDoesNotExist(this.driver, "TestPR1");
    }

    @AfterClass
    public void AfterClass(){
        driver.quit();
    }

}
