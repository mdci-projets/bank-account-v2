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

| Composant       | Technologie                         |
|-----------------|-------------------------------------|
| Langage         | Java 17                             |
| Build tool      | Maven                               |
| Framework       | Spring Boot 3 (à venir)             |
| Base de données | H2 (in-memory, à venir)             |
| Mapping         | MapStruct (à venir)                 |
| Validation      | Jakarta Validation (à venir)        |
| Tests unitaires | JUnit 5, Mockito                    |
| Tests intégrés  | Testcontainers (à venir)            |
| CI/CD           | GitLab CI                           |
| Containerisation| Docker (à venir)                    |
| Documentation   | Swagger / OpenAPI (à venir)         |

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

## Fonctionnalités

- ✅ Créer un compte bancaire
- ✅ Déposer de l'argent
- ✅ Retirer de l'argent (avec vérification de solde)
- ✅ Règles métier encapsulées (Value Object `Money`)
- ✅ Couverture par tests unitaires (TDD)

---

## Lancer les tests

```bash
mvn clean test
```

## 🛠️ En cours / TODO
- [x] Créer la logique métier autour du compte bancaire
- [x] Ajout des historiques d'opérations pour les comptes
- [x] Extraire une factory `BankOperationFactory` pour injecter `Clock` et UUID
- [x] Développer la couche service avec `BankAccountService` & `BankOperationService`
- [x] Créer la persistance avec JPA
- [x] Ajout de DTOs avec MapStruct
- [x] Exposer l'API REST (Spring Boot)
- [x] Validation des entrées via @Valid
- [ ] Tests d'intégration SpringBootTest
- [ ] Containerisation Docker
- [ ] Pipeline CI/CD complète GitLab
- [ ] Documentation Swagger

## Auteur
Ce projet est développé par Youssef Massaoudi dans le cadre d’un kata technique avec engagement qualité élevé