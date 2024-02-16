package ui.t1.tests.engine;

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
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.extension.ExtendWith;
import steps.orderService.ActionParameters;
import steps.orderService.OrderServiceSteps;
import steps.resourceManager.ResourceManagerSteps;
import steps.vpc.SecurityGroupResponse;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.T1LoginPage;
import ui.t1.pages.cloudEngine.backup.BackupCreate;
import ui.t1.pages.cloudEngine.backup.BackupsList;
import ui.t1.pages.cloudEngine.compute.SelectBox;
import ui.t1.pages.cloudEngine.compute.VmCreate;
import ui.t1.pages.cloudEngine.compute.VmList;
import ui.t1.pages.cloudEngine.vpc.PublicIpList;
import ui.t1.tests.AbstractT1Test;
import ui.t1.tests.WithAuthorization;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static tests.routes.OrderServiceApi.deleteV1ProjectsProjectNameOrdersId;
import static tests.routes.OrderServiceApi.getV1ProjectsProjectNameOrdersId;
import static tests.routes.VpcApi.deleteNetworkApiV1ProjectsProjectNameNetworksNetworkIdDelete;
import static ui.t1.pages.cloudEngine.compute.SshKeyList.SSH_USER;

@Log4j2
@ExtendWith(ConfigExtension.class)
@WithAuthorization(Role.CLOUD_ADMIN)
@Tags({@Tag("t1_ui_cloud_compute"), @Tag("t1")})
public abstract class AbstractComputeTest extends AbstractT1Test {
    protected String availabilityZone = "ru-central1-a";
    protected String region = "ru-central1";
    protected SelectBox.Image image = new SelectBox.Image("Ubuntu", "20.04");
    protected String hddTypeFirst = "Write: 3000";
    protected String hddTypeSecond = "Read: 10000";
    protected String defaultNetwork = "default";
    protected String defaultSubNetwork = "default";
    protected String securityGroup = "default";
    protected String flavorName = "Intel";
    private final String entitiesPrefix = "at-" + this.getClass().getSimpleName().toLowerCase();
    protected static final String sshKey = "AT-default";

