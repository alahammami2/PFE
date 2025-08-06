import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

export interface NotificationOptions {
  title?: string;
  timeout?: number;
  enableHtml?: boolean;
  closeButton?: boolean;
  progressBar?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private toastr: ToastrService) {}

  /**
   * Affiche une notification de succès
   */
  success(message: string, options?: NotificationOptions): void {
    this.toastr.success(message, options?.title || 'Succès', {
      timeOut: options?.timeout || 3000,
      enableHtml: options?.enableHtml || false,
      closeButton: options?.closeButton || true,
      progressBar: options?.progressBar || true,
      positionClass: 'toast-top-right'
    });
  }

  /**
   * Affiche une notification d'erreur
   */
  error(message: string, options?: NotificationOptions): void {
    this.toastr.error(message, options?.title || 'Erreur', {
      timeOut: options?.timeout || 5000,
      enableHtml: options?.enableHtml || false,
      closeButton: options?.closeButton || true,
      progressBar: options?.progressBar || true,
      positionClass: 'toast-top-right'
    });
  }

  /**
   * Affiche une notification d'avertissement
   */
  warning(message: string, options?: NotificationOptions): void {
    this.toastr.warning(message, options?.title || 'Attention', {
      timeOut: options?.timeout || 4000,
      enableHtml: options?.enableHtml || false,
      closeButton: options?.closeButton || true,
      progressBar: options?.progressBar || true,
      positionClass: 'toast-top-right'
    });
  }

  /**
   * Affiche une notification d'information
   */
  info(message: string, options?: NotificationOptions): void {
    this.toastr.info(message, options?.title || 'Information', {
      timeOut: options?.timeout || 3000,
      enableHtml: options?.enableHtml || false,
      closeButton: options?.closeButton || true,
      progressBar: options?.progressBar || true,
      positionClass: 'toast-top-right'
    });
  }

  /**
   * Efface toutes les notifications
   */
  clear(): void {
    this.toastr.clear();
  }

  /**
   * Affiche une notification de succès pour une opération CRUD
   */
  successOperation(operation: 'créé' | 'modifié' | 'supprimé', entity: string): void {
    this.success(`${entity} ${operation} avec succès`);
  }

  /**
   * Affiche une notification d'erreur pour une opération CRUD
   */
  errorOperation(operation: 'création' | 'modification' | 'suppression', entity: string): void {
    this.error(`Erreur lors de la ${operation} de ${entity}`);
  }

  /**
   * Affiche une notification de chargement
   */
  loading(message: string = 'Chargement en cours...'): void {
    this.info(message, { timeout: 0, closeButton: false, progressBar: false });
  }

  /**
   * Affiche une notification de validation d'erreur
   */
  validationError(message: string = 'Veuillez vérifier les données saisies'): void {
    this.warning(message, { title: 'Validation' });
  }

  /**
   * Affiche une notification de connexion
   */
  connectionError(message: string = 'Problème de connexion au serveur'): void {
    this.error(message, { title: 'Connexion', timeout: 0 });
  }
}
