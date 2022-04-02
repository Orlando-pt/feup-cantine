import { Meal } from './Meal';

export type Menu = {
  id: number;
  name: string;
  additionalInformation: string;
  startPrice: number;
  endPrice: number;
  meatMeal: Meal;
  fishMeal: Meal;
  dietMeal: Meal;
  vegetarianMeal: Meal;
  desertMeal: Meal;
  numberOfIntentions: number;
  discount: number;
};
