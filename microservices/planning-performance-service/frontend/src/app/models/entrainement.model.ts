export interface Entrainement {
  id?: number;
  titre: string;
  description?: string;
  date: string; // Format ISO date
  heureDebut: string; // Format HH:mm
  heureFin: string; // Format HH:mm
  type: TypeEntrainement;
  lieu: string;
  coachId: number;
  nombreMaxParticipants: number;
  statut?: StatutEntrainement;
  notes?: string;
  
  // Informations calculées (lecture seule)
  nombreInscrits?: number;
  nombrePresents?: number;
  nombreAbsents?: number;
  placesDisponibles?: boolean;
}

export enum TypeEntrainement {
  PHYSIQUE = 'PHYSIQUE',
  TECHNIQUE = 'TECHNIQUE',
  TACTIQUE = 'TACTIQUE',
  MATCH = 'MATCH'
}

export enum StatutEntrainement {
  PLANIFIE = 'PLANIFIE',
  EN_COURS = 'EN_COURS',
  TERMINE = 'TERMINE',
  ANNULE = 'ANNULE'
}

export interface EntrainementRequest {
  titre: string;
  description?: string;
  date: string;
  heureDebut: string;
  heureFin: string;
  type: TypeEntrainement;
  lieu: string;
  coachId: number;
  nombreMaxParticipants: number;
  notes?: string;
}

export interface EntrainementFilters {
  coachId?: number;
  type?: TypeEntrainement;
  statut?: StatutEntrainement;
  dateDebut?: string;
  dateFin?: string;
  lieu?: string;
}

export interface EntrainementStats {
  totalEntrainements: number;
  entrainementsTermines: number;
  entrainementsPlanifies: number;
  entrainementsAnnules: number;
  tauxParticipation: number;
  moyenneParticipants: number;
}

// Utilitaires pour les types
export const TypeEntrainementLabels: Record<TypeEntrainement, string> = {
  [TypeEntrainement.PHYSIQUE]: 'Physique',
  [TypeEntrainement.TECHNIQUE]: 'Technique',
  [TypeEntrainement.TACTIQUE]: 'Tactique',
  [TypeEntrainement.MATCH]: 'Match'
};

export const StatutEntrainementLabels: Record<StatutEntrainement, string> = {
  [StatutEntrainement.PLANIFIE]: 'Planifié',
  [StatutEntrainement.EN_COURS]: 'En cours',
  [StatutEntrainement.TERMINE]: 'Terminé',
  [StatutEntrainement.ANNULE]: 'Annulé'
};

export const TypeEntrainementColors: Record<TypeEntrainement, string> = {
  [TypeEntrainement.PHYSIQUE]: '#e91e63',
  [TypeEntrainement.TECHNIQUE]: '#9c27b0',
  [TypeEntrainement.TACTIQUE]: '#673ab7',
  [TypeEntrainement.MATCH]: '#ff5722'
};

export const StatutEntrainementColors: Record<StatutEntrainement, string> = {
  [StatutEntrainement.PLANIFIE]: '#2196f3',
  [StatutEntrainement.EN_COURS]: '#ff9800',
  [StatutEntrainement.TERMINE]: '#4caf50',
  [StatutEntrainement.ANNULE]: '#f44336'
};
