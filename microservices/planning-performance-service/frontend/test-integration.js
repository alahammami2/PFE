#!/usr/bin/env node

/**
 * Script de test d'intégration backend-frontend
 * Vérifie que tous les endpoints backend sont accessibles
 */

const http = require('http');
const https = require('https');

const BASE_URL = 'http://localhost:8082';
const API_BASE = `${BASE_URL}/api`;

// Liste des endpoints à tester
const endpoints = [
  // Health check
  { method: 'GET', path: '/actuator/health', description: 'Health check' },
  
  // Entraînements
  { method: 'GET', path: '/api/entrainements', description: 'Liste des entraînements' },
  { method: 'GET', path: '/api/entrainements/statistiques', description: 'Statistiques des entraînements' },
  
  // Participations
  { method: 'GET', path: '/api/participations', description: 'Liste des participations' },
  { method: 'GET', path: '/api/participations/statistiques', description: 'Statistiques des participations' },
  
  // Performances
  { method: 'GET', path: '/api/performances', description: 'Liste des performances' },
  { method: 'GET', path: '/api/performances/statistiques', description: 'Statistiques des performances' },
  
  // Absences
  { method: 'GET', path: '/api/absences', description: 'Liste des absences' },
  { method: 'GET', path: '/api/absences/statistiques', description: 'Statistiques des absences' },
  
  // Objectifs
  { method: 'GET', path: '/api/objectifs', description: 'Liste des objectifs' },
  { method: 'GET', path: '/api/objectifs/statistiques', description: 'Statistiques des objectifs' },
  
  // Statistiques
  { method: 'GET', path: '/api/statistiques', description: 'Statistiques générales' },
  { method: 'GET', path: '/api/statistiques/globales', description: 'Statistiques globales' }
];

/**
 * Effectue une requête HTTP
 */
function makeRequest(endpoint) {
  return new Promise((resolve, reject) => {
    const url = `${BASE_URL}${endpoint.path}`;
    const options = {
      method: endpoint.method,
      timeout: 5000
    };

    const req = http.request(url, options, (res) => {
      let data = '';
      
      res.on('data', (chunk) => {
        data += chunk;
      });
      
      res.on('end', () => {
        resolve({
          status: res.statusCode,
          headers: res.headers,
          data: data,
          endpoint: endpoint
        });
      });
    });

    req.on('error', (error) => {
      reject({
        error: error.message,
        endpoint: endpoint
      });
    });

    req.on('timeout', () => {
      req.destroy();
      reject({
        error: 'Timeout',
        endpoint: endpoint
      });
    });

    req.end();
  });
}

/**
 * Teste tous les endpoints
 */
async function testAllEndpoints() {
  console.log('🚀 Test d\'intégration backend-frontend');
  console.log('=====================================\n');
  
  let successCount = 0;
  let errorCount = 0;
  
  for (const endpoint of endpoints) {
    try {
      console.log(`📡 Test: ${endpoint.description}`);
      console.log(`   ${endpoint.method} ${endpoint.path}`);
      
      const result = await makeRequest(endpoint);
      
      if (result.status >= 200 && result.status < 300) {
        console.log(`   ✅ Succès (${result.status})`);
        successCount++;
      } else if (result.status === 404) {
        console.log(`   ⚠️  Endpoint non trouvé (${result.status})`);
        errorCount++;
      } else if (result.status >= 400 && result.status < 500) {
        console.log(`   ⚠️  Erreur client (${result.status})`);
        errorCount++;
      } else {
        console.log(`   ❌ Erreur serveur (${result.status})`);
        errorCount++;
      }
      
    } catch (error) {
      console.log(`   ❌ Erreur: ${error.error}`);
      errorCount++;
    }
    
    console.log('');
  }
  
  // Résumé
  console.log('📊 Résumé des tests');
  console.log('==================');
  console.log(`✅ Succès: ${successCount}`);
  console.log(`❌ Erreurs: ${errorCount}`);
  console.log(`📈 Total: ${successCount + errorCount}`);
  
  if (errorCount === 0) {
    console.log('\n🎉 Tous les tests sont passés avec succès !');
    console.log('Le backend est prêt pour l\'intégration frontend.');
  } else {
    console.log('\n⚠️  Certains endpoints ne sont pas accessibles.');
    console.log('Vérifiez que le backend est démarré et configuré correctement.');
  }
  
  // Instructions
  console.log('\n📋 Instructions pour démarrer les services:');
  console.log('Backend:');
  console.log('  cd microservices/planning-performance-service/backend');
  console.log('  mvn spring-boot:run');
  console.log('');
  console.log('Frontend:');
  console.log('  cd microservices/planning-performance-service/frontend');
  console.log('  npm start');
  console.log('');
  console.log('🌐 URLs utiles:');
  console.log(`  Backend API: ${API_BASE}`);
  console.log(`  Health Check: ${BASE_URL}/actuator/health`);
  console.log('  Frontend: http://localhost:4200');
}

// Exécution du script
if (require.main === module) {
  testAllEndpoints().catch(console.error);
}

module.exports = { testAllEndpoints, makeRequest };
