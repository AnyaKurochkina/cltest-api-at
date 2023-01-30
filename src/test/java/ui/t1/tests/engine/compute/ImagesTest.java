package ui.t1.tests.engine.compute;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.cloud.pages.CompareType;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.BeforeAllExtension;
import ui.t1.pages.cloudEngine.compute.*;
import ui.t1.tests.engine.AbstractComputeTest;

import static core.utils.AssertUtils.assertHeaders;

@ExtendWith(BeforeAllExtension.class)
@Feature("Образы")
public class ImagesTest extends AbstractComputeTest {

    @Test
    @TmsLink("1249434")
    @DisplayName("Cloud Compute. Образы")
    void snapshotList() {
        new IndexPage().goToImages();
        assertHeaders(new ImageList.ImageTable(),"", "Имя", "Зона доступности", "Формат диска", "Размер, МБ", "Дата обновления", "");
    }

    @Test
    @TmsLink("1307076")
    @DisplayName("Cloud Compute. Образы. Виртуальная машина с диском с образа")
    void createVmWidthUserImage() {
        DiskCreate disk = new IndexPage().goToDisks().addDisk().setAvailabilityZone(availabilityZone).setName(getRandomName()).clickOrder();
        Disk diskPage = new DiskList().selectDisk(disk.getName()).checkCreate();
        diskPage.runActionWithCheckCost(CompareType.MORE, () -> diskPage.createImage(disk.getName()));
        Image imagePage = new IndexPage().goToImages().selectImage(disk.getName()).checkCreate();
        new IndexPage().goToDisks().selectDisk(disk.getName()).runActionWithCheckCost(CompareType.ZERO, diskPage::delete);
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setUserImage(disk.getName())
                .setDeleteOnTermination(true)
                .setName(disk.getName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        new IndexPage().goToVirtualMachine().selectCompute(disk.getName()).runActionWithCheckCost(CompareType.ZERO, vmPage::delete);
        new IndexPage().goToImages().selectImage(disk.getName()).runActionWithCheckCost(CompareType.ZERO, imagePage::delete);
    }
}
