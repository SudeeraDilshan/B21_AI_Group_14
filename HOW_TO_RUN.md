# How to Run This Project
### IS3440 QA Automation Framework — B21 AI Group 14

---

## Prerequisites Checklist

Before you start, make sure you have all of these installed:

| Tool | Version Required | Check Command |
|---|---|---|
| Java JDK | 17 or higher | `java -version` |
| Maven (`mvn`) or Maven Daemon (`mvnd`) | Any recent | `mvn -version` or `mvnd -version` |
| MySQL Server | 8.x recommended | `mysql --version` |
| Google Chrome | Latest | Open Chrome → Help → About |

> **Note for this machine:** Maven Daemon (`mvnd`) is installed at `C:\Program Files\Apache\Maven\bin\mvnd.cmd`. Use `mvnd` in place of `mvn` in all commands below. Java 25.0.2 is installed and available.

---

## Step 1 — Set Up the MySQL Database

The QA Training App stores its data in MySQL. You need to create the database before starting the app.

### Open MySQL command line:
```powershell
mysql -u root -p
```
Enter your root password when prompted.

### Create the database:
```sql
CREATE DATABASE qa_training;
EXIT;
```

> The database name must be exactly `qa_training` (lowercase, underscore).

---

## Step 2 — Start the QA Training Application

The application is a Spring Boot JAR file located at `application\qa-training-app.jar`.

Open a terminal in the **project root** (`B21_AI_Group_14\`) and run:

```powershell
java -jar application\qa-training-app.jar --spring.datasource.username=root --spring.datasource.password=YOUR_MYSQL_PASSWORD
```

Replace `YOUR_MYSQL_PASSWORD` with your actual MySQL root password.

If your MySQL password matches what's already in `application\application.properties` (password is `admin`), you can simply run:

```powershell
java -jar application\qa-training-app.jar
```

### Verify the app is running:
Open a browser and go to: **http://localhost:8080/ui/login**

You should see a login page. Use these credentials to confirm the app works:
- **Admin login:** username `admin`, password `admin123`
- **User login:** username `testuser`, password `test123`

> Keep this terminal open. The app must stay running while tests execute.

---

## Step 3 — Run the Automated Tests

Open a **new** terminal window (keep the app terminal open), then navigate to the automation framework folder:

```powershell
cd "d:\Project\ITQA\B21_AI_Group_14\automation-framework"
```

### Run all tests:

**If `mvn` is in your PATH:**
```powershell
mvn clean test
```

**If using Maven Daemon (this machine):**
```powershell
& "C:\Program Files\Apache\Maven\bin\mvnd.cmd" clean test
```

**Or add Maven to PATH first, then use `mvn`:**
```powershell
$env:PATH += ";C:\Program Files\Apache\Maven\bin"
mvnd clean test
```

### What happens during the test run:
1. Maven downloads any missing dependencies (first run only — may take a few minutes)
2. Chrome browser windows open automatically for UI tests
3. API tests run in the background without opening a browser
4. You will see step-by-step output in the terminal (e.g. `✓ Given I am logged in as Admin`)
5. Results are saved to `automation-framework\target\allure-results\`

> **Chrome must be installed** on the machine. Selenium will launch it automatically — you don't need to open Chrome manually.

---

## Step 4 — View the Test Report

After the tests finish, generate the visual Allure HTML report:

**Using `mvn`:**
```powershell
mvn allure:serve
```

**Using Maven Daemon (this machine):**
```powershell
& "C:\Program Files\Apache\Maven\bin\mvnd.cmd" allure:serve
```

This command:
1. Processes the raw test result files from `target\allure-results\`
2. Starts a local web server
3. Automatically opens the report in your default browser

The report shows:
- Total pass / fail / skip counts
- Each scenario with step-by-step details
- Charts and timelines
- Error messages and screenshots for failures

---

## Running a Specific Feature File Only

To run only one feature file (e.g. just the API tests), use a filter tag or specify the feature in the runner. Currently all feature files run together. To run a subset, you can temporarily rename other `.feature` files or configure a tag filter.

---

## Project Files Reference

| File | Location | Purpose |
|---|---|---|
| Spring Boot App | `application\qa-training-app.jar` | The app being tested |
| App Config | `application\application.properties` | Database URL, port, credentials |
| Maven Config | `automation-framework\pom.xml` | Libraries and build settings |
| Test Launcher | `automation-framework\src\test\java\runners\TestRunner.java` | Entry point for test run |
| API Tests | `automation-framework\src\test\resources\features\DashboardAPI_215516N.feature` | 10 API test scenarios |
| Dashboard UI Tests | `automation-framework\src\test\resources\features\DashboardUI_215516N.feature` | 10 dashboard UI scenarios |
| Plants UI Tests | `automation-framework\src\test\resources\features\PlantsUI_215517T.feature` | 22 plants page UI scenarios |
| Health Check Tests | `automation-framework\src\test\resources\features\HealthCheck.feature` | 2 health check scenarios |
| API Step Definitions | `automation-framework\src\test\java\stepdefinitions\DashboardAPISteps.java` | Java code for API tests |
| Dashboard UI Steps | `automation-framework\src\test\java\stepdefinitions\DashboardUISteps.java` | Java code for dashboard UI tests |
| Plants UI Steps | `automation-framework\src\test\java\stepdefinitions\PlantsUISteps.java` | Java code for plants UI tests |
| Test Results | `automation-framework\target\allure-results\` | Raw test data (auto-generated) |
| Allure Report | `automation-framework\target\allure-report\` | HTML report (auto-generated) |

---

## Troubleshooting

### "Connection refused" or tests fail immediately
- The app is not running. Go back to Step 2 and start the JAR.
- Check that port 8080 is not blocked by a firewall or used by another app.

### "Unknown database 'qa_training'" error on startup
- You haven't created the MySQL database. Run Step 1.

### Chrome crashes or Selenium errors
- Make sure Google Chrome is installed.
- If Chrome was recently updated, Selenium 4.21 should auto-download the matching ChromeDriver — no manual driver install needed.
- If you see `DevToolsActivePort file doesn't exist`, try rebooting Chrome instances.

### "mvn is not recognized" error
- Maven is not in your system PATH.
- Use the full path: `& "C:\Program Files\Apache\Maven\bin\mvnd.cmd" clean test`
- Or add it to PATH: `$env:PATH += ";C:\Program Files\Apache\Maven\bin"`

### Tests run but all fail with "Login failed"
- The app is running but credentials don't match. Check `application.properties` — the database may not be seeded with default users.
- Check MySQL that the `qa_training` database has data (tables and user records).

### Allure report shows no data
- Run `mvn clean test` first to generate results in `target\allure-results\`.
- Then run `mvn allure:serve` to build the report from those results.

---

## Quick Reference — All Commands in Order

```powershell
# 1. Create MySQL database (one-time setup)
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS qa_training;"

# 2. Start the application (keep this terminal open)
java -jar "d:\Project\ITQA\B21_AI_Group_14\application\qa-training-app.jar"

# 3. In a new terminal — run all tests
cd "d:\Project\ITQA\B21_AI_Group_14\automation-framework"
& "C:\Program Files\Apache\Maven\bin\mvnd.cmd" clean test

# 4. View the Allure report
& "C:\Program Files\Apache\Maven\bin\mvnd.cmd" allure:serve
```
