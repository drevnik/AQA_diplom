package page;

import data.Card;

public class PaymentPage extends PageForm {
    public static CreditPage getFilledPaymentPage(Card card) {
        CreditPage creditPage = StartPage.getStartPage().creditPage();
        creditPage.getCreditCardForm().fillData(card);
        return creditPage;
    }

    public PaymentPage() {
        creditCardForm = new CreditCardForm();
    }
}