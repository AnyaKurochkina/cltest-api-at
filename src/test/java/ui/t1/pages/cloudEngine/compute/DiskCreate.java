package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ui.cloud.pages.EntitiesUtils;
import ui.elements.Input;
import ui.elements.Select;
import ui.elements.Switch;

import java.time.Duration;

@Getter
public class DiskCreate {
    private String availabilityZone;
    private String name;
    private Long size;
    private String type;

    private SelectBox.Image marketPlaceImage;
    private String userImage;

    public DiskCreate setAvailabilityZone(String availabilityZone) {
        Select.byLabel("Зона доступности").set(availabilityZone);
        this.availabilityZone = availabilityZone;
        return this;
    }

    public DiskCreate setName(String name) {
        Input.byLabel("Имя").setValue(name);
        this.name = name;
        return this;
    }

    public DiskCreate setSize(Long size) {
        Input.byLabel("Размер, Гб").setValue(String.valueOf(size));
        this.size = size;
        return this;
    }

    public DiskCreate setType(String type) {
        Select.byLabel("Тип").set(type);
        this.type = type;
        return this;
    }

    public DiskCreate setMarketPlaceImage(SelectBox.Image marketPlaceImage) {
        Switch.byLabel("Создать из образа").setEnabled(true);
        SelectBox.setMarketPlaceImage(marketPlaceImage);
        this.marketPlaceImage = marketPlaceImage;
        return this;
    }

    public DiskCreate setUserImage(String userImage) {
        Switch.byLabel("Создать из образа").setEnabled(true);
        SelectBox.setUserImage(userImage);
        this.userImage = userImage;
        return this;
    }

    public DiskCreate clickOrder(){
        EntitiesUtils.clickOrder();
        EntitiesUtils.waitCreate(() -> new DiskList.DiskTable()
                .getRowByColumnValue(DiskList.DiskTable.COLUMN_NAME, name)
                .getElementByColumn(DiskList.DiskTable.COLUMN_DATE)
                .shouldNot(Condition.exactText(""), Duration.ofMinutes(1)));
        return this;
    }
}
