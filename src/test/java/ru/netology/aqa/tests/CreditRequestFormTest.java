package ru.netology.aqa.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.aqa.data.DataHelper;
import ru.netology.aqa.pages.CreditRequestPage;
import ru.netology.aqa.pages.DashboardPage;

import static com.codeborne.selenide.Selenide.open;

public class CreditRequestFormTest {
    CreditRequestPage creditPage;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    void setUp() {
        var dashboardPage = open(System.getProperty("aqa-diploma.localhostURL"), DashboardPage.class);
        creditPage = dashboardPage.chooseCreditRequestOption();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Happy path, should be no errors")
    void happyPath_ShouldBeNoErrors() {
        creditPage.processTheCard(DataHelper.generateValidCardInfo());
        creditPage.shouldBeNoErrors();
    }

    @Test
    @DisplayName("No card number, should be an error")
    void noCardNumber_ShouldBeError() {
        var cardInfo = new DataHelper.CardInfo("", DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.processTheCard(cardInfo);
        creditPage.shouldBeError("Неверный формат");
    }

    @Test
    @DisplayName("No card expire month, should be an error")
    void noCardExpireMonth_ShouldBeError() {
        var cardInfo = new DataHelper.CardInfo(DataHelper.generateValidCardNumber(), "", DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.processTheCard(cardInfo);
        creditPage.shouldBeError("Неверный формат");
    }

    @Test
    @DisplayName("No card expire year, should be an error")
    void noCardExpireYear_ShouldBeError() {
        var cardInfo = new DataHelper.CardInfo(DataHelper.generateValidCardNumber(), DataHelper.generateValidCardExpireMonth(), "",
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.processTheCard(cardInfo);
        creditPage.shouldBeError("Неверный формат");
    }

    @Test
    @DisplayName("No card owner name, should be an error")
    void noCardOwnerName_ShouldBeError() {
        var cardInfo = new DataHelper.CardInfo(DataHelper.generateValidCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                "", DataHelper.generateValidCardCVV());
        creditPage.processTheCard(cardInfo);
        creditPage.shouldBeError("Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("No card CVV, should be an error")
    void noCardCVV_ShouldBeError() {
        var cardInfo = new DataHelper.CardInfo(DataHelper.generateValidCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), "");
        creditPage.processTheCard(cardInfo);
        creditPage.shouldBeError("Неверный формат");
    }

    @Test
    @DisplayName("Invalid card number, should be an error")
    void invalidCardNumber_ShouldBeError() {
        var cardInfo = new DataHelper.CardInfo("0123", DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.processTheCard(cardInfo);
        creditPage.shouldBeError("Неверный формат");
    }

    @Test
    @DisplayName("Invalid card expire month, should be an error")
    void invalidCardExpireMonth_ShouldBeError() {
        var cardInfo = new DataHelper.CardInfo(DataHelper.generateValidCardNumber(), "25", DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.processTheCard(cardInfo);
        creditPage.shouldBeError("Неверно указан срок действия карты");
    }

    @Test
    @DisplayName("Invalid card expire year, should be an error")
    void invalidCardExpireYear_ShouldBeError() {
        var cardInfo = new DataHelper.CardInfo(DataHelper.generateValidCardNumber(), DataHelper.generateValidCardExpireMonth(), "50",
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.processTheCard(cardInfo);
        creditPage.shouldBeError("Неверно указан срок действия карты");
    }

    @Test
    @DisplayName("Invalid card owner name, should be an error")
    void invalidCardOwnerName_ShouldBeError() {
        var cardInfo = new DataHelper.CardInfo(DataHelper.generateValidCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                "шцчяфэъ", DataHelper.generateValidCardCVV());
        creditPage.processTheCard(cardInfo);
        creditPage.shouldBeError("Неверный формат");
    }

    @Test
    @DisplayName("Invalid card CVV, should be an error")
    void invalidCardCVV_ShouldBeError() {
        var cardInfo = new DataHelper.CardInfo(DataHelper.generateValidCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), "1");
        creditPage.processTheCard(cardInfo);
        creditPage.shouldBeError("Неверный формат");
    }
}
