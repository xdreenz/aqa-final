package ru.netology.aqa.data;

import lombok.Getter;
import net.datafaker.Faker;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

public class DataHelper {
    private static final Faker faker = new Faker();
    public static final String APPROVED_STATUS = "APPROVED";
    public static final String DECLINED_STATUS = "DECLINED";
    public static final String DataJSONLocation = "gate-emulator/data.json";
    public static final String localhostURL = "http://localhost:8080";

    private DataHelper() {
    }

    public static String generateValidCardNumber() {
        return faker.finance().creditCard();
    }

    public static String generateValidCardExpireMonth() {
        int randomNumber = faker.number().numberBetween(1, 12);
        return String.valueOf(randomNumber);
    }

    public static String generateValidCardExpireYear() {
        int randomYear = faker.number().numberBetween(23, 28);
        return String.valueOf(randomYear);
    }

    public static String generateValidCardOwnerName() {
        return faker.lorem().characters(20);
    }

    public static String generateValidCardCVV() {
        return faker.numerify("###");
    }

    public static String getPaymentAmount() {
        return "4500000";
    }

    public static List<CardItem> getCardItemsFromFile(String fileName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = new File(fileName);
        return mapper.readValue(jsonFile, new TypeReference<>(){});
    }

    public static CardInfo generateValidCardInfo() {
        return new CardInfo(generateValidCardNumber(), generateValidCardExpireMonth(), generateValidCardExpireYear(),
                generateValidCardOwnerName(), generateValidCardCVV());
    }

    @Value
    public static class CardInfo {
        String cardNumber;
        String cardExpireMonth;
        String cardExpireYear;
        String cardOwnerName;
        String cardCVC;
    }

    @Value
    public static class CardItem {
        String cardNumber;
        String cardStatus;

        public CardItem(
                @JsonProperty("number") String cardNumber,
                @JsonProperty("status") String cardStatus) {
            this.cardNumber = cardNumber;
            this.cardStatus = cardStatus;
        }

    }

    @Getter
    public static class CreditRequestEntity {
        private String id;
        private String bank_id;
        private String created;
        private String status;
    }

    @Getter
    public static class PaymentEntity {
        private String id;
        private String amount;
        private String created;
        private String status;
        private String transaction_id;
    }

    @Getter
    public static class OrderEntity {
        private String id;
        private String created;
        private String credit_id;
        private String payment_id;
    }
}
