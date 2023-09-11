package api.cloud.tagService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.tagService.Filter;
import models.cloud.tagService.TagServiceSteps;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.Isolated;

@Isolated
@Epic("Сервис тегов")
@Feature("Изолированные тесты")
@Execution(ExecutionMode.SAME_THREAD)
public class IsolatedInventoryTest extends AbstractTagServiceTest {

    @Test
    @org.junit.jupiter.api.Tag("health_check")
    @TmsLink("1623789")
    @DisplayName("Inventory. Фильтр v1 по context_path_isnull")
    void findInventoriesByContextPathIsnullV1() {
        Filter filterWidthNullPath = Filter.builder()
                .contextPathIsnull(true)
                .allowEmptyTagFilter(true)
                .build();
        Filter filterWithoutNullPath = Filter.builder()
                .contextPathIsnull(false)
                .allowEmptyTagFilter(true)
                .build();
        Assertions.assertTrue(TagServiceSteps.inventoryFilterV1(context, filterWidthNullPath).getMeta().getTotalCount() >
                TagServiceSteps.inventoryFilterV1(context, filterWithoutNullPath).getMeta().getTotalCount(), "(contextPathIsnull = true) <= (contextPathIsnull = false)");
    }

    @Test
    @TmsLink("1623685")
    @DisplayName("Inventory. Фильтр V2 по context_path_isnull")
    void findInventoriesByContextPathIsnullV2() {
        Filter filterWidthNullPath = Filter.builder()
                .contextPathIsnull(true)
                .allowEmptyTagFilter(true)
                .build();
        Filter filterWithoutNullPath = Filter.builder()
                .contextPathIsnull(false)
                .allowEmptyTagFilter(true)
                .build();
        Assertions.assertTrue(TagServiceSteps.inventoryFilterV2(context, filterWidthNullPath).getMeta().getTotalCount() >
                TagServiceSteps.inventoryFilterV2(context, filterWithoutNullPath).getMeta().getTotalCount(), "(contextPathIsnull = true) <= (contextPathIsnull = false)");
    }
}
