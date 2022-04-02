export type Review = {
  id: number;
  clientId: number;
  clientFullName: string;
  clientProfileImageUrl: string;
  restaurantId: number;
  classificationGrade: number;
  comment: string;
  answer: string;
  timestamp: Date;
};

