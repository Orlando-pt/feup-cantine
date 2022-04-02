import { ChangeDetectorRef, Component, ErrorHandler, OnDestroy, OnInit } from '@angular/core';
import { NbToastrService } from '@nebular/theme';
import { LocalDataSource } from 'ng2-smart-table';
import { of, Subscription } from 'rxjs';
import { catchError, finalize, tap } from 'rxjs/operators';
import { AssignmentsService } from '../_services/assignments/assignments.service';
import { MenuService } from '../_services/menu/menu.service';
import { Assignment } from '../_types/Assignment';
import { DateEditorComponent } from './date-editor.component';
import { DateRenderComponent } from './date-render.component';
import { MenuRenderComponent } from './menu-render.component';

@Component({
  selector: 'ngx-assignments',
  templateUrl: './assignments.component.html',
  styleUrls: ['./assignments.component.scss'],
})
export class AssignmentsComponent implements OnInit, OnDestroy {

  source: LocalDataSource = new LocalDataSource();

  private subscriptions: Subscription[] = [];

  data: Assignment[] = [];

  settings = {
    add: {
      addButtonContent: '<i class="nb-plus"></i>',
      createButtonContent: '<i class="nb-checkmark"></i>',
      cancelButtonContent: '<i class="nb-close"></i>',
      confirmCreate: true,
    },
    edit: {
      editButtonContent: '<i class="nb-edit"></i>',
      saveButtonContent: '<i class="nb-checkmark"></i>',
      cancelButtonContent: '<i class="nb-close"></i>',
      confirmSave: true,
    },
    delete: {
      deleteButtonContent: '<i class="nb-trash"></i>',
      confirmDelete: true,
    },
    columns: {
      date: {
        title: 'Date',
        type: 'custom',
        renderComponent: DateRenderComponent,
        editor: {
          type: 'custom',
          component: DateEditorComponent,
        },
      },
      schedule: {
        title: 'Schedule',
        type: 'text',
        editor: {
          type: 'list',
          config: {
            list: [
              {
                value: 'LUNCH',
                title: 'Lunch',
              },
              {
                value: 'DINNER',
                title: 'Dinner',
              },
            ],
          },
        },
      },
      menu: {
        title: 'Menu',
        type: 'custom',
        renderComponent: MenuRenderComponent,
        editor: {
          type: 'list',
          config: {
            list: [],
          },
        },
      },
    },
  };

  constructor(
    private readonly assignmentsService: AssignmentsService,
    private readonly menusService: MenuService,
    private readonly cdr: ChangeDetectorRef,
    private readonly handleError: ErrorHandler,
    private readonly toastService: NbToastrService,
  ) { }
  ngOnInit(): void {
    this.getAssignments();
    this.getMenus();
  }
  ngOnDestroy(): void {
    this.subscriptions.forEach((sb) => sb.unsubscribe());
  }

  getAssignments(): void {
    const assignmentsSubscribe = this.assignmentsService.getAssignments()
      .pipe(
        tap((res) => {
          this.data = res;
          this.source.load(this.data);
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
    this.subscriptions.push(assignmentsSubscribe);
  }

  getMenus(): void {
    const menusSubscribe = this.menusService.getMenus()
      .pipe(
        tap((res) =>
          res.forEach((menu) =>
            this.settings.columns.menu.editor.config.list.push({
              value: menu.id,
              title: menu.name,
            }),
          ),
        ),
        finalize(() => {
          this.cdr.markForCheck();

          this.settings = Object.assign({}, this.settings);
        }),
        catchError((err) => {
          this.handleError.handleError(err);
          return of(null);
        }),
      )
      .subscribe();
    this.subscriptions.push(menusSubscribe);
  }

  onCreateConfirm(event): void {
    if (window.confirm('Are you sure you want to add?')) {
      const assignmentsSubscribe = this.assignmentsService
        .addAssignment(event.newData)
        .pipe(
          tap((res) => {
            event.newData = res;
            event.newData.date = res.date.toString().split('T')[0];
            this.successToast('Assignment added successfully!');
          }),
          finalize(() => {
            this.cdr.markForCheck();
            event.confirm.resolve(event.newData);
          }),
          catchError((error) => {
            this.handleError.handleError(error);
            event.confirm.reject();
            this.errorToast('Failed to save assignment.');
            return of(null);
          }),
        )
        .subscribe();
      this.subscriptions.push(assignmentsSubscribe);
    } else {
      event.confirm.reject();
    }
  }

  onSaveConfirm(event): void {
    const id = event.newData.id;
    delete event.newData.id;

    if (event.newData.menu && event.newData.menu.id) event.newData.menu = event.newData.menu.id;

    const assignmentsSubscribe = this.assignmentsService
      .updateAssignment(id, event.newData)
      .pipe(
        tap((res) => {
          event.newData = res;
          event.newData.date = res.date.toString().split('T')[0];
          this.successToast('Assignment updated successfully!');
        }),
        finalize(() => {
          this.cdr.markForCheck();
          event.confirm.resolve(event.newData);
        }),
        catchError((err) => {
          this.handleError.handleError(err);
          event.confirm.reject();
          this.errorToast('Failed to update assignment.');
          return of(null);
        }),
      )
      .subscribe();

    this.subscriptions.push(assignmentsSubscribe);
  }

  onDeleteConfirm(event): void {
    if (window.confirm('Are you sure you want to delete?')) {
      const assignmentsSubscribe = this.assignmentsService
        .deleteAssignment(event.data.id)
        .pipe(
          tap((res) => {
            this.successToast('Assignment deleted successfully!');
          }),
          finalize(() => {
            this.cdr.markForCheck();
            event.confirm.resolve();
          }),
          catchError((err) => {
            this.handleError.handleError(err);
            event.confirm.reject();
            this.errorToast('Failed to delete assignment.');
            return of(null);
          }),
        )
        .subscribe();

      this.subscriptions.push(assignmentsSubscribe);
    } else {
      event.confirm.reject();
    }
  }

  private successToast(message: string) {
    this.toastService.show(message, 'Success', {status: 'success'});
  }

  private errorToast(message: string) {
    this.toastService.show(message, 'Danger', {status : 'danger'});
  }

}
