package ui.cloud.pages.orders;

import com.codeborne.selenide.WebDriverRunner;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ui.elements.Tooltip;

import java.time.Duration;

@EqualsAndHashCode
@Log4j2
public class OrderStatus {
    final public static OrderStatus CREATING = new OrderStatus("Разворачивается");
    final public static OrderStatus CHANGING = new OrderStatus("Изменение");
    final public static OrderStatus PENDING = new OrderStatus("В процессе");
    final public static OrderStatus SUCCESS = new OrderStatus("В порядке");
    final public static OrderStatus DELETING = new OrderStatus("Удаляется");
    final public static OrderStatus ERROR = new OrderStatus("Ошибка");
    final public static OrderStatus BLOCKED = new OrderStatus("Заблокирован");
    final public static OrderStatus DEPROVISIONED = new OrderStatus("Удалено");
    @Getter
    String status;

    public OrderStatus(String status) {
        this.status = status;
    }

    public OrderStatus(WebElement e) {
        init(e);
    }

    @Override
    public String toString() {
        return status;
    }

    private void init(WebElement e) {
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebDriverWait wait = new WebDriverWait(webDriver, 20);
        Actions builder = new Actions(webDriver);
        wait.until(driver -> {
            builder.moveToElement(e).pause(Duration.ofSeconds(1)).perform();
            return Tooltip.isVisible();
        });
        this.status = new Tooltip().toString();
    }

    public boolean isNeedWaiting() {
        return CREATING.equals(this) || CHANGING.equals(this) || PENDING.equals(this) || DELETING.equals(this);
    }

    public boolean isStatus() {
        return CREATING.equals(this) || CHANGING.equals(this) || PENDING.equals(this) || DELETING.equals(this)
                || SUCCESS.equals(this) || ERROR.equals(this);
    }
}
