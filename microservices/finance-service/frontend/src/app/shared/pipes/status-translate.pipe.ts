import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'statusTranslate'
})
export class StatusTranslatePipe implements PipeTransform {

  private readonly translations: { [key: string]: string } = {
    // Statuts Budget
    'ACTIF': 'Actif',
    'CLOTURE': 'Clôturé',
    'SUSPENDU': 'Suspendu',
    
    // Statuts Transaction
    'EN_ATTENTE': 'En attente',
    'VALIDEE': 'Validée',
    'REJETEE': 'Rejetée',
    'ANNULEE': 'Annulée',
    
    // Types Transaction
    'RECETTE': 'Recette',
    'DEPENSE': 'Dépense',
    
    // Statuts Sponsor
    'EXPIRE': 'Expiré',
    'RESILIE': 'Résilié',
    
    // Types Partenariat
    'PRINCIPAL': 'Principal',
    'OFFICIEL': 'Officiel',
    'TECHNIQUE': 'Technique',
    'MEDIA': 'Média',
    'INSTITUTIONNEL': 'Institutionnel',
    
    // Statuts Contrat
    // 'ACTIF': 'Actif', // Déjà défini
    // 'EXPIRE': 'Expiré', // Déjà défini
    // 'RESILIE': 'Résilié', // Déjà défini
    
    // Statuts Paiement
    'ATTENDU': 'Attendu',
    'RECU': 'Reçu',
    'EN_RETARD': 'En retard',
    'ANNULE': 'Annulé',
    
    // Statuts Salaire
    'CALCULE': 'Calculé',
    'VALIDE': 'Validé',
    'PAYE': 'Payé',
    // 'ANNULE': 'Annulé', // Déjà défini
    
    // Types Élément Salaire
    'GAIN': 'Gain',
    'RETENUE': 'Retenue',
    'COTISATION': 'Cotisation',
    'INFORMATION': 'Information',
    
    // Périodes Budget
    'MENSUEL': 'Mensuel',
    'TRIMESTRIEL': 'Trimestriel',
    'SEMESTRIEL': 'Semestriel',
    'ANNUEL': 'Annuel',
    'PONCTUEL': 'Ponctuel',
    
    // Statuts génériques
    'ACTIVE': 'Actif',
    'INACTIVE': 'Inactif',
    'PENDING': 'En attente',
    'COMPLETED': 'Terminé',
    'CANCELLED': 'Annulé',
    'DRAFT': 'Brouillon',
    'PUBLISHED': 'Publié',
    'ARCHIVED': 'Archivé'
  };

  transform(value: string | null | undefined, fallback?: string): string {
    if (!value) {
      return fallback || '';
    }
    
    // Conversion en majuscules pour la recherche
    const upperValue = value.toUpperCase();
    
    // Recherche de la traduction
    const translation = this.translations[upperValue];
    
    if (translation) {
      return translation;
    }
    
    // Si pas de traduction trouvée, on retourne le fallback ou la valeur formatée
    if (fallback) {
      return fallback;
    }
    
    // Formatage par défaut : première lettre en majuscule, remplace les _ par des espaces
    return value
      .toLowerCase()
      .replace(/_/g, ' ')
      .replace(/\b\w/g, (char) => char.toUpperCase());
  }
}
