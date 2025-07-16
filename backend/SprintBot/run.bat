@echo off
echo ========================================
echo     SprintBot - Volleyball Manager
echo ========================================
echo.

REM Vérifier si Java est installé
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Erreur: Java n'est pas installé ou n'est pas dans le PATH
    echo Veuillez installer Java 17 ou supérieur
    pause
    exit /b 1
)

REM Utiliser Maven local s'il est installé, sinon utiliser le chemin complet
set MAVEN_CMD=mvn
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Maven n'est pas dans le PATH, utilisation du Maven local...
    set MAVEN_CMD=C:\Users\Administrator\apache-maven-3.9.6\bin\mvn.cmd
)

echo Compilation et démarrage de l'application...
echo L'application sera accessible sur: http://localhost:8080
echo.
echo Endpoints disponibles:
echo - GET /                    : Page d'accueil
echo - GET /api/health          : Vérification de l'état
echo - GET /actuator/health     : Monitoring Actuator
echo - GET /h2-console          : Console base de données H2
echo.
echo Appuyez sur Ctrl+C pour arrêter l'application
echo.

%MAVEN_CMD% spring-boot:run

pause
