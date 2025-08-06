// Interface principale pour les statistiques d'entraînement
export interface StatistiqueEntrainement {
  id?: number;
  entrainementId: number;
  joueurId: number;
  dureeEffective: number; // en minutes
  intensiteMoyenne: number; // sur 10
  caloriesBrulees?: number;
  frequenceCardiaqueMax?: number;
  frequenceCardiaqueMoyenne?: number;
  distanceParcourue?: number; // en mètres
  vitesseMax?: number; // en km/h
  vitesseMoyenne?: number; // en km/h
  nombreSauts?: number;
  nombreSprints?: number;
  tempsRecuperation?: number; // en minutes
  
  // Métriques spécifiques au volleyball
  nombreServices?: number;
  nombreAttaques?: number;
  nombreBlocs?: number;
  nombreReceptions?: number;
  pourcentageReussiteService?: number;
  pourcentageReussiteAttaque?: number;
  pourcentageReussiteReception?: number;
  
  // Informations contextuelles
  dateEntrainement?: string;
  commentaires?: string;
  conditions?: string; // Conditions d'entraînement (météo, terrain, etc.)
  
  // Données calculées côté frontend
  scorePerformance?: number;
  niveauIntensiteLabel?: string;
  comparaisonMoyenne?: 'superieur' | 'inferieur' | 'egal';
}

// DTOs pour les opérations CRUD
export interface CreateStatistiqueDto {
  entrainementId: number;
  joueurId: number;
  dureeEffective: number;
  intensiteMoyenne: number;
  caloriesBrulees?: number;
  frequenceCardiaqueMax?: number;
  frequenceCardiaqueMoyenne?: number;
  distanceParcourue?: number;
  vitesseMax?: number;
  vitesseMoyenne?: number;
  nombreSauts?: number;
  nombreSprints?: number;
  tempsRecuperation?: number;
  nombreServices?: number;
  nombreAttaques?: number;
  nombreBlocs?: number;
  nombreReceptions?: number;
  pourcentageReussiteService?: number;
  pourcentageReussiteAttaque?: number;
  pourcentageReussiteReception?: number;
  commentaires?: string;
  conditions?: string;
}

export interface UpdateStatistiqueDto {
  dureeEffective?: number;
  intensiteMoyenne?: number;
  caloriesBrulees?: number;
  frequenceCardiaqueMax?: number;
  frequenceCardiaqueMoyenne?: number;
  distanceParcourue?: number;
  vitesseMax?: number;
  vitesseMoyenne?: number;
  nombreSauts?: number;
  nombreSprints?: number;
  tempsRecuperation?: number;
  nombreServices?: number;
  nombreAttaques?: number;
  nombreBlocs?: number;
  nombreReceptions?: number;
  pourcentageReussiteService?: number;
  pourcentageReussiteAttaque?: number;
  pourcentageReussiteReception?: number;
  commentaires?: string;
  conditions?: string;
}

// Filtres pour la recherche
export interface StatistiqueFilters {
  entrainementId?: number;
  joueurId?: number;
  dateDebut?: string;
  dateFin?: string;
  dureeMin?: number;
  dureeMax?: number;
  intensiteMin?: number;
  intensiteMax?: number;
  caloriesMin?: number;
  caloriesMax?: number;
  recherche?: string;
}

// Statistiques globales
export interface StatistiqueGlobale {
  totalEntrainements: number;
  dureeTotale: number; // en minutes
  dureeMoyenne: number;
  intensiteMoyenne: number;
  caloriesMoyennes: number;
  
  // Métriques d'équipe
  nombreJoueursActifs: number;
  tauxParticipation: number;
  
  // Évolution
  evolutionDernierMois: {
    entrainements: number;
    duree: number;
    intensite: number;
  };
  
  // Répartition par intensité
  repartitionIntensite: {
    faible: number; // 0-3
    moderee: number; // 4-6
    elevee: number; // 7-8
    tresElevee: number; // 9-10
  };
  
  // Top performers
  topJoueurs: {
    joueurId: number;
    scorePerformance: number;
    nombreEntrainements: number;
  }[];
}

// Statistiques par joueur
export interface StatistiqueJoueur {
  joueurId: number;
  nombreEntrainements: number;
  dureeTotale: number;
  dureeMoyenne: number;
  intensiteMoyenne: number;
  caloriesMoyennes: number;
  scorePerformanceMoyen: number;
  
