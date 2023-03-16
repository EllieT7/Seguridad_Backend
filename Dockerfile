# Con jar
# FROM eclipse-temurin:17
# COPY target/*.jar app.jar

# ENV USERNAME_DB "postgres"   
# ENV PASSWORD "password"
# ENV URL "jdbc:postgresql://localhost:5432/arqui"

# ENTRYPOINT ["java", "-jar", "/app.jar"]

# Con clases
FROM --platform=linux/x86_64 eclipse-temurin:17-jdk-alpine as build
# FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# RUN chmod +x mvnw 
# CMD ["./mvnw", "install", "-DskipTests"]
# RUN yum install maven

# Make the maven wrapper script executalbe (needed when running on Windows)
RUN chmod +x ./mvnw
# Change the line endint to unix-style (needed when running on Windows)
RUN dos2unix mvnw

# RUN apk add --no-cache maven
RUN ./mvnw install -DskipTests
# RUN mvn install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
# CMD ["mkdir", "-p", "target/dependency", "&&", "(cd", "target/dependency;", "jar", "-xf", "../*.jar)"]
# CMD ./mvnw install -DskipTests; mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)


FROM eclipse-temurin:17
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENV USERNAME_DB "postgres"   
ENV PASSWORD "password"
ENV URL "jdbc:postgresql://localhost:5432/arqui"

ENTRYPOINT ["java","-cp","app:app/lib/*","arquitectura.software.demo.CurrencyApiKotlinApplicationKt"]