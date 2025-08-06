// Enums
export enum TypeObjectif {
  TECHNIQUE = 'TECHNIQUE',
  PHYSIQUE = 'PHYSIQUE',
  TACTIQUE = 'TACTIQUE',
  MENTAL = 'MENTAL',
  PERSONNEL = 'PERSONNEL'
}

export enum StatutObjectif {
  EN_COURS = 'EN_COURS',
  ATTEINT = 'ATTEINT',
  ECHOUE = 'ECHOUE',
  REPORTE = 'REPORTE'
}

export enum PrioriteObjectif {
  HAUTE = 'HAUTE',
  MOYENNE = 'MOYENNE',
  BASSE = 'BASSE'
}

// Labels pour l'affichage
export const TypeObjectifLabels: Record<TypeObjectif, string> = {
  [TypeObjectif.TECHNIQUE]: 'Technique',
  [TypeObjectif.PHYSIQUE]: 'Physique',
  [TypeObjectif.TACTIQUE]: 'Tactique',
  [TypeObjectif.MENTAL]: 'Mental',
  [TypeObjectif.PERSONNEL]: 'Personnel'
};

export const StatutObjectifLabels: Record<StatutObjectif, string> = {
  [StatutObjectif.EN_COURS]: 'En cours',
  [StatutObjectif.ATTEINT]: 'Atteint',
  [StatutObjectif.ECHOUE]: 'Échoué',
  [StatutObjectif.REPORTE]: 'Reporté'
};

export const PrioriteObjectifLabels: Record<PrioriteObjectif, string> = {
  [PrioriteObjectif.HAUTE]: 'Haute',
  [PrioriteObjectif.MOYENNE]: 'Moyenne',
  [PrioriteObjectif.BASSE]: 'Basse'
};

// Couleurs pour l'affichage
export const TypeObjectifColors: Record<TypeObjectif, string> = {
  [TypeObjectif.TECHNIQUE]: '#2196f3',
  [TypeObjectif.PHYSIQUE]: '#4caf50',
  [TypeObjectif.TACTIQUE]: '#ff9800',
  [TypeObjectif.MENTAL]: '#9c27b0',
  [TypeObjectif.PERSONNEL]: '#607d8b'
};

export const StatutObjectifColors: Record<StatutObjectif, string> = {
  [StatutObjectif.EN_COURS]: '#2196f3',
  [StatutObjectif.ATTEINT]: '#4caf50',
  [StatutObjectif.ECHOUE]: '#f44336',
  [StatutObjectif.REPORTE]: '#ff9800'
};

export const PrioriteObjectifColors: Record<PrioriteObjectif, string> = {
  [PrioriteObjectif.HAUTE]: '#f44336',
  [PrioriteObjectif.MOYENNE]: '#ff9800',
  [PrioriteObjectif.BASSE]: '#4caf50'
};

// Icônes pour l'affichage
export const TypeObjectifIcons: Record<TypeObjectif, string> = {
  [TypeObjectif.TECHNIQUE]: 'sports_volleyball',
  [TypeObjectif.PHYSIQUE]: 'fitness_center',
  [TypeObjectif.TACTIQUE]: 'psychology',
  [TypeObjectif.MENTAL]: 'psychology',
  [TypeObjectif.PERSONNEL]: 'person'
};

export const StatutObjectifIcons: Record<StatutObjectif, string> = {
  [StatutObjectif.EN_COURS]: 'play_arrow',
  [StatutObjectif.ATTEINT]: 'check_circle',
  [StatutObjectif.ECHOUE]: 'cancel',
  [StatutObjectif.REPORTE]: 'schedule'
};

export const PrioriteObjectifIcons: Record<PrioriteObjectif, string> = {
  [PrioriteObjectif.HAUTE]: 'priority_high',
  [PrioriteObjectif.MOYENNE]: 'remove',
  [PrioriteObjectif.BASSE]: 'keyboard_arrow_down'
};

// Interface principale
export interface ObjectifIndividuel {
  id?: number;
  joueurId: number;
  titre: string;
  description?: string;
  type: TypeObjectif;
  priorite: PrioriteObjectif;
  dateDebut: string;
  dateFin: string;
  statut: StatutObjectif;
  progression: number; // Pourcentage de 0 à 100
  
  // Critères de mesure
  criteresMesure?: string;
  valeurCible?: number;
  valeurActuelle?: number;
  uniteMesure?: string;
  
