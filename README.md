# Coffee Website - Full Stack Java Project

## Technologies
- HTML, CSS, JavaScript
- Java Servlets
- Apache Tomcat 10.1+
- MySQL
- JDBC
- Maven

## Folder structure
frontend files are inside `src/main/webapp`.
Java backend is inside `src/main/java/com/coffee`.
Database script is `database/schema.sql`.

## Setup
1. Install Java 17+, Maven, Tomcat 10.1+, and MySQL.
2. Create the database by running `database/schema.sql`.
3. Open `src/main/java/com/coffee/DBConnection.java` and change:
   - DB_USER
   - DB_PASSWORD
4. From the project folder run:
   `mvn clean package`
5. Copy `target/CoffeeWebsite.war` to Tomcat's `webapps` folder.
6. Start Tomcat.
7. Open:
   `http://localhost:8080/CoffeeWebsite/`

## API endpoints
GET  /api/menu
POST /api/orders
POST /api/contact

The frontend calls these endpoints automatically.
