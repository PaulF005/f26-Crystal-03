
# KIP
This application was generated using JHipster 9.3.0. You can find documentation and help in the JHipster 9.3.0 documentation archive.

KIP uses JHipster for application scaffolding and entity generation. The project's domain data is maintained separately using our team's manually maintained CSV files rather than JHipster-generated fake development data.
## Prerequisites
Before starting development, make sure you have:- Docker Desktop running- Node.js installed- Java/Gradle available- The repository cloned locally

*Note: Docker must be running because the local development database uses MySQL.*
## Project Structure
Node is required for generation and recommended for development. `package.json` is generated for a better development experience with Prettier, commit hooks, scripts, and other development tools.
`/src/*` follows the default Java project structure.

Important project files and directories include:
- `.yo-rc.json` — JHipster/Yeoman configuration. JHipster configuration is stored under the `generator-jhipster` key.
- `.jhipster/*.json` — JHipster entity configuration files.
- `jhipster-jdl.jdl` — The project's source JDL defining entities, relationships, and validation constraints.
- `npmw` — Wrapper for the locally installed npm version.
- `src/main/docker/` — Docker configurations for the application and required services.
- `src/main/resources/config/liquibase/` — Liquibase database migrations and data.
- `src/main/resources/config/liquibase/fake-data/` — Team-maintained CSV data used to populate development data.
- `src/main/webapp/` — Angular frontend application workspace.
- `src/main/java/` — Spring Boot backend java source files.
## Development Setup
The build system automatically installs the recommended version of Node and npm.

When dependencies change in `package.json`, run:
```bash
./npmw install
```

We use npm scripts and Angular CLI with esbuild. For development, run the backend and frontend in separate terminals:
```bash
./npmw run backend:start
```
```bash
./npmw run start
```

Open your browser to development address: `http://localhost:9000`

The `./npmw run` command lists all scripts available for this project.
### Using Angular CLI
Angular CLI can be used to generate custom client code. For example:
```bash
ng generate component my-component
```

This may generate files such as:
```text
create src/main/webapp/app/my-component/my-component.html
create src/main/webapp/app/my-component/my-component.ts
update src/main/webapp/app/app.config.ts
```

For additional JHipster development documentation, see the JHipster development documentation.
## Project Data and JHipster Fake Data
The application has been re-scaffolded so that development environments use our team's manually maintained CSV data instead of JHipster-generated fake datasets. The JDL/entity structure defines the database schema. The CSV files provide the actual development records that are loaded into those tables.
### Important: Do Not Reintroduce JHipster Fake Data
JHipster-generated fake data should not be used for KIP's domain data. Never replace KIP data with JHipster-generated fake records. If you regenerate entities from the JDL, check: `src/main/resources/config/liquibase/fake-data/`

After regeneration, inspect the generated CSV files:- If the files contain our human-readable KIP-specific data, preserve it.- If JHipster has replaced the files with large amounts of generated/fake data, restore or replace them with the team's CSV data.- Do not commit JHipster-generated mock records as KIP's development data.

The `.jhipster/*.json` files and generated application code should remain synchronized with `jhipster-jdl.jdl`.
### Student Profile IDs
When adding or referencing data that maps to the seeded user profile relationships inside the CSV datasets, use these explicit values:
- Rachel → `user_profile_id = 1`
- Chewie → `user_profile_id = 2`
## CSV Data Rules
All manually maintained CSV files under `src/main/resources/config/liquibase/fake-data/` must use a **semicolon (`;`) as the column delimiter**.

