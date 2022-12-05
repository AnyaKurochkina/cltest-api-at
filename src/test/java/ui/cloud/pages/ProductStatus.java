package ui.cloud.pages;

import com.codeborne.selenide.WebDriverRunner;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import ui.elements.Tooltip;

import java.time.Duration;

@EqualsAndHashCode
@Log4j2
public class ProductStatus {
    @Getter
    String status;

    final public static ProductStatus CREATING = new ProductStatus("Разворачивается");
    final public static ProductStatus PENDING = new ProductStatus("Изменение");
    final public static ProductStatus SUCCESS = new ProductStatus("В порядке");
    final public static ProductStatus DELETING = new ProductStatus("Удаляется");
    final public static ProductStatus ERROR = new ProductStatus("Ошибка");
    final public static ProductStatus BLOCKED = new ProductStatus("Заблокирован");

    public ProductStatus(String status) {
        this.status = status;
    }

    public ProductStatus(WebElement e) {
        init(e);
    }

    @Override
    public String toString() {
        return status;
    }

    private void init(WebElement e){
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebDriverWait wait = new WebDriverWait(webDriver,20);
        Actions builder = new Actions(webDriver);
        wait.until(driver -> {
            builder.moveToElement(e).pause(Duration.ofSeconds(1)).perform();
            return Tooltip.isVisible();
        });
        this.status = new Tooltip().toString();
    }

    public boolean isNeedWaiting() {
        return CREATING.equals(this) || PENDING.equals(this) || DELETING.equals(this);
    }

    public boolean isStatus() {
        return CREATING.equals(this) || PENDING.equals(this) || DELETING.equals(this) || SUCCESS.equals(this) || ERROR.equals(this);
    }
}
