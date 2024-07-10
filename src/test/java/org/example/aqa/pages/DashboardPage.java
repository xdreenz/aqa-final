package org.example.aqa.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selectors.byTagAndText;

public class DashboardPage {
    private final SelenideElement heading = $(byTagAndText("h2", "Путешествие дня"));
    private final SelenideElement paymentButton = $("button", 0);
    private final SelenideElement creditButton = $("button", 1);

    public DashboardPage() {
        heading.shouldBe(visible);
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
