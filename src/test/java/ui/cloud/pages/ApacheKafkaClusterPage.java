package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import models.cloud.orderService.products.ApacheKafkaCluster;
import models.cloud.subModels.Flavor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";
    private static final String HEADER_ACL_IDEMPOTENT = "CN сертификата клиента";


    SelenideElement btnTopics = $x("//button[.='Топики']");
    SelenideElement btnAclTopics = $x("//button[.='ACL на топики']");
    SelenideElement btnAclTrans = $x("//button[.='ACL на транзакции']");
    SelenideElement btnIdempAcl = $x("//button[.='Идемпотентные ACL']");
    SelenideElement cpu = $x("(//h5)[1]");
    SelenideElement ram = $x("(//h5)[2]");
    SelenideElement btnAdd = $x("//button[@class='MuiButtonBase-root MuiButton-root MuiButton-text array-item-add MuiButton-textSecondary']");
    SelenideElement currentProduct = $x("(//span/preceding-sibling::a[text()='Интеграция приложений' or text()='Базовые вычисления' or text()='Контейнеры' or text()='Базы данных' or text()='Инструменты DevOps' or text()='Логирование' or text()='Объектное хранилище' or text()='Веб-приложения' or text()='Управление секретами' or text()='Сетевые службы']/parent::div/following-sibling::div/a)[1]");


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
        currentProduct.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
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
        runActionWithoutParameters(BLOCK_CLUSTER, "Перезагрузить кластер", ActionParameters.builder().timeOut(Duration.ofMinutes(20)).build());
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
            CheckBox.byLabel("Я прочитал предупреждение ниже, и понимаю, что я делаю").setChecked(true);
            DropDown.byLabel("Конфигурация Core/RAM").select(Product.getFlavor(maxFlavor)); //,ActionParameters.builder().timeOut(Duration.ofMinutes(20)).build()
        });
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        Table table = new Table("Роли узла");
        table.getRowByIndex(0).click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
    }

    public void changeNameCluster(String name) {
        runActionWithParameters(BLOCK_CLUSTER, "Изменить имя кластера", "Подтвердить", () -> {
            Dialog dlg = new Dialog("Изменить имя кластера");
            dlg.setInputValue("Новое имя кластера", name);
            CheckBox.byLabel("Я прочитал предупреждение ниже и подтверждаю, что понимаю что делаю.").setChecked(true);
        });
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        Assertions.assertEquals(name, new Table(HEADER_NAME_CLUSTER).getRowByColumnValue(HEADER_NAME_CLUSTER, name).getValueByColumn(HEADER_NAME_CLUSTER), "БД не принадлежит пользователю");
    }

    public void updateCertificate() {
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Обновить кластерный сертификат", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение ниже, и понимаю, что я делаю").setChecked(true);
        });
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void updateCertificateEmergency() {
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Обновить кластерный сертификат (аварийно)", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение ниже, и понимаю, что я делаю").setChecked(true);
        });
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void updateOs() {
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Обновить ОС на кластере Kafka", ActionParameters.builder().timeOut(Duration.ofMinutes(20)).build());
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void updateDistributionVtb() {
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Обновление дистрибутива ВТБ-Kafka", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение ниже и подтверждаю, что знаю что делаю.").setChecked(true);
        });
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void updateKernelVtb() {
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Обновление ядра Kafka до версии 2.8.1", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю.").setChecked(true);
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
        currentProduct.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        node.scrollIntoView(scrollCenter).click();
        String firstSizeDisk = getTableByHeader("Дополнительные точки монтирования")
                .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE);
        currentProduct.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
         runActionWithParameters(BLOCK_CLUSTER, "Увеличить дисковое пространство", "Подтвердить",
                        () -> Input.byLabel("Дополнительный объем дискового пространства, Гб").setValue(size));
         //expandDisk(name, size, node);
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        currentProduct.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        node.scrollIntoView(scrollCenter).click();
        String value = String.valueOf(Integer.parseInt(firstSizeDisk) +
                Integer.parseInt(size));
        Assertions.assertEquals(value, getTableByHeader("Дополнительные точки монтирования")
                        .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE),
                "Неверный размер диска");
    }

    public void createTopics(String nameT1, String nameT2) {
        btnTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (!(new Table(HEADER_NAME_TOPIC).isColumnValueContains(HEADER_NAME_TOPIC, nameT1))) {
            runActionWithParameters("Список топиков", "Пакетное создание Topic-ов Kafka", "Подтвердить", () -> {
                Dialog dlg = new Dialog("Пакетное создание Topic-ов Kafka");
                Input.byLabel("Имя Topic").setValue(nameT1);
                btnAdd.shouldBe(Condition.enabled).click();
                Input.byXpath("(//label[starts-with(.,'Имя Topic')]/parent::*//input)[2]").setValue(nameT2);
            });
            btnTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            Assertions.assertEquals(nameT1, new Table(HEADER_NAME_TOPIC).getRowByColumnValue(HEADER_NAME_TOPIC, nameT1).getValueByColumn(HEADER_NAME_TOPIC),
                    "Ошибка создания топика");
        }
    }

    //createTopics2(Arrays.asList("1", "2", "11", "5", "100500"));
