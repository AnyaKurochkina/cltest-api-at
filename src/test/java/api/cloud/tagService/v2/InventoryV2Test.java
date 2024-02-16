package api.cloud.tagService.v2;

import api.cloud.tagService.AbstractTagServiceTest;
import core.enums.Role;
import core.helper.http.AssertResponse;
import core.helper.http.QueryBuilder;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.cloud.tagService.*;
import models.cloud.tagService.v1.FilterResultV1;
import models.cloud.tagService.v1.InventoryTagsV1;
import models.cloud.tagService.v2.FilterResultV2Page;
import models.cloud.tagService.v2.InventoryTagListV2Page;
import models.cloud.tagService.v2.InventoryV2Page;
import models.cloud.tagService.v2.PutInventoryRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.authorizer.AuthorizerSteps;
import steps.keyCloak.KeyCloakSteps;

import java.util.*;
import java.util.stream.Collectors;

import static models.cloud.tagService.TagServiceSteps.inventoryTagsV1;

@Epic("Сервис тегов")
@Feature("Inventory тесты")
public class InventoryV2Test extends AbstractTagServiceTest {

    @Test
    @TmsLink("1664947")
    @DisplayName("Inventory V2. Delete batch.")
    void inventoriesDeleteBatch() {
        List<Inventory> inventories = new ArrayList<>();
        Project project = Project.builder().projectName("API. inventoriesDeleteBatch").build().createObject();
        Context context = Context.byId(project.getId());
        for (int i = 0; i < 3; i++)
            inventories.add(Inventory.builder().context(context).build().createObjectPrivateAccess());
        TagServiceSteps.inventoriesDeleteBatchV2(context, inventories.stream().map(Inventory::getId).collect(Collectors.toList()));
        InventoryV2Page inventoryList = TagServiceSteps.inventoriesListV2(context, new QueryBuilder().add("with_deleted", false));
        Assertions.assertEquals(0, inventoryList.stream()
                .filter(e -> Objects.nonNull(e.getContextPath()))
                .filter(e -> !e.getContextPath().isEmpty())
                .count(), "Неверное кол-во inventories");
    }

    @Test
    @TmsLink("1664951")
    @DisplayName("Inventory V2. Inventories list. with_deleted")
    void inventoriesList() {
        InventoryV2Page inventoriesWithoutDeleted = TagServiceSteps.inventoriesListV2(context, new QueryBuilder().add("with_deleted", false));
        InventoryV2Page inventoriesWithDeleted = TagServiceSteps.inventoriesListV2(context, new QueryBuilder().add("with_deleted", true));
        Assertions.assertTrue(inventoriesWithoutDeleted.getMeta().getTotalCount() <
                inventoriesWithDeleted.getMeta().getTotalCount(), "Неверное кол-во inventories");
    }

    @Test
    @TmsLink("")
    @DisplayName("Inventory V2. Создание с dataSources")
    void createWidthDataSources() {
        Inventory.builder().context(context).dataSources(Collections.singletonList("state_service")).build().createObjectPrivateAccess();
    }

    @Test
    @TmsLink("")
    @DisplayName("Inventory V2. Создание с securityPrincipals")
    void createWidthSecurityPrincipals() {
        Inventory inventory = Inventory.builder().context(context)
                .securityPrincipals(Collections.singletonList("cloud_day2_roles:test-admin1")).build().createObjectPrivateAccess();
        Inventory inventoryIncorrect = Inventory.builder().context(context)
                .securityPrincipals(Collections.singletonList("cloud_day2_roles:incorrect")).build().createObjectPrivateAccess();
        checkImpersonateFilterTest(inventory, inventoryIncorrect);
    }

    @Test
    @TmsLink("")
    @DisplayName("Inventory V2. Создание с managers")
    void createWidthSecurityManagers() {
        Inventory inventory = Inventory.builder().context(context)
                .managers(Collections.singletonList("qa-admin1")).build().createObjectPrivateAccess();
        Inventory inventoryIncorrect = Inventory.builder()
                .context(context).managers(Collections.singletonList("incorrect")).build().createObjectPrivateAccess();
        checkImpersonateFilterTest(inventory, inventoryIncorrect);
    }

    private void checkImpersonateFilterTest(Inventory inventory, Inventory inventoryIncorrect) {
        Filter filter = Filter.builder().allowEmptyTagFilter(true).impersonate(KeyCloakSteps.getUserInfo(Role.CLOUD_ADMIN))
                .inventoryPks(Arrays.asList(inventory.getId(), inventoryIncorrect.getId())).build();
        FilterResultV2Page filterResult = TagServiceSteps.inventoryFilterV2(context, filter);

        Assertions.assertEquals(1, filterResult.getList().size(), "Неверное кол-во inventories");
        Assertions.assertEquals(inventory.getId(), filterResult.getList().get(0).getInventory(), "Неверный inventory");
    }

