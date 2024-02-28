package ui.t1.tests.cdn;

import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.AbstractEntity;
import models.t1.cdn.Certificate;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import steps.t1.cdn.CdnCertificateClient;
import ui.elements.Alert;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.tests.AbstractT1Test;
import ui.t1.tests.WithAuthorization;
import ui.t1.tests.engine.EntitySupplier;

@Epic("CDN")
@Feature("Действия с ертификатами")
@Tags({@Tag("cdn")})
@Tag("morozov_ilya")
@WithAuthorization(Role.CLOUD_ADMIN)
@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CdnCertificatesTests extends AbstractT1Test {

    private final EntitySupplier<Certificate> cdnCertificate = lazy(() -> {
        Certificate certificate = Certificate.builder()
                .projectId(getProjectId())
                .name(RandomStringUtils.randomAlphabetic(4).toLowerCase() + "-autotest-certificate")
                .build()
                .deleteMode(AbstractEntity.Mode.AFTER_CLASS);
        new IndexPage().goToCdn()
                .switchToCertificateTab()
                .create(certificate);
        Alert.green("Сертификат успешно добавлен");
        return certificate;
    });

    @Test
    @Order(1)
    @DisplayName("CDN. Добавление пользовательского   сертификата через поле")
    @TmsLink("SOUL-5375")
    public void createCertificateTest() {
        String name = cdnCertificate.get().getName();
        new IndexPage().goToCdn()
                .switchToCertificateTab()
                .checkCdnEntityExistByName(name);
    }

    @Test
    @Order(2)
    @DisplayName("CDN. Получение списка пользовательских сертификатов")
    @TmsLink("SOUL-5374")
    public void checkCounterTest() {
        cdnCertificate.get();
        String expectedCountOfSourceGroups = String.valueOf(CdnCertificateClient.getCertificates(getProjectId()).size());
        new IndexPage().goToCdn()
                .switchToCertificateTab()
                .checkCounter(expectedCountOfSourceGroups);
    }

    @Test
    @Order(100)
    @DisplayName("CDN. Удаление пользовательского сертификата")
    @TmsLink("SOUL-5376")
    public void deleteCertificateTest() {
        String name = cdnCertificate.get().getName();
        new IndexPage().goToCdn()
                .switchToCertificateTab()
                .delete(name)
                .checkThatCdnEntityDoesNotExist(name);
    }
}
