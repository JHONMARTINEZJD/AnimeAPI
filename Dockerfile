FROM amazoncorretto:17-alpine-jdk

COPY target/AnimeAPI-0.0.1-SNAPSHOT.jar /animeapi-v1.jar

ENTRYPOINT ["java", "-jar", "/animeapi-v1.jar"]
