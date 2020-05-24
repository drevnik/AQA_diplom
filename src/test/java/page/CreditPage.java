package page;

import data.Card;

public class CreditPage {
    public CreditCardForm creditCardForm = new CreditCardForm();

    public static CreditPage getFilledCreditPage(Card card) {
        CreditPage creditPage = StartPage.getStartPage().creditPage();
        creditPage.creditCardForm.fillData(card);
        return creditPage;
    }
}