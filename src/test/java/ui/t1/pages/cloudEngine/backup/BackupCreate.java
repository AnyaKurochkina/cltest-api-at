package ui.t1.pages.cloudEngine.backup;

import core.utils.Waiting;
import lombok.Getter;
import ui.cloud.pages.orders.OrderUtils;
import ui.elements.Alert;
import ui.elements.Button;
import ui.elements.Select;

import java.time.Duration;

@Getter
public class BackupCreate {
    private String sourceType;
    private String availabilityZone;
    private String objectForBackup;

    public BackupCreate setSourceType(String sourceType) {
        Select.byLabel("Тип объекта").set(sourceType);
        this.sourceType = sourceType;
        return this;
    }

    public BackupCreate setAvailabilityZone(String availabilityZone) {
        Select.byLabel("Зона доступности").set(availabilityZone);
        this.availabilityZone = availabilityZone;
        return this;
    }

    public BackupCreate setObjectForBackup(String objectForBackup) {
        Select.byLabel("Доступные объекты").setContains(objectForBackup);
        this.objectForBackup = objectForBackup;
        return this;
    }

    public BackupCreate clickOrder() {
        Button.byText("Заказать").click();
        Alert.green("Заказ успешно создан");
        OrderUtils.waitCreate(() -> Waiting.find(() -> new BackupsList.BackupTable()
                        .isColumnValueEquals("Имя объекта", objectForBackup), Duration.ofMinutes(8),
                "Произошла ошибка при создании резервной копии"));
        return this;
    }
}
