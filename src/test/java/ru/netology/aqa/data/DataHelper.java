package ru.netology.aqa.data;

import net.datafaker.Faker;
import lombok.Value;

public class DataHelper {
    private static final Faker faker = new Faker();

    private DataHelper() {
    }

//    public static CardInfo generateRandomCard() {
//        return new CardInfo(generateRandomCardNumber(), generateRandomCardExpireMonth(), generateRandomCardExpireYear(),
//          generateRandomCardOwnerName(), generateRandomCardCVV());
//    }


    public static String getCard1Number() {
        return "4444 4444 4444 4441";
    }

    public static String getCard2Number() {
        return "4444 4444 4444 4442";
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
}
