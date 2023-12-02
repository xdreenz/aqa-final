package ru.netology.aqa.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.datafaker.Faker;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

public class DataHelper {
    private static final Faker faker = new Faker();

    private DataHelper() {
    }

    public static List<CardItem> getCardItemsFromFile(String fileName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = new File(fileName);
        return mapper.readValue(jsonFile, new TypeReference<>() {
        });
    }

    public static CardInfo generateValidCardInfo() {
        return new CardInfo(generateValidCardNumber(), generateValidCardExpireMonth(), generateValidCardExpireYear(),
                generateValidCardOwnerName(), generateValidCardCVV());
    }

    public static String generateValidCardNumber() {
        return faker.finance().creditCard();
    }

    public static String generateValidCardExpireMonth() {
        int randomNumber = faker.number().numberBetween(1, 12);
        return String.valueOf(randomNumber);
    }

    public static String generateValidCardExpireYear() {
        int randomYear = faker.number().numberBetween(2023, 2033);
        return String.valueOf(randomYear).substring(2);
    }

    public static String generateValidCardOwnerName() {
        return faker.lorem().characters(20);
    }

    public static String generateValidCardCVV() {
        return faker.numerify("###");
    }

    @Value
    public static class CardInfo {
        String cardNumber;
        String cardExpireMonth;
        String cardExpireYear;
        String cardOwnerName;
        String CVC;
    }

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

    @Data
    @NoArgsConstructor
    public static class CreditRequestEntity {
        private String id;
        private String bank_id;
        private String created;
        private String status;
    }

    @Data
    @NoArgsConstructor
    public static class PaymentEntity {
        private String id;
        private String amount;
        private String created;
        private String status;
        private String transaction_id;
    }

    @Data
    @NoArgsConstructor
    public static class OrderEntity {
        private String id;
        private String created;
        private String credit_id;
        private String payment_id;
    }
}
