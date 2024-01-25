package ui.t1.tests.engine.vpc;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.*;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.vpc.Rule;
import ui.t1.pages.cloudEngine.vpc.SecurityGroup;
import ui.t1.pages.cloudEngine.vpc.SecurityGroupList;
import ui.t1.tests.engine.AbstractComputeTest;
import ui.t1.tests.engine.EntitySupplier;

import static core.utils.AssertUtils.assertHeaders;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Feature("Группы безопасности")
@Epic("Cloud Compute")
public class SecurityGroupTest extends AbstractComputeTest {

    private final EntitySupplier<String> securityGroupSup = lazy(() -> {
        String name = getRandomName();
        new IndexPage().goToSecurityGroups().addGroup(name, "desc");
        return name;
    });

    @Test
    @TmsLink("1263357")
    @Order(1)
    @DisplayName("Cloud VPC. Группы безопасности. Создание группы безопасности")
    void addSecurityGroup() {
        securityGroupSup.run();
    }

    @Test
    @Order(2)
    @TmsLink("982496")
    @DisplayName("Cloud VPC. Группы безопасности")
    void securityGroupList() {
        String securityGroup = securityGroupSup.get();
        new IndexPage().goToSecurityGroups();
        assertHeaders(new SecurityGroupList.SecurityGroupsTable(), "", "Наименование", "Описание", "Статус", "");
        new SecurityGroupList().selectGroup(securityGroup);
        assertHeaders(new SecurityGroup.RulesTable(), "Направление", "Тип", "Протокол", "Порт", "Тип назначения", "Назначение", "Статус", "Описание", "");
    }

    @Test
    @Order(3)
    @TmsLink("1113110")
    @DisplayName("Cloud VPC. Группы безопасности. Добавление правила")
    void addRule() {
        String securityGroup = securityGroupSup.get();
        new IndexPage().goToSecurityGroups().selectGroup(securityGroup).addRule()
                .setDestination(new Rule.CidrDestination("10.2.0.0/24"))
                .setSubnetType("IPv4")
                .setProtocol("UDP")
                .setOpenPorts(new Rule.Port(80))
                .setDescription(getRandomName())
                .setRoute("Входящее")
                .clickAdd();
    }

    @Test
    @Order(4)
    @TmsLink("1113123")
    @DisplayName("Cloud VPC. Группы безопасности. Удаление правила")
    void deleteRule() {
        String securityGroup = securityGroupSup.get();
        Rule rule = new IndexPage().goToSecurityGroups().selectGroup(securityGroup).addRule()
                .setDestination(new Rule.CidrDestination(true))
                .setSubnetType("IPv6")
                .setProtocol("TCP")
                .setOpenPorts(new Rule.AllPorts())
                .setDescription(getRandomName())
                .setRoute("Исходящее")
                .clickAdd();
        new SecurityGroup().deleteRule(rule.getDescription());
    }

    @Test
    @TmsLink("1280363")
    @Order(100)
    @DisplayName("Cloud VPC. Группы безопасности. Удаление групп безопасности")
    void deleteNetwork() {
        String securityGroup = securityGroupSup.get();
        new IndexPage().goToSecurityGroups().deleteGroup(securityGroup);
    }
}
