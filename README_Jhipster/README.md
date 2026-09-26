

 KIP README

# KIP

 This application was generated using JHipster 9.2.0. You can find documentation and help in the [JHipster 9.2.0 documentation archive](<https://www.jhipster.tech/documentation-archive/v9.2.0>).

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

```
./npmw install
```

 We use npm scripts and Angular CLI with esbuild.

 For development, run the backend and frontend in separate terminals:

```
./npmw run backend:start
```

```
./npmw run start
```

 The `./npmw run` command lists the scripts available for this project.

 ### Using Angular CLI

 Angular CLI can be used to generate custom client code.

 For example:

```
ng generate component my-component
```

 This may generate files such as:

```
create src/main/webapp/app/my-component/my-component.html
create src/main/webapp/app/my-component/my-component.ts
update src/main/webapp/app/app.config.ts
```

 For additional JHipster development documentation, see the [JHipster development documentation](<https://www.jhipster.tech/development/>).

 ## Project Data and JHipster Fake Data

 The application has been re-scaffolded so that development environments use our team's manually maintained CSV data instead of JHipster-generated fake datasets.

 The JDL/entity structure defines the database schema. The CSV files provide the actual development records that are loaded into those tables.

 ### Important: Do Not Reintroduce JHipster Fake Data

 JHipster-generated fake data should not be used for KIP's domain data.

 If you regenerate entities from the JDL, check:

```
src/main/resources/config/liquibase/fake-data/
```

 After regeneration, inspect the generated CSV files.

 - If the files contain our human-readable KIP-specific data, preserve it.
- If JHipster has replaced the files with large amounts of generated/fake data, restore or replace them with the team's CSV data.
- Do not commit JHipster-generated mock records as KIP's development data.

 The `.jhipster/*.json` files and generated application code should remain synchronized with `jhipster-jdl.jdl`.

 ## CSV Data Rules

 All manually maintained CSV files under:

```
src/main/resources/config/liquibase/fake-data/
```

 must use a **semicolon (`;`) as the column delimiter**.

 Do not use commas as the structural delimiter.

 This is important because KIP contains legal text, explanations, questions, and other fields that may contain commas.

 ### Correct CSV Format

```
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

 For example, changing:

```
topic_progress.csv
stage_attempt.csv
concept_progress.csv
game.csv
legal_content.csv
```

 does not require JHipster regeneration as long as the entity structure itself has not changed.

 After changing CSV data, reset the local database and reload the current Liquibase data:

```
./gradlew liquibaseDropAll
```

 Then start the application:

```
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

 ## 🔄 JHipster Regeneration Workflow

 When the JDL itself changes, use the following workflow.

 ### 1\. Pull the Latest Changes

 Before making regeneration changes:

```
git pull
```

 Make sure you have the latest `jhipster-jdl.jdl` and generated project files.

 ### 2\. Check the Existing CSV Data

 Go to:

```
src/main/resources/config/liquibase/fake-data/
```

 Check the CSV files before regeneration.

 If they contain the expected KIP-specific, human-readable data, make sure you have it backed up or committed before proceeding.

 If JHipster has replaced the files with generated fake data, restore the team's intended CSV data.

 ### 3\. Wipe Existing JHipster Metadata

 If a clean JHipster regeneration is required, remove the existing JHipster metadata.

 #### Windows PowerShell

```
Remove-Item -Recurse -Force -ErrorAction SilentlyContinue .jhipster, .jhipster-blueprint, .yo-rc.json
```

 #### Mac/Linux

```
rm -rf .jhipster/ .jhipster-blueprint/ .yo-rc.json
```

 ### 4\. Clear the Gradle Build

```
./gradlew clean
```

 ### 5\. Regenerate From the JDL

 Run:

```
jhipster import-jdl jhipster-jdl.jdl
```

 If JHipster asks whether existing files should be overwritten, select **Yes** when the overwrite is expected.

 After regeneration, inspect the generated files and restore/preserve the team's manually maintained CSV data as necessary.

 ### 6\. Verify the CSV Files

 Check:

```
src/main/resources/config/liquibase/fake-data/
```

 Confirm that:

 - The files use `;` as the delimiter.
- Required columns are present.
- Relationship columns use the expected IDs.
- Required fields are populated.
- Validation constraints are satisfied.
- KIP's human-readable data is present.
- JHipster-generated fake records have not replaced the team's data.

 ### 7\. Reset and Start the Application

 Once the generated structure and CSV data are correct:

```
./gradlew liquibaseDropAll
```

 Then:

```
./gradlew
```

 This gives you a clean local database using the current generated schema and CSV data.

 ## 👥 Pulling Teammate JHipster Changes

 If a teammate changes the JDL and regenerates the application, you generally **do not need to regenerate JHipster again after pulling their changes**.

 Pull their committed generated files:

```
git pull
```

 Then reset your local database if their changes affect the schema or development data:

```
./gradlew liquibaseDropAll
./gradlew
```

 Only run JHipster yourself if you are making a new JDL or application-generation change.

 ## Updating the JDL and CSV Data Together

 When an entity structure changes, update both the JDL and the corresponding CSV data.

 Recommended sequence:

 1. Update `jhipster-jdl.jdl`.
2. Regenerate the JHipster application.
3. Inspect the generated entity and Liquibase files.
4. Update the corresponding CSV files.
5. Verify relationships and required fields.
6. Reset the local database with `./gradlew liquibaseDropAll`.
7. Start the application with `./gradlew`.
8. Verify the application and database.
9. Commit the JDL, generated configuration/code, Liquibase changes, and CSV data together.

 ## Required Action for Existing Local Environments

 If you ran an older version of KIP locally and your database contains old JHipster-generated mock data, reset your local database before testing the current data:

```
./gradlew liquibaseDropAll
```

 Then:

```
./gradlew
```

 This ensures your local database is rebuilt from the current schema and current development data.

 > **Do not run JHipster simply to refresh CSV data.** Only regenerate when the JDL or JHipster application structure has changed.

 ## Troubleshooting

 ### Liquibase Database Lock Error

 If the application previously crashed, Liquibase may have left a database lock.

 If you see:

```
Waiting for changelog lock...
```

 connect to the database and release the lock:

```
UPDATE DATABASECHANGELOGLOCK
SET LOCKED = 0,
    LOCKGRANTED = NULL,
    LOCKEDBY = NULL
WHERE ID = 1;
```

 ### Liquibase DropAll Fails

 Depending on the SQL dialect and existing foreign-key constraints, `liquibaseDropAll` may fail.

 For MySQL/MariaDB, if necessary, manually recreate the development database:

```
DROP DATABASE IF EXISTS `knowledge_is_power`;
CREATE DATABASE `knowledge_is_power`;
```

 Then restart the application.

 ### Git Conflicts in `.jhipster/*.json`

 If you have local entity changes and encounter conflicts after pulling teammate changes:

```
git stash
git pull origin <branch-name>
git stash pop
```

 Resolve conflicts carefully and make sure the resulting `.jhipster/*.json` files agree with `jhipster-jdl.jdl`.

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

 See the [JHipster 9.2.0 CI/CD documentation](<https://www.jhipster.tech/documentation-archive/v9.2.0/setting-up-ci/>) for details.

 ## References

 - [JHipster Homepage](<https://www.jhipster.tech/>)
- [JHipster 9.2.0 Documentation](<https://www.jhipster.tech/documentation-archive/v9.2.0>)
- [JHipster Development](<https://www.jhipster.tech/documentation-archive/v9.2.0/development/>)
- [JHipster Docker Compose](<https://www.jhipster.tech/documentation-archive/v9.2.0/docker-compose>)
- [JHipster Production](<https://www.jhipster.tech/documentation-archive/v9.2.0/production/>)
- [JHipster Testing](<https://www.jhipster.tech/documentation-archive/v9.2.0/running-tests/>)
- [JHipster Code Quality](<https://www.jhipster.tech/documentation-archive/v9.2.0/code-quality/>)
- [JHipster CI/CD](<https://www.jhipster.tech/documentation-archive/v9.2.0/setting-up-ci/>)
- [Node.js](<https://nodejs.org/>)
- [NPM](<https://www.npmjs.com/>)
- [Angular](<https://angular.dev/>)
- [Leaflet](<https://leafletjs.com/>)
- [DefinitelyTyped](<https://definitelytyped.org/>)


 - **Changed only CSV → don't run JHipster → `liquibaseDropAll` \+ `./gradlew`**
- **Changed JDL → regenerate JHipster → fix/verify CSVs → `liquibaseDropAll` \+ `./gradlew`**
- **Pulled teammate's generated changes → don't regenerate → reset DB if needed**
