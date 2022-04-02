import { NbMenuItem } from '@nebular/theme';

export const MENU_ITEMS: NbMenuItem[] = [
  {
    title: 'Dashboard',
    icon: 'pie-chart-outline',
    link: '/pages/dashboard',
    home: true,
  },
  {
    title: 'Favorites',
    icon: 'star-outline',
    link: '/pages/restaurants/favorites',
  },
  {
    title: 'Restaurants',
    icon: 'book-open',
    link: '/pages/restaurants',
  },
  {
    title: 'Promotional Codes',
    icon: 'flash-outline',
    link: '/pages/codes',
  },
];