  // Suivi et commentaires
  commentaires?: string;
  derniereModification?: string;
  modifiePar?: number; // ID de l'utilisateur qui a modifié
  
  // Historique des progressions
  historiqueProgression?: ProgressionEntry[];
  
  // Propriétés calculées côté frontend
  joursRestants?: number;
  dureeTotal?: number;
  enRetard?: boolean;
  aRisque?: boolean;
  progressionAttendue?: number;
}

// Interface pour l'historique des progressions
export interface ProgressionEntry {
  date: string;
  progression: number;
  commentaire?: string;
  modifiePar: number;
}

// DTOs pour les opérations CRUD
export interface CreateObjectifDto {
  joueurId: number;
  titre: string;
  description?: string;
  type: TypeObjectif;
  priorite: PrioriteObjectif;
  dateDebut: string;
  dateFin: string;
  criteresMesure?: string;
  valeurCible?: number;
  uniteMesure?: string;
}

export interface UpdateObjectifDto {
  titre?: string;
  description?: string;
  type?: TypeObjectif;
  priorite?: PrioriteObjectif;
  dateDebut?: string;
  dateFin?: string;
  progression?: number;
  criteresMesure?: string;
  valeurCible?: number;
  valeurActuelle?: number;
  uniteMesure?: string;
  commentaires?: string;
}

// Filtres pour la recherche
export interface ObjectifFilters {
  joueurId?: number;
  type?: TypeObjectif;
  statut?: StatutObjectif;
  priorite?: PrioriteObjectif;
  dateDebutMin?: string;
  dateDebutMax?: string;
  dateFinMin?: string;
  dateFinMax?: string;
  progressionMin?: number;
  progressionMax?: number;
  enRetard?: boolean;
  aRisque?: boolean;
  recherche?: string;
}

// Statistiques
export interface ObjectifStats {
  totalObjectifs: number;
  objectifsAtteints: number;
  objectifsEnCours: number;
  objectifsEchoues: number;
  objectifsReportes: number;
  tauxReussite: number;
  progressionMoyenne: number;
  
  // Répartition par type
  repartitionParType: { [type: string]: number };
  
  // Répartition par priorité
  repartitionParPriorite: { [priorite: string]: number };
  
  // Évolution temporelle
  evolutionMensuelle?: { [mois: string]: { total: number; atteints: number; tauxReussite: number } };
  
  // Comparaison avec la période précédente
  comparaisonPrecedente?: {
    evolutionTotal: number;
    evolutionTauxReussite: number;
    tendance: 'amelioration' | 'degradation' | 'stable';
  };
  
  // Objectifs par joueur
  objectifsParJoueur?: {
    joueurId: number;
    totalObjectifs: number;
    objectifsAtteints: number;
    tauxReussite: number;
    progressionMoyenne: number;
  }[];
}

// Interface pour les recommandations
export interface RecommandationObjectif {
  type: string;
  description: string;
  priorite: 'haute' | 'moyenne' | 'basse';
  actionSuggere: string;
  delaiEstime: number; // en jours
}

// Interface pour les templates d'objectifs
export interface TemplateObjectif {
  id?: number;
  type: TypeObjectif;
  titre: string;
  description: string;
  dureeEstimee: number; // en jours
  criteresMesure: string;
  prioriteRecommandee: PrioriteObjectif;
  tagsAssocies: string[];
}

// Interface pour le suivi de progression
export interface SuiviProgression {
  objectifId: number;
  date: string;
  progressionPrecedente: number;
  nouvelleProgression: number;
  commentaire?: string;
  preuves?: string[]; // URLs des fichiers de preuve
  evaluateurId?: number;
}

// Interface pour les alertes d'objectifs
export interface AlerteObjectif {
  objectifId: number;
  type: 'retard' | 'risque' | 'echeance_proche' | 'stagnation';
  severite: 'low' | 'medium' | 'high' | 'urgent';
  message: string;
  actionRecommandee: string;
  dateDetection: string;
}

// Interface pour les rapports d'objectifs
export interface RapportObjectif {
  periode: {
    dateDebut: string;
    dateFin: string;
  };
  statistiques: ObjectifStats;
  detailsParJoueur: {
    joueurId: number;
    objectifs: ObjectifIndividuel[];
    statistiques: {
      totalObjectifs: number;
      objectifsAtteints: number;
      tauxReussite: number;
      progressionMoyenne: number;
    };
    recommandations: RecommandationObjectif[];
  }[];
  tendances: {
    date: string;
    nombreObjectifs: number;
    tauxReussite: number;
  }[];
  alertes: AlerteObjectif[];
}

// Utilitaires pour les objectifs
export class ObjectifUtils {
  static getTypeLabel(type: TypeObjectif): string {
    return TypeObjectifLabels[type];
  }

  static getTypeColor(type: TypeObjectif): string {
    return TypeObjectifColors[type];
  }

  static getTypeIcon(type: TypeObjectif): string {
    return TypeObjectifIcons[type];
  }

  static getStatutLabel(statut: StatutObjectif): string {
    return StatutObjectifLabels[statut];
  }

  static getStatutColor(statut: StatutObjectif): string {
    return StatutObjectifColors[statut];
  }

  static getStatutIcon(statut: StatutObjectif): string {
    return StatutObjectifIcons[statut];
  }

  static getPrioriteLabel(priorite: PrioriteObjectif): string {
    return PrioriteObjectifLabels[priorite];
  }

  static getPrioriteColor(priorite: PrioriteObjectif): string {
    return PrioriteObjectifColors[priorite];
  }

  static getPrioriteIcon(priorite: PrioriteObjectif): string {
    return PrioriteObjectifIcons[priorite];
  }

  static calculateProgression(objectif: ObjectifIndividuel): number {
    if (objectif.statut === StatutObjectif.ATTEINT) return 100;
    if (objectif.statut === StatutObjectif.ECHOUE) return objectif.progression || 0;
    
    // Calcul basé sur le temps écoulé et la progression manuelle
    const maintenant = new Date();
    const debut = new Date(objectif.dateDebut);
    const fin = new Date(objectif.dateFin);
    
    const dureeTotal = fin.getTime() - debut.getTime();
    const dureeEcoulee = maintenant.getTime() - debut.getTime();
    
    const progressionTemporelle = Math.min(100, Math.max(0, (dureeEcoulee / dureeTotal) * 100));
    
    // Moyenne entre progression temporelle et progression manuelle
    return Math.round((progressionTemporelle + (objectif.progression || 0)) / 2);
  }

  static isObjectifEnRetard(objectif: ObjectifIndividuel): boolean {
    const maintenant = new Date();
    const fin = new Date(objectif.dateFin);
    const progressionAttendue = this.calculateProgression(objectif);
    
    return maintenant > fin && objectif.statut === StatutObjectif.EN_COURS && 
           (objectif.progression || 0) < progressionAttendue;
  }

  static isObjectifARisque(objectif: ObjectifIndividuel): boolean {
    if (objectif.statut !== StatutObjectif.EN_COURS) return false;
    
    const maintenant = new Date();
    const debut = new Date(objectif.dateDebut);
    const fin = new Date(objectif.dateFin);
    
    const dureeTotal = fin.getTime() - debut.getTime();
    const dureeEcoulee = maintenant.getTime() - debut.getTime();
    const progressionTemporelle = (dureeEcoulee / dureeTotal) * 100;
    
    // Objectif à risque si la progression réelle est inférieure de 20% à la progression temporelle
    return (objectif.progression || 0) < (progressionTemporelle - 20);
  }

  static getJoursRestants(objectif: ObjectifIndividuel): number {
    const maintenant = new Date();
    const fin = new Date(objectif.dateFin);
    const diffTime = fin.getTime() - maintenant.getTime();
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  }

  static getDureeObjectif(objectif: ObjectifIndividuel): number {
    const debut = new Date(objectif.dateDebut);
    const fin = new Date(objectif.dateFin);
    const diffTime = fin.getTime() - debut.getTime();
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  }

  static getProgressionColor(progression: number): string {
    if (progression >= 80) return '#4caf50';
    if (progression >= 60) return '#8bc34a';
    if (progression >= 40) return '#ff9800';
    if (progression >= 20) return '#ff5722';
    return '#f44336';
  }

  static getObjectifPriorityLevel(objectif: ObjectifIndividuel): 'urgent' | 'high' | 'medium' | 'low' {
    if (this.isObjectifEnRetard(objectif)) return 'urgent';
    if (objectif.priorite === PrioriteObjectif.HAUTE) return 'high';
    if (this.isObjectifARisque(objectif)) return 'high';
    if (objectif.priorite === PrioriteObjectif.MOYENNE) return 'medium';
    return 'low';
  }

  static formatDateObjectif(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  }

