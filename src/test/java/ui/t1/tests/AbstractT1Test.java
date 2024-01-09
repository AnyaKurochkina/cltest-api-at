package ui.t1.tests;

import api.Tests;
import core.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.extesions.ConfigExtension;
import ui.t1.pages.T1LoginPage;

@ExtendWith(ConfigExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractT1Test extends Tests {

    /**
     * В наследнике необходимо реализовать получение айдишника для проекта
     * Пример можно посмотреть в классе T1BillsTests.java
     */
    public abstract String getProjectId();

    /**
     * Данный метод авторизуется на портале с заданной ролью
     * для авторизации необходимо в наследнике пометить тест аннотацией @WithAuthorization
     * Например: @WithAuthorization(role = Role.SUPERADMIN)
     */
    @BeforeEach
    public void auth(TestInfo info) {
        if (info.getTestMethod().get().isAnnotationPresent(WithAuthorization.class)) {
            Role role = info.getTestMethod().get()
                    .getAnnotation(WithAuthorization.class).role();
            new T1LoginPage(getProjectId()).signIn(role);
        }
    }
}
