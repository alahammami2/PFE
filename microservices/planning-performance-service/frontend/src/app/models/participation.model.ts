export interface Participation {
  id?: number;
  entrainementId: number;
  joueurId: number;
  statut: StatutParticipation;
  heureInscription: string;
  heurePresence?: string;
  commentaire?: string;

  // Propriétés calculées côté frontend
  delaiInscription?: string;
  peutModifier?: boolean;
}

export enum StatutParticipation {
  INSCRIT = 'INSCRIT',
  PRESENT = 'PRESENT',
  ABSENT = 'ABSENT',
  EXCUSE = 'EXCUSE'
}

// DTOs pour les opérations CRUD
export interface CreateParticipationDto {
  entrainementId: number;
  joueurId: number;
  commentaire?: string;
}

export interface UpdateParticipationDto {
  statut?: StatutParticipation;
  commentaire?: string;
  heurePresence?: string;
}

export interface ParticipationStats {
  totalParticipations: number;
  nombrePresences: number;
  nombreAbsences: number;
  nombreExcuses: number;
  tauxPresence: number;
  tauxAbsence: number;
}

export interface ParticipationFilters {
  joueurId?: number;
  entrainementId?: number;
  statut?: StatutParticipation;
  dateDebut?: string;
  dateFin?: string;
}

export interface ParticipationBatch {
  entrainementId: number;
  joueurIds: number[];
  commentaire?: string;
}

// Utilitaires pour les types
export const StatutParticipationLabels: Record<StatutParticipation, string> = {
  [StatutParticipation.INSCRIT]: 'Inscrit',
  [StatutParticipation.PRESENT]: 'Présent',
  [StatutParticipation.ABSENT]: 'Absent',
  [StatutParticipation.EXCUSE]: 'Excusé'
};

export const StatutParticipationColors: Record<StatutParticipation, string> = {
  [StatutParticipation.INSCRIT]: '#2196f3',
  [StatutParticipation.PRESENT]: '#4caf50',
  [StatutParticipation.ABSENT]: '#f44336',
  [StatutParticipation.EXCUSE]: '#ff9800'
};

export const StatutParticipationIcons: Record<StatutParticipation, string> = {
  [StatutParticipation.INSCRIT]: 'person_add',
  [StatutParticipation.PRESENT]: 'check_circle',
  [StatutParticipation.ABSENT]: 'cancel',
  [StatutParticipation.EXCUSE]: 'info'
};
