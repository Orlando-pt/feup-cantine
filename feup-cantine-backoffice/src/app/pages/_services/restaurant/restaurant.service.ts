import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Profile } from '../../_types/Profile';
import { VerifyCode } from '../../_types/VerifyCode';
import { Assignment } from '../../_types/Assignment';

type Summary = {
  totalReviewsReceived: number;
  totalReviewsResponded: number;
  totalIntentsReceived: number;
  moneyOffered: number;
  assignmentsCreated: number;
  menusCreated: number;
  mealsCreated: number;
  favorited: number;
};
@Injectable({
  providedIn: 'root',
})
export class RestaurantService {
  API_URL = 'api/restaurant';

  constructor(private http: HttpClient) {}

  profile(): Observable<Profile> {
    return this.http.get<Profile>(`${this.API_URL}/profile`);
  }

  updateProfile(body: Profile): Observable<Profile> {
    return this.http.put<Profile>(`${this.API_URL}/profile`, body);
  }

  getCurrentAssignment(): Observable<Assignment> {
    return this.http.get<Assignment>(`${this.API_URL}/assignment/now`);
  }

  validateCode(code: string): Observable<VerifyCode> {
    return this.http.get<VerifyCode>(
      `${this.API_URL}/assignment/verify-code/${code}`,
    );
  }

  getPopularity(
    increment: number,
    startDate: Date,
    endDate: Date,
  ): Observable<any> {
    return this.http.get<any>(
      `${this.API_URL}/stats/popularity/${increment}/${startDate}/${endDate}`,
    );
  }

  getIntentions(
    increment: number,
    startDate: Date,
    endDate: Date,
  ): Observable<any> {
    return this.http.get<any>(
      `${this.API_URL}/stats/intention/${increment}/${startDate}/${endDate}`,
    );
  }

  getSummary(): Observable<Summary> {
    return this.http.get<Summary>(`${this.API_URL}/stats/general/`);
  }
}
