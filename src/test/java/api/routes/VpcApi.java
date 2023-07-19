package api.routes;

import core.helper.http.Path;

import static core.helper.Configure.KONG_URL;

public class VpcApi implements Api {
    //Получить все сети
    @Route(method = Method.GET, path = "/api/v1/projects/{project_name}/networks", status = 200)
    public static Path getNetworksApiV1ProjectsProjectNameNetworksGet;

    //Создать сеть
    @Route(method = Method.POST, path = "/api/v1/projects/{project_name}/networks", status = 200)
    public static Path createNetworkApiV1ProjectsProjectNameNetworksPost;

    //Получить сеть по идентификатору
    @Route(method = Method.GET, path = "/api/v1/projects/{project_name}/networks/{network_id}", status = 200)
    public static Path getNetworkApiV1ProjectsProjectNameNetworksNetworkIdGet;

    //Удалить сеть по идентификатору
    @Route(method = Method.DELETE, path = "/api/v1/projects/{project_name}/networks/{network_id}", status = 200)
    public static Path deleteNetworkApiV1ProjectsProjectNameNetworksNetworkIdDelete;

    //Получить все подсети
    @Route(method = Method.GET, path = "/api/v1/projects/{project_name}/subnets", status = 200)
    public static Path getSubnetsApiV1ProjectsProjectNameSubnetsGet;

    //Создать подсеть
    @Route(method = Method.POST, path = "/api/v1/projects/{project_name}/subnets", status = 200)
    public static Path createSubnetApiV1ProjectsProjectNameSubnetsPost;

    //Получить подсеть по идентификатору
    @Route(method = Method.GET, path = "/api/v1/projects/{project_name}/subnets/{subnet_id}", status = 200)
    public static Path getSubnetApiV1ProjectsProjectNameSubnetsSubnetIdGet;

    //Удалить подсеть по идентификатору
    @Route(method = Method.DELETE, path = "/api/v1/projects/{project_name}/subnets/{subnet_id}", status = 200)
    public static Path deleteSubnetApiV1ProjectsProjectNameSubnetsSubnetIdDelete;

    //Получить статистику по объектам VPC
    @Route(method = Method.GET, path = "/api/v1/projects/{project_name}/items/stats", status = 200)
    public static Path getStatisticsApiV1ProjectsProjectNameItemsStatsGet;

    //Получить все группы безопасности
    @Route(method = Method.GET, path = "/api/v1/projects/{project_name}/security-groups", status = 200)
    public static Path getSecurityGroupsApiV1ProjectsProjectNameSecurityGroupsGet;

    //Создать группу безопасности
    @Route(method = Method.POST, path = "/api/v1/projects/{project_name}/security-groups", status = 200)
    public static Path createSecurityGroupApiV1ProjectsProjectNameSecurityGroupsPost;

    //Получить группу безопасности по идентификатору
    @Route(method = Method.GET, path = "/api/v1/projects/{project_name}/security-groups/{security_group_id}", status = 200)
    public static Path getSecurityGroupApiV1ProjectsProjectNameSecurityGroupsSecurityGroupIdGet;

    //Удалить группу безопасности по идентификатору
    @Route(method = Method.DELETE, path = "/api/v1/projects/{project_name}/security-groups/{security_group_id}", status = 200)
    public static Path deleteSecurityGroupApiV1ProjectsProjectNameSecurityGroupsSecurityGroupIdDelete;

    //Получить все правила группы безопасности
    @Route(method = Method.GET, path = "/api/v1/projects/{project_name}/security-groups/{security_group_id}/rules", status = 200)
    public static Path getRulesApiV1ProjectsProjectNameSecurityGroupsSecurityGroupIdRulesGet;

    //Создать правило группы безопасности
    @Route(method = Method.POST, path = "/api/v1/projects/{project_name}/security-groups/{security_group_id}/rules", status = 200)
    public static Path createRuleApiV1ProjectsProjectNameSecurityGroupsSecurityGroupIdRulesPost;

    //Получить правило группы безопасности по идентификатору
    @Route(method = Method.GET, path = "/api/v1/projects/{project_name}/security-groups/{security_group_id}/rules/{security_group_rule_id}", status = 200)
    public static Path getRuleApiV1ProjectsProjectNameSecurityGroupsSecurityGroupIdRulesSecurityGroupRuleIdGet;

    //Удалить правило группы безопасности по идентификатору
    @Route(method = Method.DELETE, path = "/api/v1/projects/{project_name}/security-groups/{security_group_id}/rules/{security_group_rule_id}", status = 200)
    public static Path deleteRuleApiV1ProjectsProjectNameSecurityGroupsSecurityGroupIdRulesSecurityGroupRuleIdDelete;

    @Override
    public String url() {
        return KONG_URL + "vpc";
    }
}
