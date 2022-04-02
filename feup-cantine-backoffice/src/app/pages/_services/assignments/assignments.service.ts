import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Assignment } from '../../_types/Assignment';

@Injectable({
  providedIn: 'root',
})
export class AssignmentsService {

  API_URL = 'api/restaurant';

  constructor(
    private http: HttpClient,
  ) { }

  getAssignments(): Observable<Assignment []> {
    return this.http.get<Assignment []>(`${this.API_URL}/assignment`);
  }

  addAssignment(body: Omit<Assignment, 'id'>): Observable<Assignment> {
    return this.http.post<Assignment>(`${this.API_URL}/assignment`, body);
  }

  updateAssignment(id: number, body: Omit<Assignment, 'id'>): Observable<Assignment> {
    return this.http.put<Assignment>(`${this.API_URL}/assignment/${id}`, body);
  }

  deleteAssignment(id: number): Observable<string> {
    return this.http.delete<string>(`${this.API_URL}/assignment/${id}`);
  }
}
