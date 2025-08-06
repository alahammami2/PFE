export interface ObjectifIndividuel {
  id?: number;
  joueurId: number;
  coachId: number;
  titre: string;
  description?: string;
  type: TypeObjectif;
  dateEcheance: string; // Format ISO date
  progression?: number; // Pourcentage 0-100
  statut?: StatutObjectif;
  notes?: string;
  dateCreation?: string; // Format ISO datetime
  dateModification?: string; // Format ISO datetime
  
  // Informations calculées
  joursRestants?: number;
  procheEcheance?: boolean; // Si échéance dans moins de 7 jours
  echu?: boolean;
  tendanceProgression?: string; // "RAPIDE", "NORMALE", "LENTE"
}

export enum TypeObjectif {
  TECHNIQUE = 'TECHNIQUE',
  PHYSIQUE = 'PHYSIQUE',
  MENTAL = 'MENTAL',
  TACTIQUE = 'TACTIQUE',
  COMPORTEMENTAL = 'COMPORTEMENTAL'
}

export enum StatutObjectif {
  EN_COURS = 'EN_COURS',
  ATTEINT = 'ATTEINT',
  ABANDONNE = 'ABANDONNE',
  REPORTE = 'REPORTE'
}

export interface ObjectifIndividuelRequest {
  joueurId: number;
  coachId: number;
  titre: string;
  description?: string;
  type: TypeObjectif;
  dateEcheance: string;
  notes?: string;
}

export interface ObjectifProgressionUpdate {
  progression: number;
  notes?: string;
  statut?: StatutObjectif;
}

export interface ObjectifStats {
  totalObjectifs: number;
  objectifsEnCours: number;
  objectifsAtteints: number;
  objectifsAbandonnes: number;
  tauxReussite: number;
  progressionMoyenne: number;
}

export interface ObjectifFilters {
  joueurId?: number;
  coachId?: number;
  type?: TypeObjectif;
  statut?: StatutObjectif;
  procheEcheance?: boolean;
  echu?: boolean;
  progressionMin?: number;
  progressionMax?: number;
}

export interface ObjectifReminder {
  objectifId: number;
  titre: string;
  joueurId: number;
  dateEcheance: string;
  joursRestants: number;
  progression: number;
  priorite: 'HAUTE' | 'MOYENNE' | 'BASSE';
}

export interface ObjectifAnalytics {
  repartitionParType: TypeObjectifCount[];
  evolutionProgression: ObjectifProgressionEvolution[];
  performanceParJoueur: ObjectifPerformanceJoueur[];
  tendances: ObjectifTendance[];
}

export interface TypeObjectifCount {
  type: TypeObjectif;
  count: number;
  pourcentage: number;
  tauxReussite: number;
}

export interface ObjectifProgressionEvolution {
  mois: string;
  progressionMoyenne: number;
  nombreObjectifs: number;
}

export interface ObjectifPerformanceJoueur {
  joueurId: number;
  nombreObjectifs: number;
  objectifsAtteints: number;
  tauxReussite: number;
  progressionMoyenne: number;
}

export interface ObjectifTendance {
  type: TypeObjectif;
  tendance: 'CROISSANTE' | 'STABLE' | 'DECROISSANTE';
  evolution: number; // Pourcentage d'évolution
}

// Utilitaires pour les objectifs
export const TypeObjectifLabels: Record<TypeObjectif, string> = {
  [TypeObjectif.TECHNIQUE]: 'Technique',
  [TypeObjectif.PHYSIQUE]: 'Physique',
  [TypeObjectif.MENTAL]: 'Mental',
  [TypeObjectif.TACTIQUE]: 'Tactique',
  [TypeObjectif.COMPORTEMENTAL]: 'Comportemental'
};

export const StatutObjectifLabels: Record<StatutObjectif, string> = {
  [StatutObjectif.EN_COURS]: 'En cours',
  [StatutObjectif.ATTEINT]: 'Atteint',
  [StatutObjectif.ABANDONNE]: 'Abandonné',
  [StatutObjectif.REPORTE]: 'Reporté'
};

export const TypeObjectifColors: Record<TypeObjectif, string> = {
  [TypeObjectif.TECHNIQUE]: '#9c27b0',
  [TypeObjectif.PHYSIQUE]: '#e91e63',
  [TypeObjectif.MENTAL]: '#673ab7',
  [TypeObjectif.TACTIQUE]: '#3f51b5',
  [TypeObjectif.COMPORTEMENTAL]: '#2196f3'
};

export const StatutObjectifColors: Record<StatutObjectif, string> = {
  [StatutObjectif.EN_COURS]: '#ff9800',
  [StatutObjectif.ATTEINT]: '#4caf50',
  [StatutObjectif.ABANDONNE]: '#f44336',
  [StatutObjectif.REPORTE]: '#607d8b'
};

export const TypeObjectifIcons: Record<TypeObjectif, string> = {
  [TypeObjectif.TECHNIQUE]: 'build',
  [TypeObjectif.PHYSIQUE]: 'fitness_center',
  [TypeObjectif.MENTAL]: 'psychology',
  [TypeObjectif.TACTIQUE]: 'strategy',
  [TypeObjectif.COMPORTEMENTAL]: 'emoji_people'
};

export const StatutObjectifIcons: Record<StatutObjectif, string> = {
  [StatutObjectif.EN_COURS]: 'hourglass_empty',
  [StatutObjectif.ATTEINT]: 'check_circle',
  [StatutObjectif.ABANDONNE]: 'cancel',
  [StatutObjectif.REPORTE]: 'schedule'
};

export function getProgressionColor(progression: number): string {
  if (progression >= 80) return '#4caf50';
  if (progression >= 60) return '#8bc34a';
  if (progression >= 40) return '#ff9800';
  if (progression >= 20) return '#ff5722';
  return '#f44336';
}

export function getProgressionLabel(progression: number): string {
  if (progression >= 80) return 'Excellent';
  if (progression >= 60) return 'Bon';
  if (progression >= 40) return 'Moyen';
  if (progression >= 20) return 'Faible';
  return 'Très faible';
}

export function getTendanceProgressionColor(tendance: string): string {
  switch (tendance) {
    case 'RAPIDE': return '#4caf50';
    case 'NORMALE': return '#ff9800';
    case 'LENTE': return '#f44336';
    default: return '#607d8b';
  }
}

export function getTendanceProgressionLabel(tendance: string): string {
  switch (tendance) {
    case 'RAPIDE': return 'Progression rapide';
    case 'NORMALE': return 'Progression normale';
    case 'LENTE': return 'Progression lente';
    default: return 'Indéterminée';
  }
}
