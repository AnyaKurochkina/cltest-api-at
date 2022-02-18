package utils;

import io.restassured.path.json.JsonPath;

public class JsonPathRestAssured {

    public static void main(String[] args) {
        JsonPath jsonPath = new JsonPath(json);
        Object o = jsonPath.get("data.find{it.data.config.containsKey('db_users')}.data.config.db_users.any{it.user_name=='%s'}");
        System.out.println(o);
    }

    static final String json = "{\n" +
            "  \"id\": \"9b206a50-3fe9-4b61-b958-8d810c33bf77\",\n" +
            "  \"created_at\": \"2022-02-14T18:22:22+03:00\",\n" +
            "  \"updated_at\": \"2022-02-14T19:01:47+03:00\",\n" +
            "  \"attrs\": {\n" +
            "    \"domain\": \"corp.dev.vtb\",\n" +
            "    \"flavor\": {\n" +
            "      \"cpus\": 2,\n" +
            "      \"name\": \"c2m4\",\n" +
            "      \"uuid\": \"3dfab004-286c-4f39-9932-baf865756a23\",\n" +
            "      \"memory\": 4\n" +
            "    },\n" +
            "    \"folder\": \"fold-9iwebzt6xy\",\n" +
            "    \"creator\": {\n" +
            "      \"id\": null,\n" +
            "      \"email\": \"ia.morozov@vtb.ru\",\n" +
            "      \"lastname\": \"Морозов\",\n" +
            "      \"username\": \"vtb4050570\",\n" +
            "      \"firstname\": \"Морозов Илья Андреевич (4050570)\"\n" +
            "    },\n" +
            "    \"platform\": \"OpenStack\",\n" +
            "    \"boot_disk\": {\n" +
            "      \"size\": 30\n" +
            "    },\n" +
            "    \"account_id\": \"598df75a-6f8e-4901-b94c-d03e318c77b2\",\n" +
            "    \"extra_nics\": [],\n" +
            "    \"on_support\": false,\n" +
            "    \"os_version\": \"8.4\",\n" +
            "    \"data_center\": \"5\",\n" +
            "    \"default_nic\": {\n" +
            "      \"net_segment\": \"dev-srv-app\"\n" +
            "    },\n" +
            "    \"extra_mounts\": [\n" +
            "      {\n" +
            "        \"path\": \"/pg_data\",\n" +
            "        \"size\": 50,\n" +
            "        \"file_system\": \"xfs\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"project_path\": \"/organization/vtb/folder/fold-s5wu0ff33x/folder/fold-fr4o3pcghy/folder/fold-humog4orc1/folder/fold-cq6hc1jmgd/folder/fold-9iwebzt6xy/project/proj-67nljbzjtt/\",\n" +
            "    \"graph_version\": \"1.0.75\",\n" +
            "    \"preview_items\": [\n" +
            "      {\n" +
            "        \"id\": 40433,\n" +
            "        \"data\": {\n" +
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
            "            \"domain\": \"corp.dev.vtb\",\n" +
            "            \"flavor\": {\n" +
            "              \"cpus\": 2,\n" +
            "              \"name\": \"c2m4\",\n" +
            "              \"uuid\": \"3dfab004-286c-4f39-9932-baf865756a23\",\n" +
            "              \"memory\": 4\n" +
            "            },\n" +
            "            \"mounts\": [\n" +
            "              {\n" +
            "                \"size\": 10.0,\n" +
            "                \"mount\": \"/\",\n" +
            "                \"device\": \"/dev/mapper/vg_01-lv_root\",\n" +
            "                \"fstype\": \"xfs\",\n" +
            "                \"options\": \"rw,relatime,attr2,inode64,logbufs=8,logbsize=32k,noquota\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"size\": 3.0,\n" +
            "                \"mount\": \"/tmp\",\n" +
            "                \"device\": \"/dev/mapper/vg_01-lv_tmp\",\n" +
            "                \"fstype\": \"xfs\",\n" +
            "                \"options\": \"rw,relatime,attr2,inode64,logbufs=8,logbsize=32k,noquota\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"size\": 5.0,\n" +
            "                \"mount\": \"/home\",\n" +
            "                \"device\": \"/dev/mapper/vg_01-lv_home\",\n" +
            "                \"fstype\": \"xfs\",\n" +
            "                \"options\": \"rw,relatime,attr2,inode64,logbufs=8,logbsize=32k,noquota\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"size\": 8.0,\n" +
            "                \"mount\": \"/var\",\n" +
            "                \"device\": \"/dev/mapper/vg_01-lv_var\",\n" +
            "                \"fstype\": \"xfs\",\n" +
            "                \"options\": \"rw,relatime,attr2,inode64,logbufs=8,logbsize=32k,noquota\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"size\": 0.5,\n" +
            "                \"mount\": \"/boot\",\n" +
            "                \"device\": \"/dev/vda1\",\n" +
            "                \"fstype\": \"xfs\",\n" +
            "                \"options\": \"rw,relatime,attr2,inode64,logbufs=8,logbsize=32k,noquota\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"tenant\": {\n" +
            "              \"name\": \"ct1k1-dhzorg\",\n" +
            "              \"uuid\": \"730012dcae5b46848c25043faf276705\"\n" +
            "            },\n" +
            "            \"hostname\": \"dhzorg-pgc001lk\",\n" +
            "            \"boot_disk\": {\n" +
            "              \"path\": \"/dev/vda\",\n" +
            "              \"size\": 30,\n" +
            "              \"uuid\": \"b8c384e5-a901-497f-acd3-800b47749508\",\n" +
            "              \"serial\": \"virtio-b8c384e5-a901-497f-a\"\n" +
            "            },\n" +
            "            \"swap_size\": 2,\n" +
            "            \"extra_nics\": [],\n" +
            "            \"on_support\": false,\n" +
            "            \"os_version\": \"8.4\",\n" +
            "            \"default_nic\": {\n" +
            "              \"mtu\": 9000,\n" +
            "              \"name\": \"ens3\",\n" +
            "              \"uuid\": \"2779b164-f41d-4b30-8f72-20c010a0fa6a\",\n" +
            "              \"subnet\": {\n" +
            "                \"name\": \"DEV-SRV-APP@10.226.32.0/19\",\n" +
            "                \"uuid\": \"f9082445-c477-4f70-88ec-7cfdd1a79c39\"\n" +
            "              },\n" +
            "              \"addresses\": [\n" +
            "                {\n" +
            "                  \"type\": \"ipv4\",\n" +
            "                  \"address\": \"10.226.37.44\"\n" +
            "                }\n" +
            "              ],\n" +
            "              \"mac_address\": \"02:27:79:b1:64:f4\",\n" +
            "              \"net_segment\": \"dev-srv-app\",\n" +
            "              \"address_assignment\": \"DHCP\"\n" +
            "            },\n" +
            "            \"environment\": \"DEV\",\n" +
            "            \"extra_disks\": [\n" +
            "              {\n" +
            "                \"path\": \"/dev/vdb\",\n" +
            "                \"size\": 50,\n" +
            "                \"uuid\": \"b6809b24-1117-4e2c-9fc7-9ff14e8ec521\",\n" +
            "                \"serial\": \"virtio-b6809b24-1117-4e2c-9\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"extra_mounts\": [\n" +
            "              {\n" +
            "                \"size\": 50.0,\n" +
            "                \"mount\": \"/pg_data\",\n" +
            "                \"device\": \"/dev/mapper/vg_02-lv_pg_data\",\n" +
            "                \"fstype\": \"xfs\",\n" +
            "                \"options\": \"rw,relatime,attr2,inode64,logbufs=8,logbsize=32k,noquota\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"resource_pool\": {\n" +
            "              \"name\": \"ct1k1\",\n" +
            "              \"uuid\": \"ab113d3e-3096-434d-8106-3c45adc00042\",\n" +
            "              \"domain\": \"Cloud\",\n" +
            "              \"region\": \"ct1k1\",\n" +
            "              \"ui_link\": \"https://public-ct1.cloud.vtb.ru\",\n" +
            "              \"endpoint\": \"https://public-ct1.cloud.vtb.ru:5000\",\n" +
            "              \"platform\": \"openstack\",\n" +
            "              \"tenant_prefix\": \"ct1k1\"\n" +
            "            },\n" +
            "            \"ad_integration\": true,\n" +
            "            \"environment_type\": \"DEV\",\n" +
            "            \"default_v4_address\": \"10.226.37.44\",\n" +
            "            \"default_v6_address\": \"None\"\n" +
            "          },\n" +
            "          \"parent\": \"38e139b4-1db5-5daf-9c1a-de015375da83\",\n" +
            "          \"provider\": \"openstack\"\n" +
            "        },\n" +
            "        \"type\": \"vm\",\n" +
            "        \"item_id\": \"1f93b645-e723-4f5e-8fb0-8cb4158acc81\",\n" +
            "        \"graph_id\": \"8975c85e-3f90-482f-a765-86d06013db27\",\n" +
            "        \"order_id\": \"9b206a50-3fe9-4b61-b958-8d810c33bf77\",\n" +
            "        \"action_id\": \"bf9a9fb0-0dcf-441e-89c1-29b85971a694\",\n" +
            "        \"update_dt\": \"2022-02-14T15:30:52.765008Z\",\n" +
            "        \"created_row_dt\": \"2022-02-14T15:23:28.457163Z\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 40436,\n" +
            "        \"data\": {\n" +
            "          \"state\": \"on\",\n" +
            "          \"config\": {\n" +
            "            \"dbs\": [],\n" +
            "            \"version\": \"12.10\",\n" +
            "            \"db_users\": [],\n" +
            "            \"db_owners\": [],\n" +
            "            \"configuration\": {\n" +
            "              \"max_connections\": 100\n" +
            "            },\n" +
            "            \"connection_url\": \"postgresql://dhzorg-pgc001lk.corp.dev.vtb:5432\"\n" +
            "          },\n" +
            "          \"provider\": \"postgresql_v001\"\n" +
            "        },\n" +
            "        \"type\": \"app\",\n" +
            "        \"item_id\": \"38e139b4-1db5-5daf-9c1a-de015375da83\",\n" +
            "        \"graph_id\": \"8975c85e-3f90-482f-a765-86d06013db27\",\n" +
            "        \"order_id\": \"9b206a50-3fe9-4b61-b958-8d810c33bf77\",\n" +
            "        \"action_id\": \"bf9a9fb0-0dcf-441e-89c1-29b85971a694\",\n" +
            "        \"update_dt\": \"2022-02-14T15:30:52.741151Z\",\n" +
            "        \"created_row_dt\": \"2022-02-14T15:30:52.540673Z\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"product_title\": \"PostgreSQL\",\n" +
            "    \"ad_integration\": true,\n" +
            "    \"tariff_plan_id\": \"d503215e-689e-402f-98a7-3dd084454fae\",\n" +
            "    \"ad_logon_grants\": [\n" +
            "      {\n" +
            "        \"role\": \"superuser\",\n" +
            "        \"groups\": [\n" +
            "          \"cloud-dhzorg-123\"\n" +
            "        ]\n" +
            "      }\n" +
            "    ],\n" +
            "    \"organization_name\": \"vtb\",\n" +
            "    \"postgresql_config\": {\n" +
            "      \"max_connections\": 100\n" +
            "    },\n" +
            "    \"postgresql_version\": \"12\",\n" +
            "    \"environment_prefix_id\": \"1034b18c-ce3d-4abc-bd21-90e910607632\",\n" +
            "    \"information_system_id\": \"8e33baf8-e181-457b-9d60-e5dc470fe8fa\",\n" +
            "    \"project_environment_id\": \"ce11798a-e5f6-4e54-9582-78770f15b52a\",\n" +
            "    \"information_system_priority\": 2\n" +
            "  },\n" +
            "  \"status\": \"success\",\n" +
            "  \"label\": \"PostgreSQL\",\n" +
            "  \"category\": \"vm\",\n" +
            "  \"deletable\": true,\n" +
            "  \"power_status\": [\n" +
            "    {\n" +
            "      \"status\": \"on\",\n" +
            "      \"item_id\": \"1f93b645-e723-4f5e-8fb0-8cb4158acc81\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"product_data\": [\n" +
            "    {\n" +
            "      \"ip\": \"10.226.37.44\",\n" +
            "      \"type\": \"vm\",\n" +
            "      \"hostname\": \"dhzorg-pgc001lk\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"project_name\": \"proj-67nljbzjtt\",\n" +
            "  \"product_id\": \"ff48b6ff-700a-42f3-8a55-deee61e2d5a7\",\n" +
            "  \"data\": [\n" +
            "    {\n" +
            "      \"item_id\": \"38e139b4-1db5-5daf-9c1a-de015375da83\",\n" +
            "      \"actions\": [\n" +
            "        {\n" +
            "          \"id\": \"73bcef59-2815-4b21-a942-759c7f56b11b\",\n" +
            "          \"version\": \"1.0.0\",\n" +
            "          \"name\": \"reset_db_owner_password\",\n" +
            "          \"title\": \"Сбросить пароль\",\n" +
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
            "            \"app\"\n" +
            "          ],\n" +
            "          \"event_provider\": [\n" +
            "            \"postgrespro\",\n" +
            "            \"postgresql_v001\"\n" +
            "          ],\n" +
            "          \"data_config_path\": \"db_owners\",\n" +
            "          \"data_config_key\": \"user_name\",\n" +
            "          \"data_config_fields\": [],\n" +
            "          \"config_restriction\": \"len(db_owners) \\u003e 0\",\n" +
            "          \"item_restriction\": null,\n" +
            "          \"available_without_money\": true,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": true,\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.0\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.14\",\n" +
            "          \"graph_id\": \"e6beb047-adb0-4975-9dad-32c323c7f843\",\n" +
            "          \"version_create_dt\": \"2021-10-20T18:32:43.445068Z\",\n" +
            "          \"version_changed_by_user\": null,\n" +
            "          \"active\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"c76bbc26-9883-4afa-8e2c-8c8c9f9c3c5f\",\n" +
            "          \"version\": \"1.0.1\",\n" +
            "          \"name\": \"stop_hard_two_layer\",\n" +
            "          \"title\": \"Выключить принудительно\",\n" +
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
            "            \"haproxy\",\n" +
            "            \"wildfly\",\n" +
            "            \"rabbitmq\",\n" +
            "            \"scylladb\",\n" +
            "            \"clickhouse\",\n" +
            "            \"postgrespro\",\n" +
            "            \"postgresql_v001\"\n" +
            "          ],\n" +
            "          \"data_config_path\": null,\n" +
            "          \"data_config_key\": null,\n" +
            "          \"data_config_fields\": [],\n" +
            "          \"config_restriction\": null,\n" +
            "          \"item_restriction\": null,\n" +
            "          \"available_without_money\": false,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": false,\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.1\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\",\n" +
            "            \"1.0.1\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.9\",\n" +
            "          \"graph_id\": \"67f2c71f-0ae0-4828-bfc1-dc856e6b3630\",\n" +
            "          \"version_create_dt\": \"2022-01-06T19:37:49.908734Z\",\n" +
            "          \"version_changed_by_user\": \"usp\",\n" +
            "          \"active\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"8556ecd9-e07d-47df-9fbb-3b5587cc164f\",\n" +
            "          \"version\": \"1.0.0\",\n" +
            "          \"name\": \"remove_db\",\n" +
            "          \"title\": \"Удалить БД\",\n" +
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
            "            \"app\"\n" +
            "          ],\n" +
            "          \"event_provider\": [\n" +
            "            \"postgrespro\",\n" +
            "            \"postgresql_v001\"\n" +
            "          ],\n" +
            "          \"data_config_path\": \"dbs\",\n" +
            "          \"data_config_key\": \"db_name\",\n" +
            "          \"data_config_fields\": [],\n" +
            "          \"config_restriction\": \"len(dbs) \\u003e 0\",\n" +
            "          \"item_restriction\": null,\n" +
            "          \"available_without_money\": true,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": true,\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.0\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.11\",\n" +
            "          \"graph_id\": \"f9f76c00-495b-433d-be3b-a9399ef72be0\",\n" +
            "          \"version_create_dt\": \"2021-10-20T18:32:43.856475Z\",\n" +
            "          \"version_changed_by_user\": null,\n" +
            "          \"active\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"6e2294d8-3cf1-42a3-b6c2-372b9c659c98\",\n" +
            "          \"version\": \"1.0.2\",\n" +
            "          \"name\": \"stop_two_layer\",\n" +
            "          \"title\": \"Выключить\",\n" +
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
            "            \"haproxy\",\n" +
            "            \"wildfly\",\n" +
            "            \"rabbitmq\",\n" +
            "            \"scylladb\",\n" +
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
            "          \"available_without_money\": true,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": false,\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.2\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\",\n" +
            "            \"1.0.1\",\n" +
            "            \"1.0.2\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.10\",\n" +
            "          \"graph_id\": \"39c1a317-7a2e-4874-8c28-91080bde5578\",\n" +
            "          \"version_create_dt\": \"2022-01-06T19:37:02.361397Z\",\n" +
            "          \"version_changed_by_user\": \"usp\",\n" +
            "          \"active\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"622340ff-de5f-4fe9-b1c6-197a42ebc552\",\n" +
            "          \"version\": \"1.0.0\",\n" +
            "          \"name\": \"create_dbms_user\",\n" +
            "          \"title\": \"Добавить пользователя\",\n" +
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
            "            \"app\"\n" +
            "          ],\n" +
            "          \"event_provider\": [\n" +
            "            \"postgrespro\",\n" +
            "            \"postgresql_v001\"\n" +
            "          ],\n" +
            "          \"data_config_path\": \"db_users\",\n" +
            "          \"data_config_key\": null,\n" +
            "          \"data_config_fields\": [],\n" +
            "          \"config_restriction\": \"len(dbs) \\u003e 0\",\n" +
            "          \"item_restriction\": null,\n" +
            "          \"available_without_money\": true,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": true,\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.0\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.67\",\n" +
            "          \"graph_id\": \"1a874273-391b-4828-9803-2b653a6cbb0d\",\n" +
            "          \"version_create_dt\": \"2021-10-20T18:32:43.387297Z\",\n" +
            "          \"version_changed_by_user\": null,\n" +
            "          \"active\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"8f2f39e9-5877-42e0-87aa-396be2f48790\",\n" +
            "          \"version\": \"1.0.6\",\n" +
            "          \"name\": \"delete_two_layer\",\n" +
            "          \"title\": \"Удалить рекурсивно\",\n" +
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
            "            \"kafka_develop\",\n" +
            "            \"nginx_develop\",\n" +
            "            \"postgresql_v001\",\n" +
            "            \"wildfly_develop\",\n" +
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
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.6\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\",\n" +
            "            \"1.0.1\",\n" +
            "            \"1.0.2\",\n" +
            "            \"1.0.3\",\n" +
            "            \"1.0.4\",\n" +
            "            \"1.0.6\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.7\",\n" +
            "          \"graph_id\": \"c8df29d7-cb51-4b0f-867d-4619f9463483\",\n" +
            "          \"version_create_dt\": \"2022-01-26T06:03:09.417954Z\",\n" +
            "          \"version_changed_by_user\": \"usp\",\n" +
            "          \"active\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"e80c7f02-a931-4682-b9a8-b7881c66980d\",\n" +
            "          \"version\": \"1.0.2\",\n" +
            "          \"name\": \"start_two_layer\",\n" +
            "          \"title\": \"Включить\",\n" +
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
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.2\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\",\n" +
            "            \"1.0.1\",\n" +
            "            \"1.0.2\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.8\",\n" +
            "          \"graph_id\": \"22d4b92f-c72c-46b5-b317-f26df1500a94\",\n" +
            "          \"version_create_dt\": \"2022-01-08T14:04:41.869134Z\",\n" +
            "          \"version_changed_by_user\": \"usp\",\n" +
            "          \"active\": false\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"4701735f-f53f-412b-ac48-b5e4b578114c\",\n" +
            "          \"version\": \"1.0.1\",\n" +
            "          \"name\": \"reset_two_layer\",\n" +
            "          \"title\": \"Перезагрузить\",\n" +
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
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
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
            "          \"id\": \"59cfa43c-9e36-4f7a-9a5f-c57ee286476b\",\n" +
            "          \"version\": \"1.0.0\",\n" +
            "          \"name\": \"create_db\",\n" +
            "          \"title\": \"Добавить БД\",\n" +
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
            "            \"app\"\n" +
            "          ],\n" +
            "          \"event_provider\": [\n" +
            "            \"postgrespro\",\n" +
            "            \"postgresql_v001\"\n" +
            "          ],\n" +
            "          \"data_config_path\": \"dbs\",\n" +
            "          \"data_config_key\": null,\n" +
            "          \"data_config_fields\": [],\n" +
            "          \"config_restriction\": \"len(dbs) \\u003c 51\",\n" +
            "          \"item_restriction\": null,\n" +
            "          \"available_without_money\": true,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": true,\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.0\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.52\",\n" +
            "          \"graph_id\": \"47bd1200-0060-4937-accf-f4fc90063af1\",\n" +
            "          \"version_create_dt\": \"2021-10-20T18:32:43.871645Z\",\n" +
            "          \"version_changed_by_user\": null,\n" +
            "          \"active\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"b33164b1-cff6-470c-bf57-2c7e4bdb19c1\",\n" +
            "          \"version\": \"1.0.0\",\n" +
            "          \"name\": \"resize_two_layer\",\n" +
            "          \"title\": \"Изменить конфигурацию\",\n" +
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
            "            \"app\"\n" +
            "          ],\n" +
            "          \"event_provider\": [\n" +
            "            \"redis\",\n" +
            "            \"postgrespro\",\n" +
            "            \"postgresql_v001\"\n" +
            "          ],\n" +
            "          \"data_config_path\": null,\n" +
            "          \"data_config_key\": null,\n" +
            "          \"data_config_fields\": [],\n" +
            "          \"config_restriction\": null,\n" +
            "          \"item_restriction\": null,\n" +
            "          \"available_without_money\": false,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": false,\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.0\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.11\",\n" +
            "          \"graph_id\": \"21ae28f0-1a9f-4797-9850-c90e7d7538b5\",\n" +
            "          \"version_create_dt\": \"2021-11-02T23:00:44.248683Z\",\n" +
            "          \"version_changed_by_user\": \"admin\",\n" +
            "          \"active\": true\n" +
            "        }\n" +
            "      ],\n" +
            "      \"type\": \"app\",\n" +
            "      \"config\": {\n" +
            "        \"dbs\": [\n" +
            "          {\n" +
            "            \"db_name\": \"bazan\",\n" +
            "            \"encoding\": \"UTF-8\",\n" +
            "            \"lc_ctype\": \"ru_RU.UTF-8\",\n" +
            "            \"lc_collate\": \"ru_RU.UTF-8\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"version\": \"12.10\",\n" +
            "        \"db_users\": [],\n" +
            "        \"db_owners\": [\n" +
            "          {\n" +
            "            \"comment\": \"\",\n" +
            "            \"db_name\": \"bazan\",\n" +
            "            \"dbms_role\": \"\",\n" +
            "            \"user_name\": \"bazan_admin\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"configuration\": {\n" +
            "          \"max_connections\": 100\n" +
            "        },\n" +
            "        \"connection_url\": \"postgresql://dhzorg-pgc001lk.corp.dev.vtb:5432\"\n" +
            "      },\n" +
            "      \"virtualization\": null,\n" +
            "      \"state\": \"on\",\n" +
            "      \"parent\": null,\n" +
            "      \"provider\": \"postgresql_v001\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"item_id\": \"1f93b645-e723-4f5e-8fb0-8cb4158acc81\",\n" +
            "      \"actions\": [\n" +
            "        {\n" +
            "          \"id\": \"22994556-086b-45da-9500-ce7e07640333\",\n" +
            "          \"version\": \"1.0.0\",\n" +
            "          \"name\": \"expand_mount_point\",\n" +
            "          \"title\": \"Расширить\",\n" +
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
            "          \"data_config_path\": \"extra_mounts\",\n" +
            "          \"data_config_key\": \"mount\",\n" +
            "          \"data_config_fields\": [],\n" +
            "          \"config_restriction\": \"image['os']['distribution'] in ['rhel', 'ubuntu', 'astra']\",\n" +
            "          \"item_restriction\": null,\n" +
            "          \"available_without_money\": false,\n" +
            "          \"auto_removing_if_failed\": false,\n" +
            "          \"skip_on_prebilling\": false,\n" +
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
            "          \"graph_version\": \"\",\n" +
            "          \"last_version\": \"1.0.0\",\n" +
            "          \"version_list\": [\n" +
            "            \"1.0.0\"\n" +
            "          ],\n" +
            "          \"graph_version_calculated\": \"1.0.41\",\n" +
            "          \"graph_id\": \"fa35e212-b413-436b-87c8-cfade5833477\",\n" +
            "          \"version_create_dt\": \"2021-10-20T18:32:43.806259Z\",\n" +
            "          \"version_changed_by_user\": null,\n" +
            "          \"active\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"df987af1-10f9-4437-8148-3dcaf191491b\",\n" +
            "          \"version\": \"1.0.0\",\n" +
            "          \"name\": \"check_vm\",\n" +
            "          \"title\": \"Проверить конфигурацию\",\n" +
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
            "          \"graph_version_pattern\": \"\",\n" +
            "          \"allowed_groups\": [],\n" +
            "          \"restricted_groups\": [],\n" +
            "          \"allowed_paths\": [],\n" +
            "          \"restricted_paths\": [],\n" +
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
            "        }\n" +
            "      ],\n" +
            "      \"type\": \"vm\",\n" +
            "      \"config\": {\n" +
            "        \"image\": {\n" +
            "          \"os\": {\n" +
            "            \"type\": \"linux\",\n" +
            "            \"vendor\": \"ibm\",\n" +
            "            \"version\": \"8.4\",\n" +
            "            \"architecture\": \"x86_64\",\n" +
            "            \"distribution\": \"rhel\",\n" +
            "            \"localization\": \"en\"\n" +
            "          },\n" +
            "          \"name\": \"tpl_linux_rhel_8.4_x86_64_en\",\n" +
            "          \"size\": 30,\n" +
            "          \"uuid\": \"4c2002cf-5d94-4f12-8e12-175e944b9c32\"\n" +
            "        },\n" +
            "        \"domain\": \"corp.dev.vtb\",\n" +
            "        \"flavor\": {\n" +
            "          \"cpus\": 2,\n" +
            "          \"name\": \"c2m4\",\n" +
            "          \"uuid\": \"3dfab004-286c-4f39-9932-baf865756a23\",\n" +
            "          \"memory\": 4\n" +
            "        },\n" +
            "        \"mounts\": [\n" +
            "          {\n" +
            "            \"size\": 10.0,\n" +
            "            \"mount\": \"/\",\n" +
            "            \"device\": \"/dev/mapper/vg_01-lv_root\",\n" +
            "            \"fstype\": \"xfs\",\n" +
            "            \"options\": \"rw,relatime,attr2,inode64,logbufs=8,logbsize=32k,noquota\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"size\": 3.0,\n" +
            "            \"mount\": \"/tmp\",\n" +
            "            \"device\": \"/dev/mapper/vg_01-lv_tmp\",\n" +
            "            \"fstype\": \"xfs\",\n" +
            "            \"options\": \"rw,relatime,attr2,inode64,logbufs=8,logbsize=32k,noquota\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"size\": 5.0,\n" +
            "            \"mount\": \"/home\",\n" +
            "            \"device\": \"/dev/mapper/vg_01-lv_home\",\n" +
            "            \"fstype\": \"xfs\",\n" +
            "            \"options\": \"rw,relatime,attr2,inode64,logbufs=8,logbsize=32k,noquota\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"size\": 8.0,\n" +
            "            \"mount\": \"/var\",\n" +
            "            \"device\": \"/dev/mapper/vg_01-lv_var\",\n" +
            "            \"fstype\": \"xfs\",\n" +
            "            \"options\": \"rw,relatime,attr2,inode64,logbufs=8,logbsize=32k,noquota\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"size\": 0.5,\n" +
            "            \"mount\": \"/boot\",\n" +
            "            \"device\": \"/dev/vda1\",\n" +
            "            \"fstype\": \"xfs\",\n" +
            "            \"options\": \"rw,relatime,attr2,inode64,logbufs=8,logbsize=32k,noquota\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"tenant\": {\n" +
            "          \"name\": \"ct1k1-dhzorg\",\n" +
            "          \"uuid\": \"730012dcae5b46848c25043faf276705\"\n" +
            "        },\n" +
            "        \"hostname\": \"dhzorg-pgc001lk\",\n" +
            "        \"boot_disk\": {\n" +
            "          \"path\": \"/dev/vda\",\n" +
            "          \"size\": 30,\n" +
            "          \"uuid\": \"b8c384e5-a901-497f-acd3-800b47749508\",\n" +
            "          \"serial\": \"virtio-b8c384e5-a901-497f-a\"\n" +
            "        },\n" +
            "        \"swap_size\": 2,\n" +
            "        \"extra_nics\": [],\n" +
            "        \"on_support\": false,\n" +
            "        \"os_version\": \"8.4\",\n" +
            "        \"default_nic\": {\n" +
            "          \"mtu\": 9000,\n" +
            "          \"name\": \"ens3\",\n" +
            "          \"uuid\": \"2779b164-f41d-4b30-8f72-20c010a0fa6a\",\n" +
            "          \"subnet\": {\n" +
            "            \"name\": \"DEV-SRV-APP@10.226.32.0/19\",\n" +
            "            \"uuid\": \"f9082445-c477-4f70-88ec-7cfdd1a79c39\"\n" +
            "          },\n" +
            "          \"addresses\": [\n" +
            "            {\n" +
            "              \"type\": \"ipv4\",\n" +
            "              \"address\": \"10.226.37.44\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"mac_address\": \"02:27:79:b1:64:f4\",\n" +
            "          \"net_segment\": \"dev-srv-app\",\n" +
            "          \"address_assignment\": \"DHCP\"\n" +
            "        },\n" +
            "        \"environment\": \"DEV\",\n" +
            "        \"extra_disks\": [\n" +
            "          {\n" +
            "            \"path\": \"/dev/vdb\",\n" +
            "            \"size\": 50,\n" +
            "            \"uuid\": \"b6809b24-1117-4e2c-9fc7-9ff14e8ec521\",\n" +
            "            \"serial\": \"virtio-b6809b24-1117-4e2c-9\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"extra_mounts\": [\n" +
            "          {\n" +
            "            \"size\": 50.0,\n" +
            "            \"mount\": \"/pg_data\",\n" +
            "            \"device\": \"/dev/mapper/vg_02-lv_pg_data\",\n" +
            "            \"fstype\": \"xfs\",\n" +
            "            \"options\": \"rw,relatime,attr2,inode64,logbufs=8,logbsize=32k,noquota\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"resource_pool\": {\n" +
            "          \"name\": \"ct1k1\",\n" +
            "          \"uuid\": \"ab113d3e-3096-434d-8106-3c45adc00042\",\n" +
            "          \"domain\": \"Cloud\",\n" +
            "          \"region\": \"ct1k1\",\n" +
            "          \"ui_link\": \"https://public-ct1.cloud.vtb.ru\",\n" +
            "          \"endpoint\": \"https://public-ct1.cloud.vtb.ru:5000\",\n" +
            "          \"platform\": \"openstack\",\n" +
            "          \"tenant_prefix\": \"ct1k1\"\n" +
            "        },\n" +
            "        \"ad_integration\": true,\n" +
            "        \"environment_type\": \"DEV\",\n" +
            "        \"default_v4_address\": \"10.226.37.44\",\n" +
            "        \"default_v6_address\": \"None\"\n" +
            "      },\n" +
            "      \"virtualization\": null,\n" +
            "      \"state\": \"on\",\n" +
            "      \"parent\": \"38e139b4-1db5-5daf-9c1a-de015375da83\",\n" +
            "      \"provider\": \"openstack\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"data_center\": {\n" +
            "    \"id\": \"2022de4f-2c98-43e4-9673-b21e16fa566b\",\n" +
            "    \"site\": \"КШ37\",\n" +
            "    \"code\": \"5\",\n" +
            "    \"name\": \"K37\",\n" +
            "    \"description\": \"Коровинское шоссе, д.37\",\n" +
            "    \"label\": \"КШ37 Коровинское шоссе, д.37\",\n" +
            "    \"is_deleted\": false\n" +
            "  },\n" +
            "  \"net_segment\": {\n" +
            "    \"id\": \"b7ffd0bb-ccb7-4877-9679-7f5fd97b9df2\",\n" +
            "    \"code\": \"dev-srv-app\",\n" +
            "    \"label\": \"DEV-SRV-APP\",\n" +
            "    \"is_deleted\": false\n" +
            "  }\n" +
            "}";
}
