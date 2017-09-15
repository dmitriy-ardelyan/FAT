package f2g.autotests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.util.concurrent.TimeUnit;

public class F2GIEAuth {
    public static void authenticate(WebDriver driver) {
        driver.get("http://localhost/webclient");
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        driver.findElement(By.partialLinkText("Continue to this")).click();
        try {
            //Thread.sleep(2000);
            Runtime.getRuntime().exec("C:\\AutoitScript\\ieauth2.exe");
            Thread.sleep(5000);
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
