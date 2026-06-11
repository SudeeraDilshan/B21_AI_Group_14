# QA Automation Framework

This project contains an automated testing framework for the **QA Training Application** using industry-standard tools for UI and API testing.

## Tech Stack
* **Selenium WebDriver:** For UI Testing
* **REST Assured:** For API Testing
* **Cucumber (Behavior-Driven Development):** For writing test cases in plain English
* **JUnit 5:** Test runner
* **Allure:** For generating rich, graphical test reports

## Prerequisites
Before running the tests, ensure you have the following installed on your machine and accessible from your terminal (added to your system PATH). You can verify them using the following commands:

* **Java** (JDK 17 or higher recommended)
  ```powershell
  java -version
  ```
* **Maven**
  ```powershell
  mvn -version
  ```
* **MySQL** Server
  ```powershell
  mysql --version
  ```
* **Google Chrome** browser (for Selenium UI tests)

### Database Setup
You need a local MySQL database named `qa_training`. You can create it using the following command (you will be prompted for your MySQL root password):
```powershell
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS qa_training;"
```

## Getting Started

### 1. Start the Target Application
The application we are testing is a Spring Boot Java app packaged as a `.jar` file. It requires a local MySQL database connection. 

Open a terminal at the project root and start the application:
```powershell
java -jar application\qa-training-app.jar --spring.datasource.username=root --spring.datasource.password=YOUR_MYSQL_PASSWORD
```
*(Make sure to replace `YOUR_MYSQL_PASSWORD` with your actual local MySQL root password).*

### 2. Run the Automated Tests
Once the application is running (usually on `http://localhost:8080`), open a **new** terminal window and navigate to the `automation-framework` directory:
```powershell
cd automation-framework
```

Run all tests using Maven:
```powershell
mvn clean test
```

### 3. View the Test Report
After the tests have finished running, you can generate and serve the graphical Allure report by running:
```powershell
mvn allure:serve
```
This will process the test results and automatically open the report in your default web browser.

## Project Structure
* `application/`: Contains the target application (`qa-training-app.jar`) and its properties file.
* `automation-framework/`: The Maven project containing the test framework.
  * `src/test/resources/features/`: Contains the Cucumber `.feature` files written in Gherkin.
  * `src/test/java/stepdefinitions/`: Contains the Java code that executes the steps defined in the feature files.
  * `src/test/java/runners/`: Contains the JUnit TestRunner configuration.

## Guidelines
* **Database Reset:** Always start with a fresh instance of the database or clear existing test data before running the tests to avoid state-related failures. You can drop and recreate the DB:
  ```powershell
  mysql -u root -p -e "DROP DATABASE IF EXISTS qa_training; CREATE DATABASE qa_training;"
  ```
* **Browser Drivers:** Ensure your Chrome browser is up to date. The framework uses WebDriverManager which handles driver binaries, but it requires a matching browser version.
* **Headless Mode:** By default, UI tests might run in headless mode depending on configuration. If you need to watch the test execution, check the WebDriver configuration in the framework.
* **Test Failures:** If a test fails, check the generated Allure report. It provides screenshots and detailed logs for UI and API tests respectively.
* **Running Specific Tags:** You can run specific test scenarios by passing Cucumber tags via Maven. For example, to run only UI tests:
  ```powershell
  mvn test -Dcucumber.filter.tags="@UI"
  ```
