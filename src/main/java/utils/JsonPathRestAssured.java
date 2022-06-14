package utils;

import io.restassured.path.json.JsonPath;

public class JsonPathRestAssured {

    public static void main(String[] args) {
        JsonPath jsonPath = new JsonPath(json);
//        Object o = jsonPath.get("data.find{it.data.config.containsKey('db_users')}.data.config.db_users.any{it.user_name=='%s'}");
//           Object o = jsonPath.get(".find{it.name == 'kafka:zookeeper'}.id");
        System.out.println();
        //Object o = jsonPath.get("clients.list_user_type_name.any{it.name=='dfgdfg1'}");
        //Object o = jsonPath.get("clients.list_user_type_relationship.find{it.name==('dfgdfg1')}.relationship.any{it.name=='002'}");
        //.any{it.db_name=='%s'}
    }

    static final String json = "{\n" +
            "  \"id\": \"ac81fcc8-17c3-4db6-b6fe-162f8ae7b84d\",\n" +
            "  \"created_at\": \"2022-06-01T15:11:04+03:00\",\n" +
            "  \"updated_at\": \"2022-06-01T15:18:57+03:00\",\n" +
            "  \"attrs\": {\n" +
            "    \"domain\": \"test.vtb.ru\",\n" +
            "    \"flavor\": {\n" +
            "      \"cpus\": 2,\n" +
            "      \"name\": \"c2m4\",\n" +
            "      \"uuid\": \"3dfab004-286c-4f39-9932-baf865756a23\",\n" +
            "      \"memory\": 4\n" +
            "    },\n" +
            "    \"folder\": \"fold-21yhccp939\",\n" +
            "    \"creator\": {\n" +
            "      \"id\": \"3564c2b4-cda8-4485-af7f-0858ccd4f5c3\",\n" +
            "      \"email\": \"admin@check.ru\",\n" +
            "      \"lastname\": \"check\",\n" +
            "      \"username\": \"check_admin\",\n" +
            "      \"firstname\": \"admin\"\n" +
            "    },\n" +
            "    \"platform\": \"vsphere\",\n" +
            "    \"boot_disk\": {\n" +
            "      \"size\": 30\n" +
            "    },\n" +
            "    \"account_id\": \"9a7fe9b8-30e4-40dc-aac3-51fd7bec55e7\",\n" +
            "    \"extra_nics\": [],\n" +
            "    \"on_support\": true,\n" +
            "    \"os_version\": \"8.4\",\n" +
            "    \"data_center\": \"5\",\n" +
            "    \"default_nic\": {\n" +
            "      \"net_segment\": \"test-srv-perf\"\n" +
            "    },\n" +
            "    \"access_group\": [\n" +
            "      \"cloud-ltzorg-lt-group\"\n" +
            "    ],\n" +
            "    \"extra_mounts\": [\n" +
            "      {\n" +
            "        \"path\": \"/app/app\",\n" +
            "        \"size\": 30,\n" +
            "        \"file_system\": \"xfs\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"path\": \"/app/logs\",\n" +
            "        \"size\": 20,\n" +
            "        \"file_system\": \"xfs\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"java_version\": \"1.8.0\",\n" +
            "    \"project_path\": \"/organization/vtb/folder/fold-s5wu0ff33x/folder/fold-fr4o3pcghy/folder/fold-humog4orc1/folder/fold-cq6hc1jmgd/folder/fold-dkj0exqf9q/folder/fold-21yhccp939/project/proj-j5uzlpvbjz/\",\n" +
            "    \"graph_version\": \"1.0.1\",\n" +
            "    \"preview_items\": [\n" +
            "      {\n" +
            "        \"data\": {\n" +
            "          \"build\": {},\n" +
            "          \"state\": \"on\",\n" +
            "          \"config\": {},\n" +
            "          \"provider\": \"wildfly\"\n" +
            "        },\n" +
            "        \"type\": \"app\",\n" +
            "        \"item_id\": \"a5b9ccd5-2798-4204-b030-dc5a57ea7f98\",\n" +
            "        \"update_dt\": null\n" +
            "      },\n" +
            "      {\n" +
            "        \"data\": {\n" +
            "          \"acls\": [\n" +
            "            {}\n" +
            "          ],\n" +
            "          \"state\": \"on\",\n" +
            "          \"config\": {\n" +
            "            \"image\": {\n" +
            "              \"os\": {\n" +
            "                \"type\": \"linux\",\n" +
            "                \"vendor\": \"ibm\",\n" +
            "                \"version\": \"8.4\",\n" +
            "                \"architecture\": \"x86_64\",\n" +
            "                \"distribution\": \"rhel\",\n" +
            "                \"localization\": \"en\"\n" +
            "              },\n" +
            "              \"name\": \"tpl_linux_rhel_8.4_x86_64_en\",\n" +
            "              \"size\": 30,\n" +
            "              \"uuid\": \"4c2002cf-5d94-4f12-8e12-175e944b9c32\"\n" +
            "            },\n" +
            "            \"domain\": \"test.vtb.ru\",\n" +
            "            \"flavor\": {\n" +
            "              \"cpus\": 2,\n" +
            "              \"name\": \"c2m4\",\n" +
            "              \"uuid\": \"3dfab004-286c-4f39-9932-baf865756a23\",\n" +
            "              \"memory\": 4\n" +
            "            },\n" +
            "            \"tenant\": {},\n" +
            "            \"boot_disk\": {\n" +
            "              \"size\": 30\n" +
            "            },\n" +
            "            \"extra_nics\": [],\n" +
            "            \"on_support\": true,\n" +
            "            \"os_version\": \"8.4\",\n" +
            "            \"default_nic\": {\n" +
            "              \"net_segment\": \"test-srv-perf\"\n" +
            "            },\n" +
            "            \"environment\": \"LT\",\n" +
            "            \"extra_disks\": [\n" +
            "              {\n" +
            "                \"size\": 50\n" +
            "              }\n" +
            "            ],\n" +
            "            \"resource_pool\": {\n" +
            "              \"name\": \"K37-7-NT\",\n" +
            "              \"uuid\": \"domain-c17538\",\n" +
            "              \"ui_link\": \"https://sv-vc505xv.region.vtb.ru\",\n" +
            "              \"endpoint\": \"https://sv-vc505xv.region.vtb.ru\",\n" +
            "              \"platform\": \"vsphere\",\n" +
            "              \"tenant_prefix\": \"Cloud\"\n" +
            "            },\n" +
            "            \"ad_integration\": true,\n" +
            "            \"environment_type\": \"TEST\"\n" +
            "          },\n" +
            "          \"parent\": \"a5b9ccd5-2798-4204-b030-dc5a57ea7f98\",\n" +
            "          \"provider\": \"vsphere\"\n" +
            "        },\n" +
            "        \"type\": \"vm\",\n" +
            "        \"item_id\": \"37654485-bd2b-4e6c-bf4e-fff1ac9a0ebd\",\n" +
            "        \"update_dt\": null\n" +
            "      }\n" +
            "    ],\n" +
            "    \"product_title\": \"WildFly RHEL\",\n" +
            "    \"ad_integration\": true,\n" +
            "    \"tariff_plan_id\": \"aff23e6b-14ac-4bd0-be55-f6582ff11da3\",\n" +
            "    \"ad_logon_grants\": [\n" +
            "      {\n" +
            "        \"role\": \"user\",\n" +
            "        \"groups\": [\n" +
            "          \"cloud-ltzorg-lt-group\"\n" +
            "        ]\n" +
            "      }\n" +
            "    ],\n" +
            "    \"wildfly_version\": \"23.0.2.Final\",\n" +
            "    \"organization_name\": \"vtb\",\n" +
            "    \"environment_prefix_id\": \"4488b474-356a-43df-80da-4e43c6620253\",\n" +
            "    \"information_system_id\": \"8e33baf8-e181-457b-9d60-e5dc470fe8fa\",\n" +
            "    \"project_environment_id\": \"eeb890b0-e769-48a1-ba49-904821dbc2a2\",\n" +
            "    \"information_system_priority\": 2\n" +
            "  },\n" +
            "  \"status\": \"success\",\n" +
            "  \"label\": \"WildFly RHEL\",\n" +
            "  \"category\": \"vm\",\n" +
            "  \"deletable\": true,\n" +
            "  \"power_status\": [\n" +
            "    {\n" +
            "      \"status\": \"on\",\n" +
            "      \"item_id\": \"42389bbe-71fb-b854-ab44-c6cda25d2a01\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"product_data\": [\n" +
            "    {\n" +
            "      \"ip\": \"10.221.52.90\",\n" +
            "      \"type\": \"vm\",\n" +
            "      \"hostname\": \"ltzorg-wfc004lv\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"project_name\": \"proj-j5uzlpvbjz\",\n" +
            "  \"product_id\": \"7c7bbeab-486b-4208-a112-04253a6c186b\",\n" +
            "  \"data\": [\n" +
            "    {\n" +
            "      \"id\": 68367,\n" +
            "      \"created_row_dt\": \"2022-06-01T12:18:56.802827Z\",\n" +
            "      \"update_dt\": \"2022-06-01T12:18:57.083934Z\",\n" +
            "      \"item_id\": \"eca32f82-7c0a-535c-8037-5f56672e4f4a\",\n" +
            "      \"order_id\": \"ac81fcc8-17c3-4db6-b6fe-162f8ae7b84d\",\n" +
            "      \"action_id\": \"6b583d29-284d-4d63-bfa8-c36491cc2210\",\n" +
            "      \"graph_id\": \"8975c85e-3f90-482f-a765-86d06013db27\",\n" +
            "      \"type\": \"app\",\n" +
            "      \"data\": {\n" +
            "        \"build\": {\n" +
            "          \"setup_version\": \"1.0.0\"\n" +
            "        },\n" +
            "        \"state\": \"on\",\n" +
            "        \"config\": {\n" +
            "          \"user\": [\n" +
            "            {\n" +
            "              \"name\": \"masterwftest@test.vtb.ru\",\n" +
            "              \"role\": \"SuperUser\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"mastermonregionjmx@region.vtb.ru\",\n" +
            "              \"role\": \"Monitor\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"group\": [\n" +
            "            {\n" +
            "              \"name\": \"cloud-ltzorg-lt-group\",\n" +
            "              \"role\": \"Maintainer\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"apps_admins\",\n" +
            "              \"role\": \"SuperUser\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"certificate\": {\n" +
            "            \"end_date\": \"2023-06-01T12:08:13+00:00\",\n" +
            "            \"start_date\": \"2022-06-01T12:08:13+00:00\",\n" +
            "            \"certificate_cn\": \"ltzorg-wfc004lv.test.vtb.ru\"\n" +
            "          },\n" +
            "          \"console_url\": \"https://ltzorg-wfc004lv.test.vtb.ru:9993\",\n" +
            "          \"java_version\": \"1.8.0\",\n" +
            "          \"connection_url\": \"https://ltzorg-wfc004lv.test.vtb.ru:8443\",\n" +
            "          \"wildfly_version\": \"23.0.2.Final\"\n" +
            "        },\n" +
            "        \"provider\": \"wildfly\"\n" +
            "      },\n" +
            "      \"actions\": [\n" +
            "        {\n" +
            "          \"id\": \"09ab9abd-3210-4c1a-a28e-f7ab3b76cef8\",\n" +
            "          \"version\": \"1.0.2\",\n" +
            "          \"create_dt\": \"2022-05-20T18:27:29.607776+03:00\",\n" +
            "          \"update_dt\": \"2022-05-20T18:55:07.584839+03:00\",\n" +
            "          \"current_version\": \"\",\n" +
            "          \"name\": \"wildfly_update_certs\",\n" +
            "          \"title\": \"Обновить сертификат WildFly\",\n" +
            "          \"priority\": 0,\n" +
            "          \"description\": \"\",\n" +
            "          \"icon\": \"\",\n" +
            "          \"required_order_statuses\": [\n" +
            "            \"success\"\n" +
            "          ],\n" +
            "          \"required_item_statuses\": [\n" +
            "            \"on\"\n" +
            "          ],\n" +
            "          \"type\": \"\",\n" +
            "          \"event_type\": [\n" +
            "            \"app\"\n" +
            "          ],\n" +
            "          \"event_provider\": [\n" +
            "            \"wildfly\"\n" +
            "          ],\n" +
            "          \"data_config_path\": \"data.config.certificate\",\n" +
            "          \"data_config_key\": null,\n" +
            "          \"data_config_fields\": [],\n" +
            "          \"config_restriction\": null,\n" +
            "          \"item_restriction\": null,\n" +
            "          \"available_without_money\": false,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": true,\n" +
            "          \"multiple\": false,\n" +
            "          \"location_restriction\": \"\",\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"extra_data\": {},\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.2\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\",\n" +
            "            \"1.0.1\",\n" +
            "            \"1.0.2\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.1\",\n" +
            "          \"graph_id\": \"8cc84f04-549c-4d8c-b734-9c8ca825a9cc\",\n" +
            "          \"version_create_dt\": \"2022-05-20T15:55:07.750878Z\",\n" +
            "          \"version_changed_by_user\": \"f48f06fa-6609-4ec4-9b19-256314b68250\",\n" +
            "          \"active\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"70b48d7a-46c9-4e8e-b0d1-d322a75aff2b\",\n" +
            "          \"version\": \"1.0.2\",\n" +
            "          \"create_dt\": \"2022-05-20T18:23:01.815740+03:00\",\n" +
            "          \"update_dt\": \"2022-05-20T20:28:53.035302+03:00\",\n" +
            "          \"current_version\": \"\",\n" +
            "          \"name\": \"wildfly_add_user\",\n" +
            "          \"title\": \"Добавление пользователя WildFly\",\n" +
            "          \"priority\": 0,\n" +
            "          \"description\": \"\",\n" +
            "          \"icon\": \"\",\n" +
            "          \"required_order_statuses\": [\n" +
            "            \"success\"\n" +
            "          ],\n" +
            "          \"required_item_statuses\": [\n" +
            "            \"on\"\n" +
            "          ],\n" +
            "          \"type\": \"\",\n" +
            "          \"event_type\": [\n" +
            "            \"app\"\n" +
            "          ],\n" +
            "          \"event_provider\": [\n" +
            "            \"wildfly\"\n" +
            "          ],\n" +
            "          \"data_config_path\": \"data.config.user\",\n" +
            "          \"data_config_key\": null,\n" +
            "          \"data_config_fields\": [],\n" +
            "          \"config_restriction\": null,\n" +
            "          \"item_restriction\": null,\n" +
            "          \"available_without_money\": false,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": true,\n" +
            "          \"multiple\": false,\n" +
            "          \"location_restriction\": \"\",\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"extra_data\": {},\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.2\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\",\n" +
            "            \"1.0.1\",\n" +
            "            \"1.0.2\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.11\",\n" +
            "          \"graph_id\": \"dbd94f25-ad4a-40f3-9f78-be129d757645\",\n" +
            "          \"version_create_dt\": \"2022-05-20T17:28:53.042058Z\",\n" +
            "          \"version_changed_by_user\": \"f48f06fa-6609-4ec4-9b19-256314b68250\",\n" +
            "          \"active\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"065f7957-1bdb-4583-97b3-451ec3128f14\",\n" +
            "          \"version\": \"1.0.2\",\n" +
            "          \"create_dt\": \"2022-05-20T18:20:09.285093+03:00\",\n" +
            "          \"update_dt\": \"2022-05-20T20:29:14.754681+03:00\",\n" +
            "          \"current_version\": \"\",\n" +
            "          \"name\": \"wildfly_add_group\",\n" +
            "          \"title\": \"Добавление группы WildFly\",\n" +
            "          \"priority\": 3,\n" +
            "          \"description\": \"\",\n" +
            "          \"icon\": \"\",\n" +
            "          \"required_order_statuses\": [\n" +
            "            \"success\"\n" +
            "          ],\n" +
            "          \"required_item_statuses\": [\n" +
            "            \"on\"\n" +
            "          ],\n" +
            "          \"type\": \"\",\n" +
            "          \"event_type\": [\n" +
            "            \"app\"\n" +
            "          ],\n" +
            "          \"event_provider\": [\n" +
            "            \"wildfly\"\n" +
            "          ],\n" +
            "          \"data_config_path\": \"data.config.group\",\n" +
            "          \"data_config_key\": null,\n" +
            "          \"data_config_fields\": [],\n" +
            "          \"config_restriction\": null,\n" +
            "          \"item_restriction\": null,\n" +
            "          \"available_without_money\": false,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": true,\n" +
            "          \"multiple\": false,\n" +
            "          \"location_restriction\": \"\",\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"extra_data\": {},\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.2\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\",\n" +
            "            \"1.0.1\",\n" +
            "            \"1.0.2\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.12\",\n" +
            "          \"graph_id\": \"be5a65c2-cc6e-45fa-8d9c-9e2509796047\",\n" +
            "          \"version_create_dt\": \"2022-05-20T17:29:14.761328Z\",\n" +
            "          \"version_changed_by_user\": \"f48f06fa-6609-4ec4-9b19-256314b68250\",\n" +
            "          \"active\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"d4b44339-728f-416a-9bb7-8bb2f56b9d37\",\n" +
            "          \"version\": \"1.0.1\",\n" +
            "          \"create_dt\": \"2022-05-20T18:19:37.422904+03:00\",\n" +
            "          \"update_dt\": \"2022-05-20T18:19:56.256101+03:00\",\n" +
            "          \"current_version\": \"\",\n" +
            "          \"name\": \"wildfly_del_user\",\n" +
            "          \"title\": \"Удаление пользователя WildFly\",\n" +
            "          \"priority\": 0,\n" +
            "          \"description\": \"\",\n" +
            "          \"icon\": \"\",\n" +
            "          \"required_order_statuses\": [\n" +
            "            \"success\"\n" +
            "          ],\n" +
            "          \"required_item_statuses\": [\n" +
            "            \"on\"\n" +
            "          ],\n" +
            "          \"type\": \"\",\n" +
            "          \"event_type\": [\n" +
            "            \"app\"\n" +
            "          ],\n" +
            "          \"event_provider\": [\n" +
            "            \"wildfly\"\n" +
            "          ],\n" +
            "          \"data_config_path\": \"data.config.user\",\n" +
            "          \"data_config_key\": null,\n" +
            "          \"data_config_fields\": [],\n" +
            "          \"config_restriction\": null,\n" +
            "          \"item_restriction\": null,\n" +
            "          \"available_without_money\": false,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": true,\n" +
            "          \"multiple\": false,\n" +
            "          \"location_restriction\": \"\",\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"extra_data\": {},\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.1\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\",\n" +
            "            \"1.0.1\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.43\",\n" +
            "          \"graph_id\": \"0e256672-0104-4fbc-900e-68be0947c5aa\",\n" +
            "          \"version_create_dt\": \"2022-05-20T15:19:56.279200Z\",\n" +
            "          \"version_changed_by_user\": \"f48f06fa-6609-4ec4-9b19-256314b68250\",\n" +
            "          \"active\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"ad6c0809-6568-4c34-9334-97b93afbc090\",\n" +
            "          \"version\": \"1.0.1\",\n" +
            "          \"create_dt\": \"2022-05-20T18:18:28.809518+03:00\",\n" +
            "          \"update_dt\": \"2022-05-20T18:19:16.372637+03:00\",\n" +
            "          \"current_version\": \"\",\n" +
            "          \"name\": \"wildfly_del_group\",\n" +
            "          \"title\": \"Удаление группы WildFly\",\n" +
            "          \"priority\": 2,\n" +
            "          \"description\": \"\",\n" +
            "          \"icon\": \"\",\n" +
            "          \"required_order_statuses\": [\n" +
            "            \"success\"\n" +
            "          ],\n" +
            "          \"required_item_statuses\": [\n" +
            "            \"on\"\n" +
            "          ],\n" +
            "          \"type\": \"\",\n" +
            "          \"event_type\": [\n" +
            "            \"app\"\n" +
            "          ],\n" +
            "          \"event_provider\": [\n" +
            "            \"wildfly\"\n" +
            "          ],\n" +
            "          \"data_config_path\": \"data.config.group\",\n" +
            "          \"data_config_key\": null,\n" +
            "          \"data_config_fields\": [],\n" +
            "          \"config_restriction\": null,\n" +
            "          \"item_restriction\": null,\n" +
            "          \"available_without_money\": false,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": true,\n" +
            "          \"multiple\": false,\n" +
            "          \"location_restriction\": \"\",\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"extra_data\": {},\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.1\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\",\n" +
            "            \"1.0.1\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.31\",\n" +
            "          \"graph_id\": \"4750bf78-e098-451e-a4aa-4325d5438e96\",\n" +
            "          \"version_create_dt\": \"2022-05-20T15:19:16.382457Z\",\n" +
            "          \"version_changed_by_user\": \"f48f06fa-6609-4ec4-9b19-256314b68250\",\n" +
            "          \"active\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"8f2f39e9-5877-42e0-87aa-396be2f48790\",\n" +
            "          \"version\": \"1.0.11\",\n" +
            "          \"create_dt\": \"2022-03-24T18:36:35.111277+03:00\",\n" +
            "          \"update_dt\": \"2022-05-13T14:14:41.068601+03:00\",\n" +
            "          \"current_version\": \"\",\n" +
            "          \"name\": \"delete_two_layer\",\n" +
            "          \"title\": \"Удалить рекурсивно\",\n" +
            "          \"priority\": 0,\n" +
            "          \"description\": \"\",\n" +
            "          \"icon\": \"\",\n" +
            "          \"required_order_statuses\": [\n" +
            "            \"success\",\n" +
            "            \"damaged\"\n" +
            "          ],\n" +
            "          \"required_item_statuses\": [\n" +
            "            \"on\",\n" +
            "            \"off\"\n" +
            "          ],\n" +
            "          \"type\": \"delete\",\n" +
            "          \"event_type\": [\n" +
            "            \"app\",\n" +
            "            \"cluster\"\n" +
            "          ],\n" +
            "          \"event_provider\": [\n" +
            "            \"flink\",\n" +
            "            \"kafka\",\n" +
            "            \"nginx\",\n" +
            "            \"redis\",\n" +
            "            \"podman\",\n" +
            "            \"artemis\",\n" +
            "            \"haproxy\",\n" +
            "            \"wildfly\",\n" +
            "            \"rabbitmq\",\n" +
            "            \"scylladb\",\n" +
            "            \"clickhouse\",\n" +
            "            \"postgresql\",\n" +
            "            \"postgrespro\",\n" +
            "            \"vtb-artemis\",\n" +
            "            \"kafka_develop\",\n" +
            "            \"nginx_develop\",\n" +
            "            \"airflow_develop\",\n" +
            "            \"postgresql_v001\",\n" +
            "            \"wildfly_develop\",\n" +
            "            \"rabbitmq_develop\",\n" +
            "            \"vtb-artemis_develop\",\n" +
            "            \"balancer_develop\"\n" +
            "          ],\n" +
            "          \"data_config_path\": null,\n" +
            "          \"data_config_key\": null,\n" +
            "          \"data_config_fields\": [],\n" +
            "          \"config_restriction\": null,\n" +
            "          \"item_restriction\": null,\n" +
            "          \"available_without_money\": false,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": false,\n" +
            "          \"multiple\": false,\n" +
            "          \"location_restriction\": \"\",\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"extra_data\": {},\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.11\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\",\n" +
            "            \"1.0.1\",\n" +
            "            \"1.0.2\",\n" +
            "            \"1.0.3\",\n" +
            "            \"1.0.4\",\n" +
            "            \"1.0.6\",\n" +
            "            \"1.0.7\",\n" +
            "            \"1.0.8\",\n" +
            "            \"1.0.9\",\n" +
            "            \"1.0.10\",\n" +
            "            \"1.0.11\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.9\",\n" +
            "          \"graph_id\": \"c8df29d7-cb51-4b0f-867d-4619f9463483\",\n" +
            "          \"version_create_dt\": \"2022-05-13T11:14:41.078754Z\",\n" +
            "          \"version_changed_by_user\": \"3906c241-896f-447f-8756-7d79235145ec\",\n" +
            "          \"active\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"4701735f-f53f-412b-ac48-b5e4b578114c\",\n" +
            "          \"version\": \"1.0.1\",\n" +
            "          \"create_dt\": \"2022-03-24T18:36:35.111277+03:00\",\n" +
            "          \"update_dt\": \"2022-03-24T18:36:35.127006+03:00\",\n" +
            "          \"current_version\": \"\",\n" +
            "          \"name\": \"reset_two_layer\",\n" +
            "          \"title\": \"Перезагрузить\",\n" +
            "          \"priority\": 0,\n" +
            "          \"description\": \"\",\n" +
            "          \"icon\": \"\",\n" +
            "          \"required_order_statuses\": [\n" +
            "            \"success\"\n" +
            "          ],\n" +
            "          \"required_item_statuses\": [\n" +
            "            \"on\"\n" +
            "          ],\n" +
            "          \"type\": null,\n" +
            "          \"event_type\": [\n" +
            "            \"app\",\n" +
            "            \"cluster\"\n" +
            "          ],\n" +
            "          \"event_provider\": [\n" +
            "            \"nginx\",\n" +
            "            \"redis\",\n" +
            "            \"podman\",\n" +
            "            \"wildfly\",\n" +
            "            \"rabbitmq\",\n" +
            "            \"clickhouse\",\n" +
            "            \"postgrespro\",\n" +
            "            \"postgresql_v001\",\n" +
            "            \"rabbitmq_develop\"\n" +
            "          ],\n" +
            "          \"data_config_path\": null,\n" +
            "          \"data_config_key\": null,\n" +
            "          \"data_config_fields\": [],\n" +
            "          \"config_restriction\": null,\n" +
            "          \"item_restriction\": null,\n" +
            "          \"available_without_money\": false,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": false,\n" +
            "          \"multiple\": false,\n" +
            "          \"location_restriction\": \"\",\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"extra_data\": {},\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.1\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\",\n" +
            "            \"1.0.1\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.10\",\n" +
            "          \"graph_id\": \"56318e92-3c0f-4cf0-bb8d-0e8eb3436829\",\n" +
            "          \"version_create_dt\": \"2021-12-03T09:10:25.253474Z\",\n" +
            "          \"version_changed_by_user\": \"usp\",\n" +
            "          \"active\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"e80c7f02-a931-4682-b9a8-b7881c66980d\",\n" +
            "          \"version\": \"1.0.4\",\n" +
            "          \"create_dt\": \"2022-03-24T18:36:35.111277+03:00\",\n" +
            "          \"update_dt\": \"2022-05-17T18:10:04.642720+03:00\",\n" +
            "          \"current_version\": \"\",\n" +
            "          \"name\": \"start_two_layer\",\n" +
            "          \"title\": \"Включить\",\n" +
            "          \"priority\": 0,\n" +
            "          \"description\": \"\",\n" +
            "          \"icon\": \"\",\n" +
            "          \"required_order_statuses\": [\n" +
            "            \"success\"\n" +
            "          ],\n" +
            "          \"required_item_statuses\": [\n" +
            "            \"off\"\n" +
            "          ],\n" +
            "          \"type\": \"\",\n" +
            "          \"event_type\": [\n" +
            "            \"app\",\n" +
            "            \"cluster\"\n" +
            "          ],\n" +
            "          \"event_provider\": [\n" +
            "            \"nginx\",\n" +
            "            \"redis\",\n" +
            "            \"podman\",\n" +
            "            \"haproxy\",\n" +
            "            \"wildfly\",\n" +
            "            \"rabbitmq\",\n" +
            "            \"scylladb\",\n" +
            "            \"clickhouse\",\n" +
            "            \"postgrespro\",\n" +
            "            \"postgresql_v001\",\n" +
            "            \"rabbitmq_develop\",\n" +
            "            \"balancer_develop\"\n" +
            "          ],\n" +
            "          \"data_config_path\": null,\n" +
            "          \"data_config_key\": null,\n" +
            "          \"data_config_fields\": [],\n" +
            "          \"config_restriction\": null,\n" +
            "          \"item_restriction\": null,\n" +
            "          \"available_without_money\": true,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": false,\n" +
            "          \"multiple\": false,\n" +
            "          \"location_restriction\": \"\",\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"extra_data\": {},\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.4\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\",\n" +
            "            \"1.0.1\",\n" +
            "            \"1.0.2\",\n" +
            "            \"1.0.3\",\n" +
            "            \"1.0.4\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.8\",\n" +
            "          \"graph_id\": \"22d4b92f-c72c-46b5-b317-f26df1500a94\",\n" +
            "          \"version_create_dt\": \"2022-05-17T15:10:04.649878Z\",\n" +
            "          \"version_changed_by_user\": \"9607ff03-08c0-4777-a747-839b74ceabe3\",\n" +
            "          \"active\": false\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"c76bbc26-9883-4afa-8e2c-8c8c9f9c3c5f\",\n" +
            "          \"version\": \"1.0.3\",\n" +
            "          \"create_dt\": \"2022-03-24T18:36:35.111277+03:00\",\n" +
            "          \"update_dt\": \"2022-05-17T18:07:29.051891+03:00\",\n" +
            "          \"current_version\": \"\",\n" +
            "          \"name\": \"stop_hard_two_layer\",\n" +
            "          \"title\": \"Выключить принудительно\",\n" +
            "          \"priority\": 0,\n" +
            "          \"description\": \"\",\n" +
            "          \"icon\": \"\",\n" +
            "          \"required_order_statuses\": [\n" +
            "            \"success\"\n" +
            "          ],\n" +
            "          \"required_item_statuses\": [\n" +
            "            \"on\"\n" +
            "          ],\n" +
            "          \"type\": \"\",\n" +
            "          \"event_type\": [\n" +
            "            \"app\",\n" +
            "            \"cluster\"\n" +
            "          ],\n" +
            "          \"event_provider\": [\n" +
            "            \"nginx\",\n" +
            "            \"redis\",\n" +
            "            \"podman\",\n" +
            "            \"haproxy\",\n" +
            "            \"wildfly\",\n" +
            "            \"rabbitmq\",\n" +
            "            \"scylladb\",\n" +
            "            \"clickhouse\",\n" +
            "            \"postgrespro\",\n" +
            "            \"postgresql_v001\",\n" +
            "            \"balancer_develop\"\n" +
            "          ],\n" +
            "          \"data_config_path\": null,\n" +
            "          \"data_config_key\": null,\n" +
            "          \"data_config_fields\": [],\n" +
            "          \"config_restriction\": null,\n" +
            "          \"item_restriction\": null,\n" +
            "          \"available_without_money\": true,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": false,\n" +
            "          \"multiple\": false,\n" +
            "          \"location_restriction\": \"\",\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"extra_data\": {},\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.3\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\",\n" +
            "            \"1.0.1\",\n" +
            "            \"1.0.2\",\n" +
            "            \"1.0.3\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.9\",\n" +
            "          \"graph_id\": \"67f2c71f-0ae0-4828-bfc1-dc856e6b3630\",\n" +
            "          \"version_create_dt\": \"2022-05-17T15:07:29.060605Z\",\n" +
            "          \"version_changed_by_user\": \"9607ff03-08c0-4777-a747-839b74ceabe3\",\n" +
            "          \"active\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"6e2294d8-3cf1-42a3-b6c2-372b9c659c98\",\n" +
            "          \"version\": \"1.0.3\",\n" +
            "          \"create_dt\": \"2022-03-24T18:36:35.111277+03:00\",\n" +
            "          \"update_dt\": \"2022-05-13T14:18:34.509882+03:00\",\n" +
            "          \"current_version\": \"\",\n" +
            "          \"name\": \"stop_two_layer\",\n" +
            "          \"title\": \"Выключить\",\n" +
            "          \"priority\": 0,\n" +
            "          \"description\": \"\",\n" +
            "          \"icon\": \"\",\n" +
            "          \"required_order_statuses\": [\n" +
            "            \"success\"\n" +
            "          ],\n" +
            "          \"required_item_statuses\": [\n" +
            "            \"on\"\n" +
            "          ],\n" +
            "          \"type\": \"\",\n" +
            "          \"event_type\": [\n" +
            "            \"app\",\n" +
            "            \"cluster\"\n" +
            "          ],\n" +
            "          \"event_provider\": [\n" +
            "            \"nginx\",\n" +
            "            \"redis\",\n" +
            "            \"podman\",\n" +
            "            \"haproxy\",\n" +
            "            \"wildfly\",\n" +
            "            \"rabbitmq\",\n" +
            "            \"scylladb\",\n" +
            "            \"clickhouse\",\n" +
            "            \"postgrespro\",\n" +
            "            \"postgresql_v001\",\n" +
            "            \"rabbitmq_develop\",\n" +
            "            \"balancer_develop\"\n" +
            "          ],\n" +
            "          \"data_config_path\": null,\n" +
            "          \"data_config_key\": null,\n" +
            "          \"data_config_fields\": [],\n" +
            "          \"config_restriction\": null,\n" +
            "          \"item_restriction\": null,\n" +
            "          \"available_without_money\": true,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": false,\n" +
            "          \"multiple\": false,\n" +
            "          \"location_restriction\": \"\",\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"extra_data\": {},\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.3\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\",\n" +
            "            \"1.0.1\",\n" +
            "            \"1.0.2\",\n" +
            "            \"1.0.3\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.10\",\n" +
            "          \"graph_id\": \"39c1a317-7a2e-4874-8c28-91080bde5578\",\n" +
            "          \"version_create_dt\": \"2022-05-13T11:18:34.522191Z\",\n" +
            "          \"version_changed_by_user\": \"3906c241-896f-447f-8756-7d79235145ec\",\n" +
            "          \"active\": true\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 68364,\n" +
            "      \"created_row_dt\": \"2022-06-01T12:12:43.121078Z\",\n" +
            "      \"update_dt\": \"2022-06-01T12:18:57.054088Z\",\n" +
            "      \"item_id\": \"42389bbe-71fb-b854-ab44-c6cda25d2a01\",\n" +
            "      \"order_id\": \"ac81fcc8-17c3-4db6-b6fe-162f8ae7b84d\",\n" +
            "      \"action_id\": \"6b583d29-284d-4d63-bfa8-c36491cc2210\",\n" +
            "      \"graph_id\": \"8975c85e-3f90-482f-a765-86d06013db27\",\n" +
            "      \"type\": \"vm\",\n" +
            "      \"data\": {\n" +
            "        \"acls\": [\n" +
            "          {\n" +
            "            \"role\": \"user\",\n" +
            "            \"members\": [\n" +
            "              \"cloud-ltzorg-lt-group\"\n" +
            "            ]\n" +
            "          }\n" +
            "        ],\n" +
            "        \"state\": \"on\",\n" +
            "        \"config\": {\n" +
            "          \"image\": {\n" +
            "            \"os\": {\n" +
            "              \"type\": \"linux\",\n" +
            "              \"vendor\": \"ibm\",\n" +
            "              \"version\": \"8.4\",\n" +
            "              \"architecture\": \"x86_64\",\n" +
            "              \"distribution\": \"rhel\",\n" +
            "              \"localization\": \"en\"\n" +
            "            },\n" +
            "            \"name\": \"tpl_linux_rhel_8.4_x86_64_en\",\n" +
            "            \"size\": 30,\n" +
            "            \"uuid\": \"4c2002cf-5d94-4f12-8e12-175e944b9c32\"\n" +
            "          },\n" +
            "          \"domain\": \"test.vtb.ru\",\n" +
            "          \"flavor\": {\n" +
            "            \"cpus\": 2,\n" +
            "            \"name\": \"c2m4\",\n" +
            "            \"uuid\": \"3dfab004-286c-4f39-9932-baf865756a23\",\n" +
            "            \"memory\": 4\n" +
            "          },\n" +
            "          \"mounts\": [\n" +
            "            {\n" +
            "              \"size\": 10.0,\n" +
            "              \"mount\": \"/\",\n" +
            "              \"device\": \"/dev/mapper/vg_01-lv_root\",\n" +
            "              \"fstype\": \"xfs\",\n" +
            "              \"options\": \"rw,relatime,attr2,inode64,logbufs=8,logbsize=32k,noquota\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"size\": 8.0,\n" +
            "              \"mount\": \"/var\",\n" +
            "              \"device\": \"/dev/mapper/vg_01-lv_var\",\n" +
            "              \"fstype\": \"xfs\",\n" +
            "              \"options\": \"rw,relatime,attr2,inode64,logbufs=8,logbsize=32k,noquota\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"size\": 3.0,\n" +
            "              \"mount\": \"/tmp\",\n" +
            "              \"device\": \"/dev/mapper/vg_01-lv_tmp\",\n" +
            "              \"fstype\": \"xfs\",\n" +
            "              \"options\": \"rw,relatime,attr2,inode64,logbufs=8,logbsize=32k,noquota\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"size\": 0.49,\n" +
            "              \"mount\": \"/boot\",\n" +
            "              \"device\": \"/dev/sda1\",\n" +
            "              \"fstype\": \"xfs\",\n" +
            "              \"options\": \"rw,relatime,attr2,inode64,logbufs=8,logbsize=32k,noquota\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"size\": 5.0,\n" +
            "              \"mount\": \"/home\",\n" +
            "              \"device\": \"/dev/mapper/vg_01-lv_home\",\n" +
            "              \"fstype\": \"xfs\",\n" +
            "              \"options\": \"rw,relatime,attr2,inode64,logbufs=8,logbsize=32k,noquota\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"tenant\": {\n" +
            "            \"name\": \"Cloud-ltzorg\",\n" +
            "            \"uuid\": \"group-v17901\"\n" +
            "          },\n" +
            "          \"hostname\": \"ltzorg-wfc004lv\",\n" +
            "          \"boot_disk\": {\n" +
            "            \"path\": \"/dev/sda\",\n" +
            "            \"size\": 30,\n" +
            "            \"uuid\": \"6000C29c-06cb-3c70-2073-32f14ebd2be4\"\n" +
            "          },\n" +
            "          \"swap_size\": 2,\n" +
            "          \"extra_nics\": [],\n" +
            "          \"on_support\": true,\n" +
            "          \"os_version\": \"8.5\",\n" +
            "          \"default_nic\": {\n" +
            "            \"mtu\": 1500,\n" +
            "            \"name\": \"ens192\",\n" +
            "            \"uuid\": \"4000\",\n" +
            "            \"subnet\": {\n" +
            "              \"name\": \"dvpg_K37-7-NT_1767\",\n" +
            "              \"uuid\": \"dvportgroup-39910\"\n" +
            "            },\n" +
            "            \"addresses\": [\n" +
            "              {\n" +
            "                \"type\": \"ipv4\",\n" +
            "                \"address\": \"10.221.52.90\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"type\": \"ipv6\",\n" +
            "                \"address\": \"fe80::250:56ff:feb8:a3c9\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"mac_address\": \"00:50:56:b8:a3:c9\",\n" +
            "            \"net_segment\": \"test-srv-perf\",\n" +
            "            \"address_assignment\": \"STATIC\"\n" +
            "          },\n" +
            "          \"environment\": \"LT\",\n" +
            "          \"extra_disks\": [\n" +
            "            {\n" +
            "              \"path\": \"/dev/sdb\",\n" +
            "              \"size\": 50,\n" +
            "              \"uuid\": \"6000C296-ebd3-dd96-2341-546e76f4785d\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"extra_mounts\": [\n" +
            "            {\n" +
            "              \"size\": 30.0,\n" +
            "              \"mount\": \"/app/app\",\n" +
            "              \"device\": \"/dev/mapper/vg_02-lv_app_app\",\n" +
            "              \"fstype\": \"xfs\",\n" +
            "              \"options\": \"rw,relatime,attr2,inode64,logbufs=8,logbsize=32k,noquota\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"size\": 20.0,\n" +
            "              \"mount\": \"/app/logs\",\n" +
            "              \"device\": \"/dev/mapper/vg_02-lv_app_logs\",\n" +
            "              \"fstype\": \"xfs\",\n" +
            "              \"options\": \"rw,relatime,attr2,inode64,logbufs=8,logbsize=32k,noquota\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"resource_pool\": {\n" +
            "            \"name\": \"K37-7-NT\",\n" +
            "            \"uuid\": \"domain-c17538\",\n" +
            "            \"ui_link\": \"https://sv-vc505xv.region.vtb.ru\",\n" +
            "            \"endpoint\": \"https://sv-vc505xv.region.vtb.ru\",\n" +
            "            \"platform\": \"vsphere\",\n" +
            "            \"data_store\": \"datastore-44457\",\n" +
            "            \"tenant_prefix\": \"Cloud\"\n" +
            "          },\n" +
            "          \"ad_integration\": true,\n" +
            "          \"environment_type\": \"TEST\",\n" +
            "          \"default_v4_address\": \"10.221.52.90\",\n" +
            "          \"default_v6_address\": \"None\"\n" +
            "        },\n" +
            "        \"parent\": \"eca32f82-7c0a-535c-8037-5f56672e4f4a\",\n" +
            "        \"provider\": \"vsphere\"\n" +
            "      },\n" +
            "      \"actions\": [\n" +
            "        {\n" +
            "          \"id\": \"df987af1-10f9-4437-8148-3dcaf191491b\",\n" +
            "          \"version\": \"1.0.0\",\n" +
            "          \"create_dt\": \"2022-03-24T18:36:35.111277+03:00\",\n" +
            "          \"update_dt\": \"2022-03-24T18:36:35.127006+03:00\",\n" +
            "          \"current_version\": \"\",\n" +
            "          \"name\": \"check_vm\",\n" +
            "          \"title\": \"Проверить конфигурацию\",\n" +
            "          \"priority\": 0,\n" +
            "          \"description\": \"\",\n" +
            "          \"icon\": \"\",\n" +
            "          \"required_order_statuses\": [\n" +
            "            \"success\",\n" +
            "            \"warning\"\n" +
            "          ],\n" +
            "          \"required_item_statuses\": [\n" +
            "            \"on\",\n" +
            "            \"off\"\n" +
            "          ],\n" +
            "          \"type\": null,\n" +
            "          \"event_type\": [\n" +
            "            \"vm\"\n" +
            "          ],\n" +
            "          \"event_provider\": [\n" +
            "            \"vcloud\",\n" +
            "            \"nutanix\",\n" +
            "            \"vsphere\",\n" +
            "            \"openstack\"\n" +
            "          ],\n" +
            "          \"data_config_path\": null,\n" +
            "          \"data_config_key\": null,\n" +
            "          \"data_config_fields\": [],\n" +
            "          \"config_restriction\": null,\n" +
            "          \"item_restriction\": null,\n" +
            "          \"available_without_money\": false,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": true,\n" +
            "          \"multiple\": false,\n" +
            "          \"location_restriction\": \"\",\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"extra_data\": {},\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.0\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.92\",\n" +
            "          \"graph_id\": \"de5b66c6-737c-4a33-94c0-9c849a6e8642\",\n" +
            "          \"version_create_dt\": \"2021-10-20T21:29:49.513965Z\",\n" +
            "          \"version_changed_by_user\": \"admin\",\n" +
            "          \"active\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"22994556-086b-45da-9500-ce7e07640333\",\n" +
            "          \"version\": \"1.0.0\",\n" +
            "          \"create_dt\": \"2022-03-24T18:36:35.111277+03:00\",\n" +
            "          \"update_dt\": \"2022-03-24T18:36:35.127006+03:00\",\n" +
            "          \"current_version\": \"\",\n" +
            "          \"name\": \"expand_mount_point\",\n" +
            "          \"title\": \"Расширить\",\n" +
            "          \"priority\": 0,\n" +
            "          \"description\": \"Увеличить размер заданной точки монтирования\",\n" +
            "          \"icon\": \"\",\n" +
            "          \"required_order_statuses\": [\n" +
            "            \"success\"\n" +
            "          ],\n" +
            "          \"required_item_statuses\": [\n" +
            "            \"on\"\n" +
            "          ],\n" +
            "          \"type\": null,\n" +
            "          \"event_type\": [\n" +
            "            \"vm\"\n" +
            "          ],\n" +
            "          \"event_provider\": [\n" +
            "            \"nutanix\",\n" +
            "            \"vsphere\",\n" +
            "            \"openstack\"\n" +
            "          ],\n" +
            "          \"data_config_path\": \"data.config.extra_mounts\",\n" +
            "          \"data_config_key\": \"mount\",\n" +
            "          \"data_config_fields\": [],\n" +
            "          \"config_restriction\": \"image['os']['distribution'] in ['rhel', 'ubuntu', 'astra']\",\n" +
            "          \"item_restriction\": null,\n" +
            "          \"available_without_money\": false,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": false,\n" +
            "          \"multiple\": false,\n" +
            "          \"location_restriction\": \"\",\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"extra_data\": {},\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.0\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.42\",\n" +
            "          \"graph_id\": \"fa35e212-b413-436b-87c8-cfade5833477\",\n" +
            "          \"version_create_dt\": \"2021-10-20T18:32:43.806259Z\",\n" +
            "          \"version_changed_by_user\": null,\n" +
            "          \"active\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"f0bf70a7-55f1-4300-bb28-3ea11d4700f1\",\n" +
            "          \"version\": \"1.0.1\",\n" +
            "          \"create_dt\": \"2022-03-24T18:36:35.111277+03:00\",\n" +
            "          \"update_dt\": \"2022-03-24T18:36:35.127006+03:00\",\n" +
            "          \"current_version\": \"\",\n" +
            "          \"name\": \"resize_vm\",\n" +
            "          \"title\": \"Изменить конфигурацию\",\n" +
            "          \"priority\": 0,\n" +
            "          \"description\": \"\",\n" +
            "          \"icon\": \"\",\n" +
            "          \"required_order_statuses\": [\n" +
            "            \"success\"\n" +
            "          ],\n" +
            "          \"required_item_statuses\": [\n" +
            "            \"off\"\n" +
            "          ],\n" +
            "          \"type\": null,\n" +
            "          \"event_type\": [\n" +
            "            \"vm\"\n" +
            "          ],\n" +
            "          \"event_provider\": [\n" +
            "            \"vcloud\",\n" +
            "            \"nutanix\",\n" +
            "            \"vsphere\",\n" +
            "            \"openstack\"\n" +
            "          ],\n" +
            "          \"data_config_path\": null,\n" +
            "          \"data_config_key\": null,\n" +
            "          \"data_config_fields\": [],\n" +
            "          \"config_restriction\": null,\n" +
            "          \"item_restriction\": null,\n" +
            "          \"available_without_money\": false,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": false,\n" +
            "          \"multiple\": false,\n" +
            "          \"location_restriction\": \"\",\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"extra_data\": {},\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.1\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\",\n" +
            "            \"1.0.1\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.16\",\n" +
            "          \"graph_id\": \"777cdad9-73a6-4329-9b81-191f420714b2\",\n" +
            "          \"version_create_dt\": \"2021-11-02T23:22:03.881869Z\",\n" +
            "          \"version_changed_by_user\": \"admin\",\n" +
            "          \"active\": false\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"data_center\": {\n" +
            "    \"id\": \"2022de4f-2c98-43e4-9673-b21e16fa566b\",\n" +
            "    \"site\": \"КШ37\",\n" +
            "    \"code\": \"5\",\n" +
            "    \"name\": \"K37\",\n" +
            "    \"weight\": 110,\n" +
            "    \"description\": \"Коровинское шоссе, д.37\",\n" +
            "    \"label\": \"КШ37 Коровинское шоссе, д.37\",\n" +
            "    \"is_deleted\": false\n" +
            "  },\n" +
            "  \"net_segment\": {\n" +
            "    \"id\": \"47ecc31f-ed05-4548-acb0-075a135ac1c5\",\n" +
            "    \"code\": \"test-srv-perf\",\n" +
            "    \"label\": \"TEST-SRV-PERF\",\n" +
            "    \"weight\": 60,\n" +
            "    \"is_deleted\": false\n" +
            "  },\n" +
            "  \"last_action\": {\n" +
            "    \"id\": \"6b583d29-284d-4d63-bfa8-c36491cc2210\",\n" +
            "    \"status\": \"success\",\n" +
            "    \"has_printable_output\": false\n" +
            "  }\n" +
            "}";
}