    @Test
    @TmsLink("1664955")
    @DisplayName("Inventory V2. GET inventory-tags")
    void inventoryTagsList() {
        String tagValue = "inventoryTagsList";
        List<Tag> tags = generateTags(2);
        Inventory inventory = generateInventories(1).get(0);
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tags.get(0).getId()).inventory(inventory.getId()).value(tagValue).build());
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tags.get(1).getId()).inventory(inventory.getId()).value(tagValue).build());
        InventoryTagListV2Page inventoryTagList = TagServiceSteps.inventoryTagListV2(context, inventory.getId());
        Assertions.assertAll("Проверка inventoryTagList",
                () -> Assertions.assertEquals(2, inventoryTagList.getMeta().getTotalCount()),
                () -> Assertions.assertEquals(2, inventoryTagList.stream().map(e -> e.getInventory().getId().equals(inventory.getId())).count()),
                () -> Assertions.assertTrue(inventoryTagList.stream().anyMatch(e -> e.getTag().equals(tags.get(0)))),
                () -> Assertions.assertTrue(inventoryTagList.stream().anyMatch(e -> e.getTag().equals(tags.get(1)))));
    }

    @Test
    @TmsLink("1664957")
    @DisplayName("Inventory V2. Delete")
    void inventoriesDelete() {
        Inventory inventory = generateInventories(1).get(0);
        inventory.delete();
        AssertResponse.run(() -> TagServiceSteps.inventoryTagListV2(context, inventory.getId())).status(404);
    }

    @Test
    @TmsLink("1664958")
    @DisplayName("Inventory V2. Replace ContextPatch PUT")
    void inventoryReplaceContextPatchPut() {
        String parentFolderId = AuthorizerSteps.getParentProject(project.getId());
        Context newContext = Context.byId(parentFolderId);
        Inventory inventory = Inventory.builder().context(newContext).build().createObjectPrivateAccess();
        inventory.setContextPath(context.getContextPath());
        TagServiceSteps.updateV2Put(inventory);
        Inventory response = TagServiceSteps.getInventoryV2(newContext, inventory.getId());
        Assertions.assertEquals(context.getContextPath(), response.getContextPath());
    }

    @Test
    @TmsLink("1664959")
    @DisplayName("Inventory V2. Replace ContextPatch PATH")
    void inventoryReplaceContextPatch() {
        String parentFolderId = AuthorizerSteps.getParentProject(project.getId());
        Context newContext = Context.byId(parentFolderId);
        Inventory inventory = Inventory.builder().context(newContext).build().createObjectPrivateAccess();
        inventory.setContextPath(context.getContextPath());
        TagServiceSteps.updateV2Path(inventory);
        Inventory response = TagServiceSteps.getInventoryV2(newContext, inventory.getId());
        Assertions.assertEquals(context.getContextPath(), response.getContextPath());
    }

    @Test
    @TmsLink("1664960")
    @DisplayName("Inventory V2. Массовое обновление объектов инфраструктуры")
    void updateInventories() {
        String tagValue = "updateInventories";
        String parentFolderId = AuthorizerSteps.getParentProject(project.getId());
        Context newContext = Context.byId(parentFolderId);
        Inventory inventoryOne = Inventory.builder().context(newContext).build().createObjectPrivateAccess();
        Inventory inventoryTwu = Inventory.builder().context(newContext).build().createObjectPrivateAccess();
        inventoryTwu.delete();
        Tag tag = generateTags(1).get(0);

        PutInventoryRequest.PutInventory putInventoryOne = PutInventoryRequest.PutInventory.builder()
                .contextPath(context.getContextPath())
                .id(inventoryOne.getId())
                .skipDefects(true)
                .tag(new PutInventoryRequest.PutInventory.Tag(tag.getKey(), tagValue, "base_cloud_attrs"))
                .build();

        PutInventoryRequest.PutInventory putInventoryTwo = PutInventoryRequest.PutInventory.builder()
                .contextPath(context.getContextPath())
                .id(inventoryTwu.getId())
                .skipDefects(false)
                .build();

        PutInventoryRequest.PutInventory putInventoryThree = PutInventoryRequest.PutInventory.builder()
                .contextPath(context.getContextPath())
                .id(UUID.randomUUID().toString())
                .skipDefects(false)
                .build();

        PutInventoryRequest request = PutInventoryRequest.builder()
                .inventory(putInventoryThree).inventory(putInventoryTwo).inventory(putInventoryOne).inventory(putInventoryOne)
                .build();

        List<PutInventoryRequest.PutInventory> putInventories = TagServiceSteps.updateInventoriesV2(request);
        Assertions.assertEquals(Collections.singletonList(putInventoryOne), putInventories);

        Filter filter = Filter.builder()
                .responseTags(Collections.singletonList(tag.getKey()))
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tag.getKey(), Collections.singletonList(tagValue))))
                .build();
        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter);
        Assertions.assertEquals(1, filterResult.getList().get(0).getTags().size());
    }

    @Test
    @TmsLink("")
    @DisplayName("Inventory V2. Create. Dictionary не найден")
    void notFoundDictionary() {
        AssertResponse.run(() -> Inventory.builder().context(context).objectType("not_found").build().createObjectPrivateAccess())
                .status(400)
                .responseContains("Данные не найдены: {'internal_name': 'not_found'}");
    }
}
