/**
 * Modèle pour les budgets
 */
export interface Budget {
  id: number;
  nom: string;
  description?: string;
  montantTotal: number;
  montantUtilise: number;
  montantRestant: number;
  seuilAlerte: number;
  dateDebut: string;
  dateFin: string;
  statut: StatutBudget;
  periodeBudget: PeriodeBudget;
  autoRenouvellement: boolean;
  categorieBudget?: CategorieBudget;
  utilisateurCreateurId: number;
  commentaires?: string;
  dateCreation: string;
  dateModification: string;
}

/**
 * Statuts possibles pour un budget
 */
export enum StatutBudget {
  ACTIF = 'ACTIF',
  CLOTURE = 'CLOTURE',
  SUSPENDU = 'SUSPENDU'
}

/**
 * Périodes possibles pour un budget
 */
export enum PeriodeBudget {
  MENSUEL = 'MENSUEL',
  TRIMESTRIEL = 'TRIMESTRIEL',
  SEMESTRIEL = 'SEMESTRIEL',
  ANNUEL = 'ANNUEL',
  PONCTUEL = 'PONCTUEL'
}

/**
 * Catégorie de budget
 */
export interface CategorieBudget {
  id: number;
  nom: string;
  description?: string;
  couleur?: string;
  icone?: string;
  actif: boolean;
  dateCreation: string;
  dateModification: string;
}

/**
 * DTO pour la création/modification d'un budget
 */
export interface BudgetDto {
  nom: string;
  description?: string;
  montantTotal: number;
  seuilAlerte?: number;
  dateDebut: string;
  dateFin: string;
  periodeBudget: PeriodeBudget;
  autoRenouvellement?: boolean;
  categorieBudgetId?: number;
  commentaires?: string;
}

/**
 * Statistiques d'un budget
 */
export interface StatistiquesBudget {
  pourcentageUtilise: number;
  pourcentageRestant: number;
  joursRestants: number;
  moyenneDepenseJournaliere: number;
  estimationEpuisement?: string;
  alerteSeuilAtteint: boolean;
}

/**
 * Historique des mouvements d'un budget
 */
export interface MouvementBudget {
  id: number;
  budgetId: number;
  montant: number;
  typeMouvement: 'UTILISATION' | 'LIBERATION';
  description: string;
  utilisateurId: number;
  dateCreation: string;
}
