package tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.Card;
import data.SQLHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static data.DataGenerator.*;
import static page.PaymentPage.getFilledPaymentPage;
import static page.CreditPage.getFilledCreditPage;

public class PurchaseTests {
    private Card approvedCard;
    private Card declinedCard;
    private Card invalidNumberCard;

    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_DECLINED = "DECLINED";

    @BeforeEach
    void setUp() {
        setDeclinedCard();
        setApprovedCard();
    }

    @BeforeEach
    public void newCard() {
        approvedCard = new Card();
        declinedCard = new Card();
        invalidNumberCard = new Card();
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
    void shouldConfirmPaymentWithValidDataapprovedCard() throws SQLException {
        getFilledPaymentPage(approvedCard).creditCardForm.assertNotificationOkIsVisible();
        assertEquals(SQLHelper.findPaymentStatus(), STATUS_APPROVED);
        assertNotNull(SQLHelper.findPaymentId());
    }

    @Test
    @DisplayName("Must confirm a loan with valid data and a card with the status APPROVED")
    void shouldConfirmCreditWithValidDataapprovedCard() throws SQLException {
        getFilledCreditPage(approvedCard).creditCardForm.assertNotificationOkIsVisible();
        assertEquals(SQLHelper.findCreditStatus(), STATUS_DECLINED);
        assertNotNull(SQLHelper.findCreditId());
    }

    @Test
    @DisplayName("Should not confirm the purchase when using a card with the DECLINED status")
    void shouldNotConfirmPaymentWithInvaliddeclinedCard() throws SQLException {
        getFilledPaymentPage(declinedCard).creditCardForm.assertNotificationErrorIsVisible();
        assertEquals(SQLHelper.findPaymentStatus(), STATUS_DECLINED);
        assertNull(SQLHelper.findPaymentId());
    }

    @Test
    @DisplayName("Must not confirm credit when using a card with the DECLINED status")
    void shouldNotConfirmCreditWithInvaliddeclinedCard() throws SQLException {
        getFilledCreditPage(declinedCard).creditCardForm.notificationErrorIsVisible();
        assertEquals(SQLHelper.findCreditStatus(), STATUS_DECLINED);
        assertNull(SQLHelper.findCreditId());
    }

    @Test
    @DisplayName("Should not confirm the purchase with an invalid card number")
    void shouldNotSubmitPaymentWithIllegalCard() throws SQLException {
        approvedCard.setNumber("4444 4444 4444 4444");
        getFilledPaymentPage(approvedCard).creditCardForm.assertNotificationErrorIsVisible();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Should not confirm a loan with an invalid card number")
    void shouldNotSubmitCreditWithIllegalCard() throws SQLException {
        approvedCard.setNumber("4444 4444 4444 4444");
        getFilledCreditPage(approvedCard).creditCardForm.notificationErrorIsVisible();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongMonth.cvs", numLinesToSkip = 1)
    void shouldNotSubmitPaymentWithWrongMonth(String month, String message) throws SQLException {
        approvedCard.setMonth(month);
        getFilledPaymentPage(approvedCard).creditCardForm.assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty(), message);
    }

    @Test
    @DisplayName("Should not confirm the purchase if a non-existent month is entered")
    void shouldNotConfirmPaymentWithInvalidMonth() throws SQLException {
        approvedCard.setMonth("22");
        getFilledPaymentPage(approvedCard).creditCardForm.assertInputInvalidMonth();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Must not confirm purchase without specifying year")
    void shouldNotConfirmPaymentIfEmptyYear() throws SQLException {
        approvedCard.setYear("");
        getFilledPaymentPage(approvedCard).creditCardForm.assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Should not confirm the purchase if the year precedes the current")
    void shouldNotConfirmPaymentWithOldYear() throws SQLException {
        approvedCard.setYear(getWrongYear());
        getFilledPaymentPage(approvedCard).creditCardForm.assertInputInvalidExpireDate();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongMonth.cvs", numLinesToSkip = 1)
    void shouldNotSubmitCreditWithWrongMonth(String month, String message) throws SQLException {
        approvedCard.setMonth(month);
        getFilledCreditPage(approvedCard).creditCardForm.assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty(), message);
    }

    @Test
    @DisplayName("Should not confirm a loan if a non-existent month is entered")
    void shouldNotConfirmCreditWithInvalidMonth() throws SQLException {
        approvedCard.setMonth("22");
        getFilledCreditPage(approvedCard).creditCardForm.assertInputInvalidMonth();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Should not confirm the loan without indicating the year")
    void shouldNotConfirmCreditIfEmptyYear() throws SQLException {
        approvedCard.setYear("");
        getFilledCreditPage(approvedCard).creditCardForm.assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Should not confirm a loan if the year precedes the current")
    void shouldNotConfirmCreditWithOldYear() throws SQLException {
        approvedCard.setYear(getWrongYear());
        getFilledCreditPage(approvedCard).creditCardForm.assertInputInvalidExpireDate();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Must not confirm purchase without owner name")
    void shouldNotConfirmPaymentWithoutOwner() throws SQLException {
        approvedCard.setOwner("");
        getFilledPaymentPage(approvedCard).creditCardForm.assertInputInvalidFillData();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongOwner.cvs", numLinesToSkip = 1)
    void shouldNotConfirmPaymentWithInvalidOwner(String owner, String message) throws SQLException {
        approvedCard.setOwner(owner);
        getFilledPaymentPage(approvedCard).creditCardForm.assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty(), message);
    }

    @Test
    @DisplayName("Should not confirm a loan without the name of the owner")
    void shouldNotConfirmCreditWithoutOwner() throws SQLException {
        approvedCard.setOwner("");
        getFilledCreditPage(approvedCard).creditCardForm.assertInputInvalidFillData();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongOwner.cvs", numLinesToSkip = 1)
    void shouldNotConfirmCreditWithInvalidOwner(String owner, String message) throws SQLException {
        approvedCard.setOwner(owner);
        getFilledCreditPage(approvedCard).creditCardForm.assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty(), message);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongCvc.cvs", numLinesToSkip = 1)
    void shouldNotConfirmPaymentWithInvalidCvc(String cvc, String message) throws SQLException {
        approvedCard.setCvc(cvc);
        getFilledPaymentPage(approvedCard).creditCardForm.assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty(), message);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongCvc.cvs", numLinesToSkip = 1)
    void shouldNotConfirmCreditWithInvalidCvc(String cvc, String message) throws SQLException {
        approvedCard.setCvc(cvc);
        getFilledCreditPage(approvedCard).creditCardForm.assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty(), message);
    }

    public void setDeclinedCard() {
        declinedCard.setNumber("4444 4444 4444 4442");
        declinedCard.setMonth("05");
        declinedCard.setYear(getCorrectYear());
        declinedCard.setOwner(setFakeOwner());
        declinedCard.setCvc(getRandomCvc());
    }

    public void setApprovedCard() {
        approvedCard.setNumber("4444 4444 4444 4441");
        invalidNumberCard.setNumber("4444 4444 4444 4444");
        approvedCard.setMonth("05");
        approvedCard.setYear(getCorrectYear());
        approvedCard.setOwner(setFakeOwner());
        approvedCard.setCvc(getRandomCvc());
    }
}