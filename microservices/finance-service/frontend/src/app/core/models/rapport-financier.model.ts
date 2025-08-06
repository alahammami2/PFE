/**
 * Modèle pour les rapports financiers
 */
export interface RapportFinancier {
  typeRapport: TypeRapport;
  periode: PeriodeRapport;
  dateGeneration: string;
  resumeExecutif: ResumeExecutif;
  budgets: RapportBudgets;
  transactions: RapportTransactions;
  sponsors: RapportSponsors;
  salaires: RapportSalaires;
  tresorerie: RapportTresorerie;
  indicateurs: IndicateursFinanciers;
  graphiques: GraphiquesRapport;
}

/**
 * Types de rapport
 */
export enum TypeRapport {
  GLOBAL = 'GLOBAL',
  TRESORERIE = 'TRESORERIE',
  DASHBOARD = 'DASHBOARD',
  MENSUEL = 'MENSUEL',
  TRIMESTRIEL = 'TRIMESTRIEL',
  ANNUEL = 'ANNUEL',
  PERSONNALISE = 'PERSONNALISE',
  COMPARAISON = 'COMPARAISON'
}

/**
 * Période du rapport
 */
export interface PeriodeRapport {
  dateDebut: string;
  dateFin: string;
  libelle: string;
  dureeJours: number;
}

/**
 * Résumé exécutif
 */
export interface ResumeExecutif {
  chiffreCleRecettes: number;
  chiffreCleDepenses: number;
  resultatNet: number;
  evolutionRecettes: number; // Pourcentage d'évolution
  evolutionDepenses: number; // Pourcentage d'évolution
  principauxIndicateurs: IndicateurCle[];
  alertes: AlerteFinanciere[];
}

/**
 * Indicateur clé
 */
export interface IndicateurCle {
  libelle: string;
  valeur: number;
  unite: string;
  evolution?: number;
  statut: 'POSITIF' | 'NEUTRE' | 'NEGATIF';
}

/**
 * Alerte financière
 */
export interface AlerteFinanciere {
  type: 'BUDGET' | 'SPONSOR' | 'TRESORERIE' | 'SALAIRE';
  niveau: 'INFO' | 'WARNING' | 'ERROR';
  message: string;
  valeur?: number;
  seuil?: number;
}

/**
 * Rapport des budgets
 */
export interface RapportBudgets {
  nombreBudgets: number;
  montantTotalBudgets: number;
  montantUtilise: number;
  montantRestant: number;
  tauxUtilisation: number;
  budgetsEnAlerte: BudgetAlerte[];
  repartitionParCategorie: RepartitionBudgetCategorie[];
}

/**
 * Budget en alerte
 */
export interface BudgetAlerte {
  id: number;
  nom: string;
  montantTotal: number;
  montantUtilise: number;
  pourcentageUtilise: number;
  seuilAlerte: number;
}

/**
 * Répartition budget par catégorie
 */
export interface RepartitionBudgetCategorie {
  categorie: string;
  montantTotal: number;
  montantUtilise: number;
  pourcentage: number;
}

/**
 * Rapport des transactions
 */
export interface RapportTransactions {
  nombreTransactions: number;
  totalRecettes: number;
  totalDepenses: number;
  solde: number;
  moyenneMontant: number;
  transactionsEnAttente: number;
  repartitionParCategorie: RepartitionTransactionCategorie[];
  evolutionMensuelle: EvolutionTransactionMensuelle[];
}

/**
 * Répartition transaction par catégorie
 */
export interface RepartitionTransactionCategorie {
  categorie: string;
  type: 'RECETTE' | 'DEPENSE';
  montant: number;
  nombre: number;
  pourcentage: number;
}

/**
 * Évolution mensuelle des transactions
 */
export interface EvolutionTransactionMensuelle {
  mois: string;
  recettes: number;
  depenses: number;
  solde: number;
  nombreTransactions: number;
}

/**
 * Rapport des sponsors
 */
export interface RapportSponsors {
  nombreSponsors: number;
  montantTotalContracts: number;
  montantVerse: number;
  montantRestant: number;
  tauxRealisation: number;
  repartitionParType: RepartitionSponsorType[];
  sponsorsExpirants: SponsorExpirant[];
}

/**
 * Répartition sponsor par type
 */
export interface RepartitionSponsorType {
  type: string;
  nombre: number;
  montant: number;
  pourcentage: number;
}

/**
 * Sponsor expirant
 */
export interface SponsorExpirant {
  id: number;
  nom: string;
  dateFin: string;
  montantRestant: number;
  joursRestants: number;
}

/**
 * Rapport des salaires
 */
export interface RapportSalaires {
  nombreSalaires: number;
  montantTotalBrut: number;
  montantTotalNet: number;
  montantCotisations: number;
  salaireMoyen: number;
  repartitionParStatut: RepartitionSalaireStatut[];
}

/**
 * Répartition salaire par statut
 */
export interface RepartitionSalaireStatut {
  statut: string;
  nombre: number;
  montant: number;
  pourcentage: number;
}

/**
 * Rapport de trésorerie
 */
export interface RapportTresorerie {
  soldeInitial: number;
  totalEntrees: number;
  totalSorties: number;
  soldeFinal: number;
  variationPeriode: number;
  fluxParMois: FluxMensuel[];
  previsionsTresorerie: PrevisionTresorerie[];
}

/**
 * Flux mensuel
 */
export interface FluxMensuel {
  mois: string;
  entrees: number;
  sorties: number;
  solde: number;
  variation: number;
}

/**
 * Prévision de trésorerie
 */
export interface PrevisionTresorerie {
  mois: string;
  entreesPrevisionnelles: number;
  sortiesPrevisionnelles: number;
  soldePrevu: number;
}

/**
 * Indicateurs financiers
 */
export interface IndicateursFinanciers {
  ratioRecettesDepenses: number;
  croissanceRecettes: number;
  croissanceDepenses: number;
  efficaciteBudgetaire: number;
  diversificationRecettes: number;
  stabiliteFinanciere: number;
}

/**
 * Graphiques pour le rapport
 */
export interface GraphiquesRapport {
  evolutionTresorerie: DonneesGraphique;
  repartitionRecettes: DonneesGraphique;
  repartitionDepenses: DonneesGraphique;
  evolutionBudgets: DonneesGraphique;
  performanceSponsors: DonneesGraphique;
}

/**
 * Données pour graphique
 */
export interface DonneesGraphique {
  type: 'line' | 'bar' | 'pie' | 'doughnut';
  labels: string[];
  datasets: DatasetGraphique[];
}

/**
 * Dataset pour graphique
 */
export interface DatasetGraphique {
  label: string;
  data: number[];
  backgroundColor?: string | string[];
  borderColor?: string;
  borderWidth?: number;
}
