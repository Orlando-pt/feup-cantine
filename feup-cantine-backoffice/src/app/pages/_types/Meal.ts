export type Meal = {
  id: string;
  mealType: 'MEAT' | 'FISH' | 'DIET' | 'VEGETARIAN' | 'DESERT';
  description: string;
  nutritionalInformation: string;
};
