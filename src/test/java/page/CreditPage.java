package page;

import data.Card;

public class CreditPage extends PageForm {
    public static CreditPage getFilledCreditPage(Card card) {
        CreditPage creditPage = StartPage.getStartPage().creditPage();
        creditPage.getCreditCardForm().fillData(card);
        return creditPage;
    }

    public CreditPage() {
        creditCardForm = new CreditCardForm();
    }
}