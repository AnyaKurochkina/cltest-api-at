package ui.t1.tests;

import api.Tests;
import core.enums.Role;
import core.exception.NotFoundElementException;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.extesions.ConfigExtension;
import ui.t1.pages.T1LoginPage;

@ExtendWith(ConfigExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractT1Test extends Tests {

    protected Project getProject() {
        return Project.builder().isForOrders(true).build().createObject();
    }

    /**
     * Данный метод авторизуется на портале с заданной ролью
     * для авторизации необходимо в наследнике пометить тест аннотацией @WithAuthorization,
     * либо повесить аннотацию на весь класс применив авторизацию ко всем тестам
     * Например: @WithAuthorization(role = Role.SUPERADMIN)
     */
    @BeforeEach
    public void auth(TestInfo info) {
        if (info.getTestMethod().orElseThrow(NotFoundElementException::new).isAnnotationPresent(WithAuthorization.class)) {
            Role role = info.getTestMethod().get()
                    .getAnnotation(WithAuthorization.class).value();
            new T1LoginPage(getProject().getId()).signIn(role);
        } else if (info.getTestMethod().get().getDeclaringClass().isAnnotationPresent(WithAuthorization.class)) {
            Role role = info.getTestMethod().get().getDeclaringClass()
                    .getAnnotation(WithAuthorization.class).value();
            new T1LoginPage(getProject().getId()).signIn(role);
        }
    }
}
