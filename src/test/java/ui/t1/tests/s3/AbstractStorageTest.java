package ui.t1.tests.s3;

import api.Tests;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import io.qameta.allure.Epic;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.extesions.ConfigExtension;
import ui.t1.pages.T1LoginPage;

import java.util.Locale;

@Log4j2
@ExtendWith(ConfigExtension.class)
@Epic("Cloud Storage")
@Tags({@Tag("t1_ui_s3")})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractStorageTest extends Tests {

    protected static String projectId;
    private final String entitiesPrefix = "AT-" + this.getClass().getSimpleName();
    protected String name;

    public AbstractStorageTest() {
        Project project = Project.builder().isForOrders(true).build().createObject();
        projectId = project.getId();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new T1LoginPage(projectId)
                .signIn(Role.CLOUD_ADMIN);
        name = getRandomBucketName();
    }

    protected String getRandomBucketName() {
        return new Generex(entitiesPrefix + "-[a-z]{6}").random().toLowerCase(Locale.ROOT);
    }
}
