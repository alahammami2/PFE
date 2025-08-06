import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private toastr: ToastrService) { }

  success(message: string, title?: string): void {
    this.toastr.success(message, title || 'Succès', {
      timeOut: 5000,
      progressBar: true,
      closeButton: true
    });
  }

  error(message: string, title?: string): void {
    this.toastr.error(message, title || 'Erreur', {
      timeOut: 7000,
      progressBar: true,
      closeButton: true
    });
  }

  warning(message: string, title?: string): void {
    this.toastr.warning(message, title || 'Attention', {
      timeOut: 6000,
      progressBar: true,
      closeButton: true
    });
  }

  info(message: string, title?: string): void {
    this.toastr.info(message, title || 'Information', {
      timeOut: 5000,
      progressBar: true,
      closeButton: true
    });
  }

  clear(): void {
    this.toastr.clear();
  }
}
