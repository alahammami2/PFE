import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'currencyFormat'
})
export class CurrencyFormatPipe implements PipeTransform {

  transform(
    value: number | null | undefined,
    currency: string = 'EUR',
    locale: string = 'fr-FR',
    display: 'code' | 'symbol' | 'symbol-narrow' = 'symbol'
  ): string {
    
    if (value === null || value === undefined || isNaN(value)) {
      return '0,00 €';
    }
    
    try {
      return new Intl.NumberFormat(locale, {
        style: 'currency',
        currency: currency,
        currencyDisplay: display
      }).format(value);
    } catch (error) {
      console.error('Erreur lors du formatage de la devise:', error);
      return `${value.toFixed(2)} €`;
    }
  }
}
