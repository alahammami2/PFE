import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'dateFormat'
})
export class DateFormatPipe implements PipeTransform {

  transform(
    value: string | Date | null | undefined,
    format: 'short' | 'medium' | 'long' | 'full' | 'custom' = 'short',
    locale: string = 'fr-FR',
    customFormat?: string
  ): string {
    
    if (!value) {
      return '';
    }
    
    let date: Date;
    
    if (typeof value === 'string') {
      date = new Date(value);
    } else {
      date = value;
    }
    
    if (isNaN(date.getTime())) {
      return '';
    }
    
    try {
      if (format === 'custom' && customFormat) {
        return this.formatCustom(date, customFormat);
      }
      
      const options = this.getFormatOptions(format);
      return new Intl.DateTimeFormat(locale, options).format(date);
    } catch (error) {
      console.error('Erreur lors du formatage de la date:', error);
      return date.toLocaleDateString(locale);
    }
  }
  
  private getFormatOptions(format: string): Intl.DateTimeFormatOptions {
    switch (format) {
      case 'short':
        return {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric'
        };
      case 'medium':
        return {
          day: '2-digit',
          month: 'short',
          year: 'numeric'
        };
      case 'long':
        return {
          day: '2-digit',
          month: 'long',
          year: 'numeric'
        };
      case 'full':
        return {
          weekday: 'long',
          day: '2-digit',
          month: 'long',
          year: 'numeric'
        };
      default:
        return {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric'
        };
    }
  }
  
  private formatCustom(date: Date, format: string): string {
    const map: { [key: string]: string } = {
      'dd': date.getDate().toString().padStart(2, '0'),
      'MM': (date.getMonth() + 1).toString().padStart(2, '0'),
      'yyyy': date.getFullYear().toString(),
      'HH': date.getHours().toString().padStart(2, '0'),
      'mm': date.getMinutes().toString().padStart(2, '0'),
      'ss': date.getSeconds().toString().padStart(2, '0')
    };
    
    return format.replace(/dd|MM|yyyy|HH|mm|ss/g, (match) => map[match] || match);
  }
}
