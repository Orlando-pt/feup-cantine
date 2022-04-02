import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Assignment } from '../../_types/Assignment';
import { Menu } from '../../_types/Menu';
import { PriceRange } from '../../_types/PriceRange';
import { Restaurant } from '../../_types/Restaurant';
import { Review } from '../../_types/Review';
import { Meal } from '../../_types/Meal';

@Injectable({
  providedIn: 'root',
})
export class RestaurantsService {
  API_URL = 'api/client';

  constructor(private http: HttpClient) {}

  getOne(id: string): Observable<Restaurant> {
    return this.http.get<Restaurant>(`${this.API_URL}/restaurant/${id}`);
  }

  getAll(): Observable<Restaurant[]> {
    return this.http.get<Restaurant[]>(`${this.API_URL}/restaurant`);
  }

  saveReview(
    body: {
      classificationGrade: number;
      comment: string;
    },
    restaurantId: number,
  ): Observable<any> {
    return this.http.post<any>(
      `${this.API_URL}/review/restaurant/${restaurantId}`,
      body,
    );
  }

  getClientReviews(idRestaurant: string): Observable<Review[]> {
    return this.http.get<Review[]>(
      `${this.API_URL}/review/restaurant/${idRestaurant}`,
    );
  }

  getPriceRange(idRestaurant: string): Observable<PriceRange> {
    return this.http.get<PriceRange>(
      `${this.API_URL}/restaurant/${idRestaurant}/price-range`,
    );
  }

  getFavorites(): Observable<Restaurant[]> {
    return this.http.get<Restaurant[]>(`${this.API_URL}/restaurant/favorite/`);
  }

  getFavorite(idRestaurant: string) {
    return this.http.get<{ favorite: boolean }>(
      `${this.API_URL}/restaurant/favorite/${idRestaurant}`,
    );
  }

  addFavorite(idRestaurant: string) {
    return this.http.post<any>(
      `${this.API_URL}/restaurant/favorite/${idRestaurant}`,
      null,
    );
  }

  deleteFavorite(idRestaurant: string) {
    return this.http.delete<any>(
      `${this.API_URL}/restaurant/favorite/${idRestaurant}`,
    );
  }

  getCurrentAssignment(idRestaurant: string): Observable<Assignment> {
    return this.http.get<Assignment>(
      `${this.API_URL}/restaurant/${idRestaurant}/assignment/now`,
    );
  }

  getNAssignments(idRestaurant: string, n: number): Observable<Assignment[]> {
    return this.http.get<Assignment[]>(
      `${this.API_URL}/restaurant/${idRestaurant}/assignment/${n}`,
    );
  }

  getIntentions(): Observable<
    {
      assignment: Assignment;
      code: string;
      id: string;
      meals: Meal[];
      validatedCode: boolean;
      restaurant: string;
    }[]
  > {
    return this.http.get<
      {
        assignment: Assignment;
        code: string;
        id: string;
        meals: Meal[];
        validatedCode: boolean;
        restaurant: string;
      }[]
    >(`${this.API_URL}/intention/from-today`);
  }

  addIntention(obj: {
    assignmentId: string;
    mealsId: string[];
  }): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/intention/`, obj);
  }
}
