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
import models.cloud.authorizer.Project;
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

import static api.routes.OrderServiceApi.deleteV1ProjectsProjectNameOrdersId;
import static api.routes.VpcApi.deleteNetworkApiV1ProjectsProjectNameNetworksNetworkIdDelete;
import static api.routes.VpcApi.deleteSecurityGroupApiV1ProjectsProjectNameSecurityGroupsSecurityGroupIdDelete;

@Log4j2
@ExtendWith(ConfigExtension.class)
@Tags({@Tag("t1_ui_cloud_compute"), @Tag("t1")})
public abstract class AbstractComputeTest extends Tests {
    protected static Project project = Project.builder().isForOrders(true).build().createObject();
    protected String availabilityZone = "ru-central1-a";
    protected SelectBox.Image image = new SelectBox.Image("Ubuntu", "20.04");
    protected String hddTypeFirst = "HDD";
    protected String hddTypeSecond = "HDD";
    protected String securityGroup = "default";
    protected String flavorName = "Intel";
    protected String region = "ru-central1";
    private final String entitiesPrefix = "AT-" + this.getClass().getSimpleName();
    protected static final String sshKey = "AT-default";

    public AbstractComputeTest() {
        if (!Configure.ENV.equals("t1prod"))
            availabilityZone = "ru-central1-c";
    }

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new T1LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
        Waiting.sleep(3000);
    }

    @BeforeAll
    public static void beforeAll() {
        new T1LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
        new IndexPage().goToSshKeys().addKey(sshKey, "root");
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

        public VmEntity() {
            super(StringUtils.findByRegex("orders/([^/]*)/", WebDriverRunner.getWebDriver().getCurrentUrl()));
        }
        @Override
        public void delete() {
            Http.builder().setRole(Role.CLOUD_ADMIN).api(deleteV1ProjectsProjectNameOrdersId, projectId, id);
            Waiting.sleep(30000);
        }
    }

    public static class DiskEntity extends VmEntity {
        @Override
        protected int getPriority() {
            return 1;
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
            return 1;
        }

        @Override
        public void delete() {
            Http.builder().setRole(Role.CLOUD_ADMIN).api(deleteSecurityGroupApiV1ProjectsProjectNameSecurityGroupsSecurityGroupIdDelete, projectId, id);
        }
    }

    public static class PublicIpEntity extends VmEntity {
        @Override
        protected int getPriority() {
            return 2;
        }
    }
}
