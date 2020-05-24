package page;

import data.Card;

public class PaymentPage {
    public CreditCardForm creditCardForm = new CreditCardForm();

    public static PaymentPage getFilledPaymentPage(Card card) {
        PaymentPage paymentPage = StartPage.getStartPage().paymentPage();
        paymentPage.creditCardForm.fillData(card);
        return paymentPage;
    }
}