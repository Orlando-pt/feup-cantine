import {
  ChangeDetectorRef,
  Component,
  ErrorHandler,
  OnInit,
} from '@angular/core';
import { NbToastrService } from '@nebular/theme';
import { of, Subscription } from 'rxjs';
import { catchError, finalize, tap } from 'rxjs/operators';
import { RestaurantService } from '../_services/restaurant/restaurant.service';
import { Profile } from '../_types/Profile';
import { OnDestroy } from '@angular/core';

@Component({
  selector: 'ngx-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
})
export class ProfileComponent implements OnInit, OnDestroy {
  profile: Profile = {
    fullName: null,
    location: null,
    morningOpeningSchedule: null,
    morningClosingSchedule: null,
    afternoonOpeningSchedule: null,
    afternoonClosingSchedule: null,
    cuisines: null,
    typeMeals: null,
    profileImageUrl: null,
  };

  morningOpeningSchedule: Date;
  morningClosingSchedule: Date;
  afternoonOpeningSchedule: Date;
  afternoonClosingSchedule: Date;

  changeProfileImage: boolean = false;

  loading: boolean;

  private subscriptions: Subscription[] = [];

  constructor(
    private readonly restaurantService: RestaurantService,
    private readonly cdr: ChangeDetectorRef,
    private readonly handleError: ErrorHandler,
    private readonly toastService: NbToastrService,
  ) {}

  ngOnInit(): void {
    this.getRestaurantInfo();
  }

  getRestaurantInfo() {
    const restaurantSubscribe = this.restaurantService
      .profile()
      .pipe(
        tap((res) => {
          this.profile = res;
          this.morningOpeningSchedule = this.convertStringIntoDate(
            res.morningOpeningSchedule,
          );
          this.morningClosingSchedule = this.convertStringIntoDate(
            res.morningClosingSchedule,
          );
          this.afternoonOpeningSchedule = this.convertStringIntoDate(
            res.afternoonOpeningSchedule,
          );
          this.afternoonClosingSchedule = this.convertStringIntoDate(
            res.afternoonClosingSchedule,
          );
        }),
        finalize(() => {
          this.cdr.markForCheck();
        }),
        catchError((err) => {
          this.handleError.handleError(err);
          return of(null);
        }),
      )
      .subscribe();

    this.subscriptions.push(restaurantSubscribe);
  }

  save(): void {
    this.loading = true;

    this.profile = {
      ...this.profile,
      morningOpeningSchedule: this.formatDate(this.morningOpeningSchedule),
      morningClosingSchedule: this.formatDate(this.morningClosingSchedule),
      afternoonOpeningSchedule: this.formatDate(this.afternoonOpeningSchedule),
      afternoonClosingSchedule: this.formatDate(this.afternoonClosingSchedule),
    };

    const saveChangesSubscribe = this.restaurantService
      .updateProfile(this.profile)
      .pipe(
        tap((res) => {
          this.profile = res;
        }),
        finalize(() => {
          this.loading = false;
          this.toastService.show(
            'Restaurant was updated with success',
            'Success',
            { status: 'success' },
          );
          this.cdr.markForCheck();
        }),
        catchError((err) => {
          this.handleError.handleError(err);
          return of(null);
        }),
      )
      .subscribe();

    this.subscriptions.push(saveChangesSubscribe);
  }

  updateChangeProfileImage() {
    this.changeProfileImage = !this.changeProfileImage;
  }

  private formatDate(date: Date) {
    return date && date.toTimeString().split(' ')[0];
  }

  private convertStringIntoDate(date: string) {
    if (date == null) {
      return null;
    }

    const newDate = new Date();

    newDate.setHours(Number(date.split(':')[0]));
    newDate.setMinutes(Number(date.split(':')[1]));
    newDate.setSeconds(Number(date.split(':')[2]));

    return newDate;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sb) => sb.unsubscribe());
  }
}