  // Progression
  tendanceProgression: 'croissante' | 'decroissante' | 'stable';
  evolutionIntensiteParMois: { [mois: string]: number };
  evolutionPerformanceParMois: { [mois: string]: number };
  
  // Métriques spécifiques volleyball
  statistiquesVolleyball: {
    moyenneServices: number;
    moyenneAttaques: number;
    moyenneBlocs: number;
    moyenneReceptions: number;
    tauxReussiteService: number;
    tauxReussiteAttaque: number;
    tauxReussiteReception: number;
  };
  
  // Comparaison avec l'équipe
  comparaisonEquipe: {
    intensite: 'superieur' | 'inferieur' | 'moyen';
    duree: 'superieur' | 'inferieur' | 'moyen';
    performance: 'superieur' | 'inferieur' | 'moyen';
  };
  
  // Recommandations
  recommandations: string[];
  objectifsSuggeres: string[];
}

// Statistiques d'équipe
export interface StatistiqueEquipe {
  nombreJoueurs: number;
  nombreEntrainements: number;
  dureeTotaleMoyenne: number;
  intensiteMoyenneEquipe: number;
  
  // Performance collective
  performanceGlobale: number;
  cohesionEquipe: number;
  progressionCollective: 'positive' | 'negative' | 'stable';
  
  // Répartition des joueurs par niveau
  repartitionNiveaux: {
    debutant: number;
    intermediaire: number;
    avance: number;
    expert: number;
  };
  
  // Métriques d'assiduité
  tauxPresence: number;
  joueursReguliers: number;
  joueursIrreguliers: number;
  
  // Analyse des forces et faiblesses
  forcesEquipe: string[];
  faiblessesEquipe: string[];
  
  // Objectifs d'équipe
  objectifsCollectifs: {
    objectif: string;
    progression: number;
    echeance: string;
  }[];
}

// Tendances statistiques
export interface TendanceStatistique {
  date: string;
  valeur: number;
  moyenne: number;
  tendance: 'hausse' | 'baisse' | 'stable';
  pourcentageEvolution: number;
}

// Comparaison entre joueurs
export interface ComparaisonStatistique {
  joueurId: number;
  metriques: {
    intensiteMoyenne: number;
    dureeMoyenne: number;
    performanceMoyenne: number;
    nombreEntrainements: number;
  };
  classement: {
    intensite: number;
    duree: number;
    performance: number;
    assiduite: number;
  };
  pointsForts: string[];
  pointsAmeliorer: string[];
}

// Interface pour l'analyse de charge d'entraînement
export interface AnalyseCharge {
  joueurId: number;
  periode: string;
  chargeTotale: number;
  chargeMoyenne: number;
  chargeMax: number;
  chargeMin: number;
  
  // Analyse de la charge
  niveauCharge: 'faible' | 'optimal' | 'eleve' | 'excessif';
  risqueSurmenage: 'faible' | 'moyen' | 'eleve';
  risqueBlessure: 'faible' | 'moyen' | 'eleve';
  
  // Recommandations
  recommandationsCharge: string[];
  ajustementsProposed: {
    intensite: 'augmenter' | 'diminuer' | 'maintenir';
    duree: 'augmenter' | 'diminuer' | 'maintenir';
    frequence: 'augmenter' | 'diminuer' | 'maintenir';
  };
  
  // Prédictions
  chargeOptimaleSuggere: number;
  periodeRecuperationRecommandee: number; // en jours
}

// Interface pour l'analyse de performance
export interface AnalysePerformance {
  joueurId: number;
  periode: string;
  
  // Métriques de performance
  performanceActuelle: number;
  performanceMoyenne: number;
  meilleurePerformance: number;
  progressionGlobale: number;
  
  // Analyse par domaine
  performanceParDomaine: {
    technique: number;
    physique: number;
    tactique: number;
    mental: number;
  };
  
  // Tendances
  tendanceGenerale: 'progression' | 'regression' | 'stagnation';
  domainesProgression: string[];
  domainesRegression: string[];
  
  // Prédictions et objectifs
  performancePredite: number;
  objectifsRecommandes: {
    courtTerme: string[];
    moyenTerme: string[];
    longTerme: string[];
  };
  
