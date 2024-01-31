package ru.netology.aqa.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.netology.aqa.data.DataHelper;
import ru.netology.aqa.data.SQLHelper;
import ru.netology.aqa.pages.DashboardPage;
import ru.netology.aqa.pages.PaymentPage;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentProcessTest {
    PaymentPage paymentPage;
    static List<DataHelper.CardItem> cardItems;
    static int testsToRepeat;
    public static final String DataJSONLocation = System.getProperty("aqa-diploma.datajsonLocation");

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
        try {
            cardItems = DataHelper.getCardItemsFromFile(DataJSONLocation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        testsToRepeat = cardItems.size();
    }

    @BeforeEach
    void setUp() {
        SQLHelper.cleanDatabase();
        var dashboardPage = open(System.getProperty("aqa-diploma.localhostURL"), DashboardPage.class);
        paymentPage = dashboardPage.choosePaymentOption();
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
    void card1DisplayedStatusShouldBeEqualToTheCorrect(int repeats) {
        var cardItem = cardItems.get(repeats - 1);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        paymentPage.processTheCardAndWait(cardInfo);
        if (cardItem.getCardStatus().equals(DataHelper.APPROVED_STATUS))
            paymentPage.shouldBeApprovedMessage();
        else
            paymentPage.shouldBeDeclinedMessage();
    }

    @ParameterizedTest(name = "Card №{0}")
    @MethodSource("repeatTest")
    @DisplayName("Cards from the emulator's base: does its status saved in the database equal the correct one received from the emulator")
    void card1SavedStatus_ShouldBeEqualToTheCorrect(int repeats) {
        var cardItem = cardItems.get(repeats - 1);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        paymentPage.processTheCardAndWait(cardInfo);
        var actualPaymentStatus = SQLHelper.getPaymentEntity().getStatus();
        var expectedPaymentStatus = cardItem.getCardStatus();
        assertEquals(expectedPaymentStatus, actualPaymentStatus);
    }

    @Test
    @DisplayName("The card from the emulator's base: Have the payment amount been saved")
    void knownCard_PaymentAmountShouldBeSaved() {
        var cardItem = cardItems.get(0);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        paymentPage.processTheCardAndWait(cardInfo);
        var actualPaymentAmount = SQLHelper.getPaymentEntity().getAmount();
        assertFalse(StringUtils.isEmpty(actualPaymentAmount));
    }

    @Test
    @DisplayName("The card from the emulator's base: Have the payment amount been saved correctly")
    void knownCard_SavedPaymentAmountShouldBeCorrect() {
        var cardItem = cardItems.get(0);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        paymentPage.processTheCardAndWait(cardInfo);
        var actualPaymentAmount = SQLHelper.getPaymentEntity().getAmount();
        var expectedPaymentAmount = DataHelper.getPaymentAmount();
        assertEquals(expectedPaymentAmount, actualPaymentAmount);
    }

    @Test
    @DisplayName("The card from the emulator's base: Have the transaction_id's been saved to both tables")
    void transaction_id_ShouldBeSaved() {
        var cardItem = cardItems.get(0);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        paymentPage.processTheCardAndWait(cardInfo);
        var transaction_idFromPaymentEntity = SQLHelper.getPaymentEntity().getTransaction_id();
        var transaction_idFromOrderEntity = SQLHelper.getOrderEntity().getPayment_id();
        assertAll(
                () -> assertFalse(StringUtils.isEmpty(transaction_idFromPaymentEntity)),
                () -> assertFalse(StringUtils.isEmpty(transaction_idFromOrderEntity))
        );
    }

    @Test
    @DisplayName("The card from the emulator's base: Are the transaction_id's the same in both tables")
    void transaction_id_TheSameInBothTables() {
        var cardItem = cardItems.get(0);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        paymentPage.processTheCardAndWait(cardInfo);
        var transaction_idFromPaymentEntity = SQLHelper.getPaymentEntity().getTransaction_id();
        var transaction_idFromOrderEntity = SQLHelper.getOrderEntity().getPayment_id();
        assertEquals(transaction_idFromOrderEntity, transaction_idFromPaymentEntity);
    }

    @Test
    @DisplayName("The card from the emulator's base: Is order_entity.credit_id empty")
    void order_entity_credit_id_IsEmpty() {
        var cardItem = cardItems.get(0);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        paymentPage.processTheCardAndWait(cardInfo);
        var credit_idFromOrderEntity = SQLHelper.getOrderEntity().getCredit_id();
        assertTrue(StringUtils.isEmpty(credit_idFromOrderEntity));
    }

    @Test
    @DisplayName("The card from the emulator's base: Is the credit_request_entity table empty")
    void credit_request_entity_Table_IsEmpty() {
        var cardItem = cardItems.get(0);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        paymentPage.processTheCardAndWait(cardInfo);
        assertTrue(SQLHelper.isTheTableEmpty("credit_request_entity"));
    }

    @Test
    @DisplayName("The card not from the emulator's base: is its displayed status DECLINED")
    void unknownCard_DisplayedStatusShouldBeDeclined() {
        paymentPage.processTheCardAndWait(DataHelper.generateValidCardInfo());
        paymentPage.shouldBeDeclinedMessage();
    }

    @Test
    @DisplayName("The card not from the emulator's base: the payment shouldn't be saved in database")
    void unknownCard_PaymentShouldNotBeSavedAnywhere() {
        paymentPage.processTheCardAndWait(DataHelper.generateValidCardInfo());
        assertAll(
                () -> assertTrue(SQLHelper.isTheTableEmpty("credit_request_entity")),
                () -> assertTrue(SQLHelper.isTheTableEmpty("order_entity")),
                () -> assertTrue(SQLHelper.isTheTableEmpty("payment_entity"))
        );
    }
}
