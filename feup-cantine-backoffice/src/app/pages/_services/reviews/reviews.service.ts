import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Review } from '../../_types/Review';

@Injectable({
  providedIn: 'root',
})
export class ReviewsService {

  API_URL = 'api/restaurant';

  constructor(private http: HttpClient) { }

  getAllReviews(): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.API_URL}/review`);
  }

  saveReview(
    body: {
      answer: string;
    },
    reviewId: number,
  ): Observable<Review> {
    return this.http.put<Review>(
      `${this.API_URL}/review/${reviewId}`,
      body,
    );
  }
}
