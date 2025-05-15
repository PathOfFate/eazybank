 # strart with a base imgae contaning java runtime
FROM openjdk:23-jdk-slim

# infromation around how maintence the image
LABEL "org.opencontainers.image.authors"="eazybytes.com"

# add applicaiton's jar to the image
COPY target/accounts-0.0.1-SNAPSHOT.jar accounts-0.0.1-SNAPSHOT.jar

# execute the application
ENTRYPOINT ["java", "-jar", "accounts-0.0.1-SNAPSHOT.jar"]
