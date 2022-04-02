import { Meal } from './Meal';

export type VerifyCode = {
  fullName: string;
  meals: (Meal & {
    numberOfIntentions: number;
  })[];
  profileImageUrl: string;
};