  // Plan d'amélioration
  planAmelioration: {
    priorite: string;
    actions: string[];
    dureeEstimee: number; // en semaines
    indicateursSucces: string[];
  }[];
}

// Utilitaires pour les statistiques
export class StatistiqueUtils {
  static calculatePerformanceScore(statistique: StatistiqueEntrainement): number {
    const intensiteScore = (statistique.intensiteMoyenne || 0) * 10;
    const dureeScore = Math.min((statistique.dureeEffective || 0) / 120, 1) * 20;
    const caloriesScore = Math.min((statistique.caloriesBrulees || 0) / 500, 1) * 10;
    
    return Math.round(intensiteScore + dureeScore + caloriesScore);
  }

  static formatDuree(minutes: number): string {
    const heures = Math.floor(minutes / 60);
    const mins = minutes % 60;
    
    if (heures > 0) {
      return `${heures}h${mins.toString().padStart(2, '0')}`;
    }
    return `${mins}min`;
  }

  static formatIntensiteLabel(intensite: number): string {
    if (intensite >= 8) return 'Très élevée';
    if (intensite >= 6) return 'Élevée';
    if (intensite >= 4) return 'Modérée';
    if (intensite >= 2) return 'Faible';
    return 'Très faible';
  }

  static getIntensiteColor(intensite: number): string {
    if (intensite >= 8) return '#f44336';
    if (intensite >= 6) return '#ff9800';
    if (intensite >= 4) return '#ffeb3b';
    if (intensite >= 2) return '#8bc34a';
    return '#4caf50';
  }

  static getPerformanceColor(score: number): string {
    if (score >= 80) return '#4caf50';
    if (score >= 60) return '#8bc34a';
    if (score >= 40) return '#ff9800';
    if (score >= 20) return '#ff5722';
    return '#f44336';
  }

  static calculateTendance(valeurs: number[]): 'croissante' | 'decroissante' | 'stable' {
    if (valeurs.length < 2) return 'stable';
    
    const premieresMoitie = valeurs.slice(0, Math.floor(valeurs.length / 2));
    const derniereMoitie = valeurs.slice(Math.floor(valeurs.length / 2));
    
    const moyennePremiere = premieresMoitie.reduce((sum, val) => sum + val, 0) / premieresMoitie.length;
    const moyenneDerniere = derniereMoitie.reduce((sum, val) => sum + val, 0) / derniereMoitie.length;
    
    const difference = moyenneDerniere - moyennePremiere;
    const seuilStabilite = moyennePremiere * 0.05; // 5% de variation = stable
    
    if (Math.abs(difference) <= seuilStabilite) return 'stable';
    return difference > 0 ? 'croissante' : 'decroissante';
  }

  static detecterAnomalies(statistiques: StatistiqueEntrainement[]): {
    type: 'intensite_anormale' | 'duree_anormale' | 'performance_anormale';
    description: string;
    valeur: number;
    seuil: number;
    statistiqueId?: number;
  }[] {
    const anomalies: any[] = [];
    
    if (statistiques.length === 0) return anomalies;
    
    // Calcul des moyennes
    const intensiteMoyenne = statistiques.reduce((sum, stat) => sum + (stat.intensiteMoyenne || 0), 0) / statistiques.length;
    const dureeMoyenne = statistiques.reduce((sum, stat) => sum + (stat.dureeEffective || 0), 0) / statistiques.length;
    
    statistiques.forEach(stat => {
      // Détection d'intensité anormale
      if (stat.intensiteMoyenne && Math.abs(stat.intensiteMoyenne - intensiteMoyenne) > intensiteMoyenne * 0.3) {
        anomalies.push({
          type: 'intensite_anormale',
          description: `Intensité ${stat.intensiteMoyenne > intensiteMoyenne ? 'anormalement élevée' : 'anormalement faible'}`,
          valeur: stat.intensiteMoyenne,
          seuil: intensiteMoyenne,
          statistiqueId: stat.id
        });
      }
      
      // Détection de durée anormale
      if (stat.dureeEffective && Math.abs(stat.dureeEffective - dureeMoyenne) > dureeMoyenne * 0.5) {
        anomalies.push({
          type: 'duree_anormale',
          description: `Durée ${stat.dureeEffective > dureeMoyenne ? 'anormalement longue' : 'anormalement courte'}`,
          valeur: stat.dureeEffective,
          seuil: dureeMoyenne,
          statistiqueId: stat.id
        });
      }
    });
    
    return anomalies;
  }

