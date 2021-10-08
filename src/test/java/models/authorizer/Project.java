package models.authorizer;

import core.helper.Configure;
import core.helper.Http;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import models.Entity;
import org.json.JSONObject;
import steps.authorizer.ProjectSteps;
import steps.portalBack.PortalBackSteps;

@Builder
@Getter
public class Project extends Entity {
    public String id;
    public String informationSystem;
    public ProjectEnvironment projectEnvironment;
    public String projectName;
    public Boolean isForOrders;

    transient String prefix;
    transient String folderName;


    public void init() {
        if(informationSystem == null){
            informationSystem = ((InformationSystem) InformationSystem.builder().build().createObject()).getId();
        }
        if(projectEnvironment == null){
            projectEnvironment = new PortalBackSteps().getProjectEnvironment("DEV", informationSystem);
        }
        if(folderName == null){
            folderName = ((Folder) Folder.builder().kind(Folder.DEFAULT).build().createObject()).getName();
        }
        if(prefix == null){
            ProjectSteps projectSteps = new ProjectSteps();
            prefix = projectSteps.getPrefixEnv(folderName, informationSystem, projectEnvironment.getId());
        }
    }
    public JSONObject toJson() {
        return jsonHelper.getJsonTemplate("/structure/create_project.json")
                .set("$.project.title", projectName)
                .set("$.project.information_system_id", informationSystem)
                .set("$.project.project_environment_id", projectEnvironment.getId())
                .set("$.project.environment_prefix_id", prefix)
                .build();
    }

    @Override
    @Step("Создание проекта")
    public void create() {
        id = new Http(Configure.AuthorizerURL)
                .post(String.format("folders/%s/projects", folderName), toJson())
                .assertStatus(201)
                .jsonPath()
                .getString("data.name");
    }

    @Override
    @Step("Удаление проекта")
    public void delete() {
        new Http(Configure.AuthorizerURL)
                .delete("projects/" + id)
                .assertStatus(204);
    }
}
