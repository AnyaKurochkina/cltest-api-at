package ui.t1.tests.engine;

import api.Tests;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import core.helper.Configure;
import core.helper.StringUtils;
import core.helper.http.Http;
import core.utils.Waiting;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import models.AbstractEntity;
import models.cloud.authorizer.Folder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import steps.orderService.OrderServiceSteps;
import steps.resourceManager.ResourceManagerSteps;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.T1LoginPage;
import ui.t1.pages.cloudEngine.compute.SelectBox;
import ui.t1.pages.cloudEngine.compute.VmCreate;
import ui.t1.pages.cloudEngine.compute.VmList;
import ui.t1.pages.cloudEngine.vpc.PublicIpList;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static api.routes.OrderServiceApi.deleteV1ProjectsProjectNameOrdersId;
import static api.routes.OrderServiceApi.getV1ProjectsProjectNameOrdersId;
import static api.routes.VpcApi.deleteNetworkApiV1ProjectsProjectNameNetworksNetworkIdDelete;
import static api.routes.VpcApi.deleteSecurityGroupApiV1ProjectsProjectNameSecurityGroupsSecurityGroupIdDelete;
import static ui.t1.pages.cloudEngine.compute.SshKeyList.SSH_USER;

@Log4j2
@ExtendWith(ConfigExtension.class)
@Tags({@Tag("t1_ui_cloud_compute"), @Tag("t1")})
public abstract class AbstractComputeTest extends Tests {
    protected String availabilityZone = "ru-central1-a";
    protected String region = "ru-central1";
    protected SelectBox.Image image = new SelectBox.Image("Ubuntu", "20.04");
    protected String hddTypeFirst = "500";
    protected String hddTypeSecond = "500";
    protected String defaultNetwork = "default";
    protected String securityGroup = "default";
    protected String flavorName = "Intel";
    private final String entitiesPrefix = "AT-" + this.getClass().getSimpleName();
    protected static final String sshKey = "AT-default";

    public AbstractComputeTest() {
        if (!Configure.ENV.equals("t1prod"))
            availabilityZone = "ru-central1-c";
    }

    private static final ThreadLocal<String> project = new ThreadLocal<>();
    public static final Map<String, Object> historyMutex = new HashMap<>();
    private static final Map<String, Integer> projectCountMap = new ConcurrentHashMap<>();

    static {
        Folder folderPollProject = Folder.builder().title("ProjectPool").build().onlyGetObject();
        ResourceManagerSteps.getChildren(folderPollProject.getName()).forEach(e -> {
            historyMutex.put(e, new Object());
            projectCountMap.put(e, 0);
        });
    }

