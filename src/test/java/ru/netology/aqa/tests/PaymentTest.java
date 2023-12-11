package ru.netology.aqa.tests;

import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import ru.netology.aqa.data.DataHelper;
import ru.netology.aqa.data.SQLHelper;
import ru.netology.aqa.pages.DashboardPage;
import ru.netology.aqa.pages.PaymentPage;

import java.util.List;

import static com.codeborne.selenide.Selenide.open;

public class PaymentTest {
    PaymentPage paymentPage;
    static List<DataHelper.CardItem> cardItems;

    @BeforeAll
    @SneakyThrows
    static void setUpAll() {
        cardItems = DataHelper.getCardItemsFromFile(DataHelper.DataJSONLocation);
    }

    @BeforeEach
    void setUp() {
        SQLHelper.cleanDatabase();
        var dashboardPage = open("http://localhost:8080", DashboardPage.class);
        paymentPage = dashboardPage.choosePayment();
    }

    @Test
    @DisplayName("Card №1 from the emulator's base: does its displayed status equal the correct one received from the emulator")
    void shouldBeSuccess11() {
        var cardItem = cardItems.get(0);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());    //Дополняю номер карты остальными валидными данными
        paymentPage.proceedTheCard(cardInfo);
        if (cardItem.getCardStatus().equals(DataHelper.APPROVED_STATUS))
            paymentPage.shouldBeApprovedMessage();
        else
            paymentPage.shouldBeDeclinedMessage();
    }

    @Test
    @DisplayName("Card №1 from the emulator's base: does its status saved in the database equal the correct one received from the emulator")
    void shouldBeSuccess12() {
        var cardItem = cardItems.get(0);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        paymentPage.proceedTheCard(cardInfo);
        var actualPaymentStatus = SQLHelper.getPaymentEntity().getStatus();
        var expectedPaymentStatus = cardItem.getCardStatus();
        Assertions.assertEquals(expectedPaymentStatus, actualPaymentStatus);
    }

    @Test
    @DisplayName("Card №2 from the emulator's base: does its displayed status equal the correct one received from the emulator")
    void shouldBeSuccess21() {
        var cardItem = cardItems.get(1);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        paymentPage.proceedTheCard(cardInfo);
        if (cardItem.getCardStatus().equals(DataHelper.APPROVED_STATUS))
            paymentPage.shouldBeApprovedMessage();
        else
            paymentPage.shouldBeDeclinedMessage();
    }

    @Test
    @DisplayName("Card №2 from the emulator's base: does its status saved in the database equal the correct one received from the emulator")
    void shouldBeSuccess22() {
        var cardItem = cardItems.get(1);
        var cardInfo = new DataHelper.CardInfo(cardItem.getCardNumber(), DataHelper.generateValidCardExpireMonth(), DataHelper.generateValidCardExpireYear(),
                DataHelper.generateValidCardOwnerName(), DataHelper.generateValidCardCVV());
        paymentPage.proceedTheCard(cardInfo);
        var actualPaymentStatus = SQLHelper.getPaymentEntity().getStatus();
        var expectedPaymentStatus = cardItem.getCardStatus();
        Assertions.assertEquals(expectedPaymentStatus, actualPaymentStatus);
    }

    @Test
    @DisplayName("Have the payment amount been saved")
    void shouldBeSuccess3() {
        paymentPage.proceedTheCard(DataHelper.generateApprovedCardInfo());
        var actualPaymentAmount = SQLHelper.getPaymentEntity().getAmount();
        Assertions.assertFalse(actualPaymentAmount.isEmpty());
    }

    @Test
    @DisplayName("Have the payment amount been saved correctly")
    void shouldBeSuccess4() {
        paymentPage.proceedTheCard(DataHelper.generateApprovedCardInfo());
        var actualPaymentAmount = SQLHelper.getPaymentEntity().getAmount();
        var expectedPaymentAmount = DataHelper.getPaymentAmount();
        Assertions.assertEquals(expectedPaymentAmount, actualPaymentAmount);
    }

    @Test
    @DisplayName("Have the transaction_id's been saved to both tables")
    void shouldBeSuccess5() {
        paymentPage.proceedTheCard(DataHelper.generateApprovedCardInfo());
        var transaction_idFromPaymentEntity = SQLHelper.getPaymentEntity().getTransaction_id();
        var transaction_idFromOrderEntity = SQLHelper.getOrderEntity().getPayment_id();
        Assertions.assertFalse(transaction_idFromPaymentEntity.isEmpty());
        Assertions.assertFalse(transaction_idFromOrderEntity.isEmpty());
    }

    @Test
    @DisplayName("Are the transaction_id's the same in both tables")
    void shouldBeSuccess6() {
        paymentPage.proceedTheCard(DataHelper.generateApprovedCardInfo());
        var transaction_idFromPaymentEntity = SQLHelper.getPaymentEntity().getTransaction_id();
        var transaction_idFromOrderEntity = SQLHelper.getOrderEntity().getPayment_id();
        Assertions.assertEquals(transaction_idFromOrderEntity, transaction_idFromPaymentEntity);
    }

    @Test
    @DisplayName("Is order_entity.credit_id empty")
    void shouldBeSuccess7() {
        paymentPage.proceedTheCard(DataHelper.generateApprovedCardInfo());
        var credit_idFromOrderEntity = SQLHelper.getOrderEntity().getCredit_id();
        Assertions.assertTrue(credit_idFromOrderEntity.isEmpty());
    }

    @Test
    @DisplayName("Is the credit_request_entity table empty")
    void shouldBeSuccess8() {
        paymentPage.proceedTheCard(DataHelper.generateApprovedCardInfo());
        var idFromCreditRequestEntity = SQLHelper.getCreditRequestEntity().getId();
        Assertions.assertTrue(idFromCreditRequestEntity.isEmpty());
    }
}
