package tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import com.github.javafaker.Faker;
import data.Card;
import data.SQLHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.sql.SQLException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static data.DataGenerator.*;
import static page.PaymentPage.getFilledPaymentPage;
import static page.CreditPage.getFilledCreditPage;

public class PurchaseTests {

    private Card cardOne = new Card();
    private Card cardTwo = new Card();
    private Card invalidNumberCard = new Card();
    private Faker faker = new Faker(new Locale("en"));

    @BeforeEach
    void setUp() {
        setCards();
    }

    @AfterEach
    void cleanTables() throws SQLException {
        SQLHelper.cleanTables();
    }

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Must confirm the purchase with valid data and a card with the status APPROVED")
    void shouldConfirmPaymentWithValidDataCardOne() throws SQLException {
        getFilledPaymentPage(cardOne).assertNotificationOkIsVisible();
        assertEquals(SQLHelper.findPaymentStatus(), "APPROVED");
        assertNotNull(SQLHelper.findPaymentId());
    }

    @Test
    @DisplayName("Must confirm a loan with valid data and a card with the status APPROVED")
    void shouldConfirmCreditWithValidDataCardOne() throws SQLException {
        getFilledCreditPage(cardOne).assertNotificationOkIsVisible();
        assertEquals(SQLHelper.findCreditStatus(), "APPROVED");
        assertNotNull(SQLHelper.findCreditId());
    }

    @Test
    @DisplayName("Should not confirm the purchase when using a card with the DECLINED status")
    void shouldNotConfirmPaymentWithInvalidCardTwo() throws SQLException{
        getFilledPaymentPage(cardTwo).assertNotificationErrorIsVisible();
        assertEquals(SQLHelper.findPaymentStatus(), "DECLINED");
        assertNull(SQLHelper.findPaymentId());
    }

    @Test
    @DisplayName("Must not confirm credit when using a card with the DECLINED status")
    void shouldNotConfirmCreditWithInvalidCardTwo() throws SQLException {
        getFilledCreditPage(cardTwo).notificationErrorIsVisible();
        assertEquals(SQLHelper.findCreditStatus(), "DECLINED");
        assertNull(SQLHelper.findCreditId());
    }


    @Test
    @DisplayName("Should not confirm the purchase with an invalid card number")
    void shouldNotSubmitPaymentWithIllegalCard() throws SQLException {
        cardOne.setNumber("4444 4444 4444 4444");
        getFilledPaymentPage(cardOne).assertNotificationErrorIsVisible();
        assertFalse(SQLHelper.isNotEmpty());
    }


    @Test
    @DisplayName("Should not confirm a loan with an invalid card number")
    void shouldNotSubmitCreditWithIllegalCard() throws SQLException{
        cardOne.setNumber("4444 4444 4444 4444");
        getFilledCreditPage(cardOne).notificationErrorIsVisible();
        assertFalse(SQLHelper.isNotEmpty());
    }


    @ParameterizedTest
    @CsvFileSource(resources = "/wrongMonth.cvs", numLinesToSkip = 1)
    void shouldNotSubmitPaymentWithWrongMonth(String month, String message) throws SQLException {
        cardOne.setMonth(month);
        getFilledPaymentPage(cardOne).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Should not confirm the purchase if a non-existent month is entered")
    void shouldNotConfirmPaymentWithInvalidMonth() throws SQLException {
        cardOne.setMonth("22");
        getFilledPaymentPage(cardOne).assertInputInvalidMonth();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Must not confirm purchase without specifying year")
    void shouldNotConfirmPaymentIfEmptyYear() throws SQLException {
        cardOne.setYear("");
        getFilledPaymentPage(cardOne).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Should not confirm the purchase if the year precedes the current")
    void shouldNotConfirmPaymentWithOldYear() throws SQLException {
        cardOne.setYear(getWrongYear());
        getFilledPaymentPage(cardOne).assertInputInvalidExpireDate();
        assertFalse(SQLHelper.isNotEmpty());
    }


    @ParameterizedTest
    @CsvFileSource(resources = "/wrongMonth.cvs", numLinesToSkip = 1)
    void shouldNotSubmitCreditWithWrongMonth(String month, String message) throws SQLException{
        cardOne.setMonth(month);
        getFilledCreditPage(cardOne).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Should not confirm a loan if a non-existent month is entered")
    void shouldNotConfirmCreditWithInvalidMonth() throws SQLException{
        cardOne.setMonth("22");
        getFilledCreditPage(cardOne).assertInputInvalidMonth();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Should not confirm the loan without indicating the year")
    void shouldNotConfirmCreditIfEmptyYear() throws SQLException{
        cardOne.setYear("");
        getFilledCreditPage(cardOne).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Should not confirm a loan if the year precedes the current")
    void shouldNotConfirmCreditWithOldYear() throws SQLException{
        cardOne.setYear(getWrongYear());
        getFilledCreditPage(cardOne).assertInputInvalidExpireDate();
        assertFalse(SQLHelper.isNotEmpty());
    }


    @Test
    @DisplayName("Must not confirm purchase without owner name")
    void shouldNotConfirmPaymentWithoutOwner() throws SQLException{
        cardOne.setOwner("");
        getFilledPaymentPage(cardOne).assertInputInvalidFillData();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongOwner.cvs", numLinesToSkip = 1)
    void shouldNotConfirmPaymentWithInvalidOwner(String owner, String message) throws SQLException {
        cardOne.setOwner(owner);
        getFilledPaymentPage(cardOne).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }


    @Test
    @DisplayName("Should not confirm a loan without the name of the owner")
    void shouldNotConfirmCreditWithoutOwner() throws SQLException{
        cardOne.setOwner("");
        getFilledCreditPage(cardOne).assertInputInvalidFillData();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongOwner.cvs", numLinesToSkip = 1)
    void shouldNotConfirmCreditWithInvalidOwner(String owner, String message) throws SQLException{
        cardOne.setOwner(owner);
        getFilledCreditPage(cardOne).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }


    @ParameterizedTest
    @CsvFileSource(resources = "/wrongCvc.cvs", numLinesToSkip = 1)
    void shouldNotConfirmPaymentWithInvalidCvc(String cvc, String message) throws SQLException{
        cardOne.setCvc(cvc);
        getFilledPaymentPage(cardOne).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }


    @ParameterizedTest
    @CsvFileSource(resources = "/wrongCvc.cvs", numLinesToSkip = 1)
    void shouldNotConfirmCreditWithInvalidCvc(String cvc, String message) throws SQLException {
        cardOne.setCvc(cvc);
        getFilledCreditPage(cardOne).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }


    private void setCards() {
        cardOne.setNumber("4444 4444 4444 4441");
        cardTwo.setNumber("4444 4444 4444 4442");
        invalidNumberCard.setNumber("4444 4444 4444 4444");
        cardOne.setMonth("01");
        cardTwo.setMonth("01");
        cardOne.setYear(getCorrectYear());
        cardTwo.setYear(getCorrectYear());
        cardOne.setOwner(setFakeOwner());
        cardTwo.setOwner(setFakeOwner());
        cardOne.setCvc(getRandomCvc());
        cardTwo.setCvc(getRandomCvc());
    }

    private String setFakeOwner() {
        String owner = faker.name().fullName();
        return owner;
    }
}