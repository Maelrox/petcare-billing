FROM bellsoft/liberica-openjdk-alpine:21

WORKDIR /app

COPY build/libs/billing-0.1.jar billing.jar

ENTRYPOINT ["java", "-jar", "billing.jar"]