/**
 * Modèle pour les salaires
 */
export interface Salaire {
  id: number;
  employeId: number;
  periode: string; // Format YYYY-MM-DD (premier jour du mois)
  salaireBrut: number;
  primes: number;
  bonus: number;
  deductions: number;
  heuresSupplementaires: number;
  joursAbsence: number;
  cotisationsSociales: number;
  impot: number;
  salaireNet: number;
  statut: StatutSalaire;
  dateValidation?: string;
  validateurId?: number;
  datePaiement?: string;
  modePaiement?: string;
  referencePaiement?: string;
  commentaires?: string;
  dateCreation: string;
  dateModification: string;
  elements?: ElementSalaire[];
}

/**
 * Statuts possibles pour un salaire
 */
export enum StatutSalaire {
  CALCULE = 'CALCULE',
  VALIDE = 'VALIDE',
  PAYE = 'PAYE',
  ANNULE = 'ANNULE'
}

/**
 * Élément de salaire (détail des gains/retenues)
 */
export interface ElementSalaire {
  id: number;
  salaireId: number;
  libelle: string;
  typeElement: TypeElementSalaire;
  montant: number;
  quantite?: number;
  taux?: number;
  obligatoire: boolean;
  imposable: boolean;
  cotisable: boolean;
  ordreAffichage: number;
  dateCreation: string;
  dateModification: string;
}

/**
 * Types d'éléments de salaire
 */
export enum TypeElementSalaire {
  GAIN = 'GAIN',
  RETENUE = 'RETENUE',
  COTISATION = 'COTISATION',
  INFORMATION = 'INFORMATION'
}

/**
 * DTO pour le calcul d'un salaire
 */
export interface CalculSalaireDto {
  employeId: number;
  periode: string;
  salaireBrut: number;
  primes?: number;
  bonus?: number;
  deductions?: number;
  heuresSupplementaires?: number;
  joursAbsence?: number;
}

/**
 * DTO pour l'ajout d'un élément de salaire
 */
export interface ElementSalaireDto {
  libelle: string;
  typeElement: TypeElementSalaire;
  montant: number;
  quantite?: number;
  taux?: number;
  obligatoire: boolean;
  imposable: boolean;
  cotisable: boolean;
}

/**
 * Filtres pour la recherche de salaires
 */
export interface FiltresSalaire {
  employeId?: number;
  statut?: StatutSalaire;
  periodeDebut?: string;
  periodeFin?: string;
  salaireMin?: number;
  salaireMax?: number;
}

/**
 * Statistiques des salaires
 */
export interface StatistiquesSalaires {
  nombreSalaires: number;
  montantTotalBrut: number;
  montantTotalNet: number;
  montantTotalCotisations: number;
  montantTotalImpots: number;
  salaireMoyenBrut: number;
  salaireMoyenNet: number;
  repartitionParStatut: RepartitionStatutSalaire[];
  evolutionMensuelle: EvolutionSalaireMensuelle[];
}

/**
 * Répartition par statut de salaire
 */
export interface RepartitionStatutSalaire {
  statut: StatutSalaire;
  nombre: number;
  montantTotal: number;
  pourcentage: number;
}

/**
 * Évolution mensuelle des salaires
 */
export interface EvolutionSalaireMensuelle {
  mois: string;
  nombreSalaires: number;
  montantTotalBrut: number;
  montantTotalNet: number;
  montantTotalCotisations: number;
}

/**
 * Bulletin de paie (pour l'affichage/impression)
 */
export interface BulletinPaie {
  salaire: Salaire;
  employe: {
    id: number;
    nom: string;
    prenom: string;
    numeroSecu?: string;
    poste?: string;
  };
  entreprise: {
    nom: string;
    adresse: string;
    siret?: string;
  };
  elements: ElementSalaire[];
  totaux: {
    totalGains: number;
    totalRetenues: number;
    totalCotisations: number;
    netAPayer: number;
  };
}
