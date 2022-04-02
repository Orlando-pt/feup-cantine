package pt.feup.les.feupfood.dto;

import lombok.Data;

@Data
public class StatsIntentionDto {
    private int intentionsGiven;
    private int intentionsFulfilled;
    private int intentionsNotFulfilled;

    public StatsIntentionDto() {
        this.intentionsGiven = 0;
        this.intentionsFulfilled = 0;
        this.intentionsNotFulfilled = 0;
    }

    public void addIntentionsGiven(int intentions) {
        this.intentionsGiven += intentions;
    }

    public void incrementIntentionsFulfilled() {
        this.intentionsFulfilled++;
    }

    public void incrementIntentionsNotFulfilled() {
        this.intentionsNotFulfilled++;
    }
}
