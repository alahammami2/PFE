/**
 * Modèle pour les éléments de salaire
 * (Fichier séparé pour éviter les imports circulaires)
 */

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
 * Modèle d'élément de salaire prédéfini
 */
export interface ModeleElementSalaire {
  id: number;
  libelle: string;
  typeElement: TypeElementSalaire;
  tauxDefaut?: number;
  obligatoire: boolean;
  imposable: boolean;
  cotisable: boolean;
  actif: boolean;
  ordreAffichage: number;
  description?: string;
}
