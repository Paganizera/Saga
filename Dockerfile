FROM maven:3.9.11-eclipse-temurin-25 AS build

WORKDIR /opt/saga

COPY ../pom.xml  assembly.xml ./
COPY ./src/main ./src/main

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B clean package -DskipTests

FROM maven:3.9.11-eclipse-temurin-25

WORKDIR /opt/saga

COPY --from=build /opt/blog/target/blog-*.jar saga.jar

CMD java -jar saga.jar
