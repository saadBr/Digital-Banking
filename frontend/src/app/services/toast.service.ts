import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export interface ToastMessage {
  type: 'success' | 'error' | 'info';
  message: string;
}

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  constructor() {}

  private toastSubject = new Subject<ToastMessage>();
  toast$ = this.toastSubject.asObservable();

  showSuccess(message: string) {
    this.toastSubject.next({ type: 'success', message });
  }

  showError(message: string) {
    this.toastSubject.next({ type: 'error', message });
  }

  showInfo(message: string) {
    this.toastSubject.next({ type: 'info', message });
  }
}
