package pt.feup.les.feupfood.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pt.feup.les.feupfood.dto.GetPutMealDto;
import pt.feup.les.feupfood.dto.GetPutMenuDto;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.MealTypeEnum;
import pt.feup.les.feupfood.model.Menu;

public class RestaurantParserTest {

    private Menu menu;
    private RestaurantParser parser;

    @BeforeEach
    void setup() {
        this.parser = new RestaurantParser();

        this.menu = new Menu();
        this.menu.setAdditionalInformation("additionalInformation");
        this.menu.setEndPrice(1.1);
        this.menu.setId(2L);
        this.menu.setName("name");
        this.menu.setStartPrice(0.5);
        
        var meat = new Meal();
        meat.setMealType(MealTypeEnum.MEAT);
        meat.setId(1L);

        var fish = new Meal();
        fish.setMealType(MealTypeEnum.FISH);
        fish.setId(2L);

        var diet = new Meal();
        diet.setMealType(MealTypeEnum.DIET);
        diet.setId(3L);

        var vegetarian = new Meal();
        vegetarian.setMealType(MealTypeEnum.VEGETARIAN);
        vegetarian.setId(4L);

        var desert = new Meal();
        desert.setMealType(MealTypeEnum.DESERT);
        desert.setId(5L);

        var nullMeal = new Meal();
        nullMeal.setMealType(null);
        nullMeal.setId(1L);

        this.menu.addMeal(meat);
        this.menu.addMeal(fish);
        this.menu.addMeal(diet);
        this.menu.addMeal(vegetarian);
        this.menu.addMeal(desert);
        this.menu.addMeal(nullMeal);
    }
    @Test
    void testParseMenutoMenuDto() {
        var expectedMenuDto = new GetPutMenuDto();
        expectedMenuDto.setAdditionalInformation(this.menu.getAdditionalInformation());
        expectedMenuDto.setEndPrice(this.menu.getEndPrice());
        expectedMenuDto.setId(this.menu.getId());
        expectedMenuDto.setName(this.menu.getName());
        expectedMenuDto.setStartPrice(this.menu.getStartPrice());

        var meat = new GetPutMealDto();
        meat.setMealType(MealTypeEnum.MEAT);
        meat.setId(1L);
        meat.setChoosen(false);

        var fish = new GetPutMealDto();
        fish.setMealType(MealTypeEnum.FISH);
        fish.setId(2L);
        fish.setChoosen(false);

        var diet = new GetPutMealDto();
        diet.setMealType(MealTypeEnum.DIET);
        diet.setId(3L);
        diet.setChoosen(false);

        var vegetarian = new GetPutMealDto();
        vegetarian.setMealType(MealTypeEnum.VEGETARIAN);
        vegetarian.setId(4L);
        vegetarian.setChoosen(false);

        var desert = new GetPutMealDto();
        desert.setMealType(MealTypeEnum.DESERT);
        desert.setId(5L);
        desert.setChoosen(false);

        var nullMeal = new GetPutMealDto();
        nullMeal.setMealType(null);
        nullMeal.setId(1L);
        nullMeal.setChoosen(false);

        expectedMenuDto.setMeatMeal(meat);
        expectedMenuDto.setFishMeal(fish);
        expectedMenuDto.setDietMeal(diet);
        expectedMenuDto.setVegetarianMeal(vegetarian);
        expectedMenuDto.setDesertMeal(desert);

        Assertions.assertThat(
            this.parser.parseMenutoMenuDto(this.menu)
        ).isEqualTo(expectedMenuDto);
    }
}
