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

| Composant       | Technologie                  |
|-----------------|------------------------------|
| Langage         | Java 21                      |
| Build tool      | Maven                        |
| Framework       | Spring Boot 3 (à venir)      |
| Base de données | H2 (in-memory, à venir)      |
| Mapping         | MapStruct (à venir)          |
| Validation      | Jakarta Validation (à venir) |
| Tests unitaires | JUnit 5, Mockito             |
| Tests intégrés  | Testcontainers (à venir)     |
| CI/CD           | GitLab CI                    |
| Containerisation| Docker (à venir)             |
| Documentation   | Swagger / OpenAPI (à venir)  |

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

---

## Améliorations prévues
- [ ]  Ajout de logs métiers et techniques (via SLF4J/Logback)
- [ ] Déploiement du projet sur AWS (EC2 ou Elastic Beanstalk)
- [ ] Ajout d’une authentification JWT pour sécuriser les endpoints
- [ ] Surveillance et métriques avec Spring Actuator / Prometheus / Grafana
- [ ] Passer sur une base de donnée PostgreSQL par exemple
- [ ] Augmenter la couverture de test (tests de mapping, de configuration, etc.)
- [ ] Utilisation de Testcontainers pour les tests d'intégration avec PostgreSQL

---

## À venir

- Implémentation de notifications (par e-mail ou webhook)
- Historique enrichi : catégorisation, libellés, exports PDF
- Multi-comptes et agrégation par utilisateur

## Lancer les tests

```bash
mvn clean test
```

## Auteur
Ce projet est développé par Youssef Massaoudi dans le cadre d’un kata technique avec engagement qualité élevé