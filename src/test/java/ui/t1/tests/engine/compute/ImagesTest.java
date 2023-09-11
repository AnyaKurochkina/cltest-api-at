package ui.t1.tests.engine.compute;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.CompareType;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.compute.*;
import ui.t1.tests.engine.AbstractComputeTest;

import static core.utils.AssertUtils.assertHeaders;

@Epic("Cloud Compute")
@Feature("Образы")
public class ImagesTest extends AbstractComputeTest {

    @Test
    @TmsLink("1249434")
    @DisplayName("Cloud Compute. Образы")
    void snapshotList() {
        new IndexPage().goToImages();
        assertHeaders(new ImageList.ImageTable(),"", "Имя", "Зона доступности", "Формат диска", "Размер, МБ", "Дата создания", "");
    }

    @Test
    @TmsLink("1307076")
    @DisplayName("Cloud Compute. Образы. Виртуальная машина с диском с образа")
    void createVmWidthUserImage() {
        DiskCreate disk = new IndexPage().goToDisks().addDisk().setAvailabilityZone(availabilityZone).setName(getRandomName()).clickOrder();
        Disk diskPage = new DiskList().selectDisk(disk.getName()).markForDeletion(new VolumeEntity()).checkCreate(true);
        diskPage.runActionWithCheckCost(CompareType.MORE, () -> diskPage.createImage(disk.getName()));
        new IndexPage().goToImages().selectImage(disk.getName()).markForDeletion(new ImageEntity()).checkCreate(true);
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setUserImage(disk.getName())
                .setDeleteOnTermination(true)
                .setName(disk.getName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        new VmList().selectCompute(vm.getName()).markForDeletion(new InstanceEntity()).checkCreate(true);
    }
}
