FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests


FROM tomcat:10.1-jdk17

RUN rm -rf /usr/local/tomcat/webapps/ROOT

COPY --from=build /app/target/CoffeeWebsite.war /usr/local/tomcat/webapps/CoffeeWebsite.war

EXPOSE 8080

CMD ["catalina.sh", "run"]