Do not use commas as the structural delimiter. This is important because KIP contains legal text, explanations, questions, and other fields that may contain commas.
### Correct CSV Format```csv
id;text;outcome_text;correct;terminal_resolution;question_id;next_stage_id;feedback_id
1;"Pull over immediately to the right shoulder safely.";"Correct. Virginia law requires compliance.";true;POSITIVE;1;2;1
```

When creating or editing CSV files:
- Use `;` between columns.- Use quotes around text fields when appropriate.- Make sure CSV column names correspond to the generated entity/database structure.- Populate all required fields.- Make sure relationship IDs reference records that actually exist.- Make sure values satisfy the constraints defined in the JDL.- Preserve KIP-specific human-readable data.
## Resetting the Development Database
Reset the local database when setting up from a fresh checkout, pulling significant database updates, or encountering foreign-key, authentication, or Liquibase checksum errors.

**Windows**```cmd
.\gradlew liquibaseDropAll liquibaseClearChecksums
.\gradlew
```

**macOS/Linux**```bash
./gradlew liquibaseDropAll liquibaseClearChecksums
./gradlew
```

*Warning: liquibaseDropAll deletes the local database tables. Do not run it if you need to preserve uncommitted local development data.*
### Development Test Accounts
The development database includes the following seeded accounts:
- User: Rachel | Login: `rthomas` | Password: `admin`
- User: Chewie | Login: `mablegirl` | Password: `admin`
## Updating CSV Data Only
If you are **only changing CSV values or adding/removing CSV rows**, do **not** regenerate JHipster. For example, changing fields inside:
- `topic_progress.csv`
- `stage_attempt.csv`
- `concept_progress.csv`
- `game.csv`
- `legal_content.csv`

does not require JHipster regeneration as long as the entity structure itself has not changed. After changing CSV data, reset the local database and reload the current Liquibase data:
```bash
./gradlew liquibaseDropAll
./gradlew
```

This rebuilds the local database and allows the current schema and CSV data to be loaded.
## 🔄 When to Regenerate JHipster
JHipster regeneration should be limited to changes that affect the generated application structure.
### Run JHipster regeneration when you:- Modify `jhipster-jdl.jdl`.- Add or remove an entity.- Add or remove an entity field.- Change a relationship.- Change a validation constraint.- Change global JHipster application configuration.- Change the authentication type.- Change the database type.- Change the package structure.- Change the frontend framework.- Upgrade the JHipster generator version.
### Do NOT regenerate JHipster when you:- Only modify CSV data.- Add or remove rows from existing CSV files.- Change development data values.- Modify custom Java business logic.- Modify custom Angular components.- Modify custom services or controllers.- Pull a teammate's already-generated JHipster changes.
### Quick Reference Matrix
| Change Scenario | Regenerate JHipster? | Reset Database? |
| :--- | :---: | :---: |
| Change CSV values | No | Yes |
| Add/remove CSV rows | No | Yes |
| Change Java code | No | Usually no |
| Change Angular code | No | No |
| Pull teammate's generated changes | No | If needed |
| Change JDL Schema | Yes | Yes |
| Add/remove entity fields | Yes | Yes |
| Change relationships | Yes | Yes |
| Change validation constraints | Yes | Yes |
## JHipster Regeneration Workflow
When the JDL changes and JHipster-generated files need to be updated, follow these sequential steps:
### 1. Backup or Commit Local DataVerify that your team's manually maintained CSV files in `src/main/resources/config/liquibase/fake-data/` are correct. If they contain the expected KIP-specific, human-readable data, make sure you have them safely backed up or committed before proceeding.
### 2. Update the JDL FileModify `jhipster-jdl.jdl` with your required schema changes.
### 3. Run the JHipster Regeneration CommandRun the import command to regenerate the core application architecture:
```bash
jhipster import-jdl jhipster-jdl.jdl --force --skip-fake-data
```

*Note: This updates the generated Java code, `.jhipster/*.json` entity configurations, Liquibase schemas, repositories, REST resources, and Angular entity views. Select **Yes** if JHipster asks to overwrite expected structural files.*
### 4. Verify the Generated FilesInspect the updated file structures across your workspace to ensure uniformity with your JDL changes:```text
.jhipster/
src/main/java/
src/main/resources/config/liquibase/
src/main/webapp/
```
### 5. Verify and Restore KIP CSV DataCheck `src/main/resources/config/liquibase/fake-data/` and confirm that JHipster has not wiped out or replaced KIP's data with fake records. Ensure that:
- The files use `;` as the delimiter.- Required columns are present and relationship columns contain valid IDs.- Values satisfy updated JDL validation constraints.

*If JHipster accidentally replaced these files with generated data, restore your backed-up or committed team CSV files now.*
### 6. Reset the Local DatabaseOnce both the structural files and CSV datasets are verified, reset your database mapping:
```bash
./gradlew liquibaseDropAll
```

Then boot up the application:
bash ./gradlew 
This builds a clean local database using your updated schema configurations and seeds it successfully using your team's production-ready CSV data.
## Testing## Backend
Execute unit and integration testing reports with:
bash ./gradlew test integrationTest jacocoTestReport 
## Frontend
Frontend unit tests use Vitest:
bash ./npmw test 
## Building for Production## Packaging as JAR
To build the final JAR production package:
bash ./gradlew -Pprod clean bootJar 
Run it locally with:
bash java -jar build/libs/*.jar 
Then navigate to: http://localhost:8080
## Packaging as WAR
bash ./gradlew -Pprod -Pwar clean bootWar 
## Docker Containerization
All Docker Compose configurations are located in src/main/docker/.
## Manage Background Services
bash docker compose -f src/main/docker/services.yml up -d docker compose -f src/main/docker/services.yml down 
## Build and Run Application Image
Build the container:
bash ./npmw run java:docker 
For ARM64 / Apple Silicon setups run:
bash ./npmw run java:docker:arm64 
Bring up the containerized application:
bash docker compose -f src/main/docker/app.yml up -d 
## JHipster Control Center
Start monitoring tooling via:
bash docker compose -f src/main/docker/jhipster-control-center.yml up 
Open instance via: http://localhost:7419
## Code Quality & SonarQube
Spin up SonarQube infrastructure container:
bash docker compose -f src/main/docker/sonar.yml up -d 
Execute static code analysis pipeline:
bash ./gradlew -Pprod clean check jacocoTestReport sonarqube \ -Dsonar.login=admin \ -Dsonar.password=admin 
Note: Credentials can alternatively be stored directly in sonar-project.properties.
## Optional Features## Progressive Web App (PWA)
PWA support is disabled by default. Configuration resides in src/main/webapp/app/app.config.ts:
typescript ServiceWorkerModule.register('ngsw-worker.js', { enabled: false, }), 
## Leaflet Maps Integration
Install core dependency and types mapping:
bash ./npmw install --save --save-exact leaflet ./npmw install --save-dev --save-exact @types/leaflet 
Import statement structure inside custom elements:
typescript import 'leaflet/dist/leaflet.js'; 
Append stylesheet inclusion within src/main/webapp/content/scss/vendor.scss:
scss @import 'leaflet/dist/leaflet.css'; 
## Continuous Integration
JHipster's pipeline wrapper can generate base infrastructure mappings natively:
bash jhipster ci-cd 
Review the JHipster 9.3.0 CI/CD documentation for additional provider guides.
## Troubleshooting & Common Fixes## 1. Liquibase Checksum / Validation Errors
Symptoms: Backend crashes on startup with ValidationFailedException or checksum mismatches after pulling updates from git or editing custom parameters.
Resolution: Clear your local liquibase history log constraints and re-initialize the schemas.

* Mac/Linux: ./gradlew liquibaseDropAll liquibaseClearChecksums followed by ./gradlew
* Windows: .\gradlew liquibaseDropAll liquibaseClearChecksums followed by .\gradlew

## 2. Foreign-Key Constraint Failures Natively During Database Seed
Symptoms: java.sql.SQLIntegrityConstraintViolationException during the application initialization process.
Resolution: This happens if your team-maintained CSV entries point to missing data relations.

   1. Inspect the crashing entity name provided in your terminal stack trace logs.
   2. Locate the corresponding source file inside src/main/resources/config/liquibase/fake-data/.
   3. Verify that all values assigned to relationship tracking fields (e.g., question_id, next_stage_id) map directly to an id that actually exists in the related CSV file.

## 3. Delimiter & Parsing Broken Rows
Symptoms: Text contains structural cutoffs, fields leak across multiple columns, or data columns mismatch.
Resolution: Ensure commas (,) have not been added as columns delimiters.

   1. Open the broken .csv file with a plain-text engine (e.g., VS Code, Vim, Notepad).
   2. Check that the first declaration index uses a semicolon (;) separator.
   3. Verify that text columns containing internal punctuation symbols are securely enclosed with quote characters ("...").

## 4. Seeded Authentication Issues or Login Failures
Symptoms: Accessing the application at http://localhost:9000 prompts credential rejections for rthomas or mablegirl.
Resolution: Local database storage configurations can become stale after migrations.

   1. Run a database teardown with ./gradlew liquibaseDropAll.
   2. Boot the platform fresh using ./gradlew to correctly re-seed the context parameters.
   3. Confirm that your entry references match the user profile settings (user_profile_id = 1 for Rachel, user_profile_id = 2 for Chewie).

## 5. JHipster Overwrote KIP Domain Records with Fake Mock Data
Symptoms: Human-readable text strings disappear and get replaced by randomized alphanumeric placeholder texts or lorem-ipsum generation blocks.
Resolution: JHipster defaults to overwrite configuration items if flags are dropped.

   1. Discard modified tracking items via git checkout: git checkout -- src/main/resources/config/liquibase/fake-data/
   2. Next time you migrate rules, ensure you append the explicit bypass parameters:
   bash jhipster import-jdl jhipster-jdl.jdl --force --skip-fake-data 







