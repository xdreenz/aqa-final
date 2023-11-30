package ru.netology.aqa.data;

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

//    public static CardInfo generateRandomCard() {
//        return new CardInfo(generateRandomCardNumber(), generateRandomCardExpireMonth(), generateRandomCardExpireYear(),
//          generateRandomCardOwnerName(), generateRandomCardCVV());
//    }

    public static List<CardItem> getCardItemsFromFile(String fileName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = new File(fileName);
        return mapper.readValue(jsonFile, new TypeReference<>(){});
    }
    public static String generateRandomCardNumber() {
        return faker.finance().creditCard();
    }

    @Value
    public static class CardInfo {
        String cardNumber;
        String cardExpireMonth;
        String cardExpireYear;
        String cardOwnerName;
        String CVC;
    }

    @Value
    public static class CardItem {
        String cardNumber;
        String cardStatus;
        public CardItem(
                @JsonProperty("number") String cardNumber,
                @JsonProperty("status") String cardStatus)
        {
            this.cardNumber = cardNumber;
            this.cardStatus = cardStatus;
        }

    }
}
