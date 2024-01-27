package steps.t1.cdn;

import core.helper.http.Response;
import io.qameta.allure.Step;
import models.t1.cdn.CertificateListItem;

import java.util.List;
import java.util.Objects;

public class CdnCertificateClient extends AbstractCdnClient {

    private static final String BASE_PATH = API_URL + "certificates";

    @Step("Получение всех сертификатов для проекта с id: {0}")
    public static List<CertificateListItem> getCertificate(String projectId) {
        return getRequestSpec()
                .get(BASE_PATH, projectId)
                .assertStatus(200)
                .jsonPath()
                .getList("list", CertificateListItem.class);
    }

    @Step("Удаление сертификата в проекте: {0}, имени: {1}")
    public static Response deleteCertificateByName(String projectId, String certificateName) {
        String certificateId = getCertificate(projectId).stream()
                .filter(certificate -> Objects.equals(certificate.getName(), certificateName))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        String.format("Не найден ни один сертификат с именем %s:", certificateName)))
                .getId();

        return deleteCertificate(projectId, certificateId);
    }

    @Step("Удаление сертификата в проекте: {0}, и с id: {1}")
    private static Response deleteCertificate(String projectId, String certificateId) {
        return getRequestSpec()
                .delete(BASE_PATH + "/" + certificateId, projectId);
    }
}
