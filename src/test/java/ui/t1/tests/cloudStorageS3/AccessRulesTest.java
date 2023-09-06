package ui.t1.tests.cloudStorageS3;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.BlockTests;
import org.junit.jupiter.api.*;
import ui.t1.pages.IndexPage;
import ui.t1.pages.S3Storage.AccessRules.AccessRulesModal.AccessRulesTypes;

@BlockTests
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Feature("Бакеты. Правила доступа")
public class AccessRulesTest extends AbstractStorageTest {


    @Test
    @Order(1)
    @TmsLink("520548")
    @DisplayName("Правила доступа. Добавить правило доступа ")
    void addAccessRule() {

        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, false)
                .createBucket()
                .openBucket(name)
                .gotoAccessRulesLayer()
                .addAccessRule()
                .setUser("ilkaboomboom@rambler.ru")
                .setRules(true, AccessRulesTypes.READ, AccessRulesTypes.WRITEACL)
                .createAccessRule()
                .checkRule("Илья Федосов", true);

        new IndexPage().goToS3CloudStoragePage()
                .deleteBucket(name);
    }

    @Test
    @Order(2)
    @TmsLink("520571")
    @DisplayName("Правила доступа. Редактировать правило доступа ")
    void editAccessRule() {

        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, false)
                .createBucket()
                .openBucket(name)
                .gotoAccessRulesLayer()
                .addAccessRule()
                .setUser("ilkaboomboom@rambler.ru")
                .setRules(true, AccessRulesTypes.WRITE, AccessRulesTypes.READACL)
                .createAccessRule()
                .checkRule("Илья Федосов", true)
                .editAccessRule("Илья Федосов")
                .setRules(false, AccessRulesTypes.WRITE, AccessRulesTypes.READACL)
                .setRules(true, AccessRulesTypes.READ, AccessRulesTypes.WRITEACL)
                .saveAccessRule()
                .editAccessRule("Илья Федосов")
                .checkRule(false, AccessRulesTypes.WRITE)
                .checkRule(false, AccessRulesTypes.READACL)
                .checkRule(true, AccessRulesTypes.READ)
                .checkRule(true, AccessRulesTypes.WRITEACL)
                .closeAccessRule();

        new IndexPage().goToS3CloudStoragePage()
                .deleteBucket(name);
    }

    @Test
    @Order(3)
    @TmsLink("520574")
    @DisplayName("Правила доступа. Удалить правило доступа ")
    void deleteAccessRule() {

        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, false)
                .createBucket()
                .openBucket(name)
                .gotoAccessRulesLayer()
                .addAccessRule()
                .setUser("ilkaboomboom@rambler.ru")
                .setRules(true, AccessRulesTypes.WRITE, AccessRulesTypes.READACL)
                .createAccessRule()
                .deleteAccessRule("Илья Федосов")
                .checkRule("Илья Федосов", false);

        new IndexPage().goToS3CloudStoragePage()
                .deleteBucket(name);
    }
}
