package org.example.aqa.tests;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.apache.commons.lang3.StringUtils;
import org.example.aqa.data.Config;
import org.example.aqa.data.DataHelper;
import org.example.aqa.data.SQLHelper;
import org.example.aqa.pages.CreditRequestPage;
import org.example.aqa.pages.DashboardPage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class CreditRequestProcessTest {
    CreditRequestPage creditPage;
    static List<DataHelper.DataJsonItem> dataJsonItems;
    static int testsToRepeat;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
        try {
            dataJsonItems = DataHelper.getDataJsonItems(Config.datajsonLocation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        testsToRepeat = dataJsonItems.size();
    }

    @BeforeEach
    void setUp() {
        SQLHelper.cleanDatabase();
        var dashboardPage = Selenide.open(Config.localhostURL, DashboardPage.class);
        creditPage = dashboardPage.chooseCreditRequestOption();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    private static IntStream repeatTest() {
        return IntStream.range(1, testsToRepeat + 1);
    }   //+1 ко всему - костыль, чтобы номера карт выводились с 1

    @ParameterizedTest(name = "Card №{0}")
    @MethodSource("repeatTest")
    @DisplayName("Cards from the emulator's base: does its displayed status equal the correct one received from the emulator")
    void cardDisplayedStatus_ShouldBeEqualToTheCorrect(int repeats) {
        var cardItem = dataJsonItems.get(repeats - 1);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.processTheCardAndWait(cardInfo);
        if (cardItem.getCardStatus().equals(DataHelper.APPROVED_STATUS))
            creditPage.shouldBeApprovedMessage();
        else
            creditPage.shouldBeDeclinedMessage();
    }

    @ParameterizedTest(name = "Card №{0}")
    @MethodSource("repeatTest")
    @DisplayName("Cards from the emulator's base: does its status saved in the database equal the correct one received from the emulator")
    void cardSavedStatus_ShouldBeEqualToTheCorrect(int repeats) {
        var cardItem = dataJsonItems.get(repeats - 1);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.processTheCardAndWait(cardInfo);
        var actualCreditRequestStatus = SQLHelper.getCreditRequestEntity().getStatus();
        var expectedCreditRequestStatus = cardItem.getCardStatus();
        assertEquals(expectedCreditRequestStatus, actualCreditRequestStatus);
    }

    @Test
    @DisplayName("The card from the emulator's base: Have the transaction_id's been saved to both tables")
    void transaction_id_ShouldBeSaved() {
        var cardItem = dataJsonItems.get(0);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.processTheCardAndWait(cardInfo);
        var bank_idFromCreditRequestEntity = SQLHelper.getCreditRequestEntity().getBank_id();
        var transaction_idFromOrderEntity = SQLHelper.getOrderEntity().getPayment_id();
        assertAll(
                () -> assertFalse(StringUtils.isEmpty(bank_idFromCreditRequestEntity)),
                () -> assertFalse(StringUtils.isEmpty(transaction_idFromOrderEntity))
        );
    }

    @Test
    @DisplayName("The card from the emulator's base: Are the transaction_id's the same in both tables")
    void transaction_id_TheSameInBothTables() {
        var cardItem = dataJsonItems.get(0);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.processTheCardAndWait(cardInfo);
        var bank_idFromCreditEntity = SQLHelper.getCreditRequestEntity().getBank_id();
        var transaction_idFromOrderEntity = SQLHelper.getOrderEntity().getCredit_id();
        assertEquals(transaction_idFromOrderEntity, bank_idFromCreditEntity);
    }

    @Test
    @DisplayName("The card from the emulator's base: Is order_entity.payment_id empty")
    void order_entity_payment_id_IsEmpty() {
        var cardItem = dataJsonItems.get(0);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.processTheCardAndWait(cardInfo);
        var credit_idFromOrderEntity = SQLHelper.getOrderEntity().getPayment_id();
        assertTrue(StringUtils.isEmpty(credit_idFromOrderEntity));
    }

    @Test
    @DisplayName("Is the payment_entity table empty")
    void payment_entity_Table_IsEmpty() {
        var cardItem = dataJsonItems.get(0);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.processTheCardAndWait(cardInfo);
        assertTrue(SQLHelper.isTheTableEmpty("payment_entity"));
    }

    @Test
    @DisplayName("The card not from the emulator's base: does its displayed status is DECLINED")
    void unknownCard_DisplayedStatusShouldBeDeclined() {
        creditPage.processTheCardAndWait(DataHelper.generateValidCardInfo());
        creditPage.shouldBeDeclinedMessage();
    }

    @Test
    @DisplayName("The card not from the emulator's base: the credit request shouldn't be saved in database")
    void unknownCard_RequestShouldNotBeSavedAnywhere() {
        creditPage.processTheCardAndWait(DataHelper.generateValidCardInfo());
        assertAll(
                () -> assertTrue(SQLHelper.isTheTableEmpty("credit_request_entity")),
                () -> assertTrue(SQLHelper.isTheTableEmpty("order_entity")),
                () -> assertTrue(SQLHelper.isTheTableEmpty("payment_entity"))
        );
    }
}
