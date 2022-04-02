import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Assignment } from '../../_types/Assignment';
import { Meal } from '../../_types/Meal';
import { Profile } from '../../_types/Profile';

type Summary = {
  intentionsGiven: number;
  intentionsNotFulfilled: number;
  moneySaved: number;
  numberOfFavoritRestaurants: number;
  numberOfReviews: number;
};

type Intention = {
  assignment: Assignment;
  code: string;
  id: string;
  meals: Meal[];
  validatedCode: boolean;
  restaurant: string;
};
@Injectable({
  providedIn: 'root',
})
export class ClientService {
  API_URL = 'api/client';

  constructor(private http: HttpClient) {}

  profile(): Observable<Profile> {
    return this.http.get<Profile>(`${this.API_URL}/profile`);
  }

  updateProfile(body: Profile): Observable<Profile> {
    return this.http.put<Profile>(`${this.API_URL}/profile`, body);
  }

  getNextIntention(): Observable<Intention> {
    return this.http.get<Intention>(`${this.API_URL}/intention/next`);
  }

  getSummary(): Observable<Summary> {
    return this.http.get<Summary>(`${this.API_URL}/stats/money-saved`);
  }
}
