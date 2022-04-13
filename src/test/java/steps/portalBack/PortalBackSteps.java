package steps.portalBack;

import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import models.authorizer.Folder;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironmentPrefix;
import steps.Steps;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static core.helper.Configure.PortalBackURL;

public class PortalBackSteps extends Steps {

    private final static Map<String, String> environments = Stream.of(new String[][]{
            {"dso", "DEV"},
            {"dev", "DEV"},
            {"st", "DEV"},
            {"ift", "TEST"},
            {"edu", "TEST"},
            {"migr", "TEST"},
            {"hotfix", "TEST"},
            {"preprod", "TEST"},
            {"lt", "TEST"}
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

    @SneakyThrows
    @Step("Получение ProjectEnvironmentPrefix по envType {envType} и informationSystemId {informationSystemId}")
    public static ProjectEnvironmentPrefix getProjectEnvironmentPrefix(String envType, String informationSystemId) {
        List<ProjectEnvironmentPrefix> environmentPrefixes = getProjectEnvironmentPrefixes(informationSystemId);
        return environmentPrefixes
                .stream()
                .filter(e -> Objects.nonNull(e.getEnvType()))
                .filter(e -> e.getEnvType().equals(envType.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new Exception("Не найден ProjectEnvironment с типом среды " + envType));
    }

    @SneakyThrows
    @Step("Получение ProjectEnvironmentPrefix по env {env} и informationSystemId {informationSystemId}")
    public static ProjectEnvironmentPrefix getProjectEnvironmentPrefixByEnv(String env, String informationSystemId) {
        List<ProjectEnvironmentPrefix> environmentPrefixes = getProjectEnvironmentPrefixes(informationSystemId);
        return environmentPrefixes
                .stream()
                .filter(e -> Objects.nonNull(e.getRisName()))
                .filter(e -> e.getRisName().toUpperCase().startsWith(env.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new Exception("Не найден ProjectEnvironment со средой " + env));
    }

    public static List<ProjectEnvironmentPrefix> getProjectEnvironmentPrefixes(String informationSystemId) {
        String folderName = ((Folder) Folder.builder().kind(Folder.DEFAULT).build().createObject()).getName();
        @SuppressWarnings(value = "unchecked")
        List<ProjectEnvironmentPrefix> environmentPrefixes = (List<ProjectEnvironmentPrefix>) listEntities(PortalBackURL, "/v1/folders/" + folderName +
                "/information_systems/" + informationSystemId + "/environment_prefixes?reserved=false&status=available", ProjectEnvironmentPrefix.class, "list");
        environmentPrefixes.forEach(PortalBackSteps::setEnvType);
        return environmentPrefixes;
    }

    private static void setEnvType(ProjectEnvironmentPrefix environmentPrefix) {
        if (Objects.isNull(environmentPrefix.getRisName()))
            return;
        for (String key : environments.keySet()) {
            if (environmentPrefix.getRisName().toLowerCase().startsWith(key)) {
                environmentPrefix.setEnv(key);
                environmentPrefix.setEnvType(environments.get(key));
                break;
            }
        }
    }

    @Step("Получение пользователя из LDAP")
    public static String getUsers(Project project, String username) {
        return new Http(PortalBackURL)
                .get("/v1/users?q={}&project_name={}", username, project.getId())
                .assertStatus(200)
                .jsonPath()
                .get("[0].unique_name");
    }

}
