import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { AppModule } from './app/app.module';

// Configuration pour le mode production
import { enableProdMode } from '@angular/core';
import { environment } from './environments/environment';

if (environment.production) {
  enableProdMode();
}

// Bootstrap de l'application
platformBrowserDynamic()
  .bootstrapModule(AppModule)
  .catch(err => console.error('Erreur lors du démarrage de l\'application:', err));
