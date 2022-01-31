FROM openjdk:11
COPY target/rate_limiting-0.0.1.jar rate_limiting-0.0.1.jar
ENTRYPOINT ["java","-jar","/rate_limiting-0.0.1.jar"]