    protected static final String CONNECT_INTERNET_COMMAND = "curl --connect-timeout 1 -Is http://yandex.ru";
    protected static final String CONNECT_INTERNET_COMMAND_RESPONSE = "302 Moved temporarily";

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
            historyMutex.put(e, new JSONObject());
            projectCountMap.put(e, 0);
        });
    }

    @Override
    protected String getProjectId() {
        return getComputeProjectId();
    }

    private static String getComputeProjectId() {
        String id = project.get();
        if (Objects.isNull(id)) {
            synchronized (AbstractComputeTest.class) {
                id = projectCountMap.entrySet().stream().min(Map.Entry.comparingByValue()).orElseThrow(NullPointerException::new).getKey();
                projectCountMap.merge(id, 1, Integer::sum);
                project.set(id);
            }
        }
        return id;
    }

    @BeforeAll
    public static synchronized void beforeAll() {
        new T1LoginPage(getComputeProjectId())
                .signIn(Role.CLOUD_ADMIN);
        new IndexPage().goToSshKeys().addKey(sshKey, SSH_USER);
        Selenide.closeWebDriver();
    }

    protected String getRandomName() {
        return new Generex(entitiesPrefix + "-[a-z]{6}").random();
    }

    @AllArgsConstructor
    public abstract static class BaseIdEntity extends AbstractEntity {
        String projectId;
        String id;

        public BaseIdEntity(String id) {
            this.projectId = StringUtils.findByRegex("context=([^&]*)", WebDriverRunner.getWebDriver().getCurrentUrl());
            this.id = id;
        }
    }

    public static class InstanceEntity extends ComputeEntity {
        boolean deleteOnTerminationSystemDisk;

        public InstanceEntity(String projectId, String id, boolean deleteOnTerminationSystemDisk) {
            super(projectId, id);
            this.deleteOnTerminationSystemDisk = deleteOnTerminationSystemDisk;
        }

        public InstanceEntity(boolean deleteOnTerminationSystemDisk) {
            super();
            this.deleteOnTerminationSystemDisk = deleteOnTerminationSystemDisk;
        }

        public InstanceEntity(String projectId, String id) {
            super(projectId, id);
            this.deleteOnTerminationSystemDisk = true;
        }

        @Override
        public void delete() {
            List<String> diskList = new ArrayList<>();
            Http.setFixedRole(Role.CLOUD_ADMIN);
            try {
                if (deleteOnTerminationSystemDisk)
                    diskList.add(OrderServiceSteps.getObjectClass(projectId, id, "data.find{it.type=='volume' && it.data.config.system==true}.item_id", String.class));
                OrderServiceSteps.runAction(ActionParameters.builder().name("compute_instance_delete").orderId(id).checkPrebilling(false)
                        .data(new JSONObject().put("allow_delete_volumes", deleteOnTerminationSystemDisk)
                                .put("delete_floating_ip", false).put("volumes", diskList))
                        .projectId(projectId).build());
            } finally {
                Http.removeFixedRole();
            }
        }
    }

    public static class ComputeEntity extends BaseIdEntity {
        public ComputeEntity(String projectId, String id) {
            super(projectId, id);
        }

        @Override
        protected int getPriority() {
            return 1;
        }

        public ComputeEntity() {
            super(StringUtils.findByRegex("orders/([^/]*)/", WebDriverRunner.getWebDriver().getCurrentUrl()));
        }

        @Override
        public void delete() {
            String status = Http.builder().setRole(Role.CLOUD_ADMIN).api(getV1ProjectsProjectNameOrdersId, projectId, id)
                    .jsonPath().getString("status");
            if (status.equals("changing") || status.equals("pending"))
                Waiting.sleep(20000);
            if (status.equals("success")) {
                OrderServiceSteps.switchProtect(id, projectId, false);
                Http.builder().setRole(Role.CLOUD_ADMIN).api(deleteV1ProjectsProjectNameOrdersId, projectId, id);
                Waiting.sleep(30000);
            }
        }
    }

    @NoArgsConstructor
    public static class VolumeEntity extends ComputeEntity {
        public VolumeEntity(String projectId, String id) {
            super(projectId, id);
        }
        @Override
        protected int getPriority() {
            return 2;
        }
    }

    @NoArgsConstructor
    public static class PlacementEntity extends ComputeEntity {
        public PlacementEntity(String projectId, String id) {
            super(projectId, id);
        }

        @Override
        protected int getPriority() {
            return 2;
        }
    }

    @NoArgsConstructor
    public static class SnapshotEntity extends ComputeEntity {
        public SnapshotEntity(String projectId, String id) {
            super(projectId, id);
        }
    }

    @NoArgsConstructor
    public static class ImageEntity extends ComputeEntity {
        public ImageEntity(String projectId, String id) {
            super(projectId, id);
        }
    }

    public static class NetworkEntity extends BaseIdEntity {
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

    public static class SecurityGroupEntity extends SecurityGroupResponse {
        public SecurityGroupEntity() {
            setId(StringUtils.findByRegex("security-groups/([^?]*)", WebDriverRunner.getWebDriver().getCurrentUrl()));
        }

        public SecurityGroupEntity(String projectId, String id) {
            setId(id);
            setProjectId(projectId);
        }
    }

    @NoArgsConstructor
    public static class PublicIpEntity extends ComputeEntity {
        public PublicIpEntity(String projectId, String id) {
            super(projectId, id);
        }

        @Override
        protected int getPriority() {
            return 3;
        }
    }

    @NoArgsConstructor
    public static class VipEntity extends ComputeEntity {
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
                .setRegion(region)
                .setAvailabilityZone(availabilityZone)
                .seNetwork(defaultNetwork)
                .setSubnet(defaultSubNetwork)
                .setImage(image)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        new VmList().selectCompute(v.getName())
                .markForDeletion(new InstanceEntity(true), AbstractEntity.Mode.AFTER_CLASS).checkCreate(true);
        return v;
    });

    protected final EntitySupplier<String> randomPublicIp = lazy(() -> {
        String ip = new IndexPage().goToPublicIps().addIp(region);
        new PublicIpList().selectIp(ip).markForDeletion(new PublicIpEntity(), AbstractEntity.Mode.AFTER_CLASS);
        return ip;
    });

    protected final EntitySupplier<BackupCreate> backupSup = lazy(() -> {
        VmCreate vm = randomVm.get();
        BackupCreate backupCreate = new IndexPage().goToBackups().addBackup().setAvailabilityZone(availabilityZone).setSourceType("Сервер")
                .setObjectForBackup(vm.getName()).clickOrder();
        new BackupsList().selectBackup(backupCreate.getObjectForBackup())
                .markForDeletion(new ComputeEntity(), AbstractEntity.Mode.AFTER_CLASS).checkCreate(true);
        return backupCreate;
    });
}
