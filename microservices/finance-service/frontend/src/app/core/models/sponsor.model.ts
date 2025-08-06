/**
 * Modèle pour les sponsors et partenaires
 */
export interface Sponsor {
  id: number;
  nom: string;
  contactNom?: string;
  contactEmail?: string;
  contactTelephone?: string;
  adresse?: string;
  siteWeb?: string;
  typePartenariat: TypePartenariat;
  montantContrat: number;
  montantVerse: number;
  montantRestant: number;
  dateDebut: string;
  dateFin: string;
  statut: StatutSponsor;
  autoRenouvellement: boolean;
  conditionsRenouvellement?: string;
  logoUrl?: string;
  descriptionPartenariat?: string;
  avantages?: string;
  obligations?: string;
  dateCreation: string;
  dateModification: string;
  contrats?: ContratSponsoring[];
  paiements?: PaiementSponsor[];
}

/**
 * Types de partenariat
 */
export enum TypePartenariat {
  PRINCIPAL = 'PRINCIPAL',
  OFFICIEL = 'OFFICIEL',
  TECHNIQUE = 'TECHNIQUE',
  MEDIA = 'MEDIA',
  INSTITUTIONNEL = 'INSTITUTIONNEL'
}

/**
 * Statuts possibles pour un sponsor
 */
export enum StatutSponsor {
  ACTIF = 'ACTIF',
  EXPIRE = 'EXPIRE',
  SUSPENDU = 'SUSPENDU',
  RESILIE = 'RESILIE'
}

/**
 * Contrat de sponsoring
 */
export interface ContratSponsoring {
  id: number;
  sponsorId: number;
  numeroContrat: string;
  dateSignature: string;
  dateDebut: string;
  dateFin: string;
  montant: number;
  devise: string;
  conditionsPaiement?: string;
  clausesParticulieres?: string;
  documentUrl?: string;
  statut: StatutContrat;
  dateCreation: string;
  dateModification: string;
}

/**
 * Statuts possibles pour un contrat
 */
export enum StatutContrat {
  ACTIF = 'ACTIF',
  EXPIRE = 'EXPIRE',
  RESILIE = 'RESILIE'
}

/**
 * Paiement de sponsor
 */
export interface PaiementSponsor {
  id: number;
  sponsorId: number;
  montant: number;
  datePaiement: string;
  datePrevue?: string;
  modePaiement: string;
  referencePaiement?: string;
  statut: StatutPaiement;
  commentaires?: string;
  documentUrl?: string;
  dateCreation: string;
  dateModification: string;
}

/**
 * Statuts possibles pour un paiement
 */
export enum StatutPaiement {
  ATTENDU = 'ATTENDU',
  RECU = 'RECU',
  EN_RETARD = 'EN_RETARD',
  ANNULE = 'ANNULE'
}

/**
 * DTO pour la création/modification d'un sponsor
 */
export interface SponsorDto {
  nom: string;
  contactNom?: string;
  contactEmail?: string;
  contactTelephone?: string;
  adresse?: string;
  siteWeb?: string;
  typePartenariat: TypePartenariat;
  montantContrat: number;
  dateDebut: string;
  dateFin: string;
  autoRenouvellement?: boolean;
  conditionsRenouvellement?: string;
  logoUrl?: string;
  descriptionPartenariat?: string;
  avantages?: string;
  obligations?: string;
}

/**
 * DTO pour l'enregistrement d'un paiement
 */
export interface PaiementSponsorDto {
  montant: number;
  datePaiement: string;
  datePrevue?: string;
  modePaiement: string;
  referencePaiement?: string;
  commentaires?: string;
}

/**
 * Statistiques des sponsors
 */
export interface StatistiquesSponsors {
  nombreSponsorsActifs: number;
  montantTotalContracts: number;
  montantTotalVerse: number;
  montantTotalRestant: number;
  pourcentageRealisation: number;
  repartitionParType: RepartitionTypePartenariat[];
  sponsorsExpirantProchainement: Sponsor[];
  paiementsEnRetard: PaiementSponsor[];
}

/**
 * Répartition par type de partenariat
 */
export interface RepartitionTypePartenariat {
  type: TypePartenariat;
  nombre: number;
  montantTotal: number;
  pourcentage: number;
}
