import { NbMenuItem } from '@nebular/theme';

export const MENU_ITEMS: NbMenuItem[] = [
  {
    title: 'Dashboard',
    icon: 'pie-chart-outline',
    link: '/pages/dashboard',
    home: true,
  },
  {
    title: 'FOOD',
    group: true,
  },
  {
    title: 'Meals',
    icon: 'book-open',
    link: '/pages/meals',
  },
  {
    title: 'Menus',
    icon: 'folder-outline',
    link: '/pages/menus',
  },
  {
    title: 'Assignments',
    icon: 'clock-outline',
    link: '/pages/assignments',
  },
  {
    title: 'MANAGEMENT',
    group: true,
  },
  {
    title: 'Validate Promotions',
    icon: 'checkmark-circle-2-outline',
    link: '/pages/validate-codes',
  },
  {
    title: 'Reviews',
    icon: 'book-outline',
    link: '/pages/reviews',
  },
];
