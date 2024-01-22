package ui.t1.tests.cdn;

import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.AbstractEntity;
import models.t1.cdn.SourceGroup;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.elements.Alert;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.tests.AbstractT1Test;
import ui.t1.tests.WithAuthorization;
import ui.t1.tests.engine.EntitySupplier;

@Epic("CDN")
@Feature("Действия с ертификатами")
@Tags({@Tag("cdn")})
@WithAuthorization(Role.CLOUD_ADMIN)
@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CdnCertificatesTests extends AbstractT1Test {

    private final EntitySupplier<SourceGroup> cdnSourceGroup = lazy(() -> {
        SourceGroup sourceGroup = new SourceGroup(getProjectId(), "t1.ru",
                RandomStringUtils.randomAlphabetic(4).toLowerCase() + ".autotest-source-group.com")
                .deleteMode(AbstractEntity.Mode.AFTER_CLASS);
        new IndexPage().goToCdn()
                .switchToSourceGroupTab()
                .createSourceGroup(sourceGroup);
        Alert.green("Группа источников успешно добавлена");
        return sourceGroup;
    });

    /*
    -----BEGIN CERTIFICATE-----
    gsdjhfgskjdhflksdhjflksdhjf
    -----END CERTIFICATE-----

    -----BEGIN RSA PRIVATE KEY-----
    sfsdhjfusldiyufilosdufilossdlfjujf
    -----END RSA PRIVATE KEY-----
     */

    @Test
    @Order(1)
    @DisplayName("CDN. Создание группы источников")
    @TmsLink("SOUL-5371")
    public void createSourceGroupTest() {
        // -----BEGIN CERTIFICATE----- -----END CERTIFICATE----- -----BEGIN RSA PRIVATE KEY----- -----END RSA PRIVATE KEY-----
        String name = cdnSourceGroup.get().getName();
        new IndexPage().goToCdn()
                .switchToSourceGroupTab()
                .checkCdnSourceGroupExistByName(name);
    }
}
