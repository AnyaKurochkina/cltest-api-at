package api.cloud.defectolog;

import api.cloud.defectolog.models.DefectPage;
import api.cloud.defectolog.models.StartTask;
import api.cloud.defectolog.steps.DefectologSteps;
import api.cloud.tagService.AbstractTagServiceTest;
import core.exception.NotFoundElementException;
import core.utils.Waiting;
import io.qameta.allure.Step;
import models.cloud.tagService.Context;
import models.cloud.tagService.Filter;
import models.cloud.tagService.Inventory;
import models.cloud.tagService.TagServiceSteps;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Tags({@Tag("regress"), @Tag("defectolog")})
@DisabledIfEnv("prod")
public class AbstractDefectologTest extends AbstractTagServiceTest {

    protected List<Inventory> generateInventories(int count, Context context) {
        List<Inventory> inventories = new ArrayList<>();
        for (int i = 0; i < count; i++)
            inventories.add(Inventory.builder().context(context).objectType("vm").build().createObjectPrivateAccess());
        return inventories;
    }

    @Step("Поиск дефекта {internalName} после {dateFrom}")
    protected int findDefectIdByInternalName(String internalName, ZonedDateTime dateFrom) {
        return DefectologSteps.defectsList().stream()
                .filter(e -> e.getCreatedAt().isAfter(dateFrom))
                .filter(e -> e.getGroup().getInternalName().equals(internalName))
                .findFirst()
                .orElseThrow(NotFoundElementException::new).getId();
    }

    @Step("Получение времени отсчета на основе времени создания Inventory {inventory.id}")
    protected ZonedDateTime getDateFromFilter(Inventory inventory, Context context) {
        Filter filter = Filter.builder().allowEmptyTagFilter(true)
                .inventoryPks(Collections.singletonList(inventory.getId())).build();
        return ZonedDateTime.of(TagServiceSteps.inventoryFilterV2(context, filter).getList().get(0)
                .getCreatedAt().minusSeconds(1), ZoneId.of("UTC"));
    }

    protected DefectPage readDefectPage(int defectId) {
        Waiting.sleep(10000);
        int pageId = DefectologSteps.defectsRead(defectId).getDefectPages().get(0).getId();
        return DefectologSteps.defectPagesRead(pageId);
    }

    @Step("Запуск поиска дефектов {taskValidators}")
    protected void startTaskWidthGroups(String ... taskValidators){
        StartTask task = StartTask.builder().kwargsParam(StartTask.KwargsParam.builder()
                .taskValidators(Arrays.asList(taskValidators)).build()).build();
        Assertions.assertTrue(DefectologSteps.tasksCreate(task));
    }
}
