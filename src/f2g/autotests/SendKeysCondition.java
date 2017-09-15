package f2g.autotests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class SendKeysCondition  implements ExpectedCondition<Boolean> {
    private String locator;
    private int attempts;

    public SendKeysCondition(String locator){
        this.locator = locator;
        this.attempts = 0;
    }

    @Override
    public Boolean apply(WebDriver driver) {
        WebElement element = driver.findElement(By.xpath(this.locator));
        String currentInputText = element.getAttribute("value");
        if (currentInputText.equals("TestPR1")) {
            return true;
        } else if (attempts<5){
            element.clear();
            element.sendKeys("TestPR1");
                attempts++;
          return this.apply(driver);
        } else return false;
    }

}
