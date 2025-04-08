# Bank Account Kata

Ce projet est un **kata de gestion de compte bancaire** développé en Java avec Spring Boot, en suivant les principes de l'**architecture hexagonale (ports & adapters)**, **DDD**, **SOLID**, **TDD**, et **clean code** dès le départ.

---

![GitLab CI](https://gitlab.com/exalt-it-dojo/candidats/youssef-massaoudi-bank-account-v2-ce373bc0-b409-4823-b398-a98a8000dde2/badges/bankaccount-kata-dev/pipeline.svg)

---

## Description du kata

Le cahier des charges et les règles du kata se trouvent ici :
👉 [kata-description.md](kata-description.md)

---

## Objectifs du projet

- Développer une API propre et maintenable
- Respecter les bonnes pratiques professionnelles (architecture, tests, séparation des couches)
- Appliquer les concepts DDD/TDD/Hexagone dans un projet concret
- Fournir une base pour les futurs projets backend SaaS

---

## Stack technique

| Composant       | Technologie         |
|-----------------|---------------------|
| Langage         | Java 21             |
| Build tool      | Maven               |
| Framework       | Spring Boot 3       |
| Base de données | H2 (in-memory)      |
| Mapping         | MapStruct           |
| Validation      | Jakarta Validation  |
| Tests unitaires | JUnit 5, Mockito    |
| Tests intégrés  | Testcontainers      |
| CI/CD           | GitLab CI           |
| Containerisation| Docker              |
| Documentation   | Swagger / OpenAPI   |

---

## Structure du projet

```
src/ ├── main/java/com/example/bankaccount/ 
     │ ├── domain/ # Cœur métier : entités, value objects, règles métier
     │ ├── application/ # Cas d'utilisation, services applicatifs
     │ ├── infrastructure/ # Adapters : REST, JPA, Config
     └── test/java/...
```

---

## Fonctionnalités terminées

- [x] Créer la logique métier autour du compte bancaire
- [x] Ajout des historiques d'opérations pour les comptes
- [x] Extraire une factory `BankOperationFactory` pour injecter `Clock` et UUID
- [x] Développer la couche service avec `BankAccountService` & `BankOperationService`
- [x] Créer la persistance avec JPA
- [x] Ajout de DTOs avec MapStruct
- [x] Exposition des services via une API REST documentée avec Swagger
- [x] Validation des entrées via @Valid
- [x] Tests d'intégration SpringBootTest
- [x] Containerisation Docker
- [x] Pipeline CI/CD complète GitLab
- [x] Gestion centralisée des erreurs avec un GlobalExceptionHandler
- [x] Gestion du découvert autorisé sur les comptes courants
- [x] Intégration du livret d’épargne avec règles de dépôt/retrait spécifiques
- [x] Génération du relevé bancaire mensuel glissant
- [x] Génération des relevés bancaires PDF complets et structurés à partir des opérations du compte.

---

## Améliorations prévues
- [ ] Amélioration de la gestion d'erreurs pour la génerations des PDF (relevé bancaire mensuel glissant)
- [ ] Envoi automatique (relevé bancaire) par email avec pièce jointe PDF
- [ ] Ajout de logs métiers et techniques (via SLF4J/Logback)
- [ ] Déploiement du projet sur AWS (EC2 ou Elastic Beanstalk)
- [ ] Ajout d’une authentification JWT pour sécuriser les endpoints
- [ ] Surveillance et métriques avec Spring Actuator / Prometheus / Grafana
- [ ] Passer sur une base de donnée PostgreSQL par exemple
- [ ] Augmenter la couverture de test (tests de mapping, de configuration, etc.)
- [ ] Utilisation de Testcontainers pour les tests d'intégration avec PostgreSQL
- [ ] Utiliser SonarLint pour détecter les code smells
- [ ] API non versionnée (bonne pratique à ajouter)

---

## À venir

- Implémentation de notifications (par e-mail ou webhook)
- Historique enrichi : catégorisation, libellés
- Multi-comptes et agrégation par utilisateur

---

## Build & 🔍 Test

### Prérequis

- Java 21 installé (`java -version`)
- Maven 3.9+ (`mvn -v`)
- 
### Commandes utiles

| Action                   | Commande                          |
|--------------------------|-----------------------------------|
| Compiler le projet       | `./mvnw clean compile`            |
| Lancer les tests         | `./mvnw clean test`               |
| Générer le jar           | `./mvnw clean package`            |
| Lancer l’application     | `./mvnw spring-boot:run`          |
| Vérifier les dépendances | `./mvnw dependency:tree`          |


### Builder le projet

```bash
mvn clean install
```

Cela compile le code, lance les tests et construit le JAR du projet.

### Lancer uniquement les tests

```bash
mvn test
```

### Démarrer l'application localement

```bash
mvn spring-boot:run
```
L'application sera disponible sur : 
```bash
http://localhost:8080
```

### Accéder à la documentation Swagger

Une fois l'application démarrée, Swagger est accessible à l'adresse suivante :
```bash
http://localhost:8080/swagger-ui.html
```

### Nettoyer le projet

```bash
mvn clean
```

---

## Déploiement avec Docker

### Construire l’image Docker

Assurez-vous que le projet a été compilé (`mvn clean install`) avant de construire l’image.

```bash
docker build -t bankaccount-app .
```

### Lancer le conteneur

```bash
docker run -d -p 8080:8080 --name bankaccount bankaccount-app
```
L’application sera accessible à l’adresse :
```bash
http://localhost:8080
```

### Accéder à Swagger dans le conteneur

```bash
http://localhost:8080/swagger-ui.html
```

### Arrêter et supprimer le conteneur

```bash
docker stop bankaccount && docker rm bankaccount
```

### Supprimer l’image Docker

```bash
docker rmi bankaccount-app
```

---
## Auteur
Ce projet est développé par Youssef Massaoudi dans le cadre d’un kata technique avec engagement qualité élevé