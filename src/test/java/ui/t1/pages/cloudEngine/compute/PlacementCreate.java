package ui.t1.pages.cloudEngine.compute;

import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import ui.cloud.pages.orders.OrderUtils;
import ui.elements.Input;
import ui.elements.Select;
import ui.t1.pages.cloudEngine.Column;

import java.time.Duration;

@Getter
public class PlacementCreate {
    private String name;
    private String availabilityZone;
    private Type type;

    public enum Type {
        SOFT_AFFINITY("soft-affinity"),
        SOFT_ANTI_AFFINITY("soft-anti-affinity");
        private final String type;

        Type(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    public PlacementCreate setName(String name) {
        Input.byLabel("Имя политики размещения").setValue(name);
        this.name = name;
        return this;
    }

    public PlacementCreate setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = Select.byLabel("Зона доступности").set(availabilityZone);
        return this;
    }

    public PlacementCreate setType(Type type) {
        Select.byLabel("Тип политики").set(type.toString());
        this.type = type;
        return this;
    }

    @Step("Создание политики размещения")
    public PlacementCreate clickOrder() {
        OrderUtils.clickOrder();
        OrderUtils.waitCreate(() -> Waiting.find(() -> new PlacementList.PlacementTable()
                .isColumnValueEquals(Column.NAME, name), Duration.ofMinutes(1)));
        return this;
    }
}
