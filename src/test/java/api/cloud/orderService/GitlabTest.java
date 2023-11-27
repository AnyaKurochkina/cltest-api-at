package api.cloud.orderService;

import com.google.gson.Gson;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.GitLab;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;
import api.Tests;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;


@Epic("Продукты")
@Feature("GitLab")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("gitlab"), @Tag("prod")})
@DisabledIfEnv("ift")
@Disabled
@Deprecated
public class GitlabTest extends Tests {
    private static final GitLab.Project project = GitLab.Project.builder().name("project").visibility("private").description("desc").build();

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099086")
    @ParameterizedTest(name = "[{1}] Создание {0}")
    void create(GitLab product, Integer num) {
        //noinspection EmptyTryBlock
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {}
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099087")
    @ParameterizedTest(name = "[{1}] Создать CI/CD переменную для GitLab группы {0}")
    void createVariable(GitLab product, Integer num) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.createVariable(GitLab.Variable.builder().key("key001").value("value001").build());
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099294")
    @ParameterizedTest(name = "[{1}] Обновить переменную для GitLab группы {0}")
    void updateVariable(GitLab product, Integer num) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.createVariable(GitLab.Variable.builder().key("key002").value("value001").build());
            gitLab.updateVariable(GitLab.Variable.builder().key("key002").value("value002").build());
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099295")
    @ParameterizedTest(name = "[{1}] Удалить переменную для GitLab группы {0}")
    void deleteVariable(GitLab product, Integer num) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.createVariable(GitLab.Variable.builder().key("key003").value("value001").build());
            gitLab.deleteVariable(GitLab.Variable.builder().key("key003").build());
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099296")
    @ParameterizedTest(name = "[{1}] Добавить участника к группе GitLab {0}")
    void addUser(GitLab product, Integer num) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.addUser("vtb4043675", "developer");
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099297")
    @ParameterizedTest(name = "[{1}] Изменить пользователя в группе GitLab {0}")
    void updateUser(GitLab product, Integer num) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            String userName = "vtb4050570";
            gitLab.addUser(userName, "developer");
            JSONObject user = new JSONObject(new Gson().toJson(OrderServiceSteps.getProductsField(gitLab,
                    String.format("data.find{it.data.acls.find{it.username=='%s'}}.data.acls.find{it.username=='%s'}", userName, userName), Map.class)));
            gitLab.updateUser(user, "guest");
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099298")
    @ParameterizedTest(name = "[{1}] Удалить пользователя из группы GitLab {0}")
    void deleteUser(GitLab product, Integer num) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            String userName = "vtb4057583";
            gitLab.addUser(userName, "developer");
            JSONObject user = new JSONObject(new Gson().toJson(OrderServiceSteps.getProductsField(gitLab,
                    String.format("data.find{it.data.acls.find{it.username=='%s'}}.data.acls.find{it.username=='%s'}", userName, userName), Map.class)));
            gitLab.deleteUser(user);
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099299")
    @ParameterizedTest(name = "[{1}] Создать проект GitLab {0}")
    void createProject(GitLab product, Integer num) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.createProject(project);
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099300")
    @ParameterizedTest(name = "[{1}] Создать CI/CD переменную для GitLab проекта {0}")
    void createProjectVariable(GitLab product, Integer num) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.createProject(project);
            gitLab.createProjectVariable(GitLab.Variable.builder().key("key001p").value("value001p").build());
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099301")
    @ParameterizedTest(name = "[{1}] Обновить переменную для GitLab проекта {0}")
    void updateProjectVariable(GitLab product, Integer num) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.createProject(project);
            gitLab.createProjectVariable(GitLab.Variable.builder().key("key002p").value("value002p").build());
            gitLab.updateProjectVariable(GitLab.Variable.builder().key("key002p").value("value003p").build());
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099302")
    @ParameterizedTest(name = "[{1}] Удалить переменную для GitLab проекта {0}")
    void deleteProjectVariable(GitLab product, Integer num) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.createProject(project);
            gitLab.createProjectVariable(GitLab.Variable.builder().key("key003p").value("value003p").build());
            gitLab.deleteProjectVariable(GitLab.Variable.builder().key("key003p").build());
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099303")
    @ParameterizedTest(name = "[{1}] Создать токен для GitLab проекта {0}")
    void createProjectToken(GitLab product, Integer num) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.createProject(project);
            gitLab.createProjectToken("token1", Arrays.asList("api", "read_api"));
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099304")
    @ParameterizedTest(name = "[{1}] Отозвать токен для GitLab проекта {0}")
    void deleteProjectToken(GitLab product, Integer num) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            String tokenName = "token2";
            gitLab.createProject(project);
            gitLab.createProjectToken(tokenName, Collections.singletonList("api"));
            JSONObject token = new JSONObject(new Gson().toJson(OrderServiceSteps.getProductsField(gitLab,
                    String.format("data.find{it.data.config.tokens.find{it.name=='%s'}}.data.config.tokens.find{it.name=='%s'}", tokenName, tokenName), Map.class)));
            gitLab.deleteProjectToken(token);
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099305")
    @ParameterizedTest(name = "[{1}] Удалить проект GitLab {0}")
    void deleteProject(GitLab product, Integer num) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.createProject(project);
            gitLab.deleteProject(project);
        }
    }

    @TmsLink("1099306")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удаление {0}")
    @MarkDelete
    void delete(GitLab product, Integer num) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.deleteObject();
        }
    }
}
