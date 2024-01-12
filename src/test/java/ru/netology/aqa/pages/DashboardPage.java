package ru.netology.aqa.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class DashboardPage {
    private final SelenideElement heading = $("h2");
    private final SelenideElement paymentButton = $("button", 0);
    private final SelenideElement creditButton = $("button", 1);

    public DashboardPage() {
        heading.shouldHave(exactTextCaseSensitive("Путешествие дня")).shouldBe(visible);
    }

    public PaymentPage choosePaymentOption() {
        paymentButton.click();
        return new PaymentPage();
    }

    public CreditRequestPage chooseCreditRequestOption() {
        creditButton.click();
        return new CreditRequestPage();
    }
}
