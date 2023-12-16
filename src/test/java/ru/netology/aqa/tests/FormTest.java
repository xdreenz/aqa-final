package ru.netology.aqa.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.aqa.data.DataHelper;
import ru.netology.aqa.pages.DashboardPage;
import ru.netology.aqa.pages.PaymentPage;

import static com.codeborne.selenide.Selenide.open;

public class FormTest {
    PaymentPage paymentPage;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    void setUp() {
        var dashboardPage = open(System.getProperty("aqa-diploma.localhostURL"), DashboardPage.class);
        paymentPage = dashboardPage.choosePaymentOption();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Happy path, should be no errors")
    void shouldBeSuccess1() {
        paymentPage.proceedTheCard(DataHelper.generateValidCardInfo(), false);
        paymentPage.shouldBeNoErrors();
    }

    @Test
    @DisplayName("No card number, should be an error")
    void shouldBeSuccess2() {
        var cardInfo = new DataHelper.CardInfo("", DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        paymentPage.proceedTheCard(cardInfo, false);
        paymentPage.shouldBeError("Неверный формат");
    }

    @Test
    @DisplayName("No card expire month, should be an error")
    void shouldBeSuccess3() {
        var cardInfo = new DataHelper.CardInfo(DataHelper.generateValidCardNumber(), "", DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        paymentPage.proceedTheCard(cardInfo, false);
        paymentPage.shouldBeError("Неверный формат");
    }

    @Test
    @DisplayName("No card expire year, should be an error")
    void shouldBeSuccess4() {
        var cardInfo = new DataHelper.CardInfo(DataHelper.generateValidCardNumber(), DataHelper.generateValidCardExpireMonth(), "",
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        paymentPage.proceedTheCard(cardInfo, false);
        paymentPage.shouldBeError("Неверный формат");
    }

    @Test
    @DisplayName("No card owner name, should be an error")
    void shouldBeSuccess5() {
        var cardInfo = new DataHelper.CardInfo(DataHelper.generateValidCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                "", DataHelper.generateValidCardCVV());
        paymentPage.proceedTheCard(cardInfo, false);
        paymentPage.shouldBeError("Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("No card CVV, should be an error")
    void shouldBeSuccess6() {
        var cardInfo = new DataHelper.CardInfo(DataHelper.generateValidCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), "");
        paymentPage.proceedTheCard(cardInfo, false);
        paymentPage.shouldBeError("Неверный формат");
    }

    @Test
    @DisplayName("Invalid card number, should be an error")
    void shouldBeSuccess7() {
        var cardInfo = new DataHelper.CardInfo("0123", DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        paymentPage.proceedTheCard(cardInfo, false);
        paymentPage.shouldBeError("Неверный формат");
    }

    @Test
    @DisplayName("Invalid card expire month, should be an error")
    void shouldBeSuccess8() {
        var cardInfo = new DataHelper.CardInfo(DataHelper.generateValidCardNumber(), "25", DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        paymentPage.proceedTheCard(cardInfo, false);
        paymentPage.shouldBeError("Неверно указан срок действия карты");
    }

    @Test
    @DisplayName("Invalid card expire year, should be an error")
    void shouldBeSuccess9() {
        var cardInfo = new DataHelper.CardInfo(DataHelper.generateValidCardNumber(), DataHelper.generateValidCardExpireMonth(), "50",
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        paymentPage.proceedTheCard(cardInfo, false);
        paymentPage.shouldBeError("Неверно указан срок действия карты");
    }

    @Test
    @DisplayName("Invalid card owner name, should be an error")
    void shouldBeSuccess10() {
        var cardInfo = new DataHelper.CardInfo(DataHelper.generateValidCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                "шцчяфэъ", DataHelper.generateValidCardCVV());
        paymentPage.proceedTheCard(cardInfo, false);
        paymentPage.shouldBeError("Неверный формат");
    }

    @Test
    @DisplayName("Invalid card CVV, should be an error")
    void shouldBeSuccess11() {
        var cardInfo = new DataHelper.CardInfo(DataHelper.generateValidCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), "1");
        paymentPage.proceedTheCard(cardInfo, false);
        paymentPage.shouldBeError("Неверный формат");
    }
}
