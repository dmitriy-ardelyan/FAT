package f2g.autotests;

import org.apache.xpath.operations.Bool;
import org.junit.Assert;
import org.openqa.selenium.*;


import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.awt.SystemColor.window;

public class F2GServerTest {
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
    }

    @Test(priority=1)
    private void uploadFileTest() {

        //Delete slytherin.jpeg if it exists.
        this.deleteExistingElement("slytherin.jpeg");

        //Upload slytherin.jpeg.
        try {
            this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
            this.driver.findElement(By.xpath("//a[@title='Upload']")).click();
            Runtime.getRuntime().exec("C:\\AutoitScript\\fileUpload.exe");
            Thread.sleep(7000);
        } catch (Exception ex){
            ex.printStackTrace();
        }

        //Check that slytherin.jpeg file uploaded.
        this.checkIfElementExists("slytherin.jpeg");
    }

    @Test(priority=2)
    private void createFolderTest() {

        //Delete TestFolder if it exists.
        this.deleteExistingElement("TestFolder");

        //Create folder.
        this.createFolderWithName("TestFolder");

        //Refresh the tab to view just created folder.
        this.driver.findElement(By.xpath("//a[@title='Refresh']")).click();

        //Check that folder created and exists withina list.
        this.checkIfElementExists("TestFolder");
    }

    @Test(priority=3)
    private void serverFilesSortTest() {

        //Open Sort menu.
        this.driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//div[@class='sort dropdown']")).click();
        this.driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        List<WebElement> sortList = driver.findElements(By.xpath("//div[@class='sort dropdown']/ul/li"));

        //Perform Sort by Last changed Date and Descending.
        for (WebElement el: sortList){
            this.driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            String listItemName = el.getText();
            if (listItemName.equals("Last changed date")||listItemName.equals("Descending")){
                this.driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                el.findElement(By.xpath(".//input")).click();
            }
        }

        //Click anywhere else to close sort menu.
        this.driver.findElement(By.xpath("//div[@class='top']")).click();

        //Check Sort results.
        Boolean sortResults = true;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='list-item']"));
        List<WebElement> foldersList = new ArrayList<>();
        List<WebElement> filesList= new ArrayList<>();

        //Distinguish folders and files.
        for (WebElement el:list){
            WebElement internalEl = el.findElement(By.xpath(".//div[@class='right-small-cell']/img"));
            String srcValue = internalEl.getAttribute("src");
            if(srcValue.equals("https://localhost/webclient/Home/GetIcon")){
                foldersList.add(el);
            } else{
                filesList.add(el);
            }
        }

        //Check that both lists of files and folders sorted by Date in descending order.
        Assert.assertTrue(this.checkDateDescendingSort(foldersList));
        Assert.assertTrue(this.checkDateDescendingSort(filesList));
    }

    @Test(priority=4)
    private void fileDownloadFromContextMenuTest() {

        //Check if there is at least 1 file within a server folder and upload if not
        WebElement el = this.getFirstFile();

        //When we sure that at least 1 file is in the list we download it.
        Actions action= new Actions(this.driver);
        action.contextClick(el).build().perform();
        String fileName = el.findElement(By.tagName("h2")).getText();
        String fileDate = el.findElement(By.xpath(".//div[2]/span[@class='item-date']")).getText();
        String fileSize = el.findElement(By.xpath(".//div[2]/span[@class='item-size']")).getText();
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
        Assert.assertEquals(fileDate + " - " + fileSize,fileDateAndSizeHeader.getText());

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
        //driver.manage().timeouts().pageLoadTimeout(30,TimeUnit.SECONDS);
        try {
            this.driver.findElement(By.xpath("//div[@class='dialog download']/div[@class='dialog-footer']/input[@value='Close']")).click();
        } catch(Exception e) {
            System.out.println("Timeout exception always happens here due to some IE pop-up mechanism handling!");
        }

        //Manipulations with this download window somehow corrupt driver so we have to re-init it.
        driver.quit();
        System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"\\BrowserDrivers\\IEDriverServer32.exe");
        DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
        capabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
        WebDriver wd = new InternetExplorerDriver(capabilities);
        this.driver = wd;
        F2GIEAuth.authenticate(this.driver);
    }

    @Test(priority=5)
    private void folderRenameTest() {

        //Deleting any folder which can exist due to previous tests.
        this.deleteExistingElement("Folder2Rename");
        this.deleteExistingElement("Folder2Rename-Renamed");

        //Create new Folder2Rename.
        this.createFolderWithName("Folder2Rename");

        //Refresh the tab to view just created folder.
        this.driver.findElement(By.xpath("//a[@title='Refresh']")).click();

        //Rename folder.
        this.findElementAndSelectAction("Folder2Rename","Rename");
        String tmpxpath = "//div[@class='overlay overlay-contentscale open']/div[@class='dialog']/div[@class='dialog-body']" +
                "/div[@class='form middle-form']/div[@class='inline']/input";
        WebElement inputField = this.driver.findElement(By.xpath(tmpxpath));
        inputField.sendKeys(inputField.getText()+"-Renamed");
        driver.findElement(By.xpath("//div[@class='overlay overlay-contentscale open']/div[@class='dialog']/div[@class='dialog-footer']/button[@data-bind='click: save']")).click();

        //Ensure that renamed folder exists.
        this.driver.findElement(By.xpath("//a[@title='Refresh']")).click();
        this.checkIfElementExists("Folder2Rename-Renamed");
    }

    @Test(priority=6)
    private void fileRenameTest() {

        //Deleting results of previous tests if they exist..
        this.deleteExistingElement("slytherin-Renamed.jpeg");

        //Checking that file exists and uploading it if not.
        if (!this.checkElementPresence("slytherin.jpeg")){
            //Upload slytherin.jpeg.
            try {
                this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
                this.driver.findElement(By.xpath("//a[@title='Upload']")).click();
                Runtime.getRuntime().exec("C:\\AutoitScript\\fileUpload.exe");
                Thread.sleep(7000);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }

        //Perform rename.
        this.findElementAndSelectAction("slytherin.jpeg","Rename");
        String tmpxpath = "//div[@class='overlay overlay-contentscale open']/div[@class='dialog']/div[@class='dialog-body']" +
                "/div[@class='form middle-form']/div[@class='inline']/input";
        WebElement inputField = this.driver.findElement(By.xpath(tmpxpath));
        inputField.sendKeys(inputField.getText()+"-Renamed");
        driver.findElement(By.xpath("//div[@class='overlay overlay-contentscale open']/div[@class='dialog']/div[@class='dialog-footer']/button[@data-bind='click: save']")).click();

        //Ensure that renamed folder exists.
        this.driver.findElement(By.xpath("//a[@title='Refresh']")).click();
        this.checkIfElementExists("slytherin-Renamed.jpeg");
    }

    @Test(priority=7)
    private void createSubFolderTest() {

        //Deleting results of previous tests and prepare a root folder for a test.
        this.deleteExistingElement("FolderInRoot");
        this.createFolderWithName("FolderInRoot");

        //Create sub folder.
        this.findElementAndSelectAction("FolderInRoot","Create subfolder");
        this.driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("(//input[@data-bind='value: Name'])[position()=2]")).sendKeys("SubFolder");
        this.driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("(//div[@class='dialog-footer'])[position()=13]/button[text()='Save']")).click();

        //Check that sub folder exists.
        this.openFolder("FolderInRoot");
        this.checkIfElementExists("SubFolder");

        //Return to root folder.
        this.driver.findElement(By.xpath("//a[@href='#server']")).click();
    }

    @Test(priority=8)
    private void deleteFolderTest() {

        //Prepare a folder for a test.
        if(!this.checkElementPresence("Folder2Delete")) this.createFolderWithName("Folder2Delete");

        //Delete  folder.
        this.deleteExistingElement("Folder2Delete");

        //Check that folder does not exist any more.
        Boolean folderExists = false;
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='list-item']"));
        for (WebElement el:list) {
            String name = el.findElement(By.tagName("h2")).getText();
            if (name.equals("Folder2Delete")) {
                folderExists = true;
            }
        }
        Assert.assertFalse(folderExists);
    }

    @Test(priority=9)
    private void deleteFileTest() {

        //Check if file exists and upload it if not.
        if(!this.checkElementPresence("slytherin.jpeg")){
            //Upload slytherin.jpeg.
            try {
                this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
                this.driver.findElement(By.xpath("//a[@title='Upload']")).click();
                Runtime.getRuntime().exec("C:\\AutoitScript\\fileUpload.exe");
                Thread.sleep(7000);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }

        //Delete  file.
        this.deleteExistingElement("slytherin.jpeg");

        //Check that file does not exist any more.
        Boolean fileExists = false;
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='list-item']"));
        for (WebElement el:list) {
            String name = el.findElement(By.tagName("h2")).getText();
            if (name.equals("slytherin.jpeg")) {
                fileExists = true;
            }
        }
        Assert.assertFalse(fileExists);
    }

    @Test(priority=10)
    private void fileDownloadButtonTest() {

        //Check if file exists and upload it if not.
        if(!this.checkElementPresence("slytherin.jpeg")){
            //Upload slytherin.jpeg.
            try {
                this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
                this.driver.findElement(By.xpath("//a[@title='Upload']")).click();
                Runtime.getRuntime().exec("C:\\AutoitScript\\fileUpload.exe");
                Thread.sleep(7000);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }

        //Get WebElement created for just uploaded file.
        WebElement targetEl = driver.findElement(By.xpath("//div[1]"));
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='list-item']"));
        for (WebElement el:list) {
            String name = el.findElement(By.tagName("h2")).getText();
            if (name.equals("slytherin.jpeg")) {
                targetEl = el;
            }
        }

        //Get file data
        String fileName = targetEl.findElement(By.tagName("h2")).getText();
        String fileDate = targetEl.findElement(By.xpath(".//div[2]/span[@class='item-date']")).getText();
        String fileSize = targetEl.findElement(By.xpath(".//div[2]/span[@class='item-size']")).getText();

        //Click on a download button.
        targetEl.findElement(By.xpath(".//div[@class='short-buttons']/a")).click();

        //Check that  proper file name, date and size displayed on download pop-up.
        WebElement fileNamePopUpHeader = this.driver.findElement(By.xpath("//div[@class='dialog download']/div[@class='dialog-body form']/h2"));
        Assert.assertEquals(fileName, fileNamePopUpHeader.getText());
        WebElement fileDateAndSizeHeader = this.driver.findElement(By.xpath("//div[@class='dialog download']/div[@class='dialog-body form']/h3"));
        Assert.assertEquals(fileDate + " - " + fileSize,fileDateAndSizeHeader.getText());

        //Close download pop-up.
        this.driver.findElement(By.xpath("//div[@class='dialog download']/div[@class='dialog-footer']/input[@value='Close']")).click();
    }

    @Test(priority=11)
    private void createProjectRoomTest() {

        //Delete PR if it exists due to previous tests.
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//a[@href='#my-work-space']")).click();
        if (this.checkElementPresence("TestPR1")){
            this.findElementAndSelectAction("TestPR1", "Delete");
        }
        this.driver.findElement(By.xpath("//a[@href='#server']")).click();

        //Initiate PR creation form.
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//a[contains(@title,'exist project room')]")).click();

        //Fill PR name field. Somehow here sendKeys function fails from time to time. So i had to create special STAB. See SendKeysCondition class.
        String prNameInputLocator = "//label[@for='AddShare_IsExternalAccessAllowed']/parent::div/preceding-sibling::div" +
                "/span[@class='twitter-typeahead']/input[@class='tt-input']";
        this.driver.findElement(By.xpath(prNameInputLocator)).sendKeys("TestPR1");
        SendKeysCondition condition = new SendKeysCondition(prNameInputLocator);
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(condition);

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

        //Check that external user data saved correctly.
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        String firstNameInGrid = this.driver.findElement(By.xpath("//div[@class='kgCellText colt0']")).getText();
        Assert.assertTrue(firstNameInGrid.equals("ivanka"));
        String passwordInGrid = this.driver.findElement(By.xpath("//div[@class='kgCellText colt1']")).getText();
        Assert.assertTrue(passwordInGrid.equals("Iva43"));
        String validTillInGrid = this.driver.findElement(By.xpath("//div[@class='kgCellText colt5']")).getText();
        Assert.assertTrue(validTillInGrid.equals(selectedDate));
        String permissionsInGrid = this.driver.findElement(By.xpath("//div[@class=' kgCell col6']/div/span")).getText();
        Assert.assertTrue(permissionsInGrid.equals("Read/Write"));

        //Finish creation process.
        this.driver.findElement(By.xpath("//div[@class='dialog-footer']/button[text()='Create'][@data-bind='visible: !UserSettingsVisible(),click: save']")).click();
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//div[@class='overlay overlay-contentscale open']/div[@class='dialog']" +
                "/div[@class='dialog-body']/div[@class='middle-form']/div[4]/button")).click();

        //Open PR tab and ensure that PR exists.
        this.driver.findElement(By.xpath("//a[@href='#my-work-space']")).click();
        this.checkIfElementExists("TestPR1");
        this.driver.findElement(By.xpath("//a[@href='#server']")).click();
    }

    @Test(priority=12)
    private void createProjectRoomFromFileContextMenuTest(){

        //Delete PR if it exists due to previous tests.
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//a[@href='#my-work-space']")).click();
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        if (this.checkElementPresence("TestPR1")){
            this.findElementAndSelectAction("TestPR1", "Delete");
            this.driver.navigate().refresh();
        }
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//a[@href='#server']")).click();

        //Get first file in a folder.
        WebElement el = this.getFirstFile();

        //Initiate PR creation for a first file in a folder.
        String fileName = el.findElement(By.xpath(".//div[@class='item-body']/div/h2")).getText();
        this.findElementAndSelectAction(fileName,"Add to project room");

        //Fill PR name field. Somehow here sendKeys function fails from time to time. So i had to create special STAB. See SendKeysCondition class.
        String prNameInputLocator = "//label[@for='AddShare_IsExternalAccessAllowed']/parent::div/preceding-sibling::div" +
                "/span[@class='twitter-typeahead']/input[@class='tt-input']";
        this.driver.findElement(By.xpath(prNameInputLocator)).sendKeys("TestPR1");
        SendKeysCondition condition = new SendKeysCondition(prNameInputLocator);
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(condition);

        //Finish creation process and Open PR.
        this.driver.findElement(By.xpath("//div[@class='overlay overlay-contentscale open']/div[@class='dialog share-dialog']" +
                "/div[@class='tabs tabs-style-bar']/div[@class='content-wrap']/section[@id='folder-settings-tab']" +
                "/div[@class='dialog-footer']/button[text()='Create'][@data-bind='click: save']")).click();
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//div[@class='overlay overlay-contentscale open']/div[@class='dialog']" +
                "/div[@class='dialog-body']/div[@class='middle-form']/div[3]/button")).click();

        //Ensure that selected file exists within a PR.
        this.checkIfElementExists(fileName);
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//a[@href='#server']")).click();
    }

    @Test(priority=13)
    private void createFileForwardTest(){

        //Check if at least 1 file exists and upload it if not.
        WebElement el = this.getFirstFile();
        String fileName = el.findElement(By.xpath(".//div[@class='item-body']/div/h2")).getText();

        //Switch to files forwards tab.
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//a[@href='#my-forwards']")).click();

        //Delete all forwards with name of a file which we gonna use in test.
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='list-item']"));
        Boolean isRefreshRequired = false;
        String name2Check = fileName.split("\\.")[0];
        for (WebElement element:list){
            String tmpName = element.findElement(By.xpath(".//div[@class='item-body']/div/h2")).getText();
            if (tmpName.contains(name2Check)){
                this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
                this.findElementAndSelectAction(tmpName,"Delete");
                isRefreshRequired = true;
            }
        }
        if (isRefreshRequired) this.driver.navigate().refresh();
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//a[@href='#server']")).click();

        //Initiate file forward creation form.
        this.findElementAndSelectAction(fileName, "Create file forward");

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

        //Check that external user data saved correctly.
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        String firstNameInGrid = this.driver.findElement(By.xpath("//div[@class='kgCellText colt0']")).getText();
        Assert.assertTrue(firstNameInGrid.equals("ivanka"));
        String passwordInGrid = this.driver.findElement(By.xpath("//div[@class='kgCellText colt1']")).getText();
        Assert.assertTrue(passwordInGrid.equals("Iva43"));
        String validTillInGrid = this.driver.findElement(By.xpath("//div[@class='kgCellText colt5']")).getText();
        Assert.assertTrue(validTillInGrid.equals(selectedDate));
        String permissionsInGrid = this.driver.findElement(By.xpath("//div[@class=' kgCell col6']/div/span")).getText();
        Assert.assertTrue(permissionsInGrid.equals("Read/Write"));

        //Finish creation process.
        this.driver.findElement(By.xpath("//div[@class='dialog-footer']/button[text()='Create'][@data-bind='visible: !UserSettingsVisible(),click: save']")).click();
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//div[@class='overlay overlay-contentscale open']/div[@class='dialog']" +
                "/div[@class='dialog-body']/div[@class='middle-form']/div[3]/button")).click();

        //Check if FileForward exists.
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//a[@href='#my-forwards']")).click();
        List<WebElement> ffList = driver.findElements(By.xpath("//div[@class='list-item']"));
        Boolean isFFExist = false;
        for (WebElement element:ffList){
            String tmpName = element.findElement(By.xpath(".//div[@class='item-body']/div/h2")).getText();
            if (tmpName.contains(name2Check)){
                isFFExist = true;
                break;
            }
        }
        Assert.assertTrue(isFFExist);
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//a[@href='#server']")).click();
    }

    @Test(priority=14)
    private void folderNavigationTest(){

        //Get folder for a test.
        WebElement folder2Test = this.getFirstFolder();

        //Get folder name and compare it with a name from breadcrumbs.
        String elementName = folder2Test.findElement(By.xpath(".//div[@class='item-body']/div/h2")).getText();
        folder2Test.click();
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        String folderNameInBreadcrumbs = this.driver.findElement(By.xpath("//div[@class='nav-bar']/ul/li[2]")).getText();
        Assert.assertTrue(elementName.equals(folderNameInBreadcrumbs));

        //Navigate back using breadcrumbs.
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        this.driver.findElement(By.xpath("//a[@href='#'][contains(@data-bind,'root.SelectedContent(null)')]")).click();

        //Ensure that we are within localhost root folder.
        String breadcrumbsParam = this.driver.findElement(By.xpath("//div[@class='nav-bar']/ul/li[1]/a")).getAttribute("data-bind");
        Assert.assertTrue(breadcrumbsParam.contains("root.SelectedContent(null)"));

        //Go back to the same folder.
        folder2Test = this.getFirstFolder();
        folder2Test.click();
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

        //Navigate back using back button.
        this.driver.findElement(By.xpath("//a[contains(@data-bind,'click: goBack')]")).click();

        //Ensure that we are within localhost root folder.
        breadcrumbsParam = this.driver.findElement(By.xpath("//div[@class='nav-bar']/ul/li[1]/a")).getAttribute("data-bind");
        Assert.assertTrue(breadcrumbsParam.contains("root.SelectedContent(null)"));
    }

    @Test(priority=15)
    private void serverSearchTest(){

        //Select some element which we gonna search.
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='list-item']"));
        if (list.size()<2){

            //Upload slytherin.jpeg.
            try {
                this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
                this.driver.findElement(By.xpath("//a[@title='Upload']")).click();
                Runtime.getRuntime().exec("C:\\AutoitScript\\fileUpload.exe");
                Thread.sleep(7000);
            } catch (Exception ex){
                ex.printStackTrace();
            }

            //Create folder and refresh tab.
            this.createFolderWithName("TestFolder");
            this.driver.findElement(By.xpath("//a[@title='Refresh']")).click();

            //Update list of elements.
            list = driver.findElements(By.xpath("//div[@class='list-item']"));
        }
        WebElement el2Search = list.get(0);
        String nameOfEl2Search = el2Search.findElement(By.xpath(".//div[@class='item-body']/div/h2")).getText();

        //Perform search.
        this.driver.findElement(By.xpath("//input[@placeholder='Search']")).sendKeys(nameOfEl2Search);
        this.driver.findElement(By.xpath("//input[@placeholder='Search']/preceding-sibling::span[1]")).click();

        //Ensure that search results are correct.
        list = driver.findElements(By.xpath("//div[@class='list-item']"));
        for (WebElement el:list){
            String tmpElName = el.findElement(By.xpath(".//div[@class='item-body']/div/h2")).getText();
            Assert.assertTrue(tmpElName.contains(nameOfEl2Search));
        }

        //Discard search.
        this.driver.findElement(By.xpath("//input[@placeholder='Search']")).clear();
        this.driver.findElement(By.xpath("//input[@placeholder='Search']/preceding-sibling::span[1]")).click();
    }

    @AfterClass
    public void AfterClass(){
       driver.quit();
    }

    //Additional methods.
    private WebElement getFirstFolder(){

        //Get list of all folders.
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='list-item']"));
        List<WebElement> foldersList = new ArrayList<>();
        for (WebElement el:list){
            WebElement internalEl = el.findElement(By.xpath(".//div[@class='right-small-cell']/img"));
            String srcValue = internalEl.getAttribute("src");
            if(srcValue.equals("https://localhost/webclient/Home/GetIcon")){
                foldersList.add(el);
            }
        }

        //Check if we have at least 1 folder and create 1 if not.
        if (foldersList.size()==0) {
            this.createFolderWithName("TestFolder");
            this.driver.findElement(By.xpath("//a[@title='Refresh']")).click();
            list = driver.findElements(By.xpath("//div[@class='list-item']"));
            for (WebElement el : list) {
                WebElement internalEl = el.findElement(By.xpath(".//div[@class='right-small-cell']/img"));
                String srcValue = internalEl.getAttribute("src");
                if (srcValue.equals("https://localhost/webclient/Home/GetIcon")) {
                    foldersList.add(el);
                }
            }
        }

        //Return first folder from a list.
        return foldersList.get(0);
    }

    private WebElement getFirstFile(){

        //Get list of all files in the folder.
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='list-item']"));
        List<WebElement> filesList= new ArrayList<>();
        for (WebElement el:list){
            WebElement internalEl = el.findElement(By.xpath(".//div[@class='right-small-cell']/img"));
            String srcValue = internalEl.getAttribute("src");
            if(!srcValue.equals("https://localhost/webclient/Home/GetIcon")){
                filesList.add(el);
            }
        }

        //If no files exist upload a file.
        if (filesList.size()==0){
            try {
                this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
                this.driver.findElement(By.xpath("//a[@title='Upload']")).click();
                Runtime.getRuntime().exec("C:\\AutoitScript\\fileUpload.exe");
                Thread.sleep(7000);
            } catch (Exception ex){
                ex.printStackTrace();
            }
            //And after upload we add a file into a list.
            list = driver.findElements(By.xpath("//div[@class='list-item']"));
            for (WebElement el:list){
                WebElement internalEl = el.findElement(By.xpath(".//div[@class='right-small-cell']/img"));
                String srcValue = internalEl.getAttribute("src");
                if(!srcValue.equals("https://localhost/webclient/Home/GetIcon")){
                    filesList.add(el);
                }
            }
        }
        //Return first file.
        return filesList.get(0);
    }

    private void openFolder(String folder2Open){
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='list-item']"));
        for (WebElement el:list){
            String name = el.findElement(By.tagName("h2")).getText();
            if (name.equals(folder2Open)){
                el.click();
                break;
            }
        }
    }

    private void createFolderWithName(String name){
        try {
            this.driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            this.driver.findElement(By.xpath("//a[@title='Create folder']")).click();
            Thread.sleep(1000);
            this.driver.findElement(By.xpath("(//input[@data-bind='value: Name'])[position()=2]")).sendKeys(name);
            this.driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            this.driver.findElement(By.xpath("(//div[@class='dialog-footer'])[position()=13]/button[text()='Save']")).click();
            Thread.sleep(1000);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        this.driver.findElement(By.xpath("//a[@title='Refresh']")).click();
    }

    private void findElementAndSelectAction(String el2Find, String action2Perform){
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='list-item']"));
        for (WebElement el:list){
            String name = el.findElement(By.tagName("h2")).getText();
            if (name.equals(el2Find)){
                //Here we found file time to perform action.
                this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
                Actions action= new Actions(this.driver);
                action.contextClick(el).build().perform();
                List<WebElement> actionsList = driver.findElements(By.xpath("//div[@class='context-menu']/ul/li"));
                this.clickActionInList(actionsList, action2Perform);
                break;
            }
        }
    }

    private void deleteExistingElement(String el2DeleteName){

        //We will check if element exists and delete it if YES.
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        this.findElementAndSelectAction(el2DeleteName,"Delete");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkIfElementExists(String el2Check){

        //We will check if slytherin.jpeg file present in server files list.
        Boolean fileExists = false;
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='list-item']"));
        for (WebElement el:list) {
            String name = el.findElement(By.tagName("h2")).getText();
            if (name.equals(el2Check)) {
                fileExists = true;
            }
        }
        Assert.assertTrue(fileExists);
    }

    private Boolean checkElementPresence(String el2Check){
        //We will check if slytherin.jpeg file present in server files list.
        Boolean fileExists = false;
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='list-item']"));
        for (WebElement el:list) {
            String name = el.findElement(By.tagName("h2")).getText();
            if (name.equals(el2Check)) {
                fileExists = true;
            }
        }
        return fileExists;
    }

    private Boolean checkDateDescendingSort(List<WebElement> list){
        Boolean result = true;
        List<Date> datesList = new ArrayList<>();
        //Getting list of dates.
        for (WebElement el:list){
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            String dateStr = el.findElement(By.tagName("span")).getText();
            try {
                Date date = formatter.parse(dateStr);
                datesList.add(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //Comparing dates in list.
        for (int i = 0; i<datesList.size()-1;i++){
            if(!datesList.get(i).after(datesList.get(i+1))){
                result=false;
            }
        }
        return result;
    }

    private void clickActionInList(List<WebElement> list, String action) {
        for (WebElement el : list) {
            this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
            String listElName = el.getText();
            if (listElName.contains(action)) {
                el.click();
                break;
            }
        }
    }

}
