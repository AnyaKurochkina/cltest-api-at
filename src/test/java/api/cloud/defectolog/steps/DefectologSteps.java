package api.cloud.defectolog.steps;

import api.cloud.defectolog.models.Defect;
import api.cloud.defectolog.models.DefectPage;
import api.cloud.defectolog.models.DefectsList;
import api.cloud.defectolog.models.StartTask;
import api.routes.DefectologApi;
import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.QueryBuilder;
import io.qameta.allure.Step;
import models.Entity;

public class DefectologSteps {

    @Step("Запуск задания Celery {task.taskName}")
    public static Boolean tasksCreate(StartTask task) {
        return Http.builder().setRole(Role.SUPERADMIN).body(Entity.serialize(task)).api(DefectologApi.tasksCreate)
                .jsonPath().getBoolean("status");
    }

    @Step("Получение списка дефектов")
    public static DefectsList defectsList() {
        return Http.builder().setRole(Role.SUPERADMIN).api(DefectologApi.defectsList,
                new QueryBuilder().add("ordering", "-created_at")).extractAs(DefectsList.class);
    }

    @Step("Получение defect по id {id}")
    public static Defect defectsRead(int id) {
        return Http.builder().setRole(Role.SUPERADMIN).api(DefectologApi.defectsRead, id).extractAs(Defect.class);
    }

    @Step("Получение defectPages по id {id}")
    public static DefectPage defectPagesRead(int id) {
        return Http.builder().setRole(Role.SUPERADMIN).api(DefectologApi.defectPagesRead, id).extractAs(DefectPage.class);
    }
}
