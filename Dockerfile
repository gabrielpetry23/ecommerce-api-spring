# build
FROM maven:3.9.9-amazoncorretto-24 as build
WORKDIR /build

COPY . .

RUN mvn clean package -DskipTests

# run
FROM amazoncorretto:24
WORKDIR /app

COPY --from=build /build/target/*.jar ./ecommerceapi.jar

EXPOSE 8080
EXPOSE 9090

ENV DATASOURCE_URL=''
ENV DATASOURCE_USERNAME=''
ENV DATASOURCE_PASSWORD=''
ENV GOOGLE_CLIENT_ID=''
ENV GOOGLE_CLIENT_SECRET=''
ENV EMAIL_USERNAME = ''
ENV EMAIL_PASSWORD = ''

ENV SPRING_PROFILES_ACTIVE=prod
ENV TZ=America/Sao_Paulo

ENTRYPOINT ["java", "-jar", "ecommerceapi.jar"]