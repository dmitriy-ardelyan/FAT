package f2g.autotests;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class F2GTestsLib {

    //Additional methods.
    public static WebElement getFirstFolder(WebDriver driver){

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
            createFolderWithName(driver,"TestFolder");
            driver.findElement(By.xpath("//a[@title='Refresh']")).click();
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

    public static WebElement getFirstFile(WebDriver driver){

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
                driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
                driver.findElement(By.xpath("//a[@title='Upload']")).click();
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

    public static void openFolder(WebDriver driver, String folder2Open){
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='list-item']"));
        for (WebElement el:list){
            String name = el.findElement(By.tagName("h2")).getText();
            if (name.equals(folder2Open)){
                el.click();
                break;
            }
        }
    }

    public static void openF2GElement(WebDriver driver, String el2Open){
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='list-item']"));
        for (WebElement el:list){
            WebElement el2Click = el.findElement(By.tagName("h2"));
            String name = el2Click.getText();
            if (name.equals(el2Open)){
                el2Click.click();
                break;
            }
        }
    }

    public static void createFolderWithName(WebDriver driver, String name){
        try {
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            driver.findElement(By.xpath("//a[@title='Create folder']")).click();
            Thread.sleep(1000);
            driver.findElement(By.xpath("(//input[@data-bind='value: Name'])[position()=2]")).sendKeys(name);
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            driver.findElement(By.xpath("(//div[@class='dialog-footer'])[position()=13]/button[text()='Save']")).click();
            Thread.sleep(1000);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        driver.findElement(By.xpath("//a[@title='Refresh']")).click();
    }

    public static void findElementAndSelectAction(WebDriver driver, String el2Find, String action2Perform){
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='list-item']"));
        for (WebElement el:list){
            String name = el.findElement(By.tagName("h2")).getText();
            if (name.equals(el2Find)){
                //Here we found file time to perform action.
                driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
                Actions action= new Actions(driver);
                action.contextClick(el).build().perform();
                List<WebElement> actionsList = driver.findElements(By.xpath("//div[@class='context-menu']/ul/li"));
                clickActionInList(driver, actionsList, action2Perform);
                break;
            }
        }
    }

    public static void deleteExistingElement(WebDriver driver, String el2DeleteName){

        //We will check if element exists and delete it if YES.
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        findElementAndSelectAction(driver, el2DeleteName,"Delete");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void checkIfElementExists(WebDriver driver, String el2Check){

        //We will check if element file present in server files list.
        Boolean fileExists = false;
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='list-item']"));
        for (WebElement el:list) {
            String name = el.findElement(By.tagName("h2")).getText();
            if (name.equals(el2Check)) {
                fileExists = true;
            }
        }
        Assert.assertTrue(fileExists);
    }
    public static void checkThatElementDoesNotExist(WebDriver driver, String el2Check) {
        Boolean fileExists = false;
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='list-item']"));
        for (WebElement el : list) {
            String name = el.findElement(By.tagName("h2")).getText();
            if (name.equals(el2Check)) {
                fileExists = true;
            }
        }
        Assert.assertFalse(fileExists);
    }

    public static Boolean checkElementPresence(WebDriver driver, String el2Check){
        //We will check if slytherin.jpeg file present in server files list.
        Boolean fileExists = false;
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='list-item']"));
        for (WebElement el:list) {
            String name = el.findElement(By.tagName("h2")).getText();
            if (name.equals(el2Check)) {
                fileExists = true;
            }
        }
        return fileExists;
    }

    public static Boolean checkDateDescendingSort(WebDriver driver, List<WebElement> list){
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

    public static void clickActionInList(WebDriver driver, List<WebElement> list, String action) {
        for (WebElement el : list) {
            driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
            String listElName = el.getText();
            if (listElName.contains(action)) {
                el.click();
                break;
            }
        }
    }

    public static void createTestPR1(WebDriver driver) {

        //Initiate PR creation form.
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.findElement(By.xpath("//a[@title='Create project room']")).click();

        //Fill PR name field. Somehow here sendKeys function fails from time to time. So i had to create special STAB. See SendKeysCondition class.
        String prNameInputLocator = "//div[@class='overlay overlay-contentscale open']/div[@class='dialog share-dialog']" +
                "/div[@class='tabs tabs-style-bar']/div[@class='content-wrap']/section[@id='folder-settings-tab']/div[@class='dialog-body']" +
                "/div[@class='form middle-form']/div[1]/span/input[@class='tt-input']";
        driver.findElement(By.xpath(prNameInputLocator)).sendKeys("TestPR1");
        SendKeysCondition condition = new SendKeysCondition(prNameInputLocator);
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(condition);

        //Finish creation process.
        driver.findElement(By.xpath("//div[@class='overlay overlay-contentscale open']/div[@class='dialog share-dialog']" +
                "/div[@class='tabs tabs-style-bar']/div[@class='content-wrap']/section[@id='folder-settings-tab']" +
                "/div[@class='dialog-footer']/button[@data-bind='click: save']")).click();
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        driver.findElement(By.xpath("//div[@class='overlay overlay-contentscale open']/div[@class='dialog']" +
                "/div[@class='dialog-body']/div[@class='middle-form']/div[4]/button")).click();
    }

    public static void deleteProjectRoom(WebDriver driver, String pr2Delete){
        //Delete PR if it exists due to previous tests.
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        if (F2GTestsLib.checkElementPresence(driver,pr2Delete)){
            F2GTestsLib.findElementAndSelectAction(driver,pr2Delete, "Delete");
            driver.navigate().refresh();
        }
    }

}
