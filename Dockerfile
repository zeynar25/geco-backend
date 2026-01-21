FROM maven:3.9-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-alpine
ARG GLTFPACK_URL=""
# If a GLTFPACK_URL build-arg is provided, install curl and download the binary
RUN if [ -n "$GLTFPACK_URL" ]; then \
			apk add --no-cache curl ca-certificates && \
			curl -L -o /usr/local/bin/gltfpack "$GLTFPACK_URL" && \
			chmod +x /usr/local/bin/gltfpack; \
		fi
COPY --from=build /target/*.jar demo.jar
EXPOSE 8080
ENV GLTFPACK=/usr/local/bin/gltfpack
ENTRYPOINT [ "java", "-jar", "demo.jar" ]
