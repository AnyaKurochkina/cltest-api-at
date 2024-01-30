package tests.routes;

import core.helper.http.Path;

import static core.helper.Configure.KONG_URL;

public class PortalApi implements Api{
    //Логотип портала
    @Route(method = Method.GET, path = "/v1/logo", status = 200)
    public static Path getV1Logo;

    //Удаление SSH ключа проекта
    @Route(method = Method.DELETE, path = "/v2/projects/{project_name}/ssh_keys/{id}", status = 204)
    public static Path deleteV2ProjectsProjectNameSshKeysId;

    //Обновление SSH ключа проекта
    @Route(method = Method.PATCH, path = "/v2/projects/{project_name}/ssh_keys/{id}", status = 200)
    public static Path patchV2ProjectsProjectNameSshKeysId;

    //Поиск SSH ключа проекта
    @Route(method = Method.GET, path = "/v2/projects/{project_name}/ssh_keys/{id}", status = 200)
    public static Path getV2ProjectsProjectNameSshKeysId;

    //Добавление проекту нового SSH ключа
    @Route(method = Method.POST, path = "/v2/projects/{project_name}/ssh_keys", status = 201)
    public static Path postV2ProjectsProjectNameSshKeys;

    //Список SSH ключей проекта
    @Route(method = Method.GET, path = "/v2/projects/{project_name}/ssh_keys", status = 200)
    public static Path getV2ProjectsProjectNameSshKeys;

    //Добавление пользователей в vDC организацию
    @Route(method = Method.POST, path = "/v1/projects/{project_name}/vdc_organizations/{vdc_organization_name}/vdc_organization_users", status = 200)
    public static Path postV1ProjectsProjectNameVdcOrganizationsVdcOrganizationNameVdcOrganizationUsers;

    //Список пользователей vDC организации
    @Route(method = Method.GET, path = "/v1/projects/{project_name}/vdc_organizations/{vdc_organization_name}/vdc_organization_users", status = 200)
    public static Path getV1ProjectsProjectNameVdcOrganizationsVdcOrganizationNameVdcOrganizationUsers;

    //Обновление пользователя vDC организации
    @Route(method = Method.PATCH, path = "/v1/projects/{project_name}/vdc_organizations/{vdc_organization_name}/vdc_organization_users/{id}", status = 200)
    public static Path patchV1ProjectsProjectNameVdcOrganizationsVdcOrganizationNameVdcOrganizationUsersId;

    //Поиск пользователя vDC организации
    @Route(method = Method.GET, path = "/v1/projects/{project_name}/vdc_organizations/{vdc_organization_name}/vdc_organization_users/{id}", status = 200)
    public static Path getV1ProjectsProjectNameVdcOrganizationsVdcOrganizationNameVdcOrganizationUsersId;

    //Удаление пользователей из vDC организации
    @Route(method = Method.DELETE, path = "/v1/projects/{project_name}/vdc_organizations/{vdc_organization_name}/vdc_organization_users/{id}", status = 204)
    public static Path deleteV1ProjectsProjectNameVdcOrganizationsVdcOrganizationNameVdcOrganizationUsersId;

    //Создать vDC организацию
    @Route(method = Method.POST, path = "/v1/projects/{project_name}/vdc_organizations", status = 201)
    public static Path postV1ProjectsProjectNameVdcOrganizations;

    //Cписок vDC организаций
    @Route(method = Method.GET, path = "/v1/projects/{project_name}/vdc_organizations", status = 200)
    public static Path getV1ProjectsProjectNameVdcOrganizations;

    //Удаление vDC организации
    @Route(method = Method.DELETE, path = "/v1/projects/{project_name}/vdc_organizations/{name}", status = 204)
    public static Path deleteV1ProjectsProjectNameVdcOrganizationsName;

    //Поиск vDC организации
    @Route(method = Method.GET, path = "/v1/projects/{project_name}/vdc_organizations/{name}", status = 200)
    public static Path getV1ProjectsProjectNameVdcOrganizationsName;

    //Просмотр заказа vDC в vcloud
    @Route(method = Method.GET, path = "/v1/projects/{project_name}/vdcs/{vdc_id}", status = 200)
    public static Path getV1ProjectsProjectNameVdcsVdcId;

    //Количество проектных SSH ключей
    @Route(method = Method.GET, path = "/v1/projects/{project_name}/ssh_keys_stat", status = 200)
    public static Path getV1ProjectsProjectNameSshKeysStat;

    //Cписок storage profiles
    @Route(method = Method.GET, path = "/v1/storage_profiles", status = 200)
    public static Path getV1StorageProfiles;

    //Cписок пользователей из Ldap
    @Route(method = Method.GET, path = "/v1/users", status = 200)
    public static Path getV1Users;

    //Cписок групп доступа текущего пользователя
    @Route(method = Method.GET, path = "/v1/user/access_groups", status = 200)
    public static Path getV1UserAccessGroups;

    //Выгрузка в файл списка заказов, к которым есть доступ у пользователя
    @Route(method = Method.GET, path = "/v1/user/available_orders/export", status = 200)
    public static Path getV1UserAvailableOrdersExport;

    @Route(method = Method.GET, path = "/v1/user/available_orders", status = 200)
    public static Path getV1UserAvailableOrders;

    //Добавление нового SSH ключа
    @Route(method = Method.POST, path = "/v2/ssh_keys", status = 201)
    public static Path postV2SshKeys;

    //Список SSH ключей пользователя
    @Route(method = Method.GET, path = "/v2/ssh_keys", status = 200)
    public static Path getV2SshKeys;

    //Удаление SSH ключа
    @Route(method = Method.DELETE, path = "/v2/ssh_keys/{id}", status = 204)
    public static Path deleteV2SshKeysId;

    //Обновление SSH ключа
    @Route(method = Method.PATCH, path = "/v2/ssh_keys/{id}", status = 200)
    public static Path patchV2SshKeysId;

    //Поиск SSH ключа
    @Route(method = Method.GET, path = "/v2/ssh_keys/{id}", status = 200)
    public static Path getV2SshKeysId;

    @Route(method = Method.GET, path = "/v1/health", status = 200)
    public static Path getV1Health;

    @Route(method = Method.GET, path = "/v1/version", status = 200)
    public static Path getV1Version;

    @Override
    public String url() {
        return KONG_URL + "portal/api";
    }
}
