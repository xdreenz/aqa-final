package org.example.aqa.data;

import net.datafaker.Faker;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.datafaker.providers.base.Finance;
import org.apache.commons.lang3.StringUtils;

public class DataHelper {
    private static final Faker faker = new Faker();
    public static final String APPROVED_STATUS = "APPROVED";

    private DataHelper() {
    }

    public static String generateValidCardNumber() {
        return faker.finance().creditCard(Finance.CreditCardType.VISA);
    }

    public static String generateValidCardExpirationMonth() {
        int randomNumber = faker.number().numberBetween(1, 12);
        var result = String.valueOf(randomNumber);
        return StringUtils.leftPad(result, 2, "0");
    }

    public static String generateValidCardExpirationYear() {
        int randomYear = faker.number().numberBetween(25, 29);
        return String.valueOf(randomYear);
    }

    public static String generateValidCardOwnerName() {
        return faker.name().firstName() + StringUtils.SPACE + faker.name().lastName();
    }

    public static String generateValidCardCVV() {
        return faker.numerify("###");
    }

    public static String getPaymentAmount() {
        return "4500000";
    }

    public static List<DataJsonItem> getDataJsonItems(String fileName) throws IOException {
        var mapper = new ObjectMapper();
        return mapper.readValue(new File(fileName), new TypeReference<>(){});
    }

    public static CardInfo generateValidCardInfo() {
        return new CardInfo(generateValidCardNumber(), generateValidCardExpirationMonth(), generateValidCardExpirationYear(),
                generateValidCardOwnerName(), generateValidCardCVV());
    }

    public record CardInfo (
        String cardNumber,
        String cardExpireMonth,
        String cardExpireYear,
        String cardOwnerName,
        String cardCVC
    ) {}

    public record DataJsonItem(
            @JsonProperty("number") String cardNumber,
            @JsonProperty("status") String cardStatus
    ) {}
}