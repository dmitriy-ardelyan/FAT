package f2g.autotests.trash;

import org.openqa.selenium.WebDriver;

public class F2GChromeAuth {
    public static void authenticate(WebDriver driver) {
        driver.get("http://localhost/webclient");
        try {
            Thread.sleep(2000);
            Runtime.getRuntime().exec("C:\\AutoitScript\\chromeauth.exe");
            Thread.sleep(5000);
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
