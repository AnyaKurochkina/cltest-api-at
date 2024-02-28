package ui.t1.pages.cdn;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.t1.cdn.Certificate;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.elements.TextArea;

import java.security.KeyPair;

import static com.codeborne.selenide.Selenide.$x;

public class CertificateTab extends AbstractCdnTab<CertificateTab, Certificate> {

    private final SelenideElement deleteButton = $x("//*[contains(@aria-labelledby, 'user-certificates')]//button[@type='button']");

    @Override
    @Step("Создание Пользовательского сертификата CDN")
    public void create(Certificate certificate) {
        addButton.click();
        Dialog addCertificateDialog = Dialog.byTitle("Добавить пользовательский сертификат");
        addCertificateDialog.setInputValueV2("Название", certificate.getName());
        KeyPair keyPair = certificate.generateKeyPair();
        addCertificateDialog.setTextarea(TextArea.byName("sslCertificate"), certificate.generateSelfSignedCertificateAsString(keyPair));
        addCertificateDialog.setTextarea(TextArea.byName("sslPrivateKey"), certificate.generatePrivateRSAKeyAsString(keyPair));
        addCertificateDialog.clickButtonByType("submit");
    }

    @Override
    @Step("Удаление Пользовательского сертификата CDN")
    public CertificateTab delete(String certificateName) {
        deleteButton.shouldBe(Condition.visible.because("Кнопка удаления должна отображаться"))
                .click();
        Dialog.byTitle("Удаление пользовательского сертификата").clickButton("Удалить");
        Alert.green("Сертификат успешно удален");
        return this;
    }

    @Override
    public String getMainTableName() {
        return "Пользовательские сертификаты";
    }
}
