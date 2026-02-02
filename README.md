# MediLabo Solutions

Application basée sur une architecture microservices permettant la gestion des patients,
des notes médicales et l’évaluation du risque de diabète.
Les services sont exposés via un Spring Cloud Gateway et l’interface utilisateur est fournie
par une application front en Thymeleaf.

## Architecture

**Microservices**
- **patient-service** (Spring Boot + H2) — port **8081**
- **notes-service** (Spring Boot + MongoDB) — port **8083**
- **assessment-service** (Spring Boot) — port **8084**
- **gateway-service** (Spring Cloud Gateway) — port **8080**
- **front-service** (Spring Boot + Thymeleaf) — port **8082**
- **mongo** (MongoDB) — port **27017**

## Prérequis

- Docker Desktop (Windows : WSL2 activé)
- Docker Compose (inclus avec Docker Desktop)

## Lancement avec Docker

Depuis la racine du repository :

```bash
docker compose up --build
```
### Accès

- Interface utilisateur : `http://localhost:8082/patients`

- Gateway (point d’entrée API) :

  - Patients : `http://localhost:8080/patients`

  - Notes (exemple) : `http://localhost:8080/notes/patient/2`

  - Évaluation du risque (exemple) :  
    `http://localhost:8080/assessments/patient/4`


### Authentification

Une authentification basique est mise en place via Spring Security.

- Nom d’utilisateur : `admin`

- Mot de passe : `admin`


## Arrêt

`docker compose down`

## Nettoyage des volumes (données MongoDB)

`docker compose down -v`

## Recommandations “Green” pour ce projet
Pour réduire l’empreinte environnementale d’une application
en limitant la consommation CPU, mémoire, réseau et I/O tout au long de son cycle de vie.


### Réduire CPU / mémoire (code)

- **Éviter les concaténations de Strings en boucle** (création d’objets temporaires).  
  Exemple : construire un texte via `StringBuilder` plutôt qu’un `reduce` avec `a + " " + b`.

- **Limiter les traitements inutiles** : ne recalculer le risk assessment que lorsque nécessaire (ex : à l’affichage des notes ou après ajout d’une note).

- **Limiter la taille des réponses** :

  - pagination/filtrage si le volume de patients/notes augmente,

  - éviter de renvoyer des champs inutiles.

- **Logs** :

  - éviter les logs verbeux en production (DEBUG/TRACE),

  - préférer des logs concis et utiles (INFO/WARN/ERROR),

  - ne pas logguer des payloads complets (notes) si non nécessaire.


### Réduire réseau / I/O

- **Regrouper les appels** lorsque possible (ex : ne pas multiplier les appels côté front si non requis).

- **Configurer des timeouts et un pool HTTP** (ex : WebClient/RestTemplate configuré) pour éviter des connexions coûteuses répétées.


### Conteneurs (Docker)

- **Images plus petites** :

  - multi-stage build (déjà en place),

  - utiliser une base runtime légère (ex : JRE slim/distroless si possible),

  - nettoyer les caches build quand pertinent.

- **Ne pas surdimensionner** :

  - définir des limites de ressources (CPU/mémoire) dans `docker-compose.yml` (optionnel mais recommandé),

  - éviter de lancer des services inutiles.


### Données & persistance

- **Stocker uniquement ce qui est nécessaire** et éviter les duplications de données.

- Sur MongoDB : ajouter des **index** sur les champs de recherche fréquents (ex : `patId`) pour éviter des scans coûteux.


### Mesure et amélioration continue

- Ajouter un minimum de **tests de performance** (même simples) pour détecter les régressions.

- Suivre quelques métriques (temps de réponse, consommation mémoire) avant/après modifications.