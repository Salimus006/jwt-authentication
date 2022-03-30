FROM openjdk:11-jre-slim
VOLUME /tmp
ADD target/jwt-authentication*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
