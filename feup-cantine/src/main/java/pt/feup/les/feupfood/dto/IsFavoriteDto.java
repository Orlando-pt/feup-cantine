package pt.feup.les.feupfood.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IsFavoriteDto {
    private boolean favorite;

    public boolean getFavorite() {
        return this.favorite;
    }
}
