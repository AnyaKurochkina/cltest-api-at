package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import models.cloud.orderService.products.ApacheKafkaCluster;
import models.cloud.subModels.Flavor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.IndexPage;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class ApacheKafkaClusterPage extends IProductPage {

    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String BLOCK_CLUSTER = "Кластер";
    private static final String HEADER_NAME_TOPIC = "Название";
    private static final String HEADER_NAME_CLUSTER = "Имя кластера";
    private static final String HEADER_LIMIT_CONNECT = "Предел подключений";
    private static final String STATUS = "Роли узла";
    private static final String HEADER_ACL = "CN сертификата клиента";
    private static final String HEADER_ACL_TRANSACTION = "CN сертификата клиента";
    private static final String HEADER_ACL_ID_TRANSACTION = "Идентификатор транзакции или маска";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";
    private static final String HEADER_ACL_IDEMPOTENT = "CN сертификата клиента";
    private static final String HEADER_QUOTAS = "Размер квоты producer (байт/с)";


    private final SelenideElement btnTopics = $x("//button[.='Топики']");
    private final SelenideElement btnAclTopics = $x("//button[.='ACL на топики']");
    private final SelenideElement btnAclTrans = $x("//button[.='ACL на транзакции']");
    private final SelenideElement btnIdempAcl = $x("//button[.='Идемпотентные ACL']");
    private final SelenideElement btnQuotas = $x("//button[.='Квоты']");
    private final SelenideElement cpu = $x("(//h5)[1]");
    private final SelenideElement ram = $x("(//h5)[2]");
    private final SelenideElement btnAdd = $x("//button[contains(@class, 'array-item-add')]");


    public ApacheKafkaClusterPage(ApacheKafkaCluster product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new VirtualMachineTable(STATUS).checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_APP, "Включить");
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить");
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        new Table("Роли узла").getRowByIndex(0).scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию", ActionParameters.builder().node(new Table("Роли узла").getRowByIndex(0)).checkAlert(false).build());
    }

    public void delete() {
        runActionWithParameters(BLOCK_CLUSTER, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = new Dialog("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new VirtualMachineTable("Статус").checkPowerStatus(VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Перезагрузить кластер", ActionParameters.builder().timeout(Duration.ofMinutes(20)).build());
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopHard() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void changeConfiguration() {
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(BLOCK_CLUSTER, "Вертикальное масштабирование", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение выше и подтверждаю свое действие").setChecked(true);
            DropDown.byLabel("Конфигурация Core/RAM").select(NewOrderPage.getFlavor(maxFlavor)); //,ActionParameters.builder().timeOut(Duration.ofMinutes(20)).build()
        }, ActionParameters.builder().timeout(Duration.ofMinutes(25)).build());
        btnGeneralInfo.click();
        Table table = new Table("Роли узла");
        table.getRowByIndex(0).click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
    }

    public void horizontalScaling() {
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(BLOCK_CLUSTER, "Горизонтальное масштабирование", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение выше и подтверждаю, что понимаю что делаю").setChecked(true);
            Selenide.$x("//span[text()='Увеличение количества брокеров в составе кластера и перевыпуск кластерного сертификата.']").shouldBe(Condition.visible.because("Должно отображаться сообщение"));
            Selenide.$x("//span[text()='Операция выполняется без недоступности сервиса, если текущее состояние кластера и параметры топиков позволяют обеспечить отказоустойчивость.']").shouldBe(Condition.visible);
            Select.byLabel("Количество").set("4");
        });

    }

    public void changeNameCluster(String name) {
        runActionWithParameters(BLOCK_CLUSTER, "Изменить имя кластера", "Подтвердить", () -> {
            Dialog dlg = new Dialog("Изменить имя кластера");
            dlg.setInputValue("Новое имя кластера", name);
            CheckBox.byLabel("Я прочитал предупреждение выше и подтверждаю, что понимаю что делаю.").setChecked(true);
        });
        btnGeneralInfo.click();
        Assertions.assertEquals(name, new Table(HEADER_NAME_CLUSTER).getRowByColumnValue(HEADER_NAME_CLUSTER, name).getValueByColumn(HEADER_NAME_CLUSTER), "БД не принадлежит пользователю");
    }

    public void updateCertificate() {
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Обновить кластерный сертификат", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение выше и понимаю, что я делаю").setChecked(true);
        });
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void updateCertificateEmergency() {
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Обновить кластерный сертификат (аварийно)", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение выше, и понимаю, что я делаю").setChecked(true);
        });
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void updateOs() {
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Обновить ОС на кластере Kafka", ActionParameters.builder().timeout(Duration.ofMinutes(20)).build());
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void updateDistributionVtb() {
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Обновление дистрибутива ВТБ-Kafka", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение выше и подтверждаю, что знаю что делаю.").setChecked(true);
        });
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void updateKernelVtb() {
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Обновление ядра Kafka до версии 2.8.2", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение выше и понимаю, что я делаю.").setChecked(true);
        });
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void sendConfiguration() {
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Прислать конфигурацию брокера Kafka");
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void synchronizeCluster() {
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Синхронизировать конфигурацию кластера Kafka");
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void enlargeDisk(String name, String size, @NotNull SelenideElement node) {
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        node.scrollIntoView(scrollCenter).click();
        String firstSizeDisk = getTableByHeader("Дополнительные точки монтирования")
                .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE);
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        runActionWithParameters(BLOCK_CLUSTER, "Увеличить дисковое пространство", "Подтвердить",
                () -> Input.byLabel("Дополнительный объем дискового пространства, Гб").setValue(size));
        btnGeneralInfo.click();
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        node.scrollIntoView(scrollCenter).click();
        String value = String.valueOf(Double.parseDouble(firstSizeDisk) +
                Double.parseDouble(size));
        Assertions.assertEquals(value, getTableByHeader("Дополнительные точки монтирования")
                        .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE),
                "Неверный размер диска");
    }

    public void changeParamTopics(List<String> names) {
        btnTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        List<String> topics = names.stream().filter(topic -> !new Table(HEADER_NAME_TOPIC).isColumnValueEquals(HEADER_NAME_TOPIC, topic)).collect(Collectors.toList());
        if (topics.isEmpty()) {
            createTopics(names);
        }
        runActionWithParameters("Список топиков", "Изменить параметр топиков Kafka Cluster", "Подтвердить", () -> {
            Dialog dlg = new Dialog("Изменить параметр топиков Kafka Cluster");
            for (int i = 0; i < names.size(); i++) {
                if (i != 0)
                    btnAdd.shouldBe(Condition.enabled).click();
                Select.byLabel("Топики", i + 1).set(names.get(i));
                Select.byLabel("Тип очистки").set("compact");
            }
        });
        btnTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertEquals(names.get(0), new Table(HEADER_NAME_TOPIC).getRowByColumnValue(HEADER_NAME_TOPIC, names.get(0)).getValueByColumn(HEADER_NAME_TOPIC),
                "Ошибка создания топика");
    }

    public void createTopics(List<String> names) {
        btnTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        //получаем список topics, в котором нет созданных топиков
        List<String> topics = names.stream().filter(topic -> !new Table(HEADER_NAME_TOPIC).isColumnValueEquals(HEADER_NAME_TOPIC, topic)).collect(Collectors.toList());
        //если список содержит указанный элемент, то выходим из функции, т.к. смысла запускать действия нет
        if (topics.isEmpty())
            return;
        runActionWithParameters("Список топиков", "Пакетное создание Topic-ов Kafka", "Подтвердить", () -> {
            Dialog.byTitle("Пакетное создание Topic-ов Kafka");
            //перебираем список топиков и вставляем по индексу в конкретный инпут (+1 т.к. у элементов отсчет с 1 а не с 0 как в цикле)
            for (int i = 0; i < topics.size(); i++) {
                //на первом топике жать + не нужно
                if (i != 0)
                    btnAdd.shouldBe(Condition.enabled).click();
                Input.byLabel("Имя Topic", i + 1).setValue(topics.get(i));
            }
        });
        btnTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        //получаем список топиков, которые не появилсиь после выполнения действия и сравниваем с пустым массивом, т.к. список должен быть пуст
        Assertions.assertEquals(new ArrayList<>(), topics.stream()
                .filter(topic -> !new Table(HEADER_NAME_TOPIC).isColumnValueEquals(HEADER_NAME_TOPIC, topic)).collect(Collectors.toList()), "Не все топики были созданы");
    }

    public void dellTopics(List<String> names) {
        btnTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        List<String> topics = names.stream().filter(topic -> new Table(HEADER_NAME_TOPIC).isColumnValueEquals(HEADER_NAME_TOPIC, topic)).collect(Collectors.toList());
        if (topics.isEmpty()) {
            createTopics(names);
        }
        runActionWithParameters("Список топиков", "Пакетное удаление Topic-ов Kafka", "Подтвердить", () -> {
            Dialog dlg = new Dialog("Пакетное удаление Topic-ов Kafka");
            for (String name : names) {
                dlg.setSelectValue("Имена Topic-ов", name);
            }
        });
        btnTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertEquals(new ArrayList<>(), topics.stream()
                .filter(topic -> new Table(HEADER_NAME_TOPIC).isColumnValueEquals(HEADER_NAME_TOPIC, topic)).collect(Collectors.toList()), "Не все топики были удалены");
    }

    public void createAclTopics(List<Acl> aclTopics) {
        btnAclTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        List<Acl> aclTopic = aclTopics.stream()
                .filter(acl -> !new Table(HEADER_ACL).isColumnValueEquals(HEADER_ACL, acl.certificate)).collect(Collectors.toList());
        if (aclTopic.isEmpty())
            return;
        runActionWithParameters("ACL на топики", "Пакетное создание ACL Kafka", "Подтвердить", () -> {
            for (int i = 0; i < aclTopic.size(); i++) {
                if (i != 0)
                    btnAdd.shouldBe(Condition.enabled).click();
                Acl acl = aclTopic.get(i);
                Input.byLabel("Common Name сертификата клиента", i + 1).setValue(acl.certificate);
                RadioGroup radioGroup = RadioGroup.byLabel("Выберите топик", i + 1);
                if (acl.type == Acl.Type.BY_MASK)
                    Input.byLabel("Маска имени топика", -1).setValue(acl.mask);
                if (acl.type == Acl.Type.BY_NAME) {
                    radioGroup.select("По имени");
                    DropDown.byLabel("Топики", -1).select(acl.mask);
                }
                if (acl.type == Acl.Type.ALL_TOPIC) {
                    radioGroup.select("Все топики");
                }
            }
        });
        btnAclTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertEquals(new ArrayList<>(), aclTopic.stream().filter(acl -> !new Table(HEADER_ACL).isColumnValueEquals(HEADER_ACL, acl.certificate))
                .collect(Collectors.toList()), "Не все топики ACL были созданы");
    }

    public void createAclTransaction(List<Acl> transactions) {
        btnAclTrans.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        List<Acl> acls = transactions.stream()
                .filter(acl -> !new Table(HEADER_ACL_ID_TRANSACTION).isColumnValueEquals(HEADER_ACL_ID_TRANSACTION, acl.mask)).collect(Collectors.toList());
        if (acls.isEmpty())
            return;
        runActionWithParameters("ACL на транзакции", "Пакетное создание ACL на транзакцию Kafka", "Подтвердить", () -> {
            Dialog.byTitle("Пакетное создание ACL на транзакцию Kafka");
            for (int i = 0; i < acls.size(); i++) {
                if (i != 0)
                    btnAdd.shouldBe(Condition.enabled).click();
                Acl acl = acls.get(i);
                Input.byLabel("Common Name сертификата клиента", i + 1).setValue(acl.certificate);
                RadioGroup radioGroup = RadioGroup.byLabel("Введите идентификатор транзакции", i + 1);
                if (acl.type == Acl.Type.BY_MASK)
                    Input.byLabel("Введите префикс идентификатора транзакции", -1).setValue(acl.mask);
                if (acl.type == Acl.Type.BY_NAME) {
                    radioGroup.select("По имени");
                    Input.byLabel("Введите идентификатор транзакции", -1).setValue(acl.mask);
                }
                if (acl.type == Acl.Type.ALL_TRANSACTION) {
                    radioGroup.select("Все транзакции");
                    Select.byLabel("Префикс", -1).set(acl.mask);
                }
            }
        });//,ActionParameters.builder().checkAlert(false).build()
        btnAclTrans.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertEquals(new ArrayList<>(), acls.stream().filter(acl -> !new Table(HEADER_ACL_ID_TRANSACTION).isColumnValueEquals(HEADER_ACL_ID_TRANSACTION, acl.mask))
                .collect(Collectors.toList()), "Не все acl были созданы");
    }

    public void dellAclTransaction(List<Acl> transactions) {
        btnAclTrans.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        List<Acl> acls = transactions.stream()
                .filter(acl -> new Table(HEADER_ACL_ID_TRANSACTION).isColumnValueEquals(HEADER_ACL_ID_TRANSACTION, acl.mask)).collect(Collectors.toList());
        //если список содержит указанный элемент, то выходим из функции, т.к. смысла запускать действия нет
        if (acls.isEmpty()) {
            createAclTransaction(transactions);
        }
        runActionWithParameters("ACL на транзакции", "Пакетное удаление ACL на транзакцию Kafka", "Подтвердить", () -> {
            for (int i = 0; i < acls.size(); i++) {
                if (i != 0)
                    btnAdd.shouldBe(Condition.enabled).click();
                Acl acl = acls.get(i);
                Select.byLabel("Common Name сертификата клиента", i + 1).set(acl.certificate);
                RadioGroup radioGroup = RadioGroup.byLabel("Введите идентификатор транзакции", i + 1);
                if (acl.type == Acl.Type.BY_MASK)
                    Input.byLabel("Введите префикс идентификатора транзакции", -1).setValue(acl.mask);
                if (acl.type == Acl.Type.BY_NAME) {
                    radioGroup.select("По имени");
                    Input.byLabel("Введите идентификатор транзакции", -1).setValue(acl.mask);
                }
                if (acl.type == Acl.Type.ALL_TRANSACTION) {
                    radioGroup.select("Все транзакции");
                    Select.byLabel("Префикс", -1).set(acl.mask);
                }

            }
        }, ActionParameters.builder().checkPreBilling(false).build());
        btnAclTrans.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertEquals(new ArrayList<>(), acls.stream().filter(acl -> new Table(HEADER_ACL_ID_TRANSACTION).isColumnValueEquals(HEADER_ACL_ID_TRANSACTION, acl.mask))
                .collect(Collectors.toList()), "Не все топики ACL на транзакции были удалены");

    }


    public void dellAclTopics(List<Acl> aclTopics) {
        btnAclTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        List<Acl> aclTopic = aclTopics.stream()
                .filter(acl -> new Table(HEADER_ACL).isColumnValueEquals(HEADER_ACL, acl.certificate)).collect(Collectors.toList());
        if (aclTopic.isEmpty()) {
            createAclTopics(aclTopics);
        }
        runActionWithParameters("ACL на топики", "Пакетное удаление ACL Kafka", "Подтвердить", () -> {
            for (int i = 0; i < aclTopic.size(); i++) {
                if (i != 0)
                    btnAdd.shouldBe(Condition.enabled).click();
                Acl topicAcl = aclTopic.get(i);
                Select.byLabel("Common Name сертификата клиента", i + 1).set(topicAcl.certificate);
                RadioGroup radioGroup = RadioGroup.byLabel("Выберите топик", i + 1);
                if (topicAcl.type == Acl.Type.BY_MASK)
                    Input.byLabel("Маска имени топика", -1).setValue(topicAcl.mask);
                if (topicAcl.type == Acl.Type.BY_NAME) {
                    radioGroup.select("По имени");
                    Select.byLabel("Топики", -1).set(topicAcl.mask);
                }
                if (topicAcl.type == Acl.Type.ALL_TOPIC) {
                    radioGroup.select("Все топики");
                }
            }
        });
        btnAclTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertEquals(new ArrayList<>(), aclTopic.stream().filter(topicAcl -> new Table(HEADER_ACL).isColumnValueEquals(HEADER_ACL, topicAcl.certificate))
                .collect(Collectors.toList()), "Не все топики ACL были удалены");
    }

    public void createAclIdempotent(String nameT1) {
        btnIdempAcl.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (!(new Table(HEADER_ACL_IDEMPOTENT).isColumnValueContains(HEADER_ACL_IDEMPOTENT, nameT1))) {
            runActionWithParameters("Идемпотентных ACL", "Создание идемпотентных ACL Kafka", "Подтвердить", () -> {
                Input.byLabel("Common Name сертификата клиента").setValue(nameT1);
            }, ActionParameters.builder().waitChangeStatus(false).build());
            btnIdempAcl.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            Assertions.assertEquals(nameT1, new Table(HEADER_ACL_IDEMPOTENT).getRowByColumnValue(HEADER_ACL_IDEMPOTENT, nameT1).getValueByColumn(HEADER_ACL_IDEMPOTENT),
                    "Ошибка cоздания идемпотентных ACL Kafka");
        }
    }

    public void createQuotas(String name) {
        btnQuotas.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (!(new Table(HEADER_ACL_IDEMPOTENT).isColumnValueContains(HEADER_ACL_IDEMPOTENT, name))) {
            runActionWithParameters("Квоты", "Пакетное создание квот Kafka", "Подтвердить", () -> {
                RadioGroup.byLabel("Выберите квоту").select("По умолчанию (все клиенты)");
                Input.byLabel("Размер квоты producer *").setValue(name);
            });
            btnQuotas.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            Assertions.assertEquals(name, new Table(HEADER_ACL_IDEMPOTENT).getRowByColumnValue(HEADER_QUOTAS, name).getValueByColumn(HEADER_QUOTAS),
                    "Ошибка cоздания квоты");
        }
    }

    public void deleteQuotas(String name) {
        btnQuotas.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        runActionWithParameters("Квоты", "Пакетное удаление квот Kafka", "Подтвердить", () -> {
            RadioGroup.byLabel("Выберите квоту").select("По умолчанию (все клиенты)");
        });
        btnQuotas.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        btnIdempAcl.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertFalse(new Table(HEADER_ACL_IDEMPOTENT).isColumnValueEquals("", name), "Ошибка удаления квоты");

    }

    public void dellAclIdempotent(String nameT1) {
        btnIdempAcl.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (!(new Table(HEADER_ACL_IDEMPOTENT).isColumnValueContains(HEADER_ACL_IDEMPOTENT, nameT1))) {
            createAclIdempotent(nameT1);
        }
        runActionWithParameters("Идемпотентных ACL", "Удаление идемпотентных ACL Kafka", "Подтвердить", () -> {
            Input.byLabel("Common Name сертификата клиента").setValue(nameT1);
        });
        btnIdempAcl.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertFalse(new Table(HEADER_ACL_IDEMPOTENT).isColumnValueEquals("", nameT1), "Ошибка удаления идемпотентных ACL Kafka");
    }

    public void checkNameCluster() {
        String nameCluster = new Table("Имя кластера").getFirstValueByColumn("Имя кластера");
        new IndexPage()
                .clickOrderMore()
                .selectProduct("Apache Kafka Cluster Astra");
        new ApacheKafkaClusterOrderPage().getNameCluster().setValue(nameCluster);
        Alert.red("Значение поля не уникально");
    }


    public class VirtualMachineTable extends VirtualMachine {

        public VirtualMachineTable(String columnName) {
            super(columnName);
        }

        @Override
        public String getPowerStatus() {
            return getPowerStatus("Статус");
        }

    }
}
