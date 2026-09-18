# ConnecTN — Backend

Spring Boot API for **ConnecTN**, a community platform for Tunisian NGOs: organisations
publish campaigns and crowdfunding drives, members donate, join events and challenges, form
groups, chat, go live, and buy from an NGO shop. Team project, ESPRIT (2025).

Frontend: [ConnecTN-FRONT](https://github.com/yosrikhiari/ConnecTN-FRONT) (Angular 19).

## Domain

| Area | Entities / controllers |
|---|---|
| **NGOs and members** | `OrganisationNG`, `Membre`, `User`, `Role`, `RoleOng`, `RoleRecommande`, `Competence`, document verification, role-update requests |
| **Campaigns and crowdfunding** | `CrowdFunding`, `Milestones`, `RewardTier`, `Donation`, `CampaignUpdates`, `CampaignComment`, `CampaignAnalytics`, `PromoCode` |
| **Events and challenges** | `Event`, `EventParticipation`, `Challenge`, `Test` / `Question` / `OptionReponse` / `Reponse` / `Resultat` (quizzes with results) |
| **Community** | `Group`, `Post`, `Comment`, `Reaction`, `Chat` / `Message`, `LiveSession` / `LiveComment` (live streams over WebSocket), `Notification`, `Issue` / `IssueComment` (reporting) |
| **Shop** | `Shop`, `ShopApplication`, `ShopCategory`, `ShopRegion`, `ShopReview`, `Product` |
| **AI** | `ChatbotController` / `ChatbotService` (a Rasa server), `HuggingFaceService` (inference API) |

## Stack

- **Java 17, Spring Boot 3** — REST controllers, JPA repositories, WebSocket (STOMP) for chat,
  live sessions and notifications, Spring Mail for verification and notifications
- **MySQL** (`pidatabase`)
- **Keycloak** — OAuth 2.0 / OIDC (`Keycloak/realm-export.json`), admin client for user
  provisioning
- **Rasa** for the chatbot, **Hugging Face** inference API, **Stripe** for payments (keys via
  environment)

## Configuration

Secrets are read from environment variables (see `src/main/resources/application.properties`):

```
MYSQL_PASSWORD  KEYCLOAK_CLIENT_SECRET  KEYCLOAK_ADMIN_USERNAME  KEYCLOAK_ADMIN_PASSWORD
MAIL_USERNAME   MAIL_PASSWORD           HUGGINGFACE_TOKEN        STRIPE_API_KEY
```

Local services expected: MySQL on `3306`, Keycloak with the `connect` realm, Rasa on `5005`
(actions on `5055`).

## Run

```bash
export MYSQL_PASSWORD=… KEYCLOAK_CLIENT_SECRET=… KEYCLOAK_ADMIN_USERNAME=… KEYCLOAK_ADMIN_PASSWORD=… MAIL_USERNAME=… MAIL_PASSWORD=…
./mvnw spring-boot:run
```

## Layout

```
src/main/java/.../connectn     controllers, services, repositories, entities
src/main/resources             application.properties, tessdata/ (OCR data for document verification)
Keycloak/                      realm export
```
