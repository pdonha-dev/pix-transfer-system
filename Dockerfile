# ---------- Stage 1: build ----------
# Usa imagem com Maven + JDK só para compilar; nao vai pra imagem final (reduz superficie de ataque e tamanho)
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copia primeiro so o pom.xml para aproveitar cache de camadas do Docker nas dependencias
COPY pom.xml .

# Baixa dependencias (fica cacheado enquanto pom.xml nao mudar)
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -f pom.xml dependency:go-offline

COPY src ./src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests package \
    && cp target/*.jar app.jar

# ---------- Stage 2: runtime ----------
# Imagem enxuta, apenas com JRE - runtime nao precisa de compilador/ferramentas de build
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

# Usuario nao-root por seguranca
RUN groupadd -r spring && useradd -r -g spring spring
USER spring

COPY --from=build /workspace/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
