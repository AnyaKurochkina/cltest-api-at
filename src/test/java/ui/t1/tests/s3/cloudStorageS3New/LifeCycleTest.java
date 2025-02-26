package ui.t1.tests.s3.cloudStorageS3New;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.BlockTests;
import org.junit.jupiter.api.*;
import ui.t1.pages.IndexPage;
import ui.t1.pages.S3Storage.LifeCycle.LifeCycleModal.LifeCycleConditionTriggers;
import ui.t1.pages.S3Storage.LifeCycle.LifeCycleModal.LifeCycleRuleTypes;
import ui.t1.tests.s3.AbstractStorageTest;

@BlockTests
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Feature("Бакеты. ЖЦ")
public class LifeCycleTest extends AbstractStorageTest {

    @Test
    @Order(1)
    @TmsLink("1412118")
    @DisplayName("Бакет. ЖЦ. Добавить")
    void addLifeCycle() {

        new IndexPage().goToNewS3CloudStoragePage()
                .addBucket(name, true)
                .createBucket()
                .openBucket(name)
                .gotoLifeCycle()
                .addLifeCycle()
                .setName("Name")
                .setPrefix("Prefix")
                .setRuleType(LifeCycleRuleTypes.ABORTCOMPLETEMULTIPARTUPLOAD)
                .setRuleType(LifeCycleRuleTypes.EXPIRATION)
                .setDays("30")
                .setConditionalTrigger(LifeCycleConditionTriggers.DAYSAMMOUNT)
                .createLifeCycle()
                .checkLifeCycleExists("Name", true);

        new IndexPage().goToNewS3CloudStoragePage()
                .deleteBucket(name);

    }

    @Test
    @Order(2)
    @TmsLink("1412141")
    @DisplayName("Бакет. ЖЦ. Удалить")
    void deleteLifeCycle() {

        new IndexPage().goToNewS3CloudStoragePage()
                .addBucket(name, true)
                .createBucket()
                .openBucket(name)
                .gotoLifeCycle()
                .addLifeCycle()
                .setName("Name")
                .setPrefix("Prefix")
                .setRuleType(LifeCycleRuleTypes.ABORTCOMPLETEMULTIPARTUPLOAD)
                .setRuleType(LifeCycleRuleTypes.EXPIRATION)
                .setDays("30")
                .setConditionalTrigger(LifeCycleConditionTriggers.DAYSAMMOUNT)
                .createLifeCycle()
                .checkLifeCycleExists("Name", true)
                .deleteLifeCycle("Name")
                .checkLifeCycleExists("Name", false);

        new IndexPage().goToNewS3CloudStoragePage()
                .deleteBucket(name);

    }

    @Test
    @Order(3)
    @TmsLink("1412127")
    @DisplayName("Бакет. ЖЦ. Редактировать")
    void editLifeCycle() {

        new IndexPage().goToNewS3CloudStoragePage()
                .addBucket(name, true)
                .createBucket()
                .openBucket(name)
                .gotoLifeCycle()
                .addLifeCycle()
                .setName("Name")
                .setPrefix("Prefix")
                .setRuleType(LifeCycleRuleTypes.ABORTCOMPLETEMULTIPARTUPLOAD)
                .setRuleType(LifeCycleRuleTypes.EXPIRATION)
                .setDays("30")
                .setConditionalTrigger(LifeCycleConditionTriggers.DAYSAMMOUNT)
                .createLifeCycle()
                .editLifeCycle("Name")
                .setDays("20")
                .setPrefix("Prefix2")
                .updateLifeCycle()
                .editLifeCycle("Name")
                .checkPrefix("Prefix2")
                .checkDays("20")
                .closeLifeCycleModal()
                .checkLifeCycleExists("Name", true);


        new IndexPage().goToNewS3CloudStoragePage()
                .deleteBucket(name);

    }
}
