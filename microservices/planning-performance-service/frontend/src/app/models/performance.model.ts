// Enums
export enum CategoriePerformance {
  TECHNIQUE = 'TECHNIQUE',
  PHYSIQUE = 'PHYSIQUE',
  TACTIQUE = 'TACTIQUE',
  MENTAL = 'MENTAL'
}

// Labels pour l'affichage
export const CategoriePerformanceLabels: Record<CategoriePerformance, string> = {
  [CategoriePerformance.TECHNIQUE]: 'Technique',
  [CategoriePerformance.PHYSIQUE]: 'Physique',
  [CategoriePerformance.TACTIQUE]: 'Tactique',
  [CategoriePerformance.MENTAL]: 'Mental'
};

// Couleurs pour l'affichage
export const CategoriePerformanceColors: Record<CategoriePerformance, string> = {
  [CategoriePerformance.TECHNIQUE]: '#2196f3',
  [CategoriePerformance.PHYSIQUE]: '#4caf50',
  [CategoriePerformance.TACTIQUE]: '#ff9800',
  [CategoriePerformance.MENTAL]: '#9c27b0'
};

// Icônes pour l'affichage
export const CategoriePerformanceIcons: Record<CategoriePerformance, string> = {
  [CategoriePerformance.TECHNIQUE]: 'sports_volleyball',
  [CategoriePerformance.PHYSIQUE]: 'fitness_center',
  [CategoriePerformance.TACTIQUE]: 'psychology',
  [CategoriePerformance.MENTAL]: 'psychology_alt'
};

// Interface principale
export interface Performance {
  id?: number;
  entrainementId: number;
  joueurId: number;
  categorie: CategoriePerformance;
  note: number; // Sur 10
  commentaire?: string;
  dateEvaluation: string;
  evaluateurId: number;

  // Propriétés calculées côté frontend
  niveau?: 'excellent' | 'bon' | 'moyen' | 'faible';
  progression?: 'up' | 'down' | 'stable';
}

// DTOs pour les opérations CRUD
export interface CreatePerformanceDto {
  entrainementId: number;
  joueurId: number;
  categorie: CategoriePerformance;
  note: number;
  commentaire?: string;
  evaluateurId: number;
}

export interface UpdatePerformanceDto {
  categorie?: CategoriePerformance;
  note?: number;
  commentaire?: string;
  evaluateurId?: number;
}

// Statistiques
export interface PerformanceStats {
  totalEvaluations: number;
  moyenneGenerale: number;
  moyenneParCategorie: { [categorie: string]: number };
  meilleureNote: number;
  plusFaibleNote: number;
  tendanceGenerale: 'up' | 'down' | 'stable';

  // Répartition par niveau
  repartitionNiveaux: {
    excellent: number;
    bon: number;
    moyen: number;
    faible: number;
  };

  // Évolution temporelle
  evolutionMensuelle?: { [mois: string]: number };
  evolutionParCategorie?: { [categorie: string]: { [mois: string]: number } };
}

// Analytics avancées
export interface PerformanceAnalytics {
  joueurId?: number;
  periodeAnalyse: {
    dateDebut: string;
    dateFin: string;
  };

  // Métriques principales
  metriques: {
    moyenneGenerale: number;
    progressionGlobale: number;
    consistance: number; // Écart-type inversé
    pointsForts: CategoriePerformance[];
    pointsFaibles: CategoriePerformance[];
  };

  // Évolution détaillée
  evolution: {
    date: string;
    note: number;
    categorie: CategoriePerformance;
  }[];

  // Comparaison avec l'équipe
  comparaisonEquipe?: {
    positionClassement: number;
    totalJoueurs: number;
    ecartMoyenne: number;
  };

  // Recommandations
  recommandations: string[];
  objectifsSuggeres: {
    categorie: CategoriePerformance;
    objectifActuel: number;
    objectifSuggere: number;
    delai: string;
  }[];
}

export interface PerformanceEvolution {
  date: string;
  noteGlobale: number;
  noteTechnique: number;
  notePhysique: number;
  noteMental: number;
}

export interface PerformanceComparison {
  joueurId: number;
  moyenneJoueur: number;
  moyenneEquipe: number;
  ecart: number;
  classement: number;
}

// Filtres pour la recherche
export interface PerformanceFilters {
  entrainementId?: number;
  joueurId?: number;
  categorie?: CategoriePerformance;
  noteMin?: number;
  noteMax?: number;
  dateDebut?: string;
  dateFin?: string;
  evaluateurId?: number;
  recherche?: string;
}

export interface AutoEvaluationRequest {
  entrainementId: number;
  joueurId: number;
  autoEvaluation: number;
  commentaireJoueur?: string;
  objectifAtteint?: boolean;
}

// Utilitaires pour les performances
export const PerformanceCategories = {
  EXCELLENT: { min: 8, max: 10, label: 'Excellent', color: '#4caf50', class: 'note-excellent' },
  BON: { min: 6, max: 7.99, label: 'Bon', color: '#8bc34a', class: 'note-bon' },
  MOYEN: { min: 4, max: 5.99, label: 'Moyen', color: '#ff9800', class: 'note-moyen' },
  FAIBLE: { min: 0, max: 3.99, label: 'Faible', color: '#f44336', class: 'note-faible' }
};

export function getPerformanceCategory(note: number): keyof typeof PerformanceCategories {
  if (note >= 8) return 'EXCELLENT';
  if (note >= 6) return 'BON';
  if (note >= 4) return 'MOYEN';
  return 'FAIBLE';
}

export function getPerformanceCategoryInfo(note: number) {
  const category = getPerformanceCategory(note);
  return PerformanceCategories[category];
}

export const TendanceLabels: Record<string, string> = {
  'PROGRESSION': 'En progression',
  'STABLE': 'Stable',
  'REGRESSION': 'En régression'
};

export const TendanceColors: Record<string, string> = {
  'PROGRESSION': '#4caf50',
  'STABLE': '#ff9800',
  'REGRESSION': '#f44336'
};

export const TendanceIcons: Record<string, string> = {
  'PROGRESSION': 'trending_up',
  'STABLE': 'trending_flat',
  'REGRESSION': 'trending_down'
};
