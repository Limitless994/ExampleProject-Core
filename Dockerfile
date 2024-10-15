FROM openjdk:21

ENV KEYCLOAK_BASE_URL=http://localhost:8080
ENV KEYCLOAK_REALM=ExampleRealm
ENV KEYCLOAK_AUTH_URL=/realms/ExampleRealm/protocol/openid-connect/token
ENV KEYCLOAK_USERS_URL=/admin/realms/ExampleRealm/users
ENV KEYCLOAK_CLIENT_ID=example-application-client
ENV KEYCLOAK_CLIENT_SECRET=your-client-secret
ENV KEYCLOAK_USERS_MANAGER_EMAIL=usersManager@test.com
ENV KEYCLOAK_USERS_MANAGER_PASSWORD=test

ARG JAR_FILE=target/*.jar
COPY ./target/core-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar",  "/app.jar"]