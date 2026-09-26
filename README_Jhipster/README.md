# KIP

This application was generated using JHipster 9.3.0. You can find documentation and help in the JHipster 9.3.0 documentation archive.

KIP uses JHipster for application scaffolding and entity generation. The project's domain data is maintained separately using our team's manually maintained CSV files rather than JHipster-generated fake development data.

## Project Structure

Node is required for generation and recommended for development. `package.json` is generated for a better development experience with Prettier, commit hooks, scripts, and other development tools.

`/src/*` follows the default Java project structure.

Important project files and directories include:
- `.yo-rc.json` — Yeoman/JHipster configuration. JHipster configuration is stored under the `generator-jhipster` key.
- `.jhipster/*.json` — JHipster entity configuration files.
- `jhipster-jdl.jdl` — The project's source JDL defining entities, relationships, and validation constraints.
- `npmw` — Wrapper for the locally installed npm version.
- `/src/main/docker` — Docker configurations for the application and required services.
- `/src/main/resources/config/liquibase/` — Liquibase database migrations and data.
- `/src/main/resources/config/liquibase/fake-data/` — Team-maintained CSV data used to populate development data.

## Development

The build system automatically installs the recommended version of Node and npm.

When dependencies change in `package.json`, run:

```bash
./npmw install
```

We use npm scripts and Angular CLI with esbuild.

For development, run the backend and frontend in separate terminals:

```bash
./npmw run backend:start
```

```bash
./npmw run start
```

The `./npmw run` command lists the scripts available for this project.

### Using Angular CLI

Angular CLI can be used to generate custom client code.

For example:

```bash
ng generate component my-component
```

This may generate files such as:

```text
create src/main/webapp/app/my-component/my-component.html
create src/main/webapp/app/my-component/my-component.ts
update src/main/webapp/app/app.config.ts
```

