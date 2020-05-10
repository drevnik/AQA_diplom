package tests;

import com.codeborne.selenide.logevents.SelenideLogger;
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

    private Card approvedCard = new Card();
    private Card declinedCard = new Card();
    private Card invalidNumberCard = new Card();

    String statusApproved = "APPROVED";
    String statusDeclined = "DECLINED";

    @BeforeEach
    void setUp() {
        setDeclinedCard();
        setApprovedCard();
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
        getFilledPaymentPage(approvedCard).assertNotificationOkIsVisible();
        assertEquals(SQLHelper.findPaymentStatus(), "statusApproved");
        assertNotNull(SQLHelper.findPaymentId());
    }

    @Test
    @DisplayName("Must confirm a loan with valid data and a card with the status APPROVED")
    void shouldConfirmCreditWithValidDataapprovedCard() throws SQLException {
        getFilledCreditPage(approvedCard).assertNotificationOkIsVisible();
        assertEquals(SQLHelper.findCreditStatus(), "statusDeclined");
        assertNotNull(SQLHelper.findCreditId());
    }

    @Test
    @DisplayName("Should not confirm the purchase when using a card with the DECLINED status")
    void shouldNotConfirmPaymentWithInvaliddeclinedCard() throws SQLException {
        getFilledPaymentPage(declinedCard).assertNotificationErrorIsVisible();
        assertEquals(SQLHelper.findPaymentStatus(), "statusDeclined");
        assertNull(SQLHelper.findPaymentId());
    }

    @Test
    @DisplayName("Must not confirm credit when using a card with the DECLINED status")
    void shouldNotConfirmCreditWithInvaliddeclinedCard() throws SQLException {
        getFilledCreditPage(declinedCard).notificationErrorIsVisible();
        assertEquals(SQLHelper.findCreditStatus(), "statusDeclined");
        assertNull(SQLHelper.findCreditId());
    }

    @Test
    @DisplayName("Should not confirm the purchase with an invalid card number")
    void shouldNotSubmitPaymentWithIllegalCard() throws SQLException {
        approvedCard.setNumber("4444 4444 4444 4444");
        getFilledPaymentPage(approvedCard).assertNotificationErrorIsVisible();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Should not confirm a loan with an invalid card number")
    void shouldNotSubmitCreditWithIllegalCard() throws SQLException {
        approvedCard.setNumber("4444 4444 4444 4444");
        getFilledCreditPage(approvedCard).notificationErrorIsVisible();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongMonth.cvs", numLinesToSkip = 1)
    void shouldNotSubmitPaymentWithWrongMonth(String month, String message) throws SQLException {
        approvedCard.setMonth(month);
        getFilledPaymentPage(approvedCard).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Should not confirm the purchase if a non-existent month is entered")
    void shouldNotConfirmPaymentWithInvalidMonth() throws SQLException {
        approvedCard.setMonth("22");
        getFilledPaymentPage(approvedCard).assertInputInvalidMonth();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Must not confirm purchase without specifying year")
    void shouldNotConfirmPaymentIfEmptyYear() throws SQLException {
        approvedCard.setYear("");
        getFilledPaymentPage(approvedCard).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Should not confirm the purchase if the year precedes the current")
    void shouldNotConfirmPaymentWithOldYear() throws SQLException {
        approvedCard.setYear(getWrongYear());
        getFilledPaymentPage(approvedCard).assertInputInvalidExpireDate();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongMonth.cvs", numLinesToSkip = 1)
    void shouldNotSubmitCreditWithWrongMonth(String month, String message) throws SQLException {
        approvedCard.setMonth(month);
        getFilledCreditPage(approvedCard).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Should not confirm a loan if a non-existent month is entered")
    void shouldNotConfirmCreditWithInvalidMonth() throws SQLException {
        approvedCard.setMonth("22");
        getFilledCreditPage(approvedCard).assertInputInvalidMonth();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Should not confirm the loan without indicating the year")
    void shouldNotConfirmCreditIfEmptyYear() throws SQLException {
        approvedCard.setYear("");
        getFilledCreditPage(approvedCard).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Should not confirm a loan if the year precedes the current")
    void shouldNotConfirmCreditWithOldYear() throws SQLException {
        approvedCard.setYear(getWrongYear());
        getFilledCreditPage(approvedCard).assertInputInvalidExpireDate();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Must not confirm purchase without owner name")
    void shouldNotConfirmPaymentWithoutOwner() throws SQLException {
        approvedCard.setOwner("");
        getFilledPaymentPage(approvedCard).assertInputInvalidFillData();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongOwner.cvs", numLinesToSkip = 1)
    void shouldNotConfirmPaymentWithInvalidOwner(String owner, String message) throws SQLException {
        approvedCard.setOwner(owner);
        getFilledPaymentPage(approvedCard).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Should not confirm a loan without the name of the owner")
    void shouldNotConfirmCreditWithoutOwner() throws SQLException {
        approvedCard.setOwner("");
        getFilledCreditPage(approvedCard).assertInputInvalidFillData();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongOwner.cvs", numLinesToSkip = 1)
    void shouldNotConfirmCreditWithInvalidOwner(String owner, String message) throws SQLException {
        approvedCard.setOwner(owner);
        getFilledCreditPage(approvedCard).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongCvc.cvs", numLinesToSkip = 1)
    void shouldNotConfirmPaymentWithInvalidCvc(String cvc, String message) throws SQLException {
        approvedCard.setCvc(cvc);
        getFilledPaymentPage(approvedCard).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongCvc.cvs", numLinesToSkip = 1)
    void shouldNotConfirmCreditWithInvalidCvc(String cvc, String message) throws SQLException {
        approvedCard.setCvc(cvc);
        getFilledCreditPage(approvedCard).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    private void setDeclinedCard() {
        declinedCard.setNumber("4444 4444 4444 4442");
        declinedCard.setMonth("05");
        declinedCard.setYear(getCorrectYear());
        declinedCard.setOwner(setFakeOwner());
        declinedCard.setCvc(getRandomCvc());
    }

    private void setApprovedCard() {
        approvedCard.setNumber("4444 4444 4444 4441");
        invalidNumberCard.setNumber("4444 4444 4444 4444");
        approvedCard.setMonth("05");
        approvedCard.setYear(getCorrectYear());
        approvedCard.setOwner(setFakeOwner());
        approvedCard.setCvc(getRandomCvc());
    }
}