    public static String getProjectId() {
        String id = project.get();
        if(Objects.isNull(id)) {
            synchronized (AbstractComputeTest.class) {
                id = projectCountMap.entrySet().stream().min(Map.Entry.comparingByValue()).orElseThrow(NullPointerException::new).getKey();
                projectCountMap.merge(id, 1, Integer::sum);
                project.set(id);
            }
        }
        return id;
    }

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new T1LoginPage(getProjectId())
                .signIn(Role.CLOUD_ADMIN);
        Tests.getPostLoadPage().run();
    }

    @BeforeAll
    public static synchronized void beforeAll() {
        new T1LoginPage(getProjectId())
                .signIn(Role.CLOUD_ADMIN);
        new IndexPage().goToSshKeys().addKey(sshKey, SSH_USER);
        Selenide.closeWebDriver();
    }

    protected String getRandomName() {
        return new Generex(entitiesPrefix + "-[a-z]{6}").random();
    }

    @AllArgsConstructor
    public abstract static class ComputeEntity extends AbstractEntity {
        String projectId;
        String id;

        public ComputeEntity(String id) {
            this.projectId = StringUtils.findByRegex("context=([^&]*)", WebDriverRunner.getWebDriver().getCurrentUrl());
            this.id = id;
        }
    }

    public static class InstanceEntity extends ComputeEntity {
        public InstanceEntity(String projectId, String id) {
            super(projectId, id);
        }

        @Override
        protected int getPriority() {
            return 1;
        }

        public InstanceEntity() {
            super(StringUtils.findByRegex("orders/([^/]*)/", WebDriverRunner.getWebDriver().getCurrentUrl()));
        }
        @Override
        public void delete() {
            String status = Http.builder().setRole(Role.CLOUD_ADMIN).api(getV1ProjectsProjectNameOrdersId, projectId, id).jsonPath().getString("status");
            if(status.equals("changing") || status.equals("pending"))
                Waiting.sleep(20000);
            if(status.equals("success")) {
                OrderServiceSteps.switchProtect(id, projectId, false);
                Http.builder().setRole(Role.CLOUD_ADMIN).api(deleteV1ProjectsProjectNameOrdersId, projectId, id);
                Waiting.sleep(30000);
            }
        }
    }

    @NoArgsConstructor
    public static class VolumeEntity extends InstanceEntity {
        public VolumeEntity(String projectId, String id) {
            super(projectId, id);
        }
        @Override
        protected int getPriority() {
            return 2;
        }
    }
    @NoArgsConstructor
    public static class SnapshotEntity extends InstanceEntity {
        public SnapshotEntity(String projectId, String id) {
            super(projectId, id);
        }
    }
    @NoArgsConstructor
    public static class ImageEntity extends InstanceEntity {
        public ImageEntity(String projectId, String id) {
            super(projectId, id);
        }
    }

    public static class NetworkEntity extends ComputeEntity {
        public NetworkEntity() {
            super(StringUtils.findByRegex("networks/([^?]*)", WebDriverRunner.getWebDriver().getCurrentUrl()));
        }

        public NetworkEntity(String projectId, String id) {
            super(projectId, id);
        }

        @Override
        protected int getPriority() {
            return 2;
        }

        @Override
        public void delete() {
            Http.builder().setRole(Role.CLOUD_ADMIN).api(deleteNetworkApiV1ProjectsProjectNameNetworksNetworkIdDelete, projectId, id);
        }
    }

    public static class SecurityGroupEntity extends ComputeEntity {
        public SecurityGroupEntity() {
            super(StringUtils.findByRegex("security-groups/([^?]*)", WebDriverRunner.getWebDriver().getCurrentUrl()));
        }

        public SecurityGroupEntity(String projectId, String id) {
            super(projectId, id);
        }

        @Override
        protected int getPriority() {
            return 2;
        }

        @Override
        public void delete() {
            Http.builder().setRole(Role.CLOUD_ADMIN).api(deleteSecurityGroupApiV1ProjectsProjectNameSecurityGroupsSecurityGroupIdDelete, projectId, id);
        }
    }

    @NoArgsConstructor
    public static class PublicIpEntity extends InstanceEntity {
        public PublicIpEntity(String projectId, String id) {
            super(projectId, id);
        }
        @Override
        protected int getPriority() {
            return 3;
        }
    }

    @NoArgsConstructor
    public static class VipEntity extends InstanceEntity {
        public VipEntity(String projectId, String id) {
            super(projectId, id);
        }
        @Override
        protected int getPriority() {
            return 0;
        }
    }

    protected final EntitySupplier<VmCreate> randomVm = lazy(() -> {
        VmCreate v = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(image)
                .setDeleteOnTermination(true)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        new VmList().selectCompute(v.getName())
                .markForDeletion(new InstanceEntity().setMode(AbstractEntity.Mode.AFTER_CLASS)).checkCreate(true);
        return v;
    });

    protected final EntitySupplier<VmCreate> publicIpVm = lazy(() -> {
        String ip = new IndexPage().goToPublicIps().addIp(region);
        new PublicIpList().selectIp(ip).markForDeletion(new PublicIpEntity().setMode(AbstractEntity.Mode.AFTER_CLASS));
        VmCreate v = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(image)
                .setDeleteOnTermination(true)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setPublicIp(ip)
                .setSshKey(sshKey)
                .clickOrder();
        new VmList().selectCompute(v.getName())
                .markForDeletion(new InstanceEntity().setMode(AbstractEntity.Mode.AFTER_CLASS)).checkCreate(false);
        return v;
    });
}
