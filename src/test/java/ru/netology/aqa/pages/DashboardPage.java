package ru.netology.aqa.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DashboardPage {
    private final SelenideElement heading = $("h2");
    private final ElementsCollection buttons = $$("button");
    private final SelenideElement paymentButton = buttons.get(0);
    private final SelenideElement creditButton = buttons.get(1);

    public DashboardPage() {
        heading.shouldHave(exactText("Путешествие дня")).shouldBe(visible);
    }

    public PaymentPage choosePayment() {
        paymentButton.click();
        return new PaymentPage();
    }

    public CreditRequestPage chooseCredit() {
        creditButton.click();
        return new CreditRequestPage();
    }
}
