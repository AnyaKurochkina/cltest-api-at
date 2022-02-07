package utils;

import io.restassured.path.json.JsonPath;

public class JsonPathRestAssured {

    public static void main(String[] args) {
        JsonPath jsonPath = new JsonPath(json);
        Object o = jsonPath.get("json_schema.properties.postgresql_version.collect{e -> e}.shuffled()[0]");
        System.out.println(o);
    }

    static final String json = "{\n" +
            "  \"id\": \"3affdd44-a3ab-4b90-90a9-0c1f4bbdd6fd\",\n" +
            "  \"graph\": [\n" +
            "    {\n" +
            "      \"run\": \"internal\",\n" +
            "      \"icon\": \"\",\n" +
            "      \"name\": \"add_parent_to_vm\",\n" +
            "      \"color\": \"\",\n" +
            "      \"count\": \"len(items)\",\n" +
            "      \"input\": {\n" +
            "        \"type\": \"enums.EventType.VM.value\",\n" +
            "        \"status\": \"postgresql_item_id\",\n" +
            "        \"item_id\": \"items[counter]['id']\",\n" +
            "        \"subtype\": \"enums.EventSubType.PARENT.value\"\n" +
            "      },\n" +
            "      \"output\": {},\n" +
            "      \"depends\": [\n" +
            "        \"add_postgresql_item\"\n" +
            "      ],\n" +
            "      \"timeout\": 30,\n" +
            "      \"coords_x\": 0.0,\n" +
            "      \"coords_y\": 0.0,\n" +
            "      \"priority\": 0,\n" +
            "      \"template\": \"add_event\",\n" +
            "      \"log_level\": null,\n" +
            "      \"description\": \"добавляем связь ВМ и PostgreSQL в сервис состояний\",\n" +
            "      \"on_prebilling\": false,\n" +
            "      \"allowed_groups\": [],\n" +
            "      \"printed_output\": {},\n" +
            "      \"template_version\": \"\",\n" +
            "      \"restricted_groups\": [],\n" +
            "      \"not_damage_on_error\": false,\n" +
            "      \"damage_order_on_error\": false,\n" +
            "      \"log_can_be_overridden\": false,\n" +
            "      \"template_version_pattern\": \"\",\n" +
            "      \"priority_can_be_overridden\": false,\n" +
            "      \"printed_output_can_be_overridden\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"icon\": \"\",\n" +
            "      \"name\": \"add_postgresql_item\",\n" +
            "      \"color\": \"\",\n" +
            "      \"input\": {\n" +
            "        \"type\": \"enums.EventType.APP.value\",\n" +
            "        \"config\": \"{'version': postgresql_version, 'configuration': postgresql_config, **postgresql_real_config}\",\n" +
            "        \"status\": \"enums.EventState.ON.value\",\n" +
            "        \"item_id\": \"postgresql_item_id\",\n" +
            "        \"provider\": \"enums.EventProvider.POSTGRESQL_V001.value\",\n" +
            "        \"subgraph_id\": \"'8975c85e-3f90-482f-a765-86d06013db27'\",\n" +
            "        \"subgraph_version\": \"\",\n" +
            "        \"subgraph_version_pattern\": \"\"\n" +
            "      },\n" +
            "      \"output\": {},\n" +
            "      \"depends\": [\n" +
            "        \"deploy_postgresql\"\n" +
            "      ],\n" +
            "      \"coords_x\": 0.0,\n" +
            "      \"coords_y\": 0.0,\n" +
            "      \"priority\": null,\n" +
            "      \"template\": \"run_subgraph\",\n" +
            "      \"log_level\": null,\n" +
            "      \"description\": \"добавляем PostgreSQL в сервисе состояний\",\n" +
            "      \"on_prebilling\": false,\n" +
            "      \"printed_output\": null,\n" +
            "      \"template_version\": \"\",\n" +
            "      \"not_damage_on_error\": false,\n" +
            "      \"damage_order_on_error\": false,\n" +
            "      \"template_version_pattern\": \"\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"icon\": \"\",\n" +
            "      \"name\": \"create_postgresql_node\",\n" +
            "      \"color\": \"\",\n" +
            "      \"input\": {\n" +
            "        \"role\": \"role\",\n" +
            "        \"image\": \"image\",\n" +
            "        \"domain\": \"domain\",\n" +
            "        \"flavor\": \"flavor\",\n" +
            "        \"tenant\": \"tenant\",\n" +
            "        \"is_code\": \"is_code\",\n" +
            "        \"quantity\": \"quantity\",\n" +
            "        \"boot_disk\": \"boot_disk\",\n" +
            "        \"env_prefix\": \"env_prefix\",\n" +
            "        \"extra_nics\": \"extra_nics\",\n" +
            "        \"on_support\": \"on_support\",\n" +
            "        \"data_center\": \"data_center\",\n" +
            "        \"default_nic\": \"default_nic\",\n" +
            "        \"environment\": \"environment\",\n" +
            "        \"extra_disks\": \"extra_disks\",\n" +
            "        \"subgraph_id\": \"'09614aee-7fe4-4825-99bd-cb8032152502'\",\n" +
            "        \"extra_mounts\": \"extra_mounts\",\n" +
            "        \"product_type\": \"product_type\",\n" +
            "        \"business_line\": \"business_line\",\n" +
            "        \"resource_pool\": \"resource_pool\",\n" +
            "        \"ad_integration\": \"ad_integration\",\n" +
            "        \"os_local_users\": \"os_local_users\",\n" +
            "        \"ad_logon_grants\": \"ad_logon_grants\",\n" +
            "        \"environment_type\": \"environment_type\",\n" +
            "        \"subgraph_version\": \"\",\n" +
            "        \"subgraph_version_pattern\": \"\"\n" +
            "      },\n" +
            "      \"output\": {\n" +
            "        \"update_vm_configs\": \"items\"\n" +
            "      },\n" +
            "      \"depends\": [],\n" +
            "      \"coords_x\": 0.0,\n" +
            "      \"coords_y\": 0.0,\n" +
            "      \"priority\": null,\n" +
            "      \"template\": \"run_subgraph\",\n" +
            "      \"log_level\": null,\n" +
            "      \"description\": \"Создание и настройка ВМ\",\n" +
            "      \"on_prebilling\": false,\n" +
            "      \"printed_output\": null,\n" +
            "      \"template_version\": \"\",\n" +
            "      \"not_damage_on_error\": false,\n" +
            "      \"damage_order_on_error\": false,\n" +
            "      \"template_version_pattern\": \"\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"run\": \"awx:cmd\",\n" +
            "      \"icon\": \"\",\n" +
            "      \"name\": \"deploy_postgresql\",\n" +
            "      \"color\": \"\",\n" +
            "      \"input\": {\n" +
            "        \"inventory\": \"inventory\",\n" +
            "        \"extra_vars\": \"extra_vars\",\n" +
            "        \"credentials\": \"[credential]\",\n" +
            "        \"net_segment\": \"default_nic['net_segment']\",\n" +
            "        \"job_template\": \"job_template\"\n" +
            "      },\n" +
            "      \"output\": {\n" +
            "        \"config\": \"postgresql_real_config\",\n" +
            "        \"item_id\": \"postgresql_item_id\"\n" +
            "      },\n" +
            "      \"depends\": [\n" +
            "        \"make_inventory\"\n" +
            "      ],\n" +
            "      \"timeout\": 600,\n" +
            "      \"coords_x\": 0.0,\n" +
            "      \"coords_y\": 0.0,\n" +
            "      \"priority\": 0,\n" +
            "      \"template\": \"ansible_awx\",\n" +
            "      \"log_level\": null,\n" +
            "      \"description\": \"развертываем PostgreSQL\",\n" +
            "      \"on_prebilling\": false,\n" +
            "      \"allowed_groups\": [],\n" +
            "      \"printed_output\": {},\n" +
            "      \"template_version\": \"\",\n" +
            "      \"restricted_groups\": [],\n" +
            "      \"not_damage_on_error\": false,\n" +
            "      \"damage_order_on_error\": false,\n" +
            "      \"log_can_be_overridden\": false,\n" +
            "      \"template_version_pattern\": \"\",\n" +
            "      \"priority_can_be_overridden\": false,\n" +
            "      \"printed_output_can_be_overridden\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"run\": \"internal\",\n" +
            "      \"icon\": \"\",\n" +
            "      \"name\": \"make_inventory\",\n" +
            "      \"color\": \"\",\n" +
            "      \"input\": {\n" +
            "        \"items\": \"items\",\n" +
            "        \"template\": \"inventory_template\",\n" +
            "        \"from_json\": \"True\",\n" +
            "        \"net_segment\": \"default_nic['net_segment']\",\n" +
            "        \"postgresql_config\": \"postgresql_config\",\n" +
            "        \"postgresql_version\": \"postgresql_version\"\n" +
            "      },\n" +
            "      \"output\": {\n" +
            "        \"formatted\": \"inventory\"\n" +
            "      },\n" +
            "      \"depends\": [\n" +
            "        \"create_postgresql_node\"\n" +
            "      ],\n" +
            "      \"timeout\": 10,\n" +
            "      \"coords_x\": 0.0,\n" +
            "      \"coords_y\": 0.0,\n" +
            "      \"priority\": 0,\n" +
            "      \"template\": \"jinja2_format\",\n" +
            "      \"log_level\": \"full\",\n" +
            "      \"description\": \"формируем данные для развертывания PostgreSQL\",\n" +
            "      \"on_prebilling\": false,\n" +
            "      \"allowed_groups\": [],\n" +
            "      \"printed_output\": [\n" +
            "        {\n" +
            "          \"type\": \"text\",\n" +
            "          \"data_path\": \"data\",\n" +
            "          \"node_state\": \"completed\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"text\",\n" +
            "          \"data_path\": \"data.formatted\",\n" +
            "          \"node_state\": \"completed\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"template_version\": \"\",\n" +
            "      \"restricted_groups\": [],\n" +
            "      \"not_damage_on_error\": false,\n" +
            "      \"damage_order_on_error\": false,\n" +
            "      \"log_can_be_overridden\": true,\n" +
            "      \"template_version_pattern\": \"\",\n" +
            "      \"priority_can_be_overridden\": false,\n" +
            "      \"printed_output_can_be_overridden\": true\n" +
            "    }\n" +
            "  ],\n" +
            "  \"ui_schema\": {\n" +
            "    \"domain\": {\n" +
            "      \"ui:widget\": \"DomainWidget\"\n" +
            "    },\n" +
            "    \"flavor\": {\n" +
            "      \"ui:field\": \"FlavorField\"\n" +
            "    },\n" +
            "    \"platform\": {\n" +
            "      \"ui:widget\": \"PlatformWidget\"\n" +
            "    },\n" +
            "    \"ui:order\": [\n" +
            "      \"on_support\",\n" +
            "      \"default_nic\",\n" +
            "      \"data_center\",\n" +
            "      \"platform\",\n" +
            "      \"postgresql_version\",\n" +
            "      \"postgresql_config\",\n" +
            "      \"os_version\",\n" +
            "      \"flavor\",\n" +
            "      \"boot_disk\",\n" +
            "      \"extra_mounts\",\n" +
            "      \"extra_nics\",\n" +
            "      \"domain\",\n" +
            "      \"ad_integration\",\n" +
            "      \"ad_logon_grants\"\n" +
            "    ],\n" +
            "    \"boot_disk\": {\n" +
            "      \"ui:readonly\": true\n" +
            "    },\n" +
            "    \"extra_nics\": {\n" +
            "      \"ui:readonly\": true\n" +
            "    },\n" +
            "    \"on_support\": {\n" +
            "      \"ui:readonly\": true\n" +
            "    },\n" +
            "    \"os_version\": {\n" +
            "      \"ui:field\": \"DirectoryUiListField\",\n" +
            "      \"ui:options\": {\n" +
            "        \"attrs\": {\n" +
            "          \"tags\": \"general\",\n" +
            "          \"directory__name\": \"images\",\n" +
            "          \"data__os__distribution\": \"rhel\",\n" +
            "          \"data__os__version__last\": \"8.\"\n" +
            "        },\n" +
            "        \"title\": \"data.os.version\",\n" +
            "        \"value\": \"data.os.version\",\n" +
            "        \"multiple\": false\n" +
            "      }\n" +
            "    },\n" +
            "    \"data_center\": {\n" +
            "      \"ui:widget\": \"DatacenterWidget\"\n" +
            "    },\n" +
            "    \"default_nic\": {\n" +
            "      \"net_segment\": {\n" +
            "        \"ui:widget\": \"NetSegmentWidget\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"extra_mounts\": {\n" +
            "      \"items\": {\n" +
            "        \"path\": {\n" +
            "          \"ui:options\": {\n" +
            "            \"validationError\": \"Путь монтирования должен быть уникален\",\n" +
            "            \"customValidation\": \"uniqueValue\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"file_system\": {\n" +
            "          \"ui:readonly\": true\n" +
            "        }\n" +
            "      },\n" +
            "      \"ui:options\": {\n" +
            "        \"addable\": true,\n" +
            "        \"orderable\": false,\n" +
            "        \"removable\": true\n" +
            "      }\n" +
            "    },\n" +
            "    \"ad_integration\": {\n" +
            "      \"ui:readonly\": true\n" +
            "    },\n" +
            "    \"ad_logon_grants\": {\n" +
            "      \"items\": {\n" +
            "        \"role\": {\n" +
            "          \"ui:options\": {\n" +
            "            \"validationError\": \"Данная роль уже выбрана\",\n" +
            "            \"customValidation\": \"uniqueValue\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"groups\": {\n" +
            "          \"ui:field\": \"UserGroupField\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"ui:options\": {\n" +
            "        \"orderable\": false\n" +
            "      }\n" +
            "    },\n" +
            "    \"postgresql_config\": {\n" +
            "      \"ui:order\": [\n" +
            "        \"max_connections\"\n" +
            "      ]\n" +
            "    }\n" +
            "  },\n" +
            "  \"json_schema\": {\n" +
            "    \"type\": \"object\",\n" +
            "    \"title\": \"PostgreSQL\",\n" +
            "    \"required\": [\n" +
            "      \"data_center\",\n" +
            "      \"platform\",\n" +
            "      \"os_version\",\n" +
            "      \"flavor\",\n" +
            "      \"ad_logon_grants\",\n" +
            "      \"boot_disk\",\n" +
            "      \"domain\",\n" +
            "      \"ad_integration\",\n" +
            "      \"default_nic\",\n" +
            "      \"on_support\",\n" +
            "      \"postgresql_version\",\n" +
            "      \"postgresql_config\"\n" +
            "    ],\n" +
            "    \"properties\": {\n" +
            "      \"domain\": {\n" +
            "        \"type\": \"string\",\n" +
            "        \"title\": \"Домен\"\n" +
            "      },\n" +
            "      \"flavor\": {\n" +
            "        \"type\": \"object\",\n" +
            "        \"title\": \"Конфигурация Core/RAM\",\n" +
            "        \"required\": [\n" +
            "          \"cpus\",\n" +
            "          \"memory\",\n" +
            "          \"name\",\n" +
            "          \"uuid\"\n" +
            "        ],\n" +
            "        \"properties\": {\n" +
            "          \"cpus\": {\n" +
            "            \"type\": \"number\"\n" +
            "          },\n" +
            "          \"name\": {\n" +
            "            \"type\": \"string\"\n" +
            "          },\n" +
            "          \"uuid\": {\n" +
            "            \"type\": \"string\"\n" +
            "          },\n" +
            "          \"memory\": {\n" +
            "            \"type\": \"number\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"platform\": {\n" +
            "        \"type\": \"string\",\n" +
            "        \"title\": \"Платформа\"\n" +
            "      },\n" +
            "      \"boot_disk\": {\n" +
            "        \"$ref\": \"#/definitions/disk\",\n" +
            "        \"title\": \"Загрузочный диск\",\n" +
            "        \"default\": {\n" +
            "          \"size\": 30\n" +
            "        }\n" +
            "      },\n" +
            "      \"extra_nics\": {\n" +
            "        \"type\": \"array\",\n" +
            "        \"items\": {\n" +
            "          \"$ref\": \"#/definitions/nic\"\n" +
            "        },\n" +
            "        \"title\": \"Дополнительные сетевые интерфейсы\",\n" +
            "        \"default\": [],\n" +
            "        \"maxItems\": 3,\n" +
            "        \"minItems\": 0\n" +
            "      },\n" +
            "      \"on_support\": {\n" +
            "        \"type\": \"boolean\",\n" +
            "        \"title\": \"Поддержка группой сопровождения\",\n" +
            "        \"default\": false\n" +
            "      },\n" +
            "      \"os_version\": {\n" +
            "        \"type\": \"string\",\n" +
            "        \"title\": \"Версия ОС\"\n" +
            "      },\n" +
            "      \"data_center\": {\n" +
            "        \"type\": \"string\",\n" +
            "        \"title\": \"Дата-центр\"\n" +
            "      },\n" +
            "      \"default_nic\": {\n" +
            "        \"$ref\": \"#/definitions/nic\",\n" +
            "        \"title\": \"Основной сетевой интерфейс\"\n" +
            "      },\n" +
            "      \"extra_mounts\": {\n" +
            "        \"type\": \"array\",\n" +
            "        \"items\": {\n" +
            "          \"type\": \"object\",\n" +
            "          \"required\": [\n" +
            "            \"size\",\n" +
            "            \"file_system\",\n" +
            "            \"path\"\n" +
            "          ],\n" +
            "          \"properties\": {\n" +
            "            \"path\": {\n" +
            "              \"enum\": [\n" +
            "                \"/pg_data\",\n" +
            "                \"/pg_backup\",\n" +
            "                \"/pg_walarchive\"\n" +
            "              ],\n" +
            "              \"type\": \"string\",\n" +
            "              \"title\": \"Путь монтирования\",\n" +
            "              \"maxLength\": 240,\n" +
            "              \"minLength\": 2\n" +
            "            },\n" +
            "            \"size\": {\n" +
            "              \"type\": \"integer\",\n" +
            "              \"title\": \"Размер, Гб\",\n" +
            "              \"default\": 20,\n" +
            "              \"maximum\": 2048,\n" +
            "              \"minimum\": 10\n" +
            "            },\n" +
            "            \"file_system\": {\n" +
            "              \"enum\": [\n" +
            "                \"xfs\"\n" +
            "              ],\n" +
            "              \"type\": \"string\",\n" +
            "              \"title\": \"Файловая система\",\n" +
            "              \"default\": \"xfs\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"title\": \"Точки монтирования\",\n" +
            "        \"default\": [\n" +
            "          {\n" +
            "            \"path\": \"/pg_data\",\n" +
            "            \"size\": 50,\n" +
            "            \"file_system\": \"xfs\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"maxItems\": 3,\n" +
            "        \"minItems\": 1\n" +
            "      },\n" +
            "      \"ad_integration\": {\n" +
            "        \"type\": \"boolean\",\n" +
            "        \"title\": \"Интеграция ОС c Active Directory\",\n" +
            "        \"default\": true\n" +
            "      },\n" +
            "      \"postgresql_config\": {\n" +
            "        \"type\": \"object\",\n" +
            "        \"title\": \"Конфигурация PostgreSQL\",\n" +
            "        \"required\": [\n" +
            "          \"max_connections\"\n" +
            "        ],\n" +
            "        \"properties\": {\n" +
            "          \"max_connections\": {\n" +
            "            \"type\": \"integer\",\n" +
            "            \"default\": 100,\n" +
            "            \"maximum\": 1000,\n" +
            "            \"minimum\": 100,\n" +
            "            \"description\": \"Максимум 4 активных запроса на ядро, при большем кол-ве возможна серьезная потеря производительности\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"postgresql_version\": {\n" +
            "        \"enum\": [\n" +
            "          \"12\",\n" +
            "          \"11\"\n" +
            "        ],\n" +
            "        \"type\": \"string\",\n" +
            "        \"title\": \"Версия PostgreSQL\",\n" +
            "        \"default\": \"12\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"definitions\": {\n" +
            "      \"nic\": {\n" +
            "        \"type\": \"object\",\n" +
            "        \"title\": \"Сетевой интерфейс\",\n" +
            "        \"properties\": {\n" +
            "          \"net_segment\": {\n" +
            "            \"type\": \"string\",\n" +
            "            \"title\": \"Сетевой сегмент\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"disk\": {\n" +
            "        \"type\": \"object\",\n" +
            "        \"title\": \"Диск\",\n" +
            "        \"properties\": {\n" +
            "          \"size\": {\n" +
            "            \"type\": \"integer\",\n" +
            "            \"title\": \"Размер, Гб\",\n" +
            "            \"default\": 10,\n" +
            "            \"maximum\": 2048,\n" +
            "            \"minimum\": 10\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"description\": \"Автономный PostgreSQL сервер\",\n" +
            "    \"dependencies\": {\n" +
            "      \"ad_integration\": {\n" +
            "        \"oneOf\": [\n" +
            "          {\n" +
            "            \"properties\": {\n" +
            "              \"ad_integration\": {\n" +
            "                \"enum\": [\n" +
            "                  false\n" +
            "                ]\n" +
            "              }\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"properties\": {\n" +
            "              \"ad_integration\": {\n" +
            "                \"enum\": [\n" +
            "                  true\n" +
            "                ]\n" +
            "              },\n" +
            "              \"ad_logon_grants\": {\n" +
            "                \"type\": \"array\",\n" +
            "                \"items\": {\n" +
            "                  \"type\": \"object\",\n" +
            "                  \"required\": [\n" +
            "                    \"role\",\n" +
            "                    \"groups\"\n" +
            "                  ],\n" +
            "                  \"properties\": {\n" +
            "                    \"role\": {\n" +
            "                      \"enum\": [\n" +
            "                        \"superuser\",\n" +
            "                        \"user\"\n" +
            "                      ],\n" +
            "                      \"type\": \"string\",\n" +
            "                      \"title\": \"Роль\",\n" +
            "                      \"default\": \"superuser\"\n" +
            "                    },\n" +
            "                    \"groups\": {\n" +
            "                      \"type\": \"array\",\n" +
            "                      \"items\": {\n" +
            "                        \"type\": \"string\"\n" +
            "                      },\n" +
            "                      \"title\": \"Группы\",\n" +
            "                      \"minItems\": 1\n" +
            "                    }\n" +
            "                  }\n" +
            "                },\n" +
            "                \"title\": \"Разрешения на вход через Active Directory\",\n" +
            "                \"maxItems\": 15,\n" +
            "                \"minItems\": 1,\n" +
            "                \"uniqueItems\": true\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"static_data\": {\n" +
            "    \"role\": \"postgresql\",\n" +
            "    \"tenant\": {},\n" +
            "    \"quantity\": 1,\n" +
            "    \"credential\": \"VTB.Cloud-DEV\",\n" +
            "    \"extra_nics\": [],\n" +
            "    \"extra_vars\": {\n" +
            "      \"interface\": \"deploy\"\n" +
            "    },\n" +
            "    \"extra_disks\": [],\n" +
            "    \"extra_mounts\": [],\n" +
            "    \"job_template\": \"postgresql\",\n" +
            "    \"product_type\": \"vm\",\n" +
            "    \"business_line\": \"general\",\n" +
            "    \"os_local_users\": [],\n" +
            "    \"ad_logon_grants\": [],\n" +
            "    \"inventory_template\": \"{% set inventory = {'all': {'hosts': {}, 'vars': {'net_segment': net_segment}}} %}{% for item in items %}{% set _ = inventory.all.hosts.update({item.config.hostname + '.' + item.config.domain: {'ansible_host': item.config.default_v4_address, 'machine': item.config}}) %}{% endfor %}{% set _ = inventory.all.vars.update({'postgresql_version': postgresql_version, 'postgresql_config': postgresql_config}) %}{{ inventory | tojson }}\"\n" +
            "  },\n" +
            "  \"modifications\": [],\n" +
            "  \"damage_order_on_error\": false,\n" +
            "  \"version\": \"1.0.76\",\n" +
            "  \"version_create_dt\": \"2022-01-31T13:01:41.970133Z\",\n" +
            "  \"version_changed_by_user\": \"\",\n" +
            "  \"version_list\": [\n" +
            "    \"1.0.38\",\n" +
            "    \"1.0.59\",\n" +
            "    \"1.0.64\",\n" +
            "    \"1.0.65\",\n" +
            "    \"1.0.66\",\n" +
            "    \"1.0.68\",\n" +
            "    \"1.0.71\",\n" +
            "    \"1.0.74\",\n" +
            "    \"1.0.75\",\n" +
            "    \"1.0.76\"\n" +
            "  ],\n" +
            "  \"last_version\": \"1.0.76\"\n" +
            "}";
}
