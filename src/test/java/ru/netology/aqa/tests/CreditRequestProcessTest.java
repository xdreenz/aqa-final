package ru.netology.aqa.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.aqa.data.DataHelper;
import ru.netology.aqa.data.SQLHelper;
import ru.netology.aqa.pages.DashboardPage;
import ru.netology.aqa.pages.CreditRequestPage;

import java.util.List;

import static com.codeborne.selenide.Selenide.open;

public class CreditRequestProcessTest {
    CreditRequestPage creditPage;
    static List<DataHelper.CardItem> cardItems;
    public static final String datajsonLocation = System.getProperty("aqa-diploma.datajsonLocation");
    public static final String localhostURL = System.getProperty("aqa-diploma.localhostURL");

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
        cardItems = DataHelper.getCardItemsFromFile(datajsonLocation);
    }

    @BeforeEach
    void setUp() {
        SQLHelper.cleanDatabase();
        var dashboardPage = open(localhostURL, DashboardPage.class);
        creditPage = dashboardPage.chooseCreditRequestOption();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Card №1 from the emulator's base: does its displayed status equal the correct one received from the emulator")
    void card1DisplayedStatus_ShouldBeEqualToTheCorrect() {
        var cardItem = cardItems.get(0);    //Номер и статус 1-й карты из БД эмулятора
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());    //Дополняю номер карты остальными валидными данными
        creditPage.processTheCardAndWait(cardInfo);
        if (cardItem.getCardStatus().equals(DataHelper.APPROVED_STATUS))
            creditPage.shouldBeApprovedMessage();
        else
            creditPage.shouldBeDeclinedMessage();
    }

    @Test
    @DisplayName("Card №1 from the emulator's base: does its status saved in the database equal the correct one received from the emulator")
    void card1SavedStatus_ShouldBeEqualToTheCorrect() {
        var cardItem = cardItems.get(0);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.processTheCardAndWait(cardInfo);
        var actualCreditRequestStatus = SQLHelper.getCreditRequestEntity().getStatus();
        var expectedCreditRequestStatus = cardItem.getCardStatus();
        Assertions.assertEquals(expectedCreditRequestStatus, actualCreditRequestStatus);
    }

    @Test
    @DisplayName("Card №2 from the emulator's base: does its displayed status equal the correct one received from the emulator")
    void card2DisplayedStatus_ShouldBeEqualToTheCorrect() {
        var cardItem = cardItems.get(1);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.processTheCardAndWait(cardInfo);
        if (cardItem.getCardStatus().equals(DataHelper.APPROVED_STATUS))
            creditPage.shouldBeApprovedMessage();
        else
            creditPage.shouldBeDeclinedMessage();
    }

    @Test
    @DisplayName("Card №2 from the emulator's base: does its status saved in the database equal the correct one received from the emulator")
    void card2SavedStatus_ShouldBeEqualToTheCorrect() {
        var cardItem = cardItems.get(1);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.processTheCardAndWait(cardInfo);
        var actualCreditRequestStatus = SQLHelper.getCreditRequestEntity().getStatus();
        var expectedCreditRequestStatus = cardItem.getCardStatus();
        Assertions.assertEquals(expectedCreditRequestStatus, actualCreditRequestStatus);
    }

    @Test
    @DisplayName("The card from the emulator's base: Have the transaction_id's been saved to both tables")
    void transaction_id_ShouldBeSaved() {
        var cardItem = cardItems.get(0);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.processTheCardAndWait(cardInfo);
        var bank_idFromCreditRequestEntity = SQLHelper.getCreditRequestEntity().getBank_id();
        var transaction_idFromOrderEntity = SQLHelper.getOrderEntity().getPayment_id();
        Assertions.assertFalse(bank_idFromCreditRequestEntity.isEmpty());
        Assertions.assertFalse(transaction_idFromOrderEntity.isEmpty());
    }

    @Test
    @DisplayName("The card from the emulator's base: Are the transaction_id's the same in both tables")
    void transaction_id_TheSameInBothTables() {
        var cardItem = cardItems.get(0);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.processTheCardAndWait(cardInfo);
        var bank_idFromCreditEntity = SQLHelper.getCreditRequestEntity().getBank_id();
        var transaction_idFromOrderEntity = SQLHelper.getOrderEntity().getCredit_id();
        Assertions.assertEquals(transaction_idFromOrderEntity, bank_idFromCreditEntity);
    }

    @Test
    @DisplayName("The card from the emulator's base: Is order_entity.payment_id empty")
    void order_entity_payment_id_IsEmpty() {
        var cardItem = cardItems.get(0);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.processTheCardAndWait(cardInfo);
        var credit_idFromOrderEntity = SQLHelper.getOrderEntity().getPayment_id();
        Assertions.assertTrue(credit_idFromOrderEntity.isEmpty());
    }

    @Test
    @DisplayName("Is the payment_entity table empty")
    void payment_entity_Table_IsEmpty() {
        var cardItem = cardItems.get(0);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        creditPage.processTheCardAndWait(cardInfo);
        Assertions.assertTrue(SQLHelper.isTheTableEmpty("payment_entity"));
    }

    @Test
    @DisplayName("The card not from the emulator's base: does its displayed status is DECLINED")
    void unknownCard_DisplayedStatusIsDeclined() {
        creditPage.processTheCardAndWait(DataHelper.generateValidCardInfo());
        creditPage.shouldBeDeclinedMessage();
    }

    @Test
    @DisplayName("The card not from the emulator's base: the credit request shouldn't be saved in database")
    void unknownCard_RequestShouldNotBeSavedAnywhere() {
        creditPage.processTheCardAndWait(DataHelper.generateValidCardInfo());
        Assertions.assertTrue(SQLHelper.isTheTableEmpty("credit_request_entity"));
        Assertions.assertTrue(SQLHelper.isTheTableEmpty("order_entity"));
        Assertions.assertTrue(SQLHelper.isTheTableEmpty("payment_entity"));
    }
}
