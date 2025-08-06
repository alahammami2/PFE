// Enums
export enum TypeAbsence {
  MALADIE = 'MALADIE',
  BLESSURE = 'BLESSURE',
  VACANCES = 'VACANCES',
  TRAVAIL = 'TRAVAIL',
  URGENCE_FAMILIALE = 'URGENCE_FAMILIALE',
  DERNIERE_MINUTE = 'DERNIERE_MINUTE',
  AUTRE = 'AUTRE'
}

export enum StatutAbsence {
  EN_ATTENTE = 'EN_ATTENTE',
  APPROUVEE = 'APPROUVEE',
  REJETEE = 'REJETEE',
  TRAITEE = 'TRAITEE'
}

// Labels pour l'affichage
export const TypeAbsenceLabels: Record<TypeAbsence, string> = {
  [TypeAbsence.MALADIE]: 'Maladie',
  [TypeAbsence.BLESSURE]: 'Blessure',
  [TypeAbsence.VACANCES]: 'Vacances',
  [TypeAbsence.TRAVAIL]: 'Travail',
  [TypeAbsence.URGENCE_FAMILIALE]: 'Urgence familiale',
  [TypeAbsence.DERNIERE_MINUTE]: 'Dernière minute',
  [TypeAbsence.AUTRE]: 'Autre'
};

export const StatutAbsenceLabels: Record<StatutAbsence, string> = {
  [StatutAbsence.EN_ATTENTE]: 'En attente',
  [StatutAbsence.APPROUVEE]: 'Approuvée',
  [StatutAbsence.REJETEE]: 'Rejetée',
  [StatutAbsence.TRAITEE]: 'Traitée'
};

// Couleurs pour l'affichage
export const TypeAbsenceColors: Record<TypeAbsence, string> = {
  [TypeAbsence.MALADIE]: '#f44336',
  [TypeAbsence.BLESSURE]: '#ff5722',
  [TypeAbsence.VACANCES]: '#4caf50',
  [TypeAbsence.TRAVAIL]: '#2196f3',
  [TypeAbsence.URGENCE_FAMILIALE]: '#ff9800',
  [TypeAbsence.DERNIERE_MINUTE]: '#9c27b0',
  [TypeAbsence.AUTRE]: '#607d8b'
};

export const StatutAbsenceColors: Record<StatutAbsence, string> = {
  [StatutAbsence.EN_ATTENTE]: '#ff9800',
  [StatutAbsence.APPROUVEE]: '#4caf50',
  [StatutAbsence.REJETEE]: '#f44336',
  [StatutAbsence.TRAITEE]: '#2196f3'
};

// Icônes pour l'affichage
export const TypeAbsenceIcons: Record<TypeAbsence, string> = {
  [TypeAbsence.MALADIE]: 'sick',
  [TypeAbsence.BLESSURE]: 'healing',
  [TypeAbsence.VACANCES]: 'beach_access',
  [TypeAbsence.TRAVAIL]: 'work',
  [TypeAbsence.URGENCE_FAMILIALE]: 'family_restroom',
  [TypeAbsence.DERNIERE_MINUTE]: 'schedule',
  [TypeAbsence.AUTRE]: 'help_outline'
};

export const StatutAbsenceIcons: Record<StatutAbsence, string> = {
  [StatutAbsence.EN_ATTENTE]: 'schedule',
  [StatutAbsence.APPROUVEE]: 'check_circle',
  [StatutAbsence.REJETEE]: 'cancel',
  [StatutAbsence.TRAITEE]: 'done_all'
};

// Interface principale
export interface Absence {
  id?: number;
  entrainementId: number;
  joueurId: number;
  type: TypeAbsence;
  motif: string;
  dateAbsence: string;
  dateDeclaration: string;
  statut: StatutAbsence;
  justifiee: boolean;

  // Justificatifs
  justificatifUrl?: string;
  justificatifNom?: string;

  // Commentaires et approbation
  commentaireApprobation?: string;
  commentaireRejet?: string;
  dateTraitement?: string;
  traitePar?: number; // ID de l'utilisateur qui a traité

