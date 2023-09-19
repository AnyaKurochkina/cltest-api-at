package steps.vpc;

import com.fasterxml.jackson.core.type.TypeReference;
import core.enums.Role;
import core.exception.NotFoundElementException;
import core.helper.http.Http;

import java.util.List;

import static api.routes.VpcApi.*;
import static models.Entity.serialize;

public class VpcSteps {

    public static SecurityGroupResponse createSecurityGroup(String projectId, SecurityGroup group) {
        Http.builder().setRole(Role.CLOUD_ADMIN).body(serialize(group))
                .api(createSecurityGroupApiV1ProjectsProjectNameSecurityGroupsPost, projectId);
        return getSecurityGroups(projectId).stream().filter(e -> e.getName().equals(group.getName()))
                .findFirst().orElseThrow(() -> new NotFoundElementException("Не найдена группа", group.getName()));
    }

    public static void deleteSecurityGroup(String projectId, String id) {
        Http.builder().setRole(Role.CLOUD_ADMIN).api(deleteSecurityGroupApiV1ProjectsProjectNameSecurityGroupsSecurityGroupIdDelete, projectId, id);
    }

    public static List<SecurityGroupResponse> getSecurityGroups(String projectId) {
        return Http.builder().setRole(Role.CLOUD_ADMIN).api(getSecurityGroupsApiV1ProjectsProjectNameSecurityGroupsGet, projectId)
                .extractAs(new TypeReference<List<SecurityGroupResponse>>(){});
    }
}
