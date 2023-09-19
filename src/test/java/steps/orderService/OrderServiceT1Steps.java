package steps.orderService;

import core.enums.Role;
import core.helper.http.Http;
import models.AbstractEntity;

import static api.routes.VpcApi.createSecurityGroupApiV1ProjectsProjectNameSecurityGroupsPost;
import static api.routes.VpcApi.deleteSecurityGroupApiV1ProjectsProjectNameSecurityGroupsSecurityGroupIdDelete;
import static models.Entity.serialize;

public class OrderServiceT1Steps {

    public static SecurityGroupResponse createSecurityGroup(String projectId, SecurityGroup group){
        return Http.builder().setRole(Role.CLOUD_ADMIN).body(serialize(group))
                .api(createSecurityGroupApiV1ProjectsProjectNameSecurityGroupsPost, projectId)
                .extractAs(SecurityGroupResponse.class).deleteMode(AbstractEntity.Mode.AFTER_TEST);
    }

    public static void deleteSecurityGroup(String projectId, String id){
        Http.builder().setRole(Role.CLOUD_ADMIN).api(deleteSecurityGroupApiV1ProjectsProjectNameSecurityGroupsSecurityGroupIdDelete, projectId, id);
    }
}
