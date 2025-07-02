plugins {
	java
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("nu.studer.jooq") version "8.2"
}

group = "com.github.giga-chill"
version = "0.0.1-SNAPSHOT"

// === Java toolchain ===
java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

// === Зависимости приложения и тестов ===
dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jooq")

    // Flyway (миграции будут работать только при запуске приложения)
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.1")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.1")
    runtimeOnly("io.jsonwebtoken:jjwt-gson:0.12.1")

    // PostgreSQL драйвер для приложения
    runtimeOnly("org.postgresql:postgresql")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Тесты
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // jOOQ codegen
    jooqGenerator("org.jooq:jooq-codegen")
    jooqGenerator("org.jooq:jooq-meta")
    jooqGenerator("org.postgresql:postgresql")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

sourceSets["main"].java.srcDir("build/generated-sources/jooq")

// === jOOQ codegen конфигурация ===
jooq {
    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(false) // чтобы не генерировать на каждой сборке (можно true)

            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN

                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://localhost:5432/gigachill"
                    user = "postgres"
                    password = "postgres"
                }

                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"

                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                    }

                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                    }

                    target.apply {
                        packageName = "com.github.giga_chill.jooq.generated"
                        directory = "build/generated/sources/jooq"
                    }
                }
            }
        }
    }
}

