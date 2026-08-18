## Web-chess

A full-featured chess platform built as a bachelor's thesis project, combining the game itself with a social component.

The application is written in Java (initially JDK 17, current JDK 25) using Spring Boot 4.1 as the main framework. As for the database, the application uses SQL Server. The frontend stack includes Thymeleaf for generating HTML pages, vanilla CSS and TypeScript. It also features a locally installed Stockfish engine.

## Installation

### Prerequisites

1. Clone the repository

```bash
git clone git@github.com:BBVALENTIN/web-chess.git
cd <way to the directory>
```

2. Set the required environment variables

-- The database connection
```bash
spring.datasource.url= your connection string
spring.datasource.username=${DB_NAME}
spring.datasource.password=${DB_PASSWORD}
```
-- Optional, can be deleted - in src/main/java/com/sah/config/TwitchConfig.java
```bash
TWITCH_CLIENT_ID=<twitch_client_id>
TWITCH_CLIENT_SECRET=<twitch_client_secret>
```

3. Install frontend dependencies and run TypeScript:
```bash
npm install
npm run build
```
- Aditionally, you can the run the minifier, which parses and bundles every file with this naming *-index.ts.
```bash
npm run bundle
```
4. Complie the scss files into css, the outdir by default is in src/main/resources/static/css
```bash
npm run build:css
```

5. Build and run the backend
```bash
mvn clean install
mvn spring-boot:run
```

6. The app will be available on the displayed port

## Assets / External resources

This project uses audio files from Lichess (https://lichess.org) for in-game sound effects.

Their GitHub repository: [https://github.com/lila]

This project uses stockfish.js from the official stockfish.js repository.

Their Github repository: [https://github.com/lichess-org/stockfish.js]

Official Stockfish repository: [https://github.com/official-stockfish/stockfish]

All rights belong to their respective authors. All assets are used under terms of use.
