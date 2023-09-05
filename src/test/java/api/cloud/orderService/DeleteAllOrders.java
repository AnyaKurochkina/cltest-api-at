package api.cloud.orderService;

import api.Tests;
import core.enums.Role;
import core.helper.Configure;
import core.helper.http.Http;
import core.helper.http.Path;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import models.AbstractEntity;
import models.cloud.authorizer.Folder;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;
import steps.resourceManager.ResourceManagerSteps;
import ui.t1.tests.engine.AbstractComputeTest;

import java.util.*;

import static api.routes.OrderServiceApi.*;
import static api.routes.VpcApi.getNetworksApiV1ProjectsProjectNameNetworksGet;
import static api.routes.VpcApi.getSecurityGroupsApiV1ProjectsProjectNameSecurityGroupsGet;

@DisplayName("Тестовый набор по удалению всех заказов из проекта")
@Execution(ExecutionMode.CONCURRENT)
@Order(1)
@Tag("deleteorders")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DeleteAllOrders extends Tests {

    @ParameterizedTest(name = "{0}")
    @Tag("deleteAll")
    @Source(ProductArgumentsProvider.ENV)
    @DisplayName("Удаление всех успешных заказов из проекта")
    public void deleteOrders(String env) {
        OrderServiceSteps.deleteOrders(env, label -> label.startsWith("AT-API"));
    }

    @Test
    @DisplayName("Вывод всех ошибочных заказов")
    void printAllErrorOrders() {
        List<Order> orders = new ArrayList<>();
        List<String> projects = Arrays.asList("proj-ln4zg69jek", "proj-rddf0uwi0q", "proj-ahjjqmlgnm",
                "proj-bhbyhmik3a", "proj-zoz17np8rb", "proj-114wetem0c", "proj-1oob0zjo5h", "proj-6wpfrbes0g", "proj-aei4kz2yu4",
                "proj-lcwn3pwg7z", "proj-50duh5yxy6", "proj-xryy5l8ei5", "proj-yhi3rxo07h");
        if(Configure.ENV.equals("blue")) {
            projects = Arrays.asList("proj-iv550odo9a", "proj-3wgrmny2yu", "proj-td00y68hfk",
                    "proj-anw4ujlh5u", "proj-bw5aabeuw1", "proj-2xdbtyzqs3", "proj-lww1vo6okh", "proj-7w4eov3old", "proj-99p4fdfs5c",
                    "proj-ytwcbh7rlr", "proj-6sq3n30eh0","proj-fnxokdmi0b", "proj-i6ul07p131", "proj-pr0n40cx1e", "proj-0c0ki636z5","proj-p9b5mtehhq");
        }
        for (String projectId : projects) {
            OrderServiceSteps.getProductsWithStatus(projectId, "changing", "damaged", "failure", "pending", "locked")
                    .forEach(e -> orders.add(new Order(e, projectId)));
        }
        for (Order order : orders) {
            System.out.printf("%s/all/orders/%s/main?context=%s&type=project&org=vtb%n",
                    Configure.getAppProp("base.url"), order.id, order.projectId);
        }
    }

    @Test
    @DisplayName("Удаление всех успешных заказов T1 Engine")
    void deleteOrdersCompute() {
        Map<Path, Class<? extends AbstractComputeTest.ComputeEntity>> computeEntities = new HashMap<Path, Class<? extends AbstractComputeTest.ComputeEntity>>() {{
            put(getV1ProjectsProjectNameComputeInstances, AbstractComputeTest.InstanceEntity.class);
            put(getV1ProjectsProjectNameComputeVolumes, AbstractComputeTest.VolumeEntity.class);
            put(getV1ProjectsProjectNameComputeImages, AbstractComputeTest.ImageEntity.class);
            put(getV1ProjectsProjectNameComputeSnapshots, AbstractComputeTest.SnapshotEntity.class);
            put(getV1ProjectsProjectNameComputePublicIps, AbstractComputeTest.PublicIpEntity.class);
            put(getV1ProjectsProjectNameComputeVips, AbstractComputeTest.VipEntity.class);
            put(getV1ProjectsProjectNameComputeSnats, AbstractComputeTest.InstanceEntity.class);
        }};
        Map<Path, Class<? extends AbstractComputeTest.ComputeEntity>> vpcEntities = new HashMap<Path, Class<? extends AbstractComputeTest.ComputeEntity>>() {{
            put(getNetworksApiV1ProjectsProjectNameNetworksGet, AbstractComputeTest.NetworkEntity.class);
            put(getSecurityGroupsApiV1ProjectsProjectNameSecurityGroupsGet, AbstractComputeTest.SecurityGroupEntity.class);
        }};

        Folder folderPollProject = Folder.builder().title("ProjectPool").build().onlyGetObject();
        for (String projectId : ResourceManagerSteps.getChildren(folderPollProject.getName())) {
            for (Map.Entry<Path, Class<? extends AbstractComputeTest.ComputeEntity>> entry : computeEntities.entrySet()) {
                addComputeEntitiesForRemove(entry.getKey(), projectId, entry.getValue());
            }
            for (Map.Entry<Path, Class<? extends AbstractComputeTest.ComputeEntity>> entry : vpcEntities.entrySet()) {
                addVpcEntitiesForRemove(entry.getKey(), projectId, entry.getValue());
            }
        }
        AbstractEntity.deleteCurrentTestEntities();
    }

    @SneakyThrows
    private static void addComputeEntitiesForRemove(Path path, String projectId, Class<? extends AbstractComputeTest.ComputeEntity> computeEntity) {
        List<String> list = Http.builder()
                .setRole(Role.CLOUD_ADMIN)
                .api(path, projectId)
                .jsonPath()
                .getList("list.order_id");
        for (String id : list)
            AbstractEntity.addEntity(computeEntity.getDeclaredConstructor(String.class, String.class).newInstance(projectId, id));
    }

    @SneakyThrows
    private static void addVpcEntitiesForRemove(Path path, String projectId, Class<? extends AbstractComputeTest.ComputeEntity> computeEntity) {
        List<String> list = Http.builder()
                .setRole(Role.CLOUD_ADMIN)
                .api(path, projectId)
                .jsonPath()
                .getList("findAll{it.name != 'default'}.id");
        for (String id : list)
            AbstractEntity.addEntity(computeEntity.getDeclaredConstructor(String.class, String.class).newInstance(projectId, id));
    }

    @AllArgsConstructor
    private static class Order {
        String id;
        String projectId;
    }
}
