package ui.t1.pages.cdn.resource;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.elements.Button;
import ui.elements.Dialog;

import static com.codeborne.selenide.Selectors.byText;

public class HttpHeadersTab {

    private final Button editButton = Button.byText("Редактировать");
    private final SelenideElement title = Selenide.$(byText("HTTP -заголовки и методы"));
    private final SelenideElement headersRequestToSourceTitle = Selenide.$(byText("Заголовки запроса к источнику"));
    private final SelenideElement headersResponseToSourceTitle = Selenide.$(byText("Заголовки ответа к клиенту"));
    private final SelenideElement corsTitle = Selenide.$(byText("CORS при ответе клиенту"));
    private final SelenideElement methodTitle = Selenide.$(byText("Методы запросов от клиентов"));

    @Step("[Проверка] все стандартные блоки отображаются на странице")
    public HttpHeadersTab checkTitles() {
        Assertions.assertAll("[Проверка] все стандартные блоки отображаются на странице",
                () -> title.shouldBe(Condition.visible.because("Заголовок \"HTTP -заголовки и метод\" должен отображаться")),
                () -> headersRequestToSourceTitle.shouldBe(Condition.visible.because("Блок \"Заголовки запроса к источнику\" должен отображаться")),
                () -> headersResponseToSourceTitle.shouldBe(Condition.visible.because("Блок \"Заголовки ответа к клиенту\" должен отображаться")),
                () -> corsTitle.shouldBe(Condition.visible.because("Блок \"CORS при ответе клиенту\" должен отображаться")),
                () -> methodTitle.shouldBe(Condition.visible.because("Блок \"Методы запросов от клиентов\" должен отображаться"))
        );
        return this;
    }

    @Step("Клик по кнопке Редактировать")
    public HttpHeadersTab clickEditButton() {
        editButton.click();
        return this;
    }

    @Step("[Проверка] Модальное окно редактирвоания отображается")
    public HttpHeadersTab checkEditModalIsAppear() {
        Dialog editDialog = Dialog.byTitle("Редактировать HTTP-заголовки и методы");
        editDialog.getDialog().shouldBe(Condition.visible.because("Модальное окно редактирования должно отображаться"));
        return this;
    }
}
