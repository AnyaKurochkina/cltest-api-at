package ui.t1.pages.cloudEngine.vpc;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ui.elements.*;
import ui.t1.pages.cloudEngine.Column;

import java.time.Duration;
import java.util.Objects;

@Getter
public class Rule {
    String route;
    String description;
    String protocol;
    String subnetType;
    Destination destination;
    OpenPorts openPorts;

    public Rule setRoute(String route) {
        this.route = route;
        Select.byLabel("Направление").set(route);
        return this;
    }

    public Rule setProtocol(String protocol) {
        this.protocol = protocol;
        Select.byLabel("Протокол").set(protocol);
        return this;
    }

    public Rule setSubnetType(String subnetType) {
        this.subnetType = subnetType;
        Select.byLabel("Тип подсети").set(subnetType);
        return this;
    }

    public Rule setDestination(Destination destination) {
        this.destination = destination;
        destination.setParameters();
        return this;
    }

    public Rule setOpenPorts(OpenPorts openPorts) {
        this.openPorts = openPorts;
        openPorts.setParameters();
        return this;
    }

    public Rule setDescription(String description) {
        this.description = description;
        TextArea.byLabel("Описание").setValue(description);
        return this;
    }

    public Rule clickAdd() {
        Dialog dialog = Dialog.byTitle("Добавить правило");
        dialog.clickButton("Добавить");
        dialog.getDialog().shouldNotBe(Condition.visible);
        Waiting.findWithRefresh(() -> SecurityGroup.RulesTable.getRule(description).getValueByColumn(Column.STATUS).equals("Доступно"), Duration.ofMinutes(1));
        return this;
    }

    interface OpenPorts {
        void setParameters();

        default RadioGroup getRadioGroup() {
            return RadioGroup.byLabel("Открыть порт");
        }
    }

    interface Destination {
        void setParameters();

        default RadioGroup getRadioGroup() {
            return RadioGroup.byLabel("Назначение");
        }
    }

    public static class CidrDestination implements Destination {
        String remoteIpPrefix;
        boolean any;

        public CidrDestination(String remoteIpPrefix) {
            this.remoteIpPrefix = remoteIpPrefix;
        }

        public CidrDestination(boolean any) {
            this.any = any;
        }

        @Override
        public void setParameters() {
            getRadioGroup().select("CIDR");
            if (Objects.nonNull(remoteIpPrefix))
                Input.byPlaceholder("0.0.0.0/0, ::/0").setValue(remoteIpPrefix);
            else CheckBox.byLabel("Любой").setChecked(any);
        }
    }

    @AllArgsConstructor
    public static class SecurityGroupDestination implements Destination {
        String securityGroup;

        @Override
        public void setParameters() {
            getRadioGroup().select("Группа безопасности");
            Select.byInputName("remoteGroupId").set(securityGroup);
        }
    }

    public static class AllPorts implements OpenPorts {
        @Override
        public void setParameters() {
            getRadioGroup().select("Все");
        }
    }

    @AllArgsConstructor
    public static class Port implements OpenPorts {
        public int port;

        @Override
        public void setParameters() {
            getRadioGroup().select("Порт");
            Input.byPlaceholder("0-65535").setValue(port);
        }
    }

    @AllArgsConstructor
    static class RangePorts implements OpenPorts {
        public int from;
        public int to;

        @Override
        public void setParameters() {
            getRadioGroup().select("Диапазон портов");
            Input.byLabel("от").setValue(from);
            Input.byLabel("до").setValue(to);
        }
    }
}
