package models.authorizer;

import core.helper.Configure;
import core.helper.Http;
import core.random.string.RandomStringGenerator;
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
    public String prefix;

    transient String folderName;


    public Entity init() {
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
        if(projectName == null){
            projectName = new RandomStringGenerator().generateByRegex("project [0-9a-zA-Z]{5,15}");
        }
        return this;
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
    protected void create() {
        id = new Http(Configure.AuthorizerURL)
                .post(String.format("folders/%s/projects", folderName), toJson())
                .assertStatus(201)
                .jsonPath()
                .getString("data.name");
    }

    @Override
    @Step("Удаление проекта")
    protected void delete() {
        new Http(Configure.AuthorizerURL)
                .delete("projects/" + id)
                .assertStatus(204);
    }
}
