package data;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataGenerator {

    private Faker faker = new Faker(new Locale("en"));

    public static String getCorrectYear() {
        LocalDate date = LocalDate.now().plusYears(2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy");
        return date.format(formatter);
    }

    public static String getWrongYear() {
        LocalDate date = LocalDate.now().minusYears(2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy");
        return date.format(formatter);
    }

    public static String getRandomCvc() {
        String[] cvcOptions = {"555", "999", "758", "004", "777", "255", "023", "457", "601", "111"};
        int chooseCvc =(int) (Math.random()*cvcOptions.length);
        return cvcOptions[chooseCvc];
    }

    public static String setFakeOwner() {
        Faker faker = new Faker(new Locale("en"));
        return faker.name().fullName();
    }
}