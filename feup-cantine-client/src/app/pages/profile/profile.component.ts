import {
  ChangeDetectorRef,
  Component,
  ErrorHandler,
  OnInit,
} from '@angular/core';
import { NbToastrService } from '@nebular/theme';
import { of, Subscription } from 'rxjs';
import { catchError, finalize, tap } from 'rxjs/operators';
import { ClientService } from '../_services/client/client.service';
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
    biography: null,
    profileImageUrl: null,
  };

  changeProfileImage: boolean = false;

  loading: boolean;

  private subscriptions: Subscription[] = [];

  constructor(
    private readonly clientService: ClientService,
    private readonly cdr: ChangeDetectorRef,
    private readonly handleError: ErrorHandler,
    private readonly toastService: NbToastrService,
  ) {}

  ngOnInit(): void {
    this.getClientInfo();
  }

  getClientInfo() {
    const clientSubscribe = this.clientService
      .profile()
      .pipe(
        tap((res) => {
          this.profile = res;
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

    this.subscriptions.push(clientSubscribe);
  }

  save(): void {
    this.loading = true;

    const saveChangesSubscribe = this.clientService
      .updateProfile(this.profile)
      .pipe(
        tap((res) => {
          this.profile = res;
        }),
        finalize(() => {
          this.loading = false;
          this.toastService.show('User was updated with success', 'Success', {
            status: 'success',
          });
          this.cdr.markForCheck();
          this.updateChangeProfileImage();
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
    this.changeProfileImage === false ? this.changeProfileImage = true : this.changeProfileImage = false;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sb) => sb.unsubscribe());
  }
}
