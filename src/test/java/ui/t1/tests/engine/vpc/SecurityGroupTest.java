package ui.t1.tests.engine.vpc;

import io.qameta.allure.TmsLink;
import org.junit.BlockTests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.extesions.InterceptTestExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.vpc.Rule;
import ui.t1.pages.cloudEngine.vpc.SecurityGroup;
import ui.t1.pages.cloudEngine.vpc.SecurityGroupList;
import ui.t1.tests.engine.AbstractComputeTest;

import static core.utils.AssertUtils.AssertHeaders;

@BlockTests
@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SecurityGroupTest extends AbstractComputeTest {
    String name = getRandomName();

    @Test
    @TmsLink("1263357")
    @Order(1)
    @DisplayName("Cloud VPC. Группы безопасности. Создание группы безопасности")
    void addSecurityGroup() {
        new IndexPage().goToSecurityGroups().addGroup(name, "desc");
    }

    @Test
    @Order(2)
    @TmsLink("982496")
    @DisplayName("Cloud VPC. Группы безопасности")
    void securityGroupList() {
        new IndexPage().goToSecurityGroups();
        AssertHeaders(new SecurityGroupList.SecurityGroupsTable(), "", "Наименование", "Описание", "Статус", "");
        new SecurityGroupList().selectGroup(name);
        AssertHeaders(new SecurityGroup.RulesTable(), "Направление", "Тип", "Протокол", "Порт", "Тип назначения", "Назначение", "Статус", "Описание", "");
    }

    @Test
    @Order(3)
    @TmsLink("1113110")
    @DisplayName("Cloud VPC. Группы безопасности. Добавление правила")
    void addRule() {
        new IndexPage().goToSecurityGroups().selectGroup(name).addRule()
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
        Rule rule = new IndexPage().goToSecurityGroups().selectGroup(name).addRule()
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
        new IndexPage().goToSecurityGroups().deleteGroup(name);
    }
}
