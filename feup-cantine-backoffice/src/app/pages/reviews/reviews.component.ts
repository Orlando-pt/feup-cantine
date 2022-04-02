import { ChangeDetectorRef, Component, TemplateRef, ErrorHandler, OnDestroy, OnInit } from '@angular/core';
import { NbDialogRef, NbDialogService, NbToastrService } from '@nebular/theme';
import { of, Subscription } from 'rxjs';
import { catchError, finalize, tap } from 'rxjs/operators';
import { ReviewsService } from '../_services/reviews/reviews.service';
import { Review } from '../_types/Review';

@Component({
  selector: 'ngx-reviews',
  templateUrl: './reviews.component.html',
  styleUrls: ['./reviews.component.scss'],
})
export class ReviewsComponent implements OnInit, OnDestroy {

  restaurantReviews: Review[] = [];

  reviewAnswer: string;
  currentId: number;
  currentAnswerDialog: NbDialogRef<any>;

  private subscriptions: Subscription[] = [];

  constructor(
    private readonly reviewsService: ReviewsService,
    private readonly cdr: ChangeDetectorRef,
    private readonly handleError: ErrorHandler,
    private readonly toastService: NbToastrService,
    private readonly dialogService: NbDialogService,
  ) { }

  ngOnInit(): void {
    this.getClientsReviews();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sb) => sb.unsubscribe());
  }

  openModal(id: number, name: string, answer: TemplateRef<any>) {
    this.currentId = id;
    this.reviewAnswer = '';
    this.currentAnswerDialog = this.dialogService.open(answer, { context: name });
  }

  updateReviewModal(id: number, name: string, currentAnswer: string, answer: TemplateRef<any>) {
    this.currentId = id;
    this.reviewAnswer = currentAnswer;
    this.currentAnswerDialog = this.dialogService.open(answer, { context: name });
  }

  closeModal() {
    this.currentAnswerDialog.close();
    this.saveReviewAnswer();
  }

  getClientsReviews() {
    const reviewSubscriber = this.reviewsService
      .getAllReviews()
      .pipe(
        tap((res) => {
          this.restaurantReviews = res;
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

    this.subscriptions.push(reviewSubscriber);
  }

  saveReviewAnswer() {
    const saveAnswer = this.reviewsService
      .saveReview(
        {
          answer: this.reviewAnswer,
        },
        this.currentId,
      )
      .pipe(
        tap((res) => {
          this.restaurantReviews.forEach(
            review => {
              if (review.id === res.id) review.answer = res.answer;
            },
          );
          this.showToast('Answer was provided successfully.', 'Success');
        }),
        finalize(() => {
          this.cdr.markForCheck();
        }),
        catchError((err) => {
          this.handleError.handleError(err);
          this.showToast(
            'Something went wrong while saving the respone, please try again.',
            'Danger',
          );
          return of(null);
        }),
      )
      .subscribe();

    this.subscriptions.push(saveAnswer);
  }

  showToast(message: string, type: string) {
    this.toastService.show(message, type, {
      status: type.toLowerCase(),
    });
  }


}
