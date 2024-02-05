package ui.t1.tests.engine.compute;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.AbstractEntity;
import org.junit.EnabledIfEnv;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.compute.SelectBox;
import ui.t1.pages.cloudEngine.compute.VmCreate;
import ui.t1.tests.engine.AbstractComputeTest;

import java.time.Duration;

import static ui.elements.Select.RANDOM_VALUE;

@Epic("Cloud Compute")
@Feature("Разворачивание на других образах")
public class OtherImagesTest extends AbstractComputeTest {

    @Test
    @TmsLink("")
    @EnabledIfEnv("t1prod")
    @DisplayName("Cloud Compute. Виртуальные машины. Создание Образ Windows Server")
    void createWindows() {
        orderVm(new IndexPage().goToVirtualMachine().addVm()
                .setImage(new SelectBox.Image("Windows Server", RANDOM_VALUE))
                .setBootSize(21)
                .setCreateTimeout(Duration.ofMinutes(4)));
    }

    @Test
    @TmsLink("")
    @EnabledIfEnv("t1prod")
    @DisplayName("Cloud Compute. Виртуальные машины. Создание Образ Альт Сервер")
    void createAlt() {
        orderVm(new IndexPage().goToVirtualMachine().addVm()
                .setImage(new SelectBox.Image("Альт Сервер", RANDOM_VALUE))
                .setBootSize(21)
                .setCreateTimeout(Duration.ofMinutes(4)));
    }

    @Test
    @TmsLink("")
    @EnabledIfEnv("t1prod")
    @DisplayName("Cloud Compute. Виртуальные машины. Создание Образ Astra Linux SE")
    void createAstra() {
        orderVm(new IndexPage().goToVirtualMachine().addVm()
                .setImage(new SelectBox.Image("Astra Linux SE", RANDOM_VALUE))
                .setBootSize(21)
                .setCreateTimeout(Duration.ofMinutes(4)));
    }

    @Test
    @TmsLink("")
    @EnabledIfEnv("t1prod")
    @DisplayName("Cloud Compute. Виртуальные машины. Создание Образ CentOS")
    void createCentOS() {
        orderVm(new IndexPage().goToVirtualMachine().addVm()
                .setImage(new SelectBox.Image("CentOS", RANDOM_VALUE))
                .setBootSize(21)
                .setCreateTimeout(Duration.ofMinutes(4)));
    }

    @Test
    @TmsLink("")
    @EnabledIfEnv("t1prod")
    @DisplayName("Cloud Compute. Виртуальные машины. Создание Образ Temp")
    void createTemp() {
        orderVm(new IndexPage().goToVirtualMachine().addVm()
                .setImage(new SelectBox.Image("Temp", RANDOM_VALUE))
                .setBootSize(26)
                .setCreateTimeout(Duration.ofMinutes(8)));
    }

    @Test
    @TmsLink("")
    @EnabledIfEnv("t1prod")
    @DisplayName("Cloud Compute. Виртуальные машины. Создание Образ Kaspersky Security")
    void createKS() {
        orderVm(new IndexPage().goToVirtualMachine().addVm()
                .setImage(new SelectBox.Image("Kaspersky Security", RANDOM_VALUE))
                .setBootSize(21)
                .setCreateTimeout(Duration.ofMinutes(10)));
    }

    @Test
    @TmsLink("")
    @Disabled
    @EnabledIfEnv("t1prod")
    @DisplayName("Cloud Compute. Виртуальные машины. Marketplace Образ Kaspersky Endpoint Security")
    void createKES() {
        orderVm(new IndexPage().goToVirtualMachine().addVm()
                .setMarketplaceImage("Kaspersky Endpoint Security")
                .setBootSize(21)
                .setCreateTimeout(Duration.ofMinutes(10)));
    }

    @Test
    @TmsLink("")
    @EnabledIfEnv("t1prod")
    @DisplayName("Cloud Compute. Виртуальные машины. Marketplace Образ SKDPU")
    void createSkdpu() {
        orderVm(new IndexPage().goToVirtualMachine().addVm()
                .setMarketplaceImage("SKDPU")
                .setBootSize(82)
                .setCreateTimeout(Duration.ofMinutes(7)));
    }

    @Test
    @TmsLink("")
    @EnabledIfEnv("t1prod")
    @DisplayName("Cloud Compute. Виртуальные машины. Marketplace Образ KSMG")
    void createKsmg() {
        orderVm(new IndexPage().goToVirtualMachine().addVm()
                .setMarketplaceImage("KSMG")
                .setBootSize(21)
                .setCreateTimeout(Duration.ofMinutes(4)));
    }

    @Test
    @TmsLink("")
    @EnabledIfEnv("t1prod")
    @DisplayName("Cloud Compute. Виртуальные машины. Marketplace Образ Acronis")
    void createAcronis() {
        orderVm(new IndexPage().goToVirtualMachine().addVm()
                .setMarketplaceImage("Acronis")
                .setBootSize(21)
                .setCreateTimeout(Duration.ofMinutes(4)));
    }

    @Test
    @TmsLink("")
    @EnabledIfEnv("t1prod")
    @DisplayName("Cloud Compute. Виртуальные машины. Marketplace Образ UserGate")
    void createUserGate() {
        orderVm(new IndexPage().goToVirtualMachine().addVm()
                .setMarketplaceImage("Usergate")
                .setBootSize(21)
                .setCreateTimeout(Duration.ofMinutes(4)));
    }

    private void orderVm(VmCreate vm){
        vm.setRegion(region)
                .setAvailabilityZone(availabilityZone)
                .seNetwork(defaultNetwork)
                .setSubnet(defaultSubNetwork)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        new IndexPage().goToVirtualMachine().selectCompute(vm.getName())
                .markForDeletion(new InstanceEntity(true), AbstractEntity.Mode.AFTER_TEST).checkCreate(true).delete();
    }
}
