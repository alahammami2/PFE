import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

// Models
import { Budget } from '../models/budget.model';
import { Transaction } from '../models/transaction.model';
import { Sponsor } from '../models/sponsor.model';
import { Salaire } from '../models/salaire.model';
import { ElementSalaire } from '../models/element-salaire.model';
import { RapportFinancier } from '../models/rapport-financier.model';
import { PaginatedResponse } from '../models/paginated-response.model';

/**
 * Service pour les appels API du service finance
 */
@Injectable({
  providedIn: 'root'
})
export class FinanceApiService {
  private readonly baseUrl = `${environment.apiUrl}/api`;

  constructor(private http: HttpClient) {}

  // ===== BUDGETS =====

  /**
   * Récupère tous les budgets avec pagination
   */
  getBudgets(page: number = 0, size: number = 10, sort?: string): Observable<PaginatedResponse<Budget>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    if (sort) {
      params = params.set('sort', sort);
    }
    
    return this.http.get<PaginatedResponse<Budget>>(`${this.baseUrl}/budgets`, { params });
  }

  /**
   * Récupère un budget par ID
   */
  getBudget(id: number): Observable<Budget> {
    return this.http.get<Budget>(`${this.baseUrl}/budgets/${id}`);
  }

  /**
   * Crée un nouveau budget
   */
  createBudget(budget: Partial<Budget>): Observable<Budget> {
    return this.http.post<Budget>(`${this.baseUrl}/budgets`, budget);
  }

  /**
   * Met à jour un budget
   */
  updateBudget(id: number, budget: Partial<Budget>): Observable<Budget> {
    return this.http.put<Budget>(`${this.baseUrl}/budgets/${id}`, budget);
  }

  /**
   * Supprime un budget
   */
  deleteBudget(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/budgets/${id}`);
  }

  /**
   * Utilise un montant du budget
   */
  utiliserMontantBudget(id: number, montant: number, description: string): Observable<Budget> {
    return this.http.post<Budget>(`${this.baseUrl}/budgets/${id}/utiliser`, {
      montant,
      description
    });
  }

  /**
   * Libère un montant du budget
   */
  libererMontantBudget(id: number, montant: number, description: string): Observable<Budget> {
    return this.http.post<Budget>(`${this.baseUrl}/budgets/${id}/liberer`, {
      montant,
      description
    });
  }

  /**
   * Récupère les budgets actifs
   */
  getBudgetsActifs(): Observable<Budget[]> {
    return this.http.get<Budget[]>(`${this.baseUrl}/budgets/actifs`);
  }

  // ===== TRANSACTIONS =====

  /**
   * Récupère toutes les transactions avec pagination et filtres
   */
  getTransactions(
    page: number = 0,
    size: number = 10,
    sort?: string,
    filters?: any
  ): Observable<PaginatedResponse<Transaction>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    if (sort) {
      params = params.set('sort', sort);
    }
    
    if (filters) {
      Object.keys(filters).forEach(key => {
        if (filters[key] !== null && filters[key] !== undefined && filters[key] !== '') {
          params = params.set(key, filters[key]);
        }
      });
    }
    
    return this.http.get<PaginatedResponse<Transaction>>(`${this.baseUrl}/transactions/recherche`, { params });
  }

  /**
   * Récupère une transaction par ID
   */
  getTransaction(id: number): Observable<Transaction> {
    return this.http.get<Transaction>(`${this.baseUrl}/transactions/${id}`);
  }

  /**
   * Crée une nouvelle transaction
   */
  createTransaction(transaction: Partial<Transaction>): Observable<Transaction> {
    return this.http.post<Transaction>(`${this.baseUrl}/transactions`, transaction);
  }

  /**
   * Met à jour une transaction
   */
  updateTransaction(id: number, transaction: Partial<Transaction>): Observable<Transaction> {
    return this.http.put<Transaction>(`${this.baseUrl}/transactions/${id}`, transaction);
  }

  /**
   * Supprime une transaction
   */
  deleteTransaction(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/transactions/${id}`);
  }

  /**
   * Valide une transaction
   */
  validerTransaction(id: number): Observable<Transaction> {
    return this.http.post<Transaction>(`${this.baseUrl}/transactions/${id}/valider`, {});
  }

  /**
   * Rejette une transaction
   */
  rejeterTransaction(id: number, motif: string): Observable<Transaction> {
    return this.http.post<Transaction>(`${this.baseUrl}/transactions/${id}/rejeter`, { motif });
  }

  /**
   * Récupère les transactions en attente de validation
   */
  getTransactionsEnAttente(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.baseUrl}/transactions/en-attente`);
  }

  // ===== SPONSORS =====

  /**
   * Récupère tous les sponsors avec pagination
   */
  getSponsors(page: number = 0, size: number = 10, sort?: string): Observable<PaginatedResponse<Sponsor>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    if (sort) {
      params = params.set('sort', sort);
    }
    
    return this.http.get<PaginatedResponse<Sponsor>>(`${this.baseUrl}/sponsors`, { params });
  }

  /**
   * Récupère un sponsor par ID
   */
  getSponsor(id: number): Observable<Sponsor> {
    return this.http.get<Sponsor>(`${this.baseUrl}/sponsors/${id}`);
  }

  /**
   * Crée un nouveau sponsor
   */
  createSponsor(sponsor: Partial<Sponsor>): Observable<Sponsor> {
    return this.http.post<Sponsor>(`${this.baseUrl}/sponsors`, sponsor);
  }

  /**
   * Met à jour un sponsor
   */
  updateSponsor(id: number, sponsor: Partial<Sponsor>): Observable<Sponsor> {
    return this.http.put<Sponsor>(`${this.baseUrl}/sponsors/${id}`, sponsor);
  }

  /**
   * Supprime un sponsor
   */
  deleteSponsor(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/sponsors/${id}`);
  }

  /**
   * Enregistre un paiement de sponsor
   */
  enregistrerPaiementSponsor(
    sponsorId: number,
    montant: number,
    datePaiement: string,
    modePaiement: string,
    referencePaiement?: string
  ): Observable<any> {
    return this.http.post(`${this.baseUrl}/sponsors/${sponsorId}/paiements`, {
      montant,
      datePaiement,
      modePaiement,
      referencePaiement
    });
  }

  // ===== SALAIRES =====

  /**
   * Récupère tous les salaires avec pagination et filtres
   */
  getSalaires(
    page: number = 0,
    size: number = 10,
    sort?: string,
    filters?: any
  ): Observable<PaginatedResponse<Salaire>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    if (sort) {
      params = params.set('sort', sort);
    }
    
    if (filters) {
      Object.keys(filters).forEach(key => {
        if (filters[key] !== null && filters[key] !== undefined && filters[key] !== '') {
          params = params.set(key, filters[key]);
        }
      });
    }
    
    return this.http.get<PaginatedResponse<Salaire>>(`${this.baseUrl}/salaires/recherche`, { params });
  }

  /**
   * Récupère un salaire par ID
   */
  getSalaire(id: number): Observable<Salaire> {
    return this.http.get<Salaire>(`${this.baseUrl}/salaires/${id}`);
  }

  /**
   * Calcule un nouveau salaire
   */
  calculerSalaire(salaireData: any): Observable<Salaire> {
    return this.http.post<Salaire>(`${this.baseUrl}/salaires/calculer`, salaireData);
  }

  /**
   * Valide un salaire
   */
  validerSalaire(id: number, validateurId: number): Observable<Salaire> {
    return this.http.post<Salaire>(`${this.baseUrl}/salaires/${id}/valider`, { validateurId });
  }

  /**
   * Marque un salaire comme payé
   */
  marquerSalaireCommePayé(id: number, modePaiement: string, referencePaiement: string): Observable<Salaire> {
    return this.http.post<Salaire>(`${this.baseUrl}/salaires/${id}/payer`, {
      modePaiement,
      referencePaiement
    });
  }

  /**
   * Récupère les éléments d'un salaire
   */
  getElementsSalaire(salaireId: number): Observable<ElementSalaire[]> {
    return this.http.get<ElementSalaire[]>(`${this.baseUrl}/salaires/${salaireId}/elements`);
  }

  /**
   * Supprime un salaire
   */
  deleteSalaire(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/salaires/${id}`);
  }

  // ===== RAPPORTS =====

  /**
   * Génère un rapport financier global
   */
  genererRapportGlobal(dateDebut: string, dateFin: string): Observable<RapportFinancier> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);
    
    return this.http.get<RapportFinancier>(`${this.baseUrl}/rapports/global`, { params });
  }

  /**
   * Génère un rapport de trésorerie
   */
  genererRapportTresorerie(dateDebut: string, dateFin: string): Observable<any> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);
    
    return this.http.get(`${this.baseUrl}/rapports/tresorerie`, { params });
  }

  /**
   * Télécharge un rapport Excel
   */
  telechargerRapportExcel(dateDebut: string, dateFin: string): Observable<Blob> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);
    
    return this.http.get(`${this.baseUrl}/rapports/excel`, {
      params,
      responseType: 'blob'
    });
  }

  /**
   * Génère un rapport pour le dashboard
   */
  genererRapportDashboard(): Observable<any> {
    return this.http.get(`${this.baseUrl}/rapports/dashboard`);
  }
}
