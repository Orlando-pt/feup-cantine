import { Menu } from './Menu';

export type Assignment = {
  id: number;
  date: Date;
  schedule: 'LUNCH' | 'DINNER';
  menu: Menu;
  numberOfIntentions: number;
  available: boolean;
  purchased: boolean;
};
