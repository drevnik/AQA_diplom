package data;
import lombok.Data;

@Data
public class Card {
    private String number;
    private String month;
    private String year;
    private String owner;
    private String cvc;
}