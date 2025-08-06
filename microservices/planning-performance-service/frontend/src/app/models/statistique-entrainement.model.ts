export interface StatistiqueEntrainement {
  id?: number;
  joueurId: number;
  mois: number; // 1-12
  annee: number;
  nombreEntrainements: number;
  nombrePresences: number;
  nombreAbsences: number;
  tauxPresence: number; // Pourcentage
  performanceMoyenne?: number;
  noteTechniqueMoyenne?: number;
  notePhysiqueMoyenne?: number;
  noteMentaleMoyenne?: number;
  nombreObjectifsAtteints: number;
  dateCalcul?: string; // Format ISO datetime
  
  // Informations calculées pour l'affichage
  nomMois?: string;
  evolutionPerformance?: number; // Par rapport au mois précédent
  evolutionPresence?: number; // Par rapport au mois précédent
  tendance?: string; // "PROGRESSION", "STABLE", "REGRESSION"
  profil?: string; // "EXCELLENT", "BON", "MOYEN", "FAIBLE"
  
  // Comparaison avec l'équipe
  performanceMoyenneEquipe?: number;
  tauxPresenceMoyenEquipe?: number;
  classementPerformance?: number;
  classementPresence?: number;
}

export interface StatistiqueRequest {
  joueurId: number;
  mois: number;
  annee: number;
}

export interface StatistiqueFilters {
  joueurId?: number;
  mois?: number;
  annee?: number;
  profilMin?: string;
  tauxPresenceMin?: number;
  performanceMin?: number;
}

export interface StatistiqueComparison {
  joueurId: number;
  statistiques: StatistiqueEntrainement[];
  moyenneGlobale: number;
  tendanceGlobale: string;
  classementGeneral: number;
}

export interface StatistiqueEvolution {
  mois: string;
  tauxPresence: number;
  performanceMoyenne: number;
  nombreEntrainements: number;
  objectifsAtteints: number;
}

export interface StatistiqueEquipe {
  mois: number;
  annee: number;
  nombreJoueurs: number;
  tauxPresenceMoyen: number;
  performanceMoyenne: number;
  nombreEntrainements: number;
  meilleurJoueur: StatistiqueJoueurResume;
  joueurAmeliorer: StatistiqueJoueurResume;
}

export interface StatistiqueJoueurResume {
  joueurId: number;
  tauxPresence: number;
  performanceMoyenne: number;
  classement: number;
}

export interface StatistiqueAnalytics {
  evolutionMensuelle: StatistiqueEvolution[];
  repartitionProfils: ProfilCount[];
  correlationPresencePerformance: number;
  tendancesEquipe: TendanceEquipe[];
  alertes: AlerteStatistique[];
}

export interface ProfilCount {
  profil: string;
  count: number;
  pourcentage: number;
}

export interface TendanceEquipe {
  indicateur: string;
  valeurActuelle: number;
  valeurPrecedente: number;
  evolution: number;
  tendance: 'CROISSANTE' | 'STABLE' | 'DECROISSANTE';
}

export interface AlerteStatistique {
  type: 'ABSENCE_ELEVEE' | 'PERFORMANCE_FAIBLE' | 'REGRESSION' | 'OBJECTIFS_NON_ATTEINTS';
  joueurId: number;
  message: string;
  severite: 'INFO' | 'WARNING' | 'DANGER';
  valeur: number;
  seuil: number;
}

export interface RapportMensuel {
  mois: number;
  annee: number;
  statistiquesEquipe: StatistiqueEquipe;
  statistiquesJoueurs: StatistiqueEntrainement[];
  analytics: StatistiqueAnalytics;
  recommandations: string[];
}

// Utilitaires pour les statistiques
export const ProfilLabels: Record<string, string> = {
  'EXCELLENT': 'Excellent',
  'BON': 'Bon',
  'MOYEN': 'Moyen',
  'FAIBLE': 'Faible',
  'INDETERMINE': 'Indéterminé'
};

export const ProfilColors: Record<string, string> = {
  'EXCELLENT': '#4caf50',
  'BON': '#8bc34a',
  'MOYEN': '#ff9800',
  'FAIBLE': '#f44336',
  'INDETERMINE': '#607d8b'
};

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

export const AlerteColors: Record<string, string> = {
  'INFO': '#2196f3',
  'WARNING': '#ff9800',
  'DANGER': '#f44336'
};

export const AlerteIcons: Record<string, string> = {
  'INFO': 'info',
  'WARNING': 'warning',
  'DANGER': 'error'
};

export const MoisLabels: Record<number, string> = {
  1: 'Janvier',
  2: 'Février',
  3: 'Mars',
  4: 'Avril',
  5: 'Mai',
  6: 'Juin',
  7: 'Juillet',
  8: 'Août',
  9: 'Septembre',
  10: 'Octobre',
  11: 'Novembre',
  12: 'Décembre'
};

export function getProfilInfo(profil: string) {
  return {
    label: ProfilLabels[profil] || 'Inconnu',
    color: ProfilColors[profil] || '#607d8b'
  };
}

export function getTendanceInfo(tendance: string) {
  return {
    label: TendanceLabels[tendance] || 'Inconnue',
    color: TendanceColors[tendance] || '#607d8b'
  };
}

export function getClassementColor(classement: number, total: number): string {
  const pourcentage = (classement / total) * 100;
  if (pourcentage <= 20) return '#4caf50'; // Top 20%
  if (pourcentage <= 50) return '#8bc34a'; // Top 50%
  if (pourcentage <= 80) return '#ff9800'; // Top 80%
  return '#f44336'; // Bottom 20%
}

export function formatTauxPresence(taux: number): string {
  return `${taux.toFixed(1)}%`;
}

export function formatPerformance(performance: number): string {
  return `${performance.toFixed(2)}/10`;
}