  // Propriétés calculées côté frontend
  priorite?: 'high' | 'medium' | 'low';
  modifiable?: boolean;
  peutEtreApprouvee?: boolean;
}

// DTOs pour les opérations CRUD
export interface CreateAbsenceDto {
  entrainementId: number;
  joueurId: number;
  type: TypeAbsence;
  motif: string;
  dateAbsence: string;
  justifiee?: boolean;
}

export interface UpdateAbsenceDto {
  type?: TypeAbsence;
  motif?: string;
  dateAbsence?: string;
  justifiee?: boolean;
}

export interface AbsenceStats {
  totalAbsences: number;
  absencesJustifiees: number;
  absencesNonJustifiees: number;
  tauxAbsence: number;
  motifsPrincipaux: MotifAbsenceCount[];
  absencesTardives: number;
}

export interface MotifAbsenceCount {
  motif: MotifAbsence;
  count: number;
  pourcentage: number;
}

// Filtres pour la recherche
export interface AbsenceFilters {
  joueurId?: number;
  entrainementId?: number;
  type?: TypeAbsence;
  statut?: StatutAbsence;
  dateDebut?: string;
  dateFin?: string;
  justifiee?: boolean;
  traitePar?: number;
  recherche?: string;
}

export interface AbsencePattern {
  joueurId: number;
  nombreAbsences: number;
  absencesConsecutives: number;
  motifPrincipal: MotifAbsence;
  tendance: 'CROISSANTE' | 'STABLE' | 'DECROISSANTE';
  risque: 'FAIBLE' | 'MOYEN' | 'ELEVE';
}

export interface AbsenceJustification {
  absenceId: number;
  justifiee: boolean;
  justificatifUrl?: string;
  commentaire?: string;
}

// Utilitaires pour les absences
export const MotifAbsenceLabels: Record<MotifAbsence, string> = {
  [MotifAbsence.MALADIE]: 'Maladie',
  [MotifAbsence.BLESSURE]: 'Blessure',
  [MotifAbsence.TRAVAIL]: 'Travail',
  [MotifAbsence.ETUDES]: 'Études',
  [MotifAbsence.FAMILLE]: 'Famille',
  [MotifAbsence.TRANSPORT]: 'Transport',
  [MotifAbsence.AUTRE]: 'Autre'
};

export const MotifAbsenceColors: Record<MotifAbsence, string> = {
  [MotifAbsence.MALADIE]: '#f44336',
  [MotifAbsence.BLESSURE]: '#e91e63',
  [MotifAbsence.TRAVAIL]: '#9c27b0',
  [MotifAbsence.ETUDES]: '#673ab7',
  [MotifAbsence.FAMILLE]: '#3f51b5',
  [MotifAbsence.TRANSPORT]: '#2196f3',
  [MotifAbsence.AUTRE]: '#607d8b'
};

export const MotifAbsenceIcons: Record<MotifAbsence, string> = {
  [MotifAbsence.MALADIE]: 'local_hospital',
  [MotifAbsence.BLESSURE]: 'healing',
  [MotifAbsence.TRAVAIL]: 'work',
  [MotifAbsence.ETUDES]: 'school',
  [MotifAbsence.FAMILLE]: 'family_restroom',
  [MotifAbsence.TRANSPORT]: 'directions_car',
  [MotifAbsence.AUTRE]: 'help_outline'
};

export function getAbsenceRisqueColor(risque: string): string {
  switch (risque) {
    case 'FAIBLE': return '#4caf50';
    case 'MOYEN': return '#ff9800';
    case 'ELEVE': return '#f44336';
    default: return '#607d8b';
  }
}

export function getAbsenceRisqueLabel(risque: string): string {
  switch (risque) {
    case 'FAIBLE': return 'Risque faible';
    case 'MOYEN': return 'Risque moyen';
    case 'ELEVE': return 'Risque élevé';
    default: return 'Indéterminé';
  }
}
