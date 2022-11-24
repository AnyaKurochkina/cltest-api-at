package ui.t1.pages.cloudCompute;

import lombok.Getter;
import ui.elements.*;

@Getter
public class DiskCreatePage {
    private String availabilityZone;
    private String name;
    private Integer size;
    private String type;

    private SelectBox.Image marketPlaceImage;
    private String userImage;

    public DiskCreatePage setAvailabilityZone(String availabilityZone) {
        DropDown.byLabel("Зона доступности").select(availabilityZone);
        this.availabilityZone = availabilityZone;
        return this;
    }

    public DiskCreatePage setName(String name) {
        Input.byLabel("Имя").setValue(name);
        this.name = name;
        return this;
    }

    public DiskCreatePage setSize(Integer size) {
        Input.byLabel("Размер, Гб").setValue(String.valueOf(size));
        this.size = size;
        return this;
    }

    public DiskCreatePage setType(String type) {
        DropDown.byLabel("Тип").selectByTextContains(type);
        this.type = type;
        return this;
    }

    public DiskCreatePage setMarketPlaceImage(SelectBox.Image marketPlaceImage) {
        Switch.byLabel("Создать из образа").setEnabled(true);
        Radio.byValue("MarketPlace").checked();
        SelectBox.setMarketPlaceImage(marketPlaceImage);
        this.marketPlaceImage = marketPlaceImage;
        return this;
    }

    public DiskCreatePage setUserImage(String userImage) {
        Switch.byLabel("Создать из образа").setEnabled(true);
        Radio.byValue("Пользовательские").checked();
        //Todo: доделать выбор образа
        this.userImage = userImage;
        return this;
    }

    public DiskCreatePage clickOrder(){
        Button.byText("Заказать").click();
        return this;
    }
}
