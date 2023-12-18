package ru.netology.aqa.data;

import lombok.SneakyThrows;
import net.datafaker.Faker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import net.datafaker.providers.base.CreditCardType;

public class DataHelper {
    private static final Faker faker = new Faker();
    public static final String APPROVED_STATUS = "APPROVED";
    public static final String DECLINED_STATUS = "DECLINED";
    public static final int secondsToWait = 20;

    private DataHelper() {
    }

    public static String generateValidCardNumber() {
        return faker.finance().creditCard(CreditCardType.VISA);
    }

    public static String generateValidCardExpireMonth() {
        int randomNumber = faker.number().numberBetween(1, 12);
        var result = String.valueOf(randomNumber);
        if (randomNumber < 10) result = "0" + result;
        return result;
     }

    public static String generateValidCardExpireYear() {
        int randomYear = faker.number().numberBetween(24, 28);  //Для 23 валиден только один месяц
        return String.valueOf(randomYear);
    }

    public static String generateValidCardOwnerName() {
        return faker.name().firstName() + " " + faker.name().lastName();
    }

    public static String generateValidCardCVV() {
        return faker.numerify("###");
    }

    public static String getPaymentAmount() {
        return "4500000";
    }

    @SneakyThrows
    public static List<CardItem> getCardItemsFromFile(String fileName) {
        var mapper = new ObjectMapper();
        return mapper.readValue(new File(fileName), new TypeReference<>(){});
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
}
