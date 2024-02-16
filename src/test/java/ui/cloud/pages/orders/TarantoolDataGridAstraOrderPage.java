package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ui.elements.Input;
import ui.elements.Select;

import java.util.UUID;

@Getter
public class TarantoolDataGridAstraOrderPage extends NewOrderPage {

    private final String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);
    protected Select flavorSelectCluster = Select.byLabel("Конфигурация кластера");
    protected Select versionSelect = Select.byLabel("Версия TDG");
    protected Input appTarantool = Input.byLabel("Размер, Гб",1);
    protected Input appNginx = Input.byLabel("Размер, Гб",2);
    protected Input appEtcd = Input.byLabel("Размер, Гб",3);
    protected Input appLogs = Input.byLabel("Размер, Гб",4);
    protected Input appSnap = Input.byLabel("Размер, Гб",5);
    protected Select groupSelectTarantool = Select.byLabel("Группа управления Tarantool");
    public TarantoolDataGridAstraOrderPage() {
        labelInput.setValue(labelValue);
        platformSelect.getElement().shouldBe(Condition.enabled);
    }

    public void checkOrderDetails() {
        super.checkOrderDetails();
        getHardDrives().shouldBe(Condition.visible.because("Должно отображаться сообщение"));
    }
}
