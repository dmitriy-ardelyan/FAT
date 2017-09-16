package f2g.autotests;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class F2GFileForwardsTest {
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
        this.driver.findElement(By.xpath("//a[@href='#my-forwards']")).click();
    }

    @Test(priority=1)
    private void createFileForwardTest() {

        //Delete all file forwards which contain slytherin file.
        F2GTestsLib.deleteAllElementsWithTitle(this.driver,"slytherin");

        //Create file froward.
        F2GTestsLib.createFileForward(this.driver);

        //Check that slytherin.jpeg file uploaded.
        F2GTestsLib.checkIfElementExists(this.driver,"slytherin.jpeg");
    }

    @Test(priority=2)
    private void deleteFileForwardTest() {

        if (!F2GTestsLib.checkElementPresence(this.driver, "slytherin.jpeg")){
            //Create file froward.
            F2GTestsLib.createFileForward(this.driver);
        }

        //Delete all file forwards which contain slytherin file.
        F2GTestsLib.deleteAllElementsWithTitle(this.driver,"slytherin");

        //Check that slytherin.jpeg forward does not ecxist.
        F2GTestsLib.checkThatElementDoesNotExist(this.driver, "slytherin.jpeg");
    }

    @Test(priority=3)
    private void downloadFileForwardTest() {

        //Create ff slytherin.jpeg if it does not exist.
        if (!F2GTestsLib.checkElementPresence(this.driver, "slytherin.jpeg")){
            //Create file froward.
            F2GTestsLib.createFileForward(this.driver);
        }

        //Get ff slytherin.jpeg.
        WebElement ff2Download = F2GTestsLib.getElementByName(this.driver, "slytherin.jpeg");

        //Initiate download dialog.
        Actions action= new Actions(this.driver);
        action.contextClick(ff2Download).build().perform();
        String fileName = ff2Download.findElement(By.tagName("h2")).getText();
        String fileDate = ff2Download.findElement(By.xpath(".//div[2]/span[@class='item-date']")).getText();
        String fileSize = ff2Download.findElement(By.xpath(".//div[2]/span[@class='item-size']")).getText();
        List<WebElement> actionsList = driver.findElements(By.xpath("//div[@class='context-menu']/ul/li"));
        for (WebElement actionEl:actionsList){
            //Here we delete file.
            this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
            String listElName = actionEl.getText();
            if (listElName.contains("Download")) {
                actionEl.click();
                break;
            }
        }

        //Check that  proper file name, date and size displayed on download pop-up.
        WebElement fileNamePopUpHeader = this.driver.findElement(By.xpath("//div[@class='dialog download']/div[@class='dialog-body form']/h2"));
        Assert.assertEquals(fileName, fileNamePopUpHeader.getText());
        WebElement fileDateAndSizeHeader = this.driver.findElement(By.xpath("//div[@class='dialog download']/div[@class='dialog-body form']/h3"));
        Assert.assertEquals(fileDate.split(": ")[1] + " - " + fileSize,fileDateAndSizeHeader.getText());

        //Click on a download button.
        driver.manage().timeouts().pageLoadTimeout(3,TimeUnit.SECONDS);
        try {
            this.driver.findElement(By.xpath("//div[@class='dialog download']/div[@class='dialog-footer']/input[@value='Download']")).click();
        } catch(Exception e) {
            System.out.println("Timeout exception always happens here due to some IE pop-up mechanism handling!");
        }
        try {
            Thread.sleep(2000);
            Runtime.getRuntime().exec("C:\\AutoitScript\\ieFileDownload.exe");
            Thread.sleep(5000);
            Runtime.getRuntime().exec("C:\\AutoitScript\\ieCloseDownloadDialog.exe");
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.driver.findElement(By.xpath("//div[@class='dialog download']/div[@class='dialog-footer']/input[@value='Close']")).click();
        } catch(Exception e) {
            System.out.println("Timeout exception always happens here due to some IE pop-up mechanism handling!");
        }

        //Manipulations with this download window somehow corrupt driver so we have to refresh it.
        driver.manage().timeouts().pageLoadTimeout(15,TimeUnit.SECONDS);
        this.driver.navigate().refresh();
    }

    @Test(priority=4/*,dependsOnMethods={"downloadFileForwardTest"}*/)
    private void fileForwardStatisticsTest() {

        //Open statistics.
        F2GTestsLib.findElementAndSelectAction(this.driver, "slytherin.jpeg","Statistics");

        //Check that at least 1 download displayed in statistics.
        List<WebElement> listOfDownloads = this.driver.findElements(By.xpath("//div[@class='kgCellText colt1']"));
        Assert.assertTrue(listOfDownloads.size()>0);
        this.driver.findElement(By.xpath("//div[@class='dialog share-dialog']/div[@class='dialog-footer']/button[text()='Close']")).click();
    }

    @Test(priority=5)
    private void previewFileForwardTest() {

        //Add slytherin.jpeg if it does not exist.
        if (!F2GTestsLib.checkElementPresence(this.driver, "slytherin.jpeg")){
            //Create file froward.
            F2GTestsLib.createFileForward(this.driver);
        }

        //Open preview.
        String parentHandle = driver.getWindowHandle();
        F2GTestsLib.findElementAndSelectAction(this.driver, "slytherin.jpeg","Preview");

        //Switch to preview window.
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }

        //Proceed with unsafe content.
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.findElement(By.partialLinkText("Continue to this")).click();

        //Check that image displayed on a page.
        WebElement el = this.driver.findElement(By.xpath("//img[contains(@src,'localhost/api/download-file-forward')]"));
        Object result = ((JavascriptExecutor) driver).executeScript("return arguments[0].complete && "+
                        "typeof arguments[0].naturalWidth != \"undefined\" && "+
                        "arguments[0].naturalWidth > 0", el);
        Boolean loaded = false;
        if (result instanceof Boolean) loaded = (Boolean) result;
        Assert.assertTrue(loaded);

        //Close preview and return back.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.close();
        driver.switchTo().window(parentHandle);
    }

    @Test(priority=6)
    private void editFileForwardSettingsTest() {

        //Delete slytherin.jpeg if it exists.
        if (F2GTestsLib.checkElementPresence(this.driver, "slytherin.jpeg")){
            F2GTestsLib.findElementAndSelectAction(this.driver,"slytherin.jpeg", "Delete");
        }

        //Create slytherin.jpeg.
        F2GTestsLib.createFileForward(this.driver);

        //View settings.
        F2GTestsLib.findElementAndSelectAction(this.driver, "slytherin.jpeg","Settings");

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
        this.driver.findElement(By.xpath("//option[text()='ivanka (ivanovna ivan)']/parent::select/parent::div/following-sibling::div[3]/input")).click();
        WebElement selectedDateEl = this.driver.findElement(By.xpath("//div[@class='datepicker-days']/table/tbody/tr[last()]/td[last()]"));
        selectedDateEl.click();
        String selectedDate = this.driver.findElement(By.xpath("//option[text()='ivanka (ivanovna ivan)']" +
                "/parent::select/parent::div/following-sibling::div[3]/input")).getAttribute("value");

        //Save external user clicking on apply permissions button.
        this.driver.findElement(By.xpath("//section[@id='folder-external-users-tab'][@class='content-current']/div[@class='dialog-footer']" +
                "/button[text()='Apply permissions']")).click();

        //Check selected data.
        String firstNameInGrid = this.driver.findElement(By.xpath("//div[@class='kgCellText colt0']")).getText();
        Assert.assertTrue(firstNameInGrid.equals("ivanka"));
        String passwordInGrid = this.driver.findElement(By.xpath("//div[@class='kgCellText colt1']")).getText();
        Assert.assertTrue(passwordInGrid.equals("Iva43"));
        String validTillInGrid = this.driver.findElement(By.xpath("//div[@class='kgCellText colt5']")).getText();
        Assert.assertTrue(validTillInGrid.equals(selectedDate));
        String permissionsInGrid = this.driver.findElement(By.xpath("//div[@class=' kgCell col6']/div/span")).getText();
        Assert.assertTrue(permissionsInGrid.equals("Read/Write"));

        //Finish editing process.
        this.driver.findElement(By.xpath("//div[@class='dialog-footer']/button[text()='Save'][@data-bind='visible: !UserSettingsVisible(), click: save']")).click();
        driver.findElement(By.xpath("//div[@class='overlay overlay-contentscale open']/div[@class='dialog']" +
                "/div[@class='dialog-body']/div[@class='middle-form']/div[3]/button")).click();
    }

    @Test(priority=7)
    private void renameFileForwardTest() {

        //Check if slytherin.jpeg fileforward exists.
        if (!F2GTestsLib.checkElementPresence(this.driver, "slytherin.jpeg")){
            F2GTestsLib.createFileForward(this.driver);
        }

        //And now rename time!
        F2GTestsLib.findElementAndSelectAction(this.driver, "slytherin.jpeg", "Rename");
        String tmpxpath = "//div[@class='overlay overlay-contentscale open']/div[@class='dialog']/div[@class='dialog-body']" +
                "/div[@class='form middle-form']/div[@class='inline']/input";
        WebElement inputField = this.driver.findElement(By.xpath(tmpxpath));
        inputField.sendKeys(inputField.getText()+"-Renamed");
        driver.findElement(By.xpath("//div[@class='overlay overlay-contentscale open']/div[@class='dialog']/div[@class='dialog-footer']/button[@data-bind='click: save']")).click();

        //Ensure that renamed folder exists.
        this.driver.findElement(By.xpath("//a[@title='Refresh']")).click();
        F2GTestsLib.checkIfElementExists(this.driver,"slytherin-Renamed.jpeg");

    }

    @AfterClass
    public void AfterClass(){
        driver.quit();
    }

}
