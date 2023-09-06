package ui.t1.tests.engine.compute;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.EnabledIfEnv;
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
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(new SelectBox.Image("Windows Server", RANDOM_VALUE))
                .setName(getRandomName())
                .setDeleteOnTermination(true)
                .setBootSize(21)
                .setCreateTimeout(Duration.ofMinutes(4))
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).markForDeletion(new InstanceEntity()).checkCreate(true).delete();
    }

    @Test
    @TmsLink("")
    @EnabledIfEnv("t1prod")
    @DisplayName("Cloud Compute. Виртуальные машины. Создание Образ usergate")
    void createUserGate() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(new SelectBox.Image("usergate", RANDOM_VALUE))
                .setName(getRandomName())
                .setDeleteOnTermination(true)
                .setBootSize(21)
                .setCreateTimeout(Duration.ofMinutes(4))
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).markForDeletion(new InstanceEntity()).checkCreate(true).delete();
    }

    @Test
    @TmsLink("")
    @EnabledIfEnv("t1prod")
    @DisplayName("Cloud Compute. Виртуальные машины. Создание Образ acronis")
    void createAcronis() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(new SelectBox.Image("acronis", RANDOM_VALUE))
                .setName(getRandomName())
                .setDeleteOnTermination(true)
                .setBootSize(21)
                .setCreateTimeout(Duration.ofMinutes(4))
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).markForDeletion(new InstanceEntity()).checkCreate(true).delete();
    }

    @Test
    @TmsLink("")
    @EnabledIfEnv("t1prod")
    @DisplayName("Cloud Compute. Виртуальные машины. Создание Образ skdpu")
    void createSkdpu() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(new SelectBox.Image("skdpu", RANDOM_VALUE))
                .setName(getRandomName())
                .setDeleteOnTermination(true)
                .setCreateTimeout(Duration.ofMinutes(5))
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).markForDeletion(new InstanceEntity()).checkCreate(true).delete();
    }

    @Test
    @TmsLink("")
    @EnabledIfEnv("t1prod")
    @DisplayName("Cloud Compute. Виртуальные машины. Создание Образ ksmg")
    void createKsmg() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(new SelectBox.Image("ksmg", RANDOM_VALUE))
                .setName(getRandomName())
                .setDeleteOnTermination(true)
                .setBootSize(21)
                .setCreateTimeout(Duration.ofMinutes(4))
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .setCreateTimeout(Duration.ofMinutes(5))
                .clickOrder();
        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).markForDeletion(new InstanceEntity()).checkCreate(true).delete();
    }

    @Test
    @TmsLink("")
    @EnabledIfEnv("t1prod")
    @DisplayName("Cloud Compute. Виртуальные машины. Создание Образ Альт Сервер")
    void createAlt() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(new SelectBox.Image("Альт Сервер", RANDOM_VALUE))
                .setName(getRandomName())
                .setDeleteOnTermination(true)
                .setBootSize(21)
                .setCreateTimeout(Duration.ofMinutes(4))
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).markForDeletion(new InstanceEntity()).checkCreate(true).delete();
    }

    @Test
    @TmsLink("")
    @EnabledIfEnv("t1prod")
    @DisplayName("Cloud Compute. Виртуальные машины. Создание Образ Astra Linux SE")
    void createAstra() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(new SelectBox.Image("Astra Linux SE", RANDOM_VALUE))
                .setName(getRandomName())
                .setDeleteOnTermination(true)
                .setBootSize(21)
                .setCreateTimeout(Duration.ofMinutes(4))
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).markForDeletion(new InstanceEntity()).checkCreate(true).delete();
    }
}
