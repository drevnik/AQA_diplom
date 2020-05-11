package page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

public class StartPage {

    private SelenideElement buyButton = $$(By.cssSelector("button")).first();
    private SelenideElement creditButton = $$(By.cssSelector("button")).last();

    public static StartPage getStartPage() {
        open("http://localhost:8080/");
        return new StartPage();
    }

    public PaymentPage paymentPage() {
        buyButton.click();
        return new PaymentPage();
    }

    public CreditPage creditPage() {
        creditButton.click();
        return new CreditPage();
    }
}
