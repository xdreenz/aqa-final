package ru.netology.aqa.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.netology.aqa.data.DataHelper;

import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class PaymentPage{
    private final SelenideElement heading = $x("/html/body/div[1]/div/h3");
    private final ElementsCollection inputFields = $$(".input__inner");
    private final ElementsCollection buttons = $$("button");
    private final ElementsCollection errorMessages = $$(".input__sub");
    private final SelenideElement cardNumberField = inputFields.get(0);
    private final SelenideElement cardExpireMonthField = inputFields.get(1);
    private final SelenideElement cardExpireYearField = inputFields.get(2);
    private final SelenideElement cardOwnerNameField = inputFields.get(3);
    private final SelenideElement cardCVCField = inputFields.get(4);
    private final SelenideElement paymentButton = buttons.get(0);
    private final SelenideElement creditButton = buttons.get(1);
    private final SelenideElement proceedButton = buttons.get(2);

    public PaymentPage() {
        heading.shouldHave(exactText("Оплата по карте")).shouldBe(visible);
    }

    public void shouldBeError(String message) {
        errorMessages.shouldHave(texts(message));
    }

    public void proceedCard(DataHelper.CardInfo cardInfo) {
        cardNumberField.setValue(cardInfo.getCardNumber());
        cardExpireMonthField.setValue(cardInfo.getCardExpireMonth());
        cardExpireYearField.setValue(cardInfo.getCardExpireYear());
        cardOwnerNameField.setValue(cardInfo.getCardOwnerName());
        cardCVCField.setValue(cardInfo.getCardCVC());
        proceedButton.click();
    }
}
