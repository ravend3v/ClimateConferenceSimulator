# Climate Conference Simulation

## Overview
The Climate Conference Simulation is a Java-based application that simulates various aspects of a climate conference. The simulation includes customer movements between different service points and provides a visual representation using JavaFX.

## Initial setup
1. Ensure you have a `.env` file in the root of the project containing the following environment variables:
  - `MONGODB_URI`: The connection string for your MongoDB database.
  - `DB_NAME`: The name of the database to use.

   You can refer to the `.env.example` file for guidance on setting up these variables.

## Running the project 
- To run the project open a console and type the following command:
  ```sh
  mvn clean javafx:run

## Unit tests
- Unit tests are located in the `src/test/java/test` folder.
- To run the unit tests, open a console and navigate to the project directory, then use the following command:
  ```sh
  mvn test