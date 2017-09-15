package f2g.autotests.trash;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

public class F2GAdminAuthentication {
    public static void authenticate(WebDriver driver) {

        try{
            driver.get("http://localhost/admin");
            driver.manage().window().maximize();
        } catch(Exception ex) {
            driver.findElement(By.xpath("//*[@id='advancedButton']")).click();
            driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
            driver.findElement(By.xpath("//*[@id='exceptionDialogButton']")).click();
            try {
                Thread.sleep(500);
                Runtime.getRuntime().exec("C:\\AutoitScript\\confirmException.exe");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(500);
            Runtime.getRuntime().exec("C:\\AutoitScript\\adminAuth.exe");
            //Thread.sleep(10000);
            for(int i = 1; i<16;i++){
                Thread.sleep(1000);
                System.out.println(i);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