  static formatDateComplete(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  static validateObjectif(objectif: CreateObjectifDto | UpdateObjectifDto): string[] {
    const errors: string[] = [];

    if ('joueurId' in objectif) {
      if (!objectif.joueurId || objectif.joueurId <= 0) {
        errors.push('L\'ID du joueur est requis');
      }
    }

    if ('titre' in objectif && objectif.titre) {
      if (objectif.titre.length < 5) {
        errors.push('Le titre doit contenir au moins 5 caractères');
      }
      if (objectif.titre.length > 100) {
        errors.push('Le titre ne peut pas dépasser 100 caractères');
      }
    }

    if ('description' in objectif && objectif.description) {
      if (objectif.description.length > 1000) {
        errors.push('La description ne peut pas dépasser 1000 caractères');
      }
    }

    if ('dateDebut' in objectif && 'dateFin' in objectif && objectif.dateDebut && objectif.dateFin) {
      const dateDebut = new Date(objectif.dateDebut);
      const dateFin = new Date(objectif.dateFin);
      
      if (dateFin <= dateDebut) {
        errors.push('La date de fin doit être postérieure à la date de début');
      }
    }

    if ('progression' in objectif && objectif.progression !== undefined) {
      if (objectif.progression < 0 || objectif.progression > 100) {
        errors.push('La progression doit être comprise entre 0 et 100');
      }
    }

    if ('valeurCible' in objectif && 'valeurActuelle' in objectif && 
        objectif.valeurCible !== undefined && objectif.valeurActuelle !== undefined) {
      if (objectif.valeurActuelle > objectif.valeurCible) {
        errors.push('La valeur actuelle ne peut pas être supérieure à la valeur cible');
      }
    }

    return errors;
  }

  static generateRecommandations(objectif: ObjectifIndividuel): RecommandationObjectif[] {
    const recommandations: RecommandationObjectif[] = [];
    
    if (this.isObjectifEnRetard(objectif)) {
      recommandations.push({
        type: 'retard',
        description: 'Cet objectif est en retard sur le planning prévu',
        priorite: 'haute',
        actionSuggere: 'Revoir la planification ou intensifier les efforts',
        delaiEstime: 7
      });
    }
    
    if (this.isObjectifARisque(objectif)) {
      recommandations.push({
        type: 'risque',
        description: 'La progression de cet objectif est insuffisante',
        priorite: 'moyenne',
        actionSuggere: 'Analyser les obstacles et ajuster la stratégie',
        delaiEstime: 14
      });
    }
    
    if (objectif.progression === 0 && this.getJoursRestants(objectif) < 30) {
      recommandations.push({
        type: 'stagnation',
        description: 'Aucune progression enregistrée avec échéance proche',
        priorite: 'haute',
        actionSuggere: 'Démarrer immédiatement le travail sur cet objectif',
        delaiEstime: 3
      });
    }
    
    return recommandations;
  }

  static groupObjectifsByType(objectifs: ObjectifIndividuel[]): { [type: string]: ObjectifIndividuel[] } {
    return objectifs.reduce((acc, objectif) => {
      if (!acc[objectif.type]) {
        acc[objectif.type] = [];
      }
      acc[objectif.type].push(objectif);
      return acc;
    }, {} as { [type: string]: ObjectifIndividuel[] });
  }

  static groupObjectifsByStatut(objectifs: ObjectifIndividuel[]): { [statut: string]: ObjectifIndividuel[] } {
    return objectifs.reduce((acc, objectif) => {
      if (!acc[objectif.statut]) {
        acc[objectif.statut] = [];
      }
      acc[objectif.statut].push(objectif);
      return acc;
    }, {} as { [statut: string]: ObjectifIndividuel[] });
  }

  static calculateTauxReussite(objectifs: ObjectifIndividuel[]): number {
    if (objectifs.length === 0) return 0;
    const objectifsAtteints = objectifs.filter(o => o.statut === StatutObjectif.ATTEINT).length;
    return Math.round((objectifsAtteints / objectifs.length) * 100);
  }

  static getObjectifDescription(objectif: ObjectifIndividuel): string {
    const typeLabel = this.getTypeLabel(objectif.type);
    const prioriteLabel = this.getPrioriteLabel(objectif.priorite);
    const joursRestants = this.getJoursRestants(objectif);
    
    return `${typeLabel} - ${prioriteLabel} - ${joursRestants} jours restants`;
  }
}
