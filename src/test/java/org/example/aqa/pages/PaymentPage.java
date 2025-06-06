package org.example.aqa.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.example.aqa.data.DataHelper;
import org.example.aqa.data.ConfigReader;

import java.io.IOException;
import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.Selectors.byTagAndText;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class PaymentPage {
    private final SelenideElement heading = $(byTagAndText("h3", "Оплата по карте"));
    private final SelenideElement notificationApproved = $(".notification_status_ok");
    private final SelenideElement notificationDeclined = $(".notification_status_error");
    private final SelenideElement notification = $(".notification");
    private final SelenideElement cardNumberField = $("input", 0);
    private final SelenideElement cardExpireMonthField = $("input", 1);
    private final SelenideElement cardExpireYearField = $("input", 2);
    private final SelenideElement cardOwnerNameField = $("input", 3);
    private final SelenideElement cardCVCField = $("input", 4);
    private final SelenideElement processButton = $(byText("Продолжить"));
    private final ElementsCollection errorMessages = $$(".input__sub");

    public PaymentPage() {
        heading.shouldBe(visible);
    }

    public void shouldBeError(String message) {
        $(byText(message)).shouldBe(visible);
    }

    public void shouldBeNoErrors() {
        errorMessages.shouldBe(empty);
    }

    public void shouldBeApprovedMessage() throws IOException {
        notificationApproved.shouldBe(visible, Duration.ofSeconds(ConfigReader.getInstance().getConfig().getSecondstowait()));
    }

    public void shouldBeDeclinedMessage() throws IOException {
        notificationDeclined.shouldBe(visible, Duration.ofSeconds(ConfigReader.getInstance().getConfig().getSecondstowait()));
    }

    public void processTheCard(DataHelper.CardInfo cardInfo) {
        cardNumberField.setValue(cardInfo.cardNumber());
        cardExpireMonthField.setValue(cardInfo.cardExpireMonth());
        cardExpireYearField.setValue(cardInfo.cardExpireYear());
        cardOwnerNameField.setValue(cardInfo.cardOwnerName());
        cardCVCField.setValue(cardInfo.cardCVC());
        processButton.click();
    }

    public void processTheCardAndWait(DataHelper.CardInfo cardInfo) throws IOException {
        processTheCard(cardInfo);
        notification.shouldBe(visible, Duration.ofSeconds(ConfigReader.getInstance().getConfig().getSecondstowait()));
    }
}
