# Kata Tennis (Clean Architecture — Spring Boot)

Ce dépôt implémente un **kata de score au tennis** avec une architecture **Clean** et le **pattern State** :
- **domain** : règles métiers (scores, deuce, advantage, victoire) avec le pattern State
- **application** : orchestration (service) — prend une séquence "ABABAA"
- **infrastructure** : API REST, persistance, logs structurés

## 🔧 Stack
- Java 21
- Spring Boot 3.5.5
- Maven
- H2 Database (in-memory)
- SpringDoc OpenAPI (Swagger)
- Logback avec logs structurés
- Pattern State pour la gestion des états du jeu

## 🎾 Règles du Tennis
- **0 point** → **15 points** → **30 points** → **40 points** → **Victoire**
- Si les deux joueurs ont 40 points → **Deuce**
- En deuce, le premier point donne l'**Avantage**
- Avec l'avantage, gagner un point = **Victoire**, perdre = retour au **Deuce**
- **Règle des 2 points** : Pour gagner, il faut avoir 40 points ET un écart d'au moins 2 points

## ▶️ Utilisation

### 1. API REST
```bash
# Démarrer l'application
mvn spring-boot:run

# Accéder à Swagger UI
http://localhost:8080/swagger-ui.html

# Jouer une partie
curl -X POST "http://localhost:8080/api/v1/tennis/play" \
     -H "Content-Type: application/json" \
     -d '{"sequence":"ABABAA"}'

# Réinitialiser le jeu
curl -X POST "http://localhost:8080/api/v1/tennis/reset"

# Consulter l'historique des parties terminées
curl "http://localhost:8080/api/v1/tennis/history"
```

### 2. Base de données H2
```bash
# Console H2 (développement)
http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:tennisdb
# Username: sa
# Password: (vide)
```

## 📊 Exemple de Sortie
Pour la séquence "ABABAA" :
```json
[
  {
    "score": "Player A: 15 / Player B: 0",
    "state": "Normal",
    "message": "A scored a point!",
    "timestamp": "2025-09-05T02:30:21.442431400Z"
  },
  {
    "score": "Player A: 15 / Player B: 15",
    "state": "Normal",
    "message": "B scored a point!",
    "timestamp": "2025-09-05T02:30:21.533331800Z"
  },
  {
    "score": "Player A: 30 / Player B: 15",
    "state": "Normal",
    "message": "A scored a point!",
    "timestamp": "2025-09-05T02:30:21.534332400Z"
  },
  {
    "score": "Player A: 30 / Player B: 30",
    "state": "Normal",
    "message": "B scored a point!",
    "timestamp": "2025-09-05T02:30:21.535331700Z"
  },
  {
    "score": "Player A: 40 / Player B: 30",
    "state": "Normal",
    "message": "A scored a point!",
    "timestamp": "2025-09-05T02:30:21.536331500Z"
  },
  {
    "score": "Player A wins the game",
    "state": "GameWon",
    "message": "Player A wins the game",
    "timestamp": "2025-09-05T02:30:21.537331300Z"
  }
]
```

## 🏗️ Architecture

### Pattern State
- `NormalState` : Score standard (0, 15, 30, 40)
- `DeuceState` : Égalité à 40
- `AdvantageState` : Un joueur a l'avantage
- `GameWonState` : Le jeu est terminé

### Clean Architecture
- **Domain** : Modèles et règles métier (`Game`, `GameState`)
- **Application** : Services d'orchestration (`GameService`, `GameUseCase`)
- **Infrastructure** : Contrôleur REST, persistance, logs (`GameController`, `GameEntity`, `GamePort`)

### Fonctionnalités
- **API REST** avec validation et gestion d'erreurs centralisée
- **Swagger/OpenAPI** pour la documentation interactive
- **Persistance** des parties terminées avec historique des messages
- **Logs structurés** avec correlation ID pour le traçage
- **Tests unitaires et d'intégration** complets

## 🧪 Tests
```bash
# Compiler et tester
mvn clean test

# Tests d'intégration
mvn test -Dtest="TennisGameIntegrationTestSimple"

# Démarrer l'application
mvn spring-boot:run
```

## 📝 Endpoints API
- `POST /api/v1/tennis/play` - Jouer une séquence de points
- `POST /api/v1/tennis/reset` - Réinitialiser le jeu
- `GET /api/v1/tennis/history` - Consulter l'historique des parties

## 🔍 Logs Structurés
L'application utilise des logs structurés avec correlation ID :
- **Format JSON** en production
- **Format lisible** en développement
- **Correlation ID** pour tracer les requêtes
- **Rotation automatique** des fichiers de logs

## 🚀 Déploiement
```bash
# Compiler l'application
mvn clean package

# Exécuter le JAR
java -jar target/kata-tennis-0.0.1-SNAPSHOT.jar

# Avec profil de production
java -jar target/kata-tennis-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## 📋 Exemples de Séquences
- `"ABABAA"` → A gagne (6 points)
- `"BABABA"` → Deuce (6 points)
- `"ABABABABAA"` → A gagne après deuce et avantage (10 points)
- `"AAAA"` → A gagne rapidement (4 points)
- `"BBBB"` → B gagne rapidement (4 points)
