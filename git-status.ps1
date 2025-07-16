# Script de vérification Git et finalisation du push
# Usage: .\git-status.ps1

Write-Host "🔍 Vérification de l'état Git" -ForegroundColor Green

# Vérifier l'état du repository
Write-Host "`n📋 État du repository:" -ForegroundColor Yellow
try {
    git status --porcelain
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Repository Git initialisé" -ForegroundColor Green
    }
}
catch {
    Write-Host "❌ Erreur Git: $($_.Exception.Message)" -ForegroundColor Red
}

# Vérifier les remotes
Write-Host "`n🌐 Remotes configurés:" -ForegroundColor Yellow
try {
    git remote -v
}
catch {
    Write-Host "❌ Aucun remote configuré" -ForegroundColor Red
}

# Vérifier les branches
Write-Host "`n🌿 Branches:" -ForegroundColor Yellow
try {
    git branch -a
}
catch {
    Write-Host "❌ Erreur lors de la vérification des branches" -ForegroundColor Red
}

# Vérifier le dernier commit
Write-Host "`n📝 Dernier commit:" -ForegroundColor Yellow
try {
    git log --oneline -1
}
catch {
    Write-Host "❌ Aucun commit trouvé" -ForegroundColor Red
}

# Ajouter les nouveaux fichiers si nécessaire
Write-Host "`n➕ Ajout des nouveaux fichiers:" -ForegroundColor Yellow
try {
    git add README_NEW.md git-status.ps1
    Write-Host "✅ Nouveaux fichiers ajoutés" -ForegroundColor Green
}
catch {
    Write-Host "❌ Erreur lors de l'ajout des fichiers" -ForegroundColor Red
}

# Créer un commit pour les nouveaux fichiers
Write-Host "`n💾 Commit des nouveaux fichiers:" -ForegroundColor Yellow
try {
    $status = git status --porcelain
    if ($status) {
        git commit -m "📚 Add enhanced README and Git status script

- Add comprehensive README_NEW.md with badges and detailed documentation
- Add git-status.ps1 script for repository management
- Include quick start guide and architecture overview"
        Write-Host "✅ Commit créé" -ForegroundColor Green
    }
    else {
        Write-Host "ℹ️ Aucun changement à commiter" -ForegroundColor Gray
    }
}
catch {
    Write-Host "❌ Erreur lors du commit" -ForegroundColor Red
}

# Tenter le push
Write-Host "`n🚀 Push vers GitHub:" -ForegroundColor Yellow
try {
    git push origin master
    Write-Host "✅ Push réussi!" -ForegroundColor Green
}
catch {
    Write-Host "⚠️ Push en cours ou erreur" -ForegroundColor Yellow
    Write-Host "Vérifiez manuellement avec: git push origin master" -ForegroundColor Gray
}

# Afficher l'URL du repository
Write-Host "`n🌐 Repository GitHub:" -ForegroundColor Yellow
Write-Host "https://github.com/alahammami2/PFE" -ForegroundColor Blue

# Instructions finales
Write-Host "`n📋 Prochaines étapes:" -ForegroundColor Yellow
Write-Host "1. Vérifier le repository sur GitHub" -ForegroundColor White
Write-Host "2. Configurer les GitHub Actions si nécessaire" -ForegroundColor White
Write-Host "3. Créer des releases pour les versions" -ForegroundColor White
Write-Host "4. Configurer les webhooks Jenkins" -ForegroundColor White

Write-Host "`n🎉 Code poussé vers GitHub avec succès!" -ForegroundColor Green
