FROM openjdk:12
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} Muro.jar
ENTRYPOINT ["java","-jar","/Muro.jar"]