  static genererRecommandations(statistique: StatistiqueEntrainement, moyennesEquipe?: any): string[] {
    const recommandations: string[] = [];
    
    // Recommandations basées sur l'intensité
    if ((statistique.intensiteMoyenne || 0) < 4) {
      recommandations.push('Augmenter l\'intensité des exercices pour améliorer la condition physique');
    } else if ((statistique.intensiteMoyenne || 0) > 8) {
      recommandations.push('Prévoir des périodes de récupération pour éviter le surmenage');
    }
    
    // Recommandations basées sur la durée
    if ((statistique.dureeEffective || 0) < 60) {
      recommandations.push('Prolonger la durée d\'entraînement pour optimiser les bénéfices');
    } else if ((statistique.dureeEffective || 0) > 180) {
      recommandations.push('Réduire la durée ou fractionner l\'entraînement pour maintenir l\'efficacité');
    }
    
    // Recommandations spécifiques volleyball
    if ((statistique.pourcentageReussiteService || 0) < 70) {
      recommandations.push('Travailler la technique de service pour améliorer le taux de réussite');
    }
    
    if ((statistique.pourcentageReussiteAttaque || 0) < 60) {
      recommandations.push('Renforcer le travail d\'attaque et de coordination avec le passeur');
    }
    
    if ((statistique.pourcentageReussiteReception || 0) < 80) {
      recommandations.push('Améliorer la technique de réception et la lecture du jeu adverse');
    }
    
    return recommandations;
  }

  static calculateChargeEntrainement(statistique: StatistiqueEntrainement): number {
    const duree = statistique.dureeEffective || 0;
    const intensite = statistique.intensiteMoyenne || 0;
    return Math.round(duree * intensite);
  }

  static getNiveauCharge(charge: number): 'faible' | 'optimal' | 'eleve' | 'excessif' {
    if (charge < 200) return 'faible';
    if (charge < 500) return 'optimal';
    if (charge < 800) return 'eleve';
    return 'excessif';
  }

  static formatPourcentage(valeur: number): string {
    return `${Math.round(valeur)}%`;
  }

  static formatDistance(metres: number): string {
    if (metres >= 1000) {
      return `${(metres / 1000).toFixed(1)} km`;
    }
    return `${metres} m`;
  }

  static formatVitesse(kmh: number): string {
    return `${kmh.toFixed(1)} km/h`;
  }

  static formatFrequenceCardiaque(bpm: number): string {
    return `${Math.round(bpm)} bpm`;
  }

  static validateStatistique(statistique: CreateStatistiqueDto | UpdateStatistiqueDto): string[] {
    const errors: string[] = [];

    if ('dureeEffective' in statistique && statistique.dureeEffective !== undefined) {
      if (statistique.dureeEffective < 0 || statistique.dureeEffective > 480) {
        errors.push('La durée doit être comprise entre 0 et 480 minutes');
      }
    }

    if ('intensiteMoyenne' in statistique && statistique.intensiteMoyenne !== undefined) {
      if (statistique.intensiteMoyenne < 0 || statistique.intensiteMoyenne > 10) {
        errors.push('L\'intensité doit être comprise entre 0 et 10');
      }
    }

    if ('pourcentageReussiteService' in statistique && statistique.pourcentageReussiteService !== undefined) {
      if (statistique.pourcentageReussiteService < 0 || statistique.pourcentageReussiteService > 100) {
        errors.push('Le pourcentage de réussite au service doit être entre 0 et 100');
      }
    }

    if ('pourcentageReussiteAttaque' in statistique && statistique.pourcentageReussiteAttaque !== undefined) {
      if (statistique.pourcentageReussiteAttaque < 0 || statistique.pourcentageReussiteAttaque > 100) {
        errors.push('Le pourcentage de réussite en attaque doit être entre 0 et 100');
      }
    }

    if ('pourcentageReussiteReception' in statistique && statistique.pourcentageReussiteReception !== undefined) {
      if (statistique.pourcentageReussiteReception < 0 || statistique.pourcentageReussiteReception > 100) {
        errors.push('Le pourcentage de réussite en réception doit être entre 0 et 100');
      }
    }

    return errors;
  }
}
