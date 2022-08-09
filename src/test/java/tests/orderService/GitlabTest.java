package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.GitLab;
import org.json.JSONObject;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;
import tests.Tests;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;


@Epic("Продукты")
@Feature("GitLab")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("gitlab"), @Tag("prod")})
public class GitlabTest extends Tests {
    private static final GitLab.Project project = GitLab.Project.builder().name("project").visibility("private").description("desc").build();

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099086")
    @ParameterizedTest(name = "Создание {0}")
    void create(GitLab product) {
        //noinspection EmptyTryBlock
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {}
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099087")
    @ParameterizedTest(name = "Создать CI/CD переменную для GitLab группы {0}")
    void createVariable(GitLab product) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.createVariable(GitLab.Variable.builder().key("key001").value("value001").build());
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099294")
    @ParameterizedTest(name = "Обновить переменную для GitLab группы {0}")
    void updateVariable(GitLab product) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.createVariable(GitLab.Variable.builder().key("key002").value("value001").build());
            gitLab.updateVariable(GitLab.Variable.builder().key("key002").value("value002").build());
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099295")
    @ParameterizedTest(name = "Удалить переменную для GitLab группы {0}")
    void deleteVariable(GitLab product) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.createVariable(GitLab.Variable.builder().key("key003").value("value001").build());
            gitLab.deleteVariable(GitLab.Variable.builder().key("key003").build());
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099296")
    @ParameterizedTest(name = "Добавить участника к группе GitLab {0}")
    void addUser(GitLab product) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.addUser("vtb4043675", "developer");
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099297")
    @ParameterizedTest(name = "Изменить пользователя в группе GitLab {0}")
    void updateUser(GitLab product) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            String userName = "vtb4050570";
            gitLab.addUser(userName, "developer");
            JSONObject user = new JSONObject(OrderServiceSteps.getProductsField(gitLab,
                    String.format("data.find{it.data.acls.find{it.username=='%s'}}.data.acls.find{it.username=='%s'}", userName, userName), Map.class));
            gitLab.updateUser(user, "guest");
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099298")
    @ParameterizedTest(name = "Удалить пользователя из группы GitLab {0}")
    void deleteUser(GitLab product) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            String userName = "vtb4057583";
            gitLab.addUser(userName, "developer");
            JSONObject user = new JSONObject(OrderServiceSteps.getProductsField(gitLab,
                    String.format("data.find{it.data.acls.find{it.username=='%s'}}.data.acls.find{it.username=='%s'}", userName, userName), Map.class));
            gitLab.deleteUser(user);
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099299")
    @ParameterizedTest(name = "Создать проект GitLab {0}")
    void createProject(GitLab product) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.createProject(project);
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099300")
    @ParameterizedTest(name = "Создать CI/CD переменную для GitLab проекта {0}")
    void createProjectVariable(GitLab product) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.createProject(project);
            gitLab.createProjectVariable(GitLab.Variable.builder().key("key001p").value("value001p").build());
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099301")
    @ParameterizedTest(name = "Обновить переменную для GitLab проекта {0}")
    void updateProjectVariable(GitLab product) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.createProject(project);
            gitLab.createProjectVariable(GitLab.Variable.builder().key("key002p").value("value002p").build());
            gitLab.updateProjectVariable(GitLab.Variable.builder().key("key002p").value("value003p").build());
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099302")
    @ParameterizedTest(name = "Удалить переменную для GitLab проекта {0}")
    void deleteProjectVariable(GitLab product) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.createProject(project);
            gitLab.createProjectVariable(GitLab.Variable.builder().key("key003p").value("value003p").build());
            gitLab.deleteProjectVariable(GitLab.Variable.builder().key("key003p").build());
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099303")
    @ParameterizedTest(name = "Создать токен для GitLab проекта {0}")
    void createProjectToken(GitLab product) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.createProject(project);
            gitLab.createProjectToken("token1", Arrays.asList("api", "read_api"));
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099304")
    @ParameterizedTest(name = "Отозвать токен для GitLab проекта {0}")
    void deleteProjectToken(GitLab product) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            String tokenName = "token2";
            gitLab.createProject(project);
            gitLab.createProjectToken(tokenName, Collections.singletonList("api"));
            JSONObject token = new JSONObject(OrderServiceSteps.getProductsField(gitLab,
                    String.format("data.find{it.data.config.tokens.find{it.name=='%s'}}.data.config.tokens.find{it.name=='%s'}", tokenName, tokenName), Map.class));
            gitLab.deleteProjectToken(token);
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("1099305")
    @ParameterizedTest(name = "Удалить проект GitLab {0}")
    void deleteProject(GitLab product) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.createProject(project);
            gitLab.deleteProject(project);
        }
    }

    @TmsLink("1099306")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление {0}")
    @MarkDelete
    void delete(GitLab product) {
        try (GitLab gitLab = product.createObjectExclusiveAccess()) {
            gitLab.deleteObject();
        }
    }
}
