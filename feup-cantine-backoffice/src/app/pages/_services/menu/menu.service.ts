import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AddMenu } from '../../_types/AddMenu';
import { Menu } from '../../_types/Menu';
import { Meal } from '../../_types/Meal';

@Injectable({
  providedIn: 'root',
})
export class MenuService {
  API_URL = 'api/restaurant';
  constructor(private http: HttpClient) { }

  getMenus(): Observable<Menu []> {
    return this.http.get<Menu []>(`${this.API_URL}/menu`);
  }

  addMenu(body: Omit<AddMenu, 'id'>): Observable<Menu> {
    return this.http.post<Menu>(`${this.API_URL}/menu`, body);
  }

  updateMenu(id: number, body: Omit<AddMenu, 'id'>): Observable<Menu> {
    return this.http.put<Menu>(`${this.API_URL}/menu/${id}`, body);
  }

  deleteMenu(id: number): Observable<string> {
    return this.http.delete<string>(`${this.API_URL}/menu/${id}`);
  }
}
