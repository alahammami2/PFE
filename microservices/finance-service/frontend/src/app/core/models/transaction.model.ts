/**
 * Modèle pour les transactions financières
 */
export interface Transaction {
  id: number;
  reference: string;
  description: string;
  montant: number;
  montantTtc?: number;
  tauxTva?: number;
  dateTransaction: string;
  typeTransaction: TypeTransaction;
  statut: StatutTransaction;
  modePaiement?: string;
  referenceExterne?: string;
  budget?: any; // Référence vers Budget
  categorieTransaction?: CategorieTransaction;
  utilisateurId: number;
  validateurId?: number;
  dateValidation?: string;
  motifRejet?: string;
  piecesJointes?: string[];
  dateCreation: string;
  dateModification: string;
}

/**
 * Types de transaction
 */
export enum TypeTransaction {
  RECETTE = 'RECETTE',
  DEPENSE = 'DEPENSE'
}

/**
 * Statuts possibles pour une transaction
 */
export enum StatutTransaction {
  EN_ATTENTE = 'EN_ATTENTE',
  VALIDEE = 'VALIDEE',
  REJETEE = 'REJETEE',
  ANNULEE = 'ANNULEE'
}

/**
 * Catégorie de transaction
 */
export interface CategorieTransaction {
  id: number;
  nom: string;
  description?: string;
  typeTransaction: TypeTransaction;
  couleur?: string;
  icone?: string;
  actif: boolean;
  dateCreation: string;
  dateModification: string;
}

/**
 * DTO pour la création/modification d'une transaction
 */
export interface TransactionDto {
  description: string;
  montant: number;
  montantTtc?: number;
  tauxTva?: number;
  dateTransaction: string;
  typeTransaction: TypeTransaction;
  modePaiement?: string;
  referenceExterne?: string;
  budgetId?: number;
  categorieTransactionId?: number;
  piecesJointes?: string[];
}

/**
 * Filtres pour la recherche de transactions
 */
export interface FiltresTransaction {
  typeTransaction?: TypeTransaction;
  statut?: StatutTransaction;
  dateDebut?: string;
  dateFin?: string;
  montantMin?: number;
  montantMax?: number;
  budgetId?: number;
  categorieId?: number;
  utilisateurId?: number;
  reference?: string;
  description?: string;
}

/**
 * Statistiques des transactions
 */
export interface StatistiquesTransaction {
  totalRecettes: number;
  totalDepenses: number;
  solde: number;
  nombreTransactions: number;
  moyenneMontant: number;
  transactionsEnAttente: number;
  repartitionParCategorie: RepartitionCategorie[];
  evolutionMensuelle: EvolutionMensuelle[];
}

/**
 * Répartition par catégorie
 */
export interface RepartitionCategorie {
  categorieNom: string;
  montant: number;
  pourcentage: number;
  couleur?: string;
}

/**
 * Évolution mensuelle
 */
export interface EvolutionMensuelle {
  mois: string;
  recettes: number;
  depenses: number;
  solde: number;
}
