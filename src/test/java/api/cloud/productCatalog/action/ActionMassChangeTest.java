package api.cloud.productCatalog.action;

import core.helper.http.AssertResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.action.Action;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static core.helper.StringUtils.format;
import static core.helper.StringUtils.getRandomStringApi;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ActionSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionMassChangeTest extends ActionBaseTest {

    @DisplayName("Массовое изменение параметра is_for_items у действий")
    @TmsLink("SOUL-8338")
    @Test
    public void massChangeActionTest() {
        Action action1 = createAction(createActionModel("action1_mass_change_test_api"));
        Action action2 = createAction(createActionModel("action2_mass_change_test_api"));
        Action action3 = createAction(createActionModel("action3_mass_change_test_api"));
        Action action4 = createAction(createActionModel("action4_mass_change_test_api"));
        Action action5 = createAction(createActionModel("action5_mass_change_test_api"));
        List<String> actionIdList = Arrays.asList(action1.getId(), action2.getId(), action3.getId(),
                action4.getId(), action5.getId());
        massChangeActionParam(actionIdList, false);
        actionIdList.forEach(x -> assertFalse(getActionById(x).getIsForItems()));
        massChangeActionParam(actionIdList, true);
        actionIdList.forEach(x -> assertTrue(getActionById(x).getIsForItems()));
    }

    @DisplayName("Массовое изменение параметра is_for_items у несуществующего действия")
    @TmsLink("SOUL-8674")
    @Test
    public void massChangeNotExistActionTest() {
        Action action1 = createAction(getRandomStringApi(6));
        String notExistActionUUID = UUID.randomUUID().toString();
        List<String> actionIdList = Arrays.asList(action1.getId(), notExistActionUUID);

        AssertResponse.run(() -> massChangeActionParam(actionIdList, true)).status(400)
                .responseContains(format("Object Action with id={} does not exists", notExistActionUUID));
    }
}