// если список одинаков для тестов то выносим в поле класса
    public void createTopics2(List<String> names) {
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

    public void changeParamTopics(List<String> names) {
        btnTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (!(new Table(HEADER_NAME_TOPIC).isColumnValueContains(HEADER_NAME_TOPIC, names.get(0)))) createTopics2(names);
        runActionWithParameters("Список топиков", "Изменить параметр топиков Kafka Cluster", "Подтвердить", () -> {
            Dialog dlg = new Dialog("Изменить параметр топиков Kafka Cluster");
            DropDown.byLabel("Топик").select(names.get(0));
//            DropDown.byLabel("Топик *").selectByValue(names.get(0));
//            Input.byLabel("Топик *").setValue(names.get(0));
            DropDown.byLabel("Тип очистки").select("compact");
        });
        btnTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertEquals(names.get(0), new Table(HEADER_NAME_TOPIC).getRowByColumnValue(HEADER_NAME_TOPIC, names.get(0)).getValueByColumn(HEADER_NAME_TOPIC),
                "Ошибка создания топика");
    }

    public void dellTopics(List<String> names) {
        btnTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        List<String> topics = names.stream().filter(topic -> new Table(HEADER_NAME_TOPIC).isColumnValueEquals(HEADER_NAME_TOPIC, topic)).collect(Collectors.toList());
        if (topics.isEmpty()) {
            createTopics2(names);
        }
        runActionWithParameters("Список топиков", "Пакетное удаление Topic-ов Kafka", "Подтвердить", () -> {
            Dialog dlg = new Dialog("Пакетное удаление Topic-ов Kafka");
            for (int i = 0; i < names.size(); i++) {
                dlg.setDropDownValue("Имена Topic-ов", names.get(i));
            }
        });
        btnTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertEquals(new ArrayList<>(), topics.stream()
                .filter(topic -> new Table(HEADER_NAME_TOPIC).isColumnValueEquals(HEADER_NAME_TOPIC, topic)).collect(Collectors.toList()), "Не все топики были удалены");
    }

    public void createAclTopics(List<String> names) {
        btnAclTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        List<String> topicsAcl = names.stream().filter(topic -> !new Table(HEADER_ACL).isColumnValueEquals(HEADER_ACL, topic)).collect(Collectors.toList());
        //если список содержит указанный элемент, то выходим из функции, т.к. смысла запускать действия нет
        if (topicsAcl.isEmpty())
            return;
        runActionWithParameters("ACL на топики", "Пакетное создание ACL Kafka", "Подтвердить", () -> {
            for (int i = 0; i < topicsAcl.size(); i++) {
                //на первом топике жать + не нужно
                if (i != 0)
                    btnAdd.shouldBe(Condition.enabled).click();
                Input.byLabel("Common Name сертификата клиента", i + 1).setValue(topicsAcl.get(i));
                Input.byLabel("Маска имени топика").setValue(topicsAcl.get(i));
                if (i == 1) {
                    //Input.byLabel("Common Name сертификата клиента", 2).setValue(nameT2);
                    RadioGroup.byLabel("Выберите топик *", Integer.parseInt(topicsAcl.get(i))).select("По имени");
                    DropDown.byLabel("Топики").select(topicsAcl.get(i));
                }
                if (i == 2) {
                    RadioGroup.byLabel("Выберите топик *", Integer.parseInt(topicsAcl.get(i))).select("Все топики");
                }
            }
        });
        btnAclTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertEquals(new ArrayList<>(), topicsAcl.stream()
                .filter(topic -> !new Table(HEADER_ACL)
                        .isColumnValueEquals(HEADER_ACL, topic))
                .collect(Collectors.toList()), "Не все топики ACL были созданы");
    }

/* EXAMPLE:
new ApacheKafkaClusterPage(ApacheKafkaCluster.builder().build()).createAclTopics2(
            AclTransaction.builder().certificate("cert1").type(AclTransaction.Type.BY_NAME).mask("name1").build(),
                AclTransaction.builder().certificate("cert2").type(AclTransaction.Type.BY_MASK).mask("mask").build(),
                AclTransaction.builder().certificate("cert2").type(AclTransaction.Type.ALL_TRANSACTION).mask("*").build(),
                AclTransaction.builder().certificate("cert1").type(AclTransaction.Type.BY_NAME).mask("name2").build());
*/
    public void createAclTopics2(AclTransaction... transactions) {
        btnAclTrans.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        List<AclTransaction> acls = Arrays.stream(transactions)
                .filter(acl -> !new Table(HEADER_ACL_TRANSACTION).isColumnValueEquals(HEADER_ACL_TRANSACTION, acl.mask)).collect(Collectors.toList());
        if (acls.isEmpty())
            return;
        runActionWithParameters("ACL на транзакции", "Пакетное создание ACL на транзакцию Kafka", "Подтвердить", () -> {
            Dialog.byTitle("Пакетное создание ACL на транзакцию Kafka");
            for (int i = 0; i < acls.size(); i++) {
                if (i != 0)
                    btnAdd.shouldBe(Condition.enabled).click();
                AclTransaction acl = acls.get(i);
                Input.byLabel("Common Name сертификата клиента", i + 1).setValue(acl.certificate);
                RadioGroup radioGroup = RadioGroup.byLabel("Введите идентификатор транзакции", i + 1);
                if (acl.type == AclTransaction.Type.BY_MASK)
                    Input.byLabel("Введите префикс идентификатора транзакции", -1).setValue(acl.mask);
                if (acl.type == AclTransaction.Type.BY_NAME) {
                    radioGroup.select("По имени");
                    Input.byLabel("Введите идентификатор транзакции", -1).setValue(acl.mask);
                }
                if (acl.type == AclTransaction.Type.ALL_TRANSACTION) {
                    radioGroup.select("Все транзакции");
                    DropDown.byLabel("Префикс", -1).select(acl.mask);
                }
            }
        });
        btnAclTrans.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertEquals(new ArrayList<>(), acls.stream().filter(acl -> !new Table(HEADER_ACL_TRANSACTION).isColumnValueEquals(HEADER_ACL_TRANSACTION, acl.mask))
                .collect(Collectors.toList()), "Не все acl были созданы");
    }


    public void dellAclTopics(List<String> names) {
        btnAclTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        List<String> topicsAcl = names.stream().filter(topic -> new Table(HEADER_ACL).isColumnValueEquals(HEADER_ACL, topic)).collect(Collectors.toList());
        //если список содержит указанный элемент, то выходим из функции, т.к. смысла запускать действия нет
        if (topicsAcl.isEmpty()) {
            createAclTopics(names);
        }
        runActionWithParameters("ACL на топики", "Пакетное удаление ACL Kafka", "Подтвердить", () -> {
            for (int i = 0; i < names.size(); i++) {
                //на первом топике жать + не нужно
                if (i != 0) {
                    btnAdd.shouldBe(Condition.enabled).click();
                }
                DropDown.byLabel("Common Name сертификата клиента", i + 1).select(names.get(i));
                Input.byLabel("Маска имени топика").setValue(names.get(i));
                if (i == 1) {
                    //Input.byLabel("Common Name сертификата клиента", 2).setValue(nameT2);
                    RadioGroup.byLabel("Выберите топик *", Integer.parseInt(names.get(i))).select("По имени");
                    DropDown.byLabel("Топики").select(names.get(i));
                }
                if (i == 2) {
                    RadioGroup.byLabel("Выберите топик *", Integer.parseInt(names.get(i))).select("Все топики");
                }
            }
        });
        btnAclTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertEquals(new ArrayList<>(), topicsAcl.stream()
                .filter(topic -> new Table(HEADER_ACL)
                        .isColumnValueEquals(HEADER_ACL, topic))
                .collect(Collectors.toList()), "Не все топики ACL были удалены");
    }

    public void createAclTransaction(List<String> names) {
        btnAclTrans.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        List<String> topicsAclTransaction = names.stream()
                .filter(topic -> !new Table(HEADER_ACL_TRANSACTION)
                        .isColumnValueEquals(HEADER_ACL_TRANSACTION, topic))
                .collect(Collectors.toList());
        //если список содержит указанный элемент, то выходим из функции, т.к. смысла запускать действия нет
        if (topicsAclTransaction.isEmpty())
            return;
        runActionWithParameters("ACL на транзакции", "Пакетное создание ACL на транзакцию Kafka", "Подтвердить", () -> {
            for (int i = 0; i < topicsAclTransaction.size(); i++) {
                //на первом топике жать + не нужно
                if (i != 0)
                    btnAdd.shouldBe(Condition.enabled).click();
                Input.byLabel("Common Name сертификата клиента", i + 1).setValue(topicsAclTransaction.get(i));
                Input.byLabel("Введите префикс идентификатора транзакции").setValue(topicsAclTransaction.get(i));
                if (i == 1) {
                    //Input.byLabel("Common Name сертификата клиента", 2).setValue(nameT2);
                    RadioGroup.byLabel("Введите идентификатор транзакции *", Integer.parseInt(topicsAclTransaction.get(i))).select("По имени");
                    Input.byLabel("Введите идентификатор транзакции *", 7).setValue(topicsAclTransaction.get(i));
                    //DropDown.byLabel("Топики").select(topicsAclTransaction.get(i));
                }
                if (i == 2) {
                    RadioGroup.byLabel("Введите идентификатор транзакции *", Integer.parseInt(topicsAclTransaction.get(i))).select("Все транзакции");
                }
            }
        });
        btnAclTrans.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertEquals(new ArrayList<>(), topicsAclTransaction.stream()
                .filter(topic -> !new Table(HEADER_ACL_TRANSACTION)
                        .isColumnValueEquals(HEADER_ACL_TRANSACTION, topic))
                .collect(Collectors.toList()), "Не все ACL на транзакции были созданы");
    }

    public void dellAclTransaction(List<String> names) {
        btnAclTrans.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        List<String> topicsAclTransaction = names.stream().filter(topic -> new Table(HEADER_ACL_TRANSACTION).isColumnValueEquals(HEADER_ACL_TRANSACTION, topic)).collect(Collectors.toList());
        //если список содержит указанный элемент, то выходим из функции, т.к. смысла запускать действия нет
        if (topicsAclTransaction.isEmpty()) {
            createAclTransaction(names);
        }
        runActionWithParameters("ACL на транзакции", "Пакетное удаление ACL на транзакцию Kafka", "Подтвердить", () -> {
            for (int i = 0; i < names.size(); i++) {
                //на первом топике жать + не нужно
                if (i != 0)
                    btnAdd.shouldBe(Condition.enabled).click();
                DropDown.byLabel("Common Name сертификата клиента", i + 1).select(names.get(i));
                Input.byLabel("Введите префикс идентификатора транзакции").setValue(names.get(i));
                if (i == 1) {
                    //Input.byLabel("Common Name сертификата клиента", 2).setValue(nameT2);
                    RadioGroup.byLabel("Введите идентификатор транзакции *", Integer.parseInt(names.get(i))).select("По имени");
                    Input.byLabel("Введите идентификатор транзакции *", 7).setValue(names.get(i));
                    //DropDown.byLabel("Топики").select(topicsAclTransaction.get(i));
                }
                if (i == 2) {
                    RadioGroup.byLabel("Введите идентификатор транзакции *", Integer.parseInt(names.get(i))).select("Все транзакции");
                }
        }});
        btnAclTrans.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertEquals(new ArrayList<>(), topicsAclTransaction.stream()
                .filter(topic -> new Table(HEADER_ACL_TRANSACTION)
                        .isColumnValueEquals(HEADER_ACL_TRANSACTION, topic))
                .collect(Collectors.toList()), "Не все топики ACL на транзакции были удалены");
    }

    public void createAclIdempotent(String nameT1) {
        btnIdempAcl.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (!(new Table(HEADER_ACL_IDEMPOTENT).isColumnValueContains(HEADER_ACL_IDEMPOTENT, nameT1))) {
            runActionWithParameters("Идемпотентных ACL", "Создание идемпотентных ACL Kafka", "Подтвердить", () -> {
                Input.byLabel("Common Name сертификата клиента").setValue(nameT1);
            });
            btnIdempAcl.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            Assertions.assertEquals(nameT1, new Table(HEADER_ACL_IDEMPOTENT).getRowByColumnValue(HEADER_ACL_IDEMPOTENT, nameT1).getValueByColumn(HEADER_ACL_IDEMPOTENT),
                    "Ошибка cоздания идемпотентных ACL Kafka ");
        }
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
    //    new Alert().checkColor(Alert.Color.RED).checkText("Значение поля не уникально").close();
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
