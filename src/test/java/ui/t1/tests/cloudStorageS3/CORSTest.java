package ui.t1.tests.cloudStorageS3;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.BlockTests;
import org.junit.jupiter.api.*;
import ui.t1.pages.IndexPage;
import ui.t1.pages.S3Storage.CORS.CORSModal.Method;
import ui.t1.pages.S3Storage.CORS.CORSModal.AccessControls;
import ui.t1.tests.engine.AbstractStorageTest;

@BlockTests
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Feature("Бакеты. CORS")
public class CORSTest extends AbstractStorageTest {

    @Test
    @Order(1)
    @TmsLink("1281945")
    @DisplayName("Бакет. CORS. Добавить")
    void addCORS() {
        String[] Methods = new String[]{Method.POST.getMethod(),
                                        Method.GET.getMethod()};

        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, true)
                .createBucket()
                .openBucket(name)
                .gotoCORS()
                .addCORS()
                .setCORSOrigins("http://1",true)
                .setAllowedMethods(Methods)
                .setAllowedHeaders("AHeader",true)
                .setExposeHeaders("EHeader",true)
                .setAccessControl(AccessControls.ONEMIN)
                .createCORS()
                .checkCORSExists("http://1", true);


        new IndexPage().goToS3CloudStoragePage()
                .deleteBucket(name);

    }

    @Test
    @Order(2)
    @TmsLink("1281947")
    @DisplayName("Бакет. CORS. Удалить")
    void deleteCORS() {
        String[] Methods = new String[]{Method.POST.getMethod(),
                Method.GET.getMethod()};

        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, true)
                .createBucket()
                .openBucket(name)
                .gotoCORS()
                .addCORS()
                .setCORSOrigins("http://1",true)
                .setAllowedMethods(Methods)
                .createCORS()
                .checkCORSExists("http://1", true)
                .deleteCORS("http://1")
                .checkCORSExists("http://1", false);

        new IndexPage().goToS3CloudStoragePage()
                .deleteBucket(name);

    }

    @Test
    @Order(3)
    @TmsLink("1281946")
    @DisplayName("Бакет. CORS. Редактировать")
    void changeCORS() {
        String[] Methods = new String[]{Method.POST.getMethod(),
                Method.GET.getMethod()};

        String[] MethodsNew = new String[]{Method.HEAD.getMethod(),
                Method.PUT.getMethod()};

        new IndexPage().goToS3CloudStoragePage()
                .addBucket(name, true)
                .createBucket()
                .openBucket(name)
                .gotoCORS()
                .addCORS()
                .setCORSOrigins("http://1",true)
                .setAllowedMethods(Methods)
                .createCORS()
                .checkCORSExists("http://1", true)
                .editCORS("http://1")
                .setCORSOrigins("http://2",true)
                .setAllowedMethods(MethodsNew)
                .renewCORS()
                .checkCORSExists("http://1", false)
                .checkCORSExists("http://2", true)
                .editCORS("http://2")
                .checkCORSOrigins("http://2")
                .checkAllowedMethods("HEAD, PUT")
                .closeCORSModal();

        new IndexPage().goToS3CloudStoragePage()
                .deleteBucket(name);

    }
}