For additional JHipster development documentation, see the [JHipster development documentation](https://www.jhipster.tech/development/).

## Project Data and JHipster Fake Data

The application has been re-scaffolded so that development environments use our team's manually maintained CSV data instead of JHipster-generated fake datasets.

The JDL/entity structure defines the database schema. The CSV files provide the actual development records that are loaded into those tables.

### Important: Do Not Reintroduce JHipster Fake Data

JHipster-generated fake data should not be used for KIP's domain data.

If you regenerate entities from the JDL, check:

```text
src/main/resources/config/liquibase/fake-data/
```

After regeneration, inspect the generated CSV files.
- If the files contain our human-readable KIP-specific data, preserve it.
- If JHipster has replaced the files with large amounts of generated/fake data, restore or replace them with the team's CSV data.
- Do not commit JHipster-generated mock records as KIP's development data.

The `.jhipster/*.json` files and generated application code should remain synchronized with `jhipster-jdl.jdl`.

## CSV Data Rules

All manually maintained CSV files under `src/main/resources/config/liquibase/fake-data/` must use a **semicolon (`;`) as the column delimiter**.

Do not use commas as the structural delimiter. This is important because KIP contains legal text, explanations, questions, and other fields that may contain commas.

### Correct CSV Format

```csv
id;text;outcome_text;correct;terminal_resolution;question_id;next_stage_id;feedback_id
1;"Pull over immediately to the right shoulder safely.";"Correct. Virginia law requires compliance.";true;POSITIVE;1;2;1
```

When creating or editing CSV files:
- Use `;` between columns.
- Use quotes around text fields when appropriate.
- Make sure CSV column names correspond to the generated entity/database structure.
- Populate all required fields.
- Make sure relationship IDs reference records that actually exist.
- Make sure values satisfy the constraints defined in the JDL.
- Preserve KIP-specific human-readable data.
- Do not replace KIP data with JHipster-generated fake data.

## Updating CSV Data Only

If you are **only changing CSV values or adding/removing CSV rows**, do **not** regenerate JHipster.

For example, changing fields inside:
- `topic_progress.csv`
- `stage_attempt.csv`
- `concept_progress.csv`
- `game.csv`
- `legal_content.csv`

does not require JHipster regeneration as long as the entity structure itself has not changed.

After changing CSV data, reset the local database and reload the current Liquibase data:

```bash
./gradlew liquibaseDropAll
```

Then start the application:

```bash
./gradlew
```

This rebuilds the local database and allows the current schema and CSV data to be loaded.

## 🔄 When to Regenerate JHipster

JHipster regeneration should be limited to changes that affect the generated application structure.

### Run JHipster regeneration when you:
- Modify `jhipster-jdl.jdl`.
- Add or remove an entity.
- Add or remove an entity field.
- Change a relationship.
- Change a validation constraint.
- Change global JHipster application configuration.
- Change the authentication type.
- Change the database type.
- Change the package structure.
- Change the frontend framework.
- Upgrade the JHipster generator version.

### Do NOT regenerate JHipster when you:
- Only modify CSV data.
- Add or remove rows from existing CSV files.
- Change development data values.
- Modify custom Java business logic.
- Modify custom Angular components.
- Modify custom services or controllers.
- Pull a teammate's already-generated JHipster changes.

## JHipster Regeneration Workflow

When the JDL changes and JHipster-generated files need to be updated, follow these sequential steps:

### 1. Backup or Commit Local Data
Verify that your team's manually maintained CSV files in `src/main/resources/config/liquibase/fake-data/` are correct. If they contain the expected KIP-specific, human-readable data, make sure you have them safely backed up or committed before proceeding.

### 2. Update the JDL File
Modify `jhipster-jdl.jdl` with your required schema changes.

### 3. Run the JHipster Regeneration Command
Run the import command to regenerate the core application architecture:

```bash
jhipster import-jdl jhipster-jdl.jdl --force --skip-fake-data
```

*Note: This updates the generated Java code, `.jhipster/*.json` entity configurations, Liquibase schemas, repositories, REST resources, and Angular entity views. Select **Yes** if JHipster asks to overwrite expected structural files.*

### 4. Verify the Generated Files
Inspect the updated file structures across your workspace to ensure uniformity with your JDL changes:
```text
.jhipster/
src/main/java/
src/main/resources/config/liquibase/
src/main/webapp/
```

### 5. Verify and Restore KIP CSV Data
Check `src/main/resources/config/liquibase/fake-data/` and confirm that JHipster has not wiped out or replaced KIP's data with fake records. Ensure that:
- The files use `;` as the delimiter.
- Required columns are present and relationship columns contain valid IDs.
- Values satisfy updated JDL validation constraints.

*If JHipster accidentally replaced these files with generated data, restore your backed-up or committed team CSV files now.*

### 6. Reset the Local Database
Once both the structural files and CSV datasets are verified, reset your database mapping:

```bash
./gradlew liquibaseDropAll
```

Then boot up the application:

```bash
./gradlew
```

This builds a clean local database using your updated schema configurations and seeds it successfully using your team's production-ready CSV data.


 ## Building for Production

 ### Packaging as JAR

 To build the final JAR:

```
./gradlew -Pprod clean bootJar
```

 Run it with:

```
java -jar build/libs/*.jar
```

 Then navigate to:

```
http://localhost:8080
```

 ### Packaging as WAR

```
./gradlew -Pprod -Pwar clean bootWar
```

 ## JHipster Control Center

 JHipster Control Center can help manage and control applications.

 Start it with:

```
docker compose -f src/main/docker/jhipster-control-center.yml up
```

 The local control center is available at:

```
http://localhost:7419
```

 ## Testing

 ### Spring Boot Tests

 Run:

```
./gradlew test integrationTest jacocoTestReport
```

 ### Client Tests

 Unit tests are run with Vitest:

```
./npmw test
```

 ## PWA Support

 JHipster ships with Progressive Web App support, which is disabled by default.

 To enable the service worker, update:

```
src/main/webapp/app/app.config.ts
```

 and configure:

```
ServiceWorkerModule.register('ngsw-worker.js', { enabled: false }),
```

 ## Managing Dependencies

 To add Leaflet as a runtime dependency:

```
./npmw install --save --save-exact leaflet
```

 To add TypeScript definitions:

```
./npmw install --save-dev --save-exact @types/leaflet
```

 Then import the required files in the Angular application.

 For example, in:

```
src/main/webapp/app/app.config.ts
```

```
import 'leaflet/dist/leaflet.js';
```

 And in:

```
src/main/webapp/content/scss/vendor.scss
```

```
@import 'leaflet/dist/leaflet.css';
```

 ## Docker Compose Support

 JHipster generates Docker Compose configuration files under:

```
src/main/docker/
```

 To start required services:

```
docker compose -f src/main/docker/services.yml up -d
```

 To stop them:

```
docker compose -f src/main/docker/services.yml down
```

 Spring Boot Docker Compose integration is enabled by default. It can be disabled in `application.yml`:

```
spring:
  docker:
    compose:
      enabled: false
```

 To build a Docker image:

```
npm run java:docker
```

 For ARM64/Apple Silicon:

```
npm run java:docker:arm64
```

 Then run:

```
docker compose -f src/main/docker/app.yml up -d
```

 ## Code Quality

 ### SonarQube

 Start the local SonarQube server:

```
docker compose -f src/main/docker/sonar.yml up -d
```

 Run analysis with:

```
./gradlew -Pprod clean check jacocoTestReport sonarqube -Dsonar.login=admin -Dsonar.password=admin
```

 Alternatively, configure credentials in `sonar-project.properties`:

```
sonar.login=admin
sonar.password=admin
```

 ## Continuous Integration

 JHipster's CI/CD sub-generator can be used to configure continuous integration:

```
jhipster ci-cd
```

 See the [JHipster 9.3.0 CI/CD documentation](<https://www.jhipster.tech/documentation-archive/v9.3.0/setting-up-ci/>) for details.

 ## References

 - [JHipster Homepage](<https://www.jhipster.tech/>)
- [JHipster 9.3.0 Documentation](<https://www.jhipster.tech/documentation-archive/v9.3.0>)
- [JHipster Development](<https://www.jhipster.tech/documentation-archive/v9.3.0/development/>)
- [JHipster Docker Compose](<https://www.jhipster.tech/documentation-archive/v9.3.0/docker-compose>)
- [JHipster Production](<https://www.jhipster.tech/documentation-archive/v9.3.0/production/>)
- [JHipster Testing](<https://www.jhipster.tech/documentation-archive/v9.3.0/running-tests/>)
- [JHipster Code Quality](<https://www.jhipster.tech/documentation-archive/v9.3.0/code-quality/>)
- [JHipster CI/CD](<https://www.jhipster.tech/documentation-archive/v9.3.0/setting-up-ci/>)
- [Node.js](<https://nodejs.org/>)
- [NPM](<https://www.npmjs.com/>)
- [Angular](<https://angular.dev/>)
- [Leaflet](<https://leafletjs.com/>)
- [DefinitelyTyped](<https://definitelytyped.org/>)


 - **Changed only CSV → don't run JHipster → `liquibaseDropAll` \+ `./gradlew`**
- **Changed JDL → regenerate JHipster → fix/verify CSVs → `liquibaseDropAll` \+ `./gradlew`**
- **Pulled teammate's generated changes → don't regenerate → reset DB if needed**
