plugins {
	java
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("nu.studer.jooq") version "10.1"
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

val jooqVersion = "3.20.5"

// === Зависимости приложения и тестов ===
dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jooq")

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
    implementation("org.jooq:jooq:$jooqVersion")
    jooqGenerator("org.jooq:jooq:$jooqVersion")
    jooqGenerator("org.jooq:jooq-codegen:$jooqVersion")
    jooqGenerator("org.jooq:jooq-meta:$jooqVersion")
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

                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        includes = ".*"
                        excludes = "flyway_schema_history|pgp_armor_headers"
                    }

                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isPojos = true
                        isTables = true
                        isImmutablePojos = true
                        isFluentSetters = true
                    }

                    target.apply {
                        packageName = "com.github.giga_chill.jooq.generated"
                        directory = "build/generated-sources/jooq"
                    }
                }
            }
        }
    }
}

tasks.register("applyMigrations") {
    dependsOn("classes")
    
    doLast {
        val isWindows = System.getProperty("os.name").contains("Windows", ignoreCase = true)
        val command = if (isWindows) {
            listOf("powershell", "-File", "./apply-migrations.ps1")
        } else {
            listOf("bash", "./apply-migrations.sh")
        }
        
        exec {
            environment(
                "DB_HOST" to (System.getenv("DB_HOST") ?: "localhost"),
                "DB_PORT" to (System.getenv("DB_PORT") ?: "5432"),
                "DB_NAME" to (System.getenv("DB_NAME") ?: "gigachill"),
                "DB_USER" to (System.getenv("DB_USER") ?: "postgres"),
                "DB_PASSWORD" to (System.getenv("DB_PASSWORD") ?: "postgres")
            )
            commandLine(command)
        }
    }
}