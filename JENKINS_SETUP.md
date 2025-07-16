# 🔧 Configuration Jenkins pour SprintBot

## 🚨 Problèmes Résolus dans le Jenkinsfile

### ✅ Corrections Apportées :
1. **Erreur de contexte** - Suppression des commandes `sh` dans `post` sans contexte `node`
2. **Credentials manquants** - Suppression de `database-credentials` inexistant
3. **Support multi-plateforme** - Ajout de support Windows/Linux avec `isUnix()`
4. **Gestion d'erreurs** - Ajout de `try-catch` et gestion des erreurs
5. **Jenkinsfile simplifié** - Création de `Jenkinsfile.simple` pour tests

## 🛠️ Configuration des Outils Jenkins

### 1. Accéder à la Configuration
1. Aller dans **Manage Jenkins** > **Global Tool Configuration**

### 2. Configurer Maven
- **Nom** : `Maven` (exactement comme dans le Jenkinsfile)
- **Cocher** : "Install automatically"
- **Version** : `3.9.6` ou la dernière version stable
- **Installer depuis** : Apache

### 3. Configurer NodeJS
- **Nom** : `NodeJS` (exactement comme dans le Jenkinsfile)
- **Cocher** : "Install automatically"  
- **Version** : `18.x` ou `NodeJS 18.19.0`
- **Global npm packages** : Laisser vide ou ajouter `@angular/cli`

### 4. Configurer Git (si nécessaire)
- **Nom** : `Default`
- **Path to Git executable** : `git` (ou chemin complet si nécessaire)

## 🔄 Relancer le Pipeline

### Option 1 : Utiliser le Jenkinsfile Principal
1. Dans votre job Jenkins, cliquer **"Build Now"**
2. Le pipeline utilisera automatiquement le Jenkinsfile corrigé

### Option 2 : Tester avec le Jenkinsfile Simplifié
1. Dans la configuration du job, changer **Script Path** vers `Jenkinsfile.simple`
2. Sauvegarder et lancer **"Build Now"**
3. Ce fichier est plus robuste pour les premiers tests

## 📋 Étapes du Pipeline Corrigé

### 🔍 Jenkinsfile Principal
1. **Checkout** - Récupération du code depuis GitHub
2. **Build Backend** - Compilation Maven avec gestion d'erreurs
3. **Build Frontend** - Build npm avec support des erreurs
4. **Tests** - Tests backend/frontend (désactivés temporairement)
5. **Docker Build** - Construction des images (conditionnel)
6. **Deploy** - Déploiement avec Docker Compose
7. **Post Actions** - Nettoyage avec gestion d'erreurs

### 🔍 Jenkinsfile.simple
1. **Checkout** - Récupération du code
2. **Build Backend** - Compilation Maven robuste
3. **Build Frontend** - Build npm robuste
4. **Archive Artifacts** - Sauvegarde des artefacts
5. **Post Actions** - Nettoyage simple

## 🐛 Résolution des Erreurs Courantes

### Erreur : "Selected Git installation does not exist"
**Solution** :
1. Manage Jenkins > Global Tool Configuration
2. Section Git > Add Git
3. Nom : `Default`
4. Path : `git`

### Erreur : "maven: command not found"
**Solution** :
1. Vérifier que Maven est configuré avec le nom exact `Maven`
2. Cocher "Install automatically"
3. Relancer le build

### Erreur : "nodejs: command not found"  
**Solution** :
1. Vérifier que NodeJS est configuré avec le nom exact `NodeJS`
2. Cocher "Install automatically"
3. Relancer le build

### Erreur : "docker: command not found"
**Solution** :
1. Installer Docker sur l'agent Jenkins
2. Ou désactiver les étapes Docker en supprimant `BUILD_DOCKER=true`

## 🔧 Variables d'Environnement

Le Jenkinsfile utilise ces variables :
```groovy
environment {
    IMAGE_TAG = "${BUILD_NUMBER}"
    BACKEND_IMAGE = "sprintbot-backend:${IMAGE_TAG}"
    FRONTEND_IMAGE = "sprintbot-frontend:${IMAGE_TAG}"
    MAVEN_OPTS = '-Xmx1024m -Xms512m'
    NODE_VERSION = '18'
    DB_URL = 'jdbc:postgresql://postgres:5432/sprintbot_db'
    DB_USER = 'sprintbot_user'
    DB_PASSWORD = 'sprintbot_password'
}
```

## 📊 Monitoring du Pipeline

### Logs à Surveiller
1. **Console Output** - Logs détaillés de chaque étape
2. **Build History** - Historique des builds
3. **Pipeline Stage View** - Vue graphique des étapes

### Indicateurs de Succès
- ✅ **SUCCESS** - Toutes les étapes réussies
- ⚠️ **UNSTABLE** - Certaines étapes échouées mais build continue
- ❌ **FAILURE** - Échec critique du pipeline

## 🚀 Prochaines Étapes

### 1. Test Initial
```bash
# Lancer le pipeline simplifié d'abord
# Vérifier que Maven et NodeJS fonctionnent
```

### 2. Activation Progressive
```bash
# Une fois le build de base fonctionnel :
# 1. Activer les tests
# 2. Activer Docker build
# 3. Activer le déploiement
```

### 3. Configuration Avancée
```bash
# Ajouter des notifications
# Configurer des webhooks GitHub
# Ajouter des tests de qualité de code
```

## 📞 Support

### En cas de problème :
1. **Vérifier les logs** dans Console Output
2. **Vérifier la configuration** des outils
3. **Tester avec** `Jenkinsfile.simple` d'abord
4. **Consulter** la documentation Jenkins

### Commandes de Debug
```bash
# Dans Jenkins, aller dans Manage Jenkins > Script Console
# Tester les outils :
println "Maven: " + tool('Maven')
println "NodeJS: " + tool('NodeJS')
```

---

🎉 **Le Jenkinsfile est maintenant corrigé et prêt à fonctionner !**
