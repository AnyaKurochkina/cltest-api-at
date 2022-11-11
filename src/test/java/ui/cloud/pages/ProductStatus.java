package ui.cloud.pages;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import ui.elements.Tooltip;

import java.time.Duration;

@EqualsAndHashCode
public class ProductStatus {
    @Getter
    String status;

    final public static ProductStatus CREATING = new ProductStatus("Разворачивается");
    final public static ProductStatus PENDING = new ProductStatus("Изменение");
    final public static ProductStatus SUCCESS = new ProductStatus("В порядке");
    final public static ProductStatus DELETING = new ProductStatus("Удаляется");
    final public static ProductStatus ERROR = new ProductStatus("Ошибка");

    ProductStatus(String status) {
        this.status = status;
    }

    public ProductStatus(SelenideElement e) {
        e.hover();
        this.status = new Tooltip().toString();
    }

    ProductStatus(WebElement e) {
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebDriverWait wait = new WebDriverWait(webDriver,20);
        Actions builder = new Actions(webDriver);
        wait.until((ExpectedCondition<Boolean>) driver -> {
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
