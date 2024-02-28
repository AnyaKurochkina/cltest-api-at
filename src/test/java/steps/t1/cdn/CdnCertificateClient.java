package steps.t1.cdn;

import io.qameta.allure.Step;
import models.t1.cdn.CertificateListItem;
import tests.routes.cdn.CdnCertificateApi;

import java.util.List;
import java.util.Objects;

public class CdnCertificateClient extends AbstractCdnClient {

    @Step("Получение всех сертификатов для проекта с id: {0}")
    public static List<CertificateListItem> getCertificates(String projectId) {
        return getRequestSpec().api(CdnCertificateApi.getCertificates, projectId)
                .jsonPath()
                .getList("list", CertificateListItem.class);
    }

    @Step("Удаление сертификата в проекте: {0}, по имени: {1}")
    public static void deleteCertificateByName(String projectId, String certificateName) {
        String certificateId = getCertificates(projectId).stream()
                .filter(certificate -> Objects.equals(certificate.getName(), certificateName))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        String.format("Не найден ни один сертификат с именем %s:", certificateName)))
                .getId();

        deleteCertificate(projectId, certificateId);
    }

    @Step("Удаление сертификата в проекте: {0}, и с id: {1}")
    private static void deleteCertificate(String projectId, String certificateId) {
        getRequestSpec().api(CdnCertificateApi.deleteCertificateById, projectId, certificateId);
    }
}
