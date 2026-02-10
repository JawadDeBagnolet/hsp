@echo off
echo === Test de connexion et affichage des utilisateurs ===
echo.

echo Verification de MySQL...
sc query mysql 2>nul | find "RUNNING" >nul
if %ERRORLEVEL% == 0 (
    echo [OK] MySQL est en cours d'execution
) else (
    echo [ERREUR] MySQL n'est pas en cours d'execution
    echo Veuillez demarrer MySQL (WAMP/MAMP/XAMPP)
    pause
    exit /b 1
)

echo.
echo Tentative de connexion a la base de donnees HSP...
echo.

echo Configuration testees:
echo - Serveur: localhost:3306 (WAMP)
echo - Base: hsp
echo - Utilisateur: root
echo - Mot de passe: (vide)
echo.

echo Si la connexion echoue, verifiez:
echo 1. Que WAMP/MAMP est bien demarre
echo 2. Que la base de donnees "hsp" existe
echo 3. Que l'utilisateur "root" a les droits necessaires
echo.

echo Pour lancer l'application et voir les utilisateurs:
echo 1. Assurez-vous que Java JDK est installe
echo 2. Configurez JAVA_HOME correctement
echo 3. Lancez: mvn clean compile exec:java -Dexec.mainClass="appli.StartApplication"
echo 4. Naviguez vers la page "Utilisateurs"
echo.

pause
