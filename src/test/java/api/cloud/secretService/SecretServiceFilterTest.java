package api.cloud.secretService;

import api.cloud.secretService.models.*;
import api.cloud.secretService.steps.SecretServiceAdminSteps;
import com.mifmif.common.regex.Generex;
import core.exception.NotFoundElementException;
import core.helper.http.QueryBuilder;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

@Feature("Сервис секретов")
@Epic("Список секретов")
public class SecretServiceFilterTest extends AbstractSecretTest {

    @Test
    @TmsLink("")
    @DisplayName("Фильтр по uri")
    void getV1SecretsByUri() {
        SecretResponse secretResponse = generateSecret();
        SecretResponsePage secretResponses = SecretServiceAdminSteps.getV1Secrets(new QueryBuilder().add("f[uri]", secretResponse.getUri()));
        Assertions.assertEquals(1, secretResponses.getList().size());
        Assertions.assertEquals(secretResponse, secretResponses.getList().get(0));
    }

    @Test
    @TmsLink("")
    @DisplayName("Фильтр по tags")
    void getV1SecretsByTags() {
        SecretResponse secretResponse = generateSecret();
        SecretResponsePage secretResponses = SecretServiceAdminSteps.getV1Secrets(new QueryBuilder().add("f[tags][]", secretResponse.getTags().get(0)));
        Assertions.assertEquals(1, secretResponses.getList().size());
        Assertions.assertEquals(secretResponse, secretResponses.getList().get(0));
    }

    @Test
    @TmsLink("")
    @DisplayName("Фильтр по author")
    void getV1SecretsByAuthor() {
        SecretResponse secretResponse = generateSecret();
        AccessRuleResponsePage ruleResponsePage = SecretServiceAdminSteps.getV1SecretsSecretIdAccessRules(secretResponse.getId());
        SecretResponsePage secretResponses = SecretServiceAdminSteps.getV1Secrets(new QueryBuilder()
                .add("f[author]", ruleResponsePage.getList().get(0).getEmail())
                .add("include", "with_author"));
        Assertions.assertTrue(secretResponses.stream().allMatch(e -> e.getAuthor().getEmail().equals(ruleResponsePage.getList().get(0).getEmail())));
    }

    @Test
    @TmsLink("")
    @DisplayName("Фильтр по exclude_tags & uri")
    void getV1SecretsByExcludeTags() {
        String tag1 = new Generex("TAG" + "-[a-z]{5}").random();
        String tag2 = new Generex("TAG" + "-[a-z]{5}").random();
        EnginePage engineList = SecretServiceAdminSteps.getV1Engines(new QueryBuilder());
        Secret secret1 = Secret.builder()
                .uri(new Generex("AT" + "-[a-z]{8}").random())
                .engineId(engineList.getList().get(0).getId())
                .tag(tag1)
                .tag(tag2)
                .build();
        Secret secret2 = Secret.builder()
                .uri(new Generex("AT" + "-[a-z]{8}").random())
                .engineId(engineList.getList().get(0).getId())
                .tag(tag2)
                .build();
        SecretServiceAdminSteps.postV1Secrets(secret1);
        SecretResponse secretResponse2 = SecretServiceAdminSteps.postV1Secrets(secret2);

        SecretResponsePage secretResponses = SecretServiceAdminSteps.getV1Secrets(new QueryBuilder()
                .add("f[tags][]", tag2)
                .add("f[exclude_tags][]", tag1));

        Assertions.assertEquals(1, secretResponses.getList().size());
        Assertions.assertEquals(secretResponse2, secretResponses.getList().get(0));
    }

    @Test
    @TmsLink("")
    @DisplayName("Список секретов c write_access")
    void getV1SecretsWidthWriteAccess() {
        SecretResponse secretResponse = generateSecret();
        SecretResponsePage secretResponses = SecretServiceAdminSteps.getV1Secrets(new QueryBuilder().add("include", "write_access"));
        SecretResponse getSecretResponse = secretResponses.stream().filter(e -> e.getUri().equals(secretResponse.getUri())).findFirst()
                .orElseThrow(() -> new NotFoundElementException(secretResponse.getUri()));
        Assertions.assertTrue(getSecretResponse.getWriteAccess());
    }

    @Test
    @TmsLink("")
    @DisplayName("Список секретов c available_for_current_user")
    void getV1SecretsWidthAvailableForCurrentUser() {
         generateSecret();
        SecretResponsePage secretResponses = SecretServiceAdminSteps.getV1Secrets(new QueryBuilder().add("include", "available_for_current_user"));
        Assertions.assertTrue(secretResponses.stream().allMatch(e -> Objects.nonNull(e.getAvailable())));
    }
}
