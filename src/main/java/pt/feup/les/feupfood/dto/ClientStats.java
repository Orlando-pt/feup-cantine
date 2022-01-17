package pt.feup.les.feupfood.dto;

import lombok.Data;

@Data
public class ClientStats {
    private double moneySaved;
    private int intentionsGiven;
    private int intentionsNotFulfilled;

    public ClientStats() {
        this.moneySaved = 0.0;
        this.intentionsGiven = 0;
        this.intentionsNotFulfilled = 0;
    }

    public void addMoney(double money) {
        this.moneySaved += money;
    }

    public void incrementIntentionsGiven() {
        this.intentionsGiven++;
    }

    public void incrementIntentionsNotFulfilled() {
        this.intentionsNotFulfilled++;
    }
}
