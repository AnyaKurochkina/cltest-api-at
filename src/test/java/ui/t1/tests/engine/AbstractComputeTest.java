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
import lombok.extern.log4j.Log4j2;
import models.AbstractEntity;
import models.cloud.authorizer.ProjectPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.T1LoginPage;
import ui.t1.pages.cloudEngine.compute.SelectBox;

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
    protected String hddTypeFirst = "HDD";
    protected String hddTypeSecond = "HDD";
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
    private static final Map<String, Integer> projectCountMap = new ConcurrentHashMap<>();

    static {
        ProjectPool pool = ProjectPool.builder().build().createObject();
        pool.getId().forEach(e -> projectCountMap.put(e, 0));
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
        Waiting.sleep(3000);
    }

    @BeforeAll
    public static void beforeAll() {
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

    protected static class VmEntity extends ComputeEntity {
        public VmEntity(String projectId, String id) {
            super(projectId, id);
        }

        @Override
        protected int getPriority() {
            return 1;
        }

        public VmEntity() {
            super(StringUtils.findByRegex("orders/([^/]*)/", WebDriverRunner.getWebDriver().getCurrentUrl()));
        }
        @Override
        public void delete() {
            String status = Http.builder().setRole(Role.CLOUD_ADMIN).api(getV1ProjectsProjectNameOrdersId, projectId, id).jsonPath().getString("status");
            if(status.equals("changing") || status.equals("pending"))
                Waiting.sleep(20000);
            if(status.equals("success")) {
                Http.builder().setRole(Role.CLOUD_ADMIN).api(deleteV1ProjectsProjectNameOrdersId, projectId, id);
                Waiting.sleep(30000);
            }
        }
    }

    public static class DiskEntity extends VmEntity {
        @Override
        protected int getPriority() {
            return 2;
        }
    }
    public static class SnapshotEntity extends VmEntity {}
    public static class ImageEntity extends VmEntity {}

    protected static class NetworkEntity extends ComputeEntity {
        public NetworkEntity() {
            super(StringUtils.findByRegex("networks/([^?]*)", WebDriverRunner.getWebDriver().getCurrentUrl()));
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

        @Override
        protected int getPriority() {
            return 2;
        }

        @Override
        public void delete() {
            Http.builder().setRole(Role.CLOUD_ADMIN).api(deleteSecurityGroupApiV1ProjectsProjectNameSecurityGroupsSecurityGroupIdDelete, projectId, id);
        }
    }

    public static class IpEntity extends VmEntity {
        @Override
        protected int getPriority() {
            return 3;
        }
    }

    public static class VipEntity extends VmEntity {
        @Override
        protected int getPriority() {
            return 0;
        }
    }
}
