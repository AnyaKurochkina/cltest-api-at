package api.cloud.secretService;

import api.Tests;
import com.mifmif.common.regex.Generex;
import api.cloud.secretService.models.EnginePage;
import api.cloud.secretService.models.Secret;
import api.cloud.secretService.models.SecretResponse;
import org.junit.jupiter.api.Tag;
import api.cloud.secretService.steps.SecretServiceAdminSteps;

@Tag("secret_service")
public class AbstractSecretTest extends Tests {

    protected SecretResponse generateSecret() {
        EnginePage engineList = SecretServiceAdminSteps.getV1Engines();
        Secret secret = Secret.builder()
                .uri(new Generex("AT" + "-[a-z]{8}").random())
                .engineId(engineList.getList().get(0).getId())
                .tag(new Generex("TAG" + "-[a-z]{4}").random())
                .build();
        return SecretServiceAdminSteps.postV1Secrets(secret);
    }
}
