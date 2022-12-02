package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.helper.Configure;
import models.cloud.orderService.products.ApacheKafkaCluster;
import models.cloud.subModels.Flavor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;
import java.time.Duration;
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
    private static final String HEADER_ACL = "Маска";
    private static final String HEADER_ACL_TRANSACTION = "Идентификатор транзакции или маска";
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
        runActionWithParameters(BLOCK_APP, "Удалить рекурсивно", "Удалить", () ->
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
        expandDisk(name, size, node);
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

    public void changeParamTopics(String nameT1) {
        btnTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        runActionWithParameters("Список топиков", "Изменить параметр топиков Kafka Cluster", "Подтвердить", () -> {
            Dialog dlg = new Dialog("Изменить параметр топиков Kafka Cluster");
            dlg.setDropDownValue("Топик *", nameT1);
            DropDown.byLabel("Тип очистки").select("compact");
        });
        btnTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertEquals(nameT1, new Table(HEADER_NAME_TOPIC).getRowByColumnValue(HEADER_NAME_TOPIC, nameT1).getValueByColumn(HEADER_NAME_TOPIC),
                "Ошибка создания топика");
    }

    public void dellTopics(String nameT1, String nameT2) {

        btnTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (!(new Table(HEADER_NAME_TOPIC).isColumnValueContains(HEADER_NAME_TOPIC, nameT1))) {createTopics(nameT1,nameT2);}
        runActionWithParameters("Список топиков", "Пакетное удаление Topic-ов Kafka", "Подтвердить", () -> {
            Dialog dlg = new Dialog("Пакетное удаление Topic-ов Kafka");
            dlg.setDropDownValue("Имена Topic-ов", nameT1);
            dlg.setDropDownValue("Имена Topic-ов", nameT2);
        });
        btnTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertFalse(new Table(HEADER_NAME_TOPIC).isColumnValueEquals("", nameT1), "Топик не удален");
    }

    public void createAclTopics(String nameT1, String nameT2) {
        btnAclTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (!(new Table(HEADER_ACL).isColumnValueContains(HEADER_ACL, nameT1))){
            runActionWithParameters("ACL на топики", "Пакетное создание ACL Kafka", "Подтвердить", () -> {
            Input.byLabel("Common Name сертификата клиента").setValue(nameT1);
            Input.byLabel("Маска имени топика").setValue(nameT1);
            btnAdd.shouldBe(Condition.enabled).click();
            Input.byLabel("Common Name сертификата клиента", 2).setValue(nameT2);
            RadioGroup.byLabel("Выберите топик *", 2).select("По имени");
            DropDown.byLabel("Топики").select(nameT1);
        });
        btnAclTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertEquals(nameT1, new Table(HEADER_ACL).getRowByColumnValue(HEADER_ACL, nameT1).getValueByColumn(HEADER_ACL),
                "Ошибка создания ACL на топик");
    }}

    public void dellAclTopics(String nameT1, String nameT2) {
        btnAclTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (!(new Table(HEADER_ACL).isColumnValueContains(HEADER_ACL, nameT1))){createAclTopics(nameT1,nameT2);}
        runActionWithParameters("ACL на топики", "Пакетное удаление ACL Kafka", "Подтвердить", () -> {
            DropDown.byLabel("Common Name сертификата клиента").select(nameT1);
            Input.byLabel("Маска имени топика").setValue(nameT1);
            btnAdd.shouldBe(Condition.enabled).click();
            DropDown.byLabel("Common Name сертификата клиента", 2).select(nameT2);
            RadioGroup.byLabel("Выберите топик *", 2).select("По имени");
            DropDown.byLabel("Топики").select(nameT1);
        });
        btnAclTopics.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertFalse(new Table(HEADER_ACL).isColumnValueEquals("", nameT1), "ТопикACL не удален");
    }

    public void createAclTrans(String nameT1, String nameT2) {
        btnAclTrans.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (!(new Table(HEADER_ACL_TRANSACTION).isColumnValueContains(HEADER_ACL_TRANSACTION, nameT1))){
        runActionWithParameters("ACL на транзакции", "Пакетное создание ACL на транзакцию Kafka", "Подтвердить", () -> {
            Input.byLabel("Common Name сертификата клиента").setValue(nameT1);
            Input.byLabel("Введите префикс идентификатора транзакции").setValue(nameT1);
            btnAdd.shouldBe(Condition.enabled).click();
            Input.byLabel("Common Name сертификата клиента", 2).setValue(nameT2);
            RadioGroup.byLabel("Введите идентификатор транзакции *", 2).select("По имени");
            Input.byLabel("Введите идентификатор транзакции *", 7).setValue(nameT1);
            btnAdd.shouldBe(Condition.enabled).click();
            Input.byLabel("Common Name сертификата клиента", 3).setValue(nameT1);
            RadioGroup.byLabel("Введите идентификатор транзакции *", 3).select("Все транзакции");
        });
        btnAclTrans.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertEquals(nameT1, new Table(HEADER_ACL_TRANSACTION).getRowByColumnValue(HEADER_ACL_TRANSACTION, nameT1).getValueByColumn(HEADER_ACL_TRANSACTION),
                "Ошибка создания ACL на транзакции");
    }}

    public void dellAclTrans(String nameT1, String nameT2) {
        btnAclTrans.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (!(new Table(HEADER_ACL_TRANSACTION).isColumnValueContains(HEADER_ACL_TRANSACTION, nameT1))){createAclTrans(nameT1,nameT2);}
        runActionWithParameters("ACL на транзакции", "Пакетное удаление ACL на транзакцию Kafka", "Подтвердить", () -> {
            DropDown.byLabel("Common Name сертификата клиента").select(nameT1);
            Input.byLabel("Введите префикс идентификатора транзакции").setValue(nameT1);
            btnAdd.shouldBe(Condition.enabled).click();
            DropDown.byLabel("Common Name сертификата клиента", 2).select(nameT2);
            RadioGroup.byLabel("Введите идентификатор транзакции *", 2).select("По имени");
            Input.byLabel("Введите идентификатор транзакции *", 7).setValue(nameT1);
            btnAdd.shouldBe(Condition.enabled).click();
            DropDown.byLabel("Common Name сертификата клиента", 3).select(nameT1);
            RadioGroup.byLabel("Введите идентификатор транзакции *", 3).select("Все транзакции");

        });
        btnAclTrans.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertFalse(new Table(HEADER_ACL_TRANSACTION).isColumnValueEquals("", nameT1), "ACLTransaction не удален");
    }

    public void createAclIdemp(String nameT1) {
        btnIdempAcl.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (!(new Table(HEADER_ACL_IDEMPOTENT).isColumnValueContains(HEADER_ACL_IDEMPOTENT, nameT1))){
        runActionWithParameters("Идемпотентных ACL", "Создание идемпотентных ACL Kafka", "Подтвердить", () -> {
            Input.byLabel("Common Name сертификата клиента").setValue(nameT1);
        });
        btnIdempAcl.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertEquals(nameT1, new Table(HEADER_ACL_IDEMPOTENT).getRowByColumnValue(HEADER_ACL_IDEMPOTENT, nameT1).getValueByColumn(HEADER_ACL_IDEMPOTENT),
                "Ошибка cоздания идемпотентных ACL Kafka ");
    }}

    public void dellAclIdemp(String nameT1) {
        btnIdempAcl.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (!(new Table(HEADER_ACL_IDEMPOTENT).isColumnValueContains(HEADER_ACL_IDEMPOTENT, nameT1))){createAclIdemp(nameT1);}
        runActionWithParameters("Идемпотентных ACL", "Удаление идемпотентных ACL Kafka", "Подтвердить", () -> {
            Input.byLabel("Common Name сертификата клиента").setValue(nameT1);
        });
        btnIdempAcl.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertFalse(new Table(HEADER_ACL_IDEMPOTENT).isColumnValueEquals("", nameT1), "Ошибка удаления идемпотентных ACL Kafka");
    }

    public void checkNameCluster() {

        String nameCluster = new Table("Имя кластера").getFirstValueByColumn("Имя кластера");
        if (Configure.ENV.equals("prod") || Configure.ENV.equals("blue"))
            product = ApacheKafkaCluster.builder().env("LT").platform("OpenStack").segment("dev-srv-app").build();
        else
            product = ApacheKafkaCluster.builder().env("DEV").platform("vSphere").segment("dev-srv-app").build();
        product.init();
        new IndexPage()
                .clickOrderMore()
                .selectProduct(product.getProductName());
        ApacheKafkaClusterOrderPage orderPage = new ApacheKafkaClusterOrderPage();

        //Проверка кнопки Заказать на неактивность, до заполнения полей
        orderPage.getOrderBtn().shouldBe(Condition.disabled);

        //Проверка Детали заказа
        orderPage.getNameCluster().setValue(nameCluster);
        new Alert().checkColor(Alert.Color.RED).checkText("Значение поля не уникально").close();

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
