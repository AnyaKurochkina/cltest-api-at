package ui.t1.tests.cloudEngine.compute;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.cloud.pages.CompareType;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.BeforeAllExtension;
import ui.t1.pages.cloudEngine.compute.*;

import java.util.Random;

import static core.utils.AssertUtils.AssertHeaders;

@ExtendWith(BeforeAllExtension.class)
public class ImagesTest extends AbstractComputeTest {

    @Test
    @TmsLink("1249434")
    @DisplayName("Cloud Compute. Образы")
    void snapshotList() {
        new IndexPage().goToDisks();
        AssertHeaders(new ImageList.ImageTable(),"", "Имя", "Зона доступности", "Формат диска", "Размер, МБ", "Дата обновления", "");
    }

    @Test
    @TmsLink("1307076")
    @DisplayName("Создание ВМ c пользовательским образом")
    void createVmWidthUserImage() {
        String name = "AT-UI-" + Math.abs(new Random().nextInt());
        DiskCreate disk = new IndexPage()
                .goToDisks()
                .addDisk()
                .setAvailabilityZone(availabilityZone)
                .setName(name)
                .setSize(2L)
                .clickOrder();

        Disk diskPage = new DiskList().selectDisk(disk.getName()).checkCreate();
        diskPage.runActionWithCheckCost(CompareType.MORE, () -> diskPage.createImage(name));
        Image imagePage = new IndexPage().goToImages().selectImage(name).checkCreate();

        new IndexPage()
                .goToDisks()
                .selectDisk(name)
                .runActionWithCheckCost(CompareType.ZERO, diskPage::delete);

        VmCreate vm = new IndexPage()
                .goToVirtualMachine()
                .addVm()
                .setUserImage(name)
                .setDeleteOnTermination(true)
                .setAvailabilityZone(availabilityZone)
                .setName(name)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();

        new IndexPage()
                .goToVirtualMachine()
                .selectCompute(name)
                .runActionWithCheckCost(CompareType.ZERO, vmPage::delete);

        new IndexPage()
                .goToImages()
                .selectImage(name)
                .runActionWithCheckCost(CompareType.ZERO, imagePage::delete);
    }
}
