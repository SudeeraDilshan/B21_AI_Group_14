# QA Automation Framework

This project contains an automated testing framework for the **QA Training Application** using industry-standard tools for UI and API testing.

## Tech Stack
* **Selenium WebDriver:** For UI Testing
* **REST Assured:** For API Testing
* **Cucumber (Behavior-Driven Development):** For writing test cases in plain English
* **JUnit 5:** Test runner
* **Allure:** For generating rich, graphical test reports

## Prerequisites
Before running the tests, ensure you have the following installed on your machine:
* **Java** (JDK 17 or higher recommended)
* **Maven**
* **MySQL** Server (Running locally with a database named `qa_training`)
* **Google Chrome** browser (for Selenium headless UI tests)

## Getting Started

### 1. Start the Target Application
The application we are testing is a Spring Boot Java app packaged as a `.jar` file. It requires a local MySQL database connection. 

Open a terminal at the project root and start the application:
```powershell
java -jar application\qa-training-app.jar --spring.datasource.username=root --spring.datasource.password=YOUR_MYSQL_PASSWORD
```
*(Make sure to replace `YOUR_MYSQL_PASSWORD` with your actual local MySQL root password. You also need to have created an empty `qa_training` database).*

### 2. Run the Automated Tests
Once the application is running (usually on `http://localhost:8080`), open a **new** terminal window and navigate to the `automation-framework` directory:
```powershell
cd automation-framework
```

Run the tests using Maven:
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
