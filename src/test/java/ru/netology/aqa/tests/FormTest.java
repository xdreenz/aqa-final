package ru.netology.aqa.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.aqa.data.DataHelper;
import ru.netology.aqa.pages.DashboardPage;
import ru.netology.aqa.pages.PaymentPage;

import static com.codeborne.selenide.Selenide.open;

public class FormTest {
    PaymentPage paymentPage;

    @BeforeEach
    void setUp() {
        var dashboardPage = open("http://localhost:8080", DashboardPage.class);
        paymentPage = dashboardPage.choosePayment();
    }

    @Test
    @DisplayName("Happy path, should be no errors")
    void shouldBeSuccess1() {
        paymentPage.proceedTheCard(DataHelper.generateValidCardInfo());
        paymentPage.shouldBeNoErrors();
    }

    @Test
    @DisplayName("No card number, should be an error")
    void shouldBeSuccess2() {
        var cardInfo = new DataHelper.CardInfo("", DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        paymentPage.proceedTheCard(cardInfo);
        paymentPage.shouldBeError("Ошибка");
    }

    @Test
    @DisplayName("No card expire month, should be an error")
    void shouldBeSuccess3() {
        var cardInfo = new DataHelper.CardInfo(DataHelper.generateValidCardNumber(), "", DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        paymentPage.proceedTheCard(cardInfo);
        paymentPage.shouldBeError("Ошибка");
    }

    @Test
    @DisplayName("No card expire year, should be an error")
    void shouldBeSuccess4() {
        var cardInfo = new DataHelper.CardInfo(DataHelper.generateValidCardNumber(), DataHelper.generateValidCardExpireMonth(), "",
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        paymentPage.proceedTheCard(cardInfo);
        paymentPage.shouldBeError("Ошибка");
    }

    @Test
    @DisplayName("No card owner name, should be an error")
    void shouldBeSuccess5() {
        var cardInfo = new DataHelper.CardInfo(DataHelper.generateValidCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                "", DataHelper.generateValidCardCVV());
        paymentPage.proceedTheCard(cardInfo);
        paymentPage.shouldBeError("Ошибка");
    }

    @Test
    @DisplayName("No card CVV, should be an error")
    void shouldBeSuccess6() {
        var cardInfo = new DataHelper.CardInfo(DataHelper.generateValidCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), "");
        paymentPage.proceedTheCard(cardInfo);
        paymentPage.shouldBeError("Ошибка");
    }
}
