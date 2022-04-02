import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Meal } from '../../_types/Meal';

@Injectable({
  providedIn: 'root',
})
export class MealsService {
  API_URL = 'api/restaurant';

  constructor(private http: HttpClient) {}

  getMeals(): Observable<Meal[]> {
    return this.http.get<Meal[]>(`${this.API_URL}/meal`);
  }

  addMeal(body: Omit<Meal, 'id'>): Observable<Meal> {
    return this.http.post<Meal>(`${this.API_URL}/meal`, body);
  }

  updateMeal(id: string, body: Omit<Meal, 'id'>): Observable<Meal> {
    return this.http.put<Meal>(`${this.API_URL}/meal/${id}`, body);
  }

  deleteMeal(id: string): Observable<unknown> {
    return this.http.delete<unknown>(`${this.API_URL}/meal/${id}`);
  }
}
