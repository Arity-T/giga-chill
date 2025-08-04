import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
	java
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("nu.studer.jooq") version "10.1"
    id("com.diffplug.spotless") version "6.19.0"
    id("org.openapi.generator") version "7.14.0"
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

// === spotless конфигурация ===
spotless {
    java {
        target("src/**/*.java")
        // Форматирование для Java файлов
        googleJavaFormat().aosp()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()

        // Исключить сгенерированные JOOQ файлы
        targetExclude("**/build/generated-sources/**", "**/build/generated/**")
    }
}

// === Зависимости приложения и тестов ===
dependencies {

    //Swagger API
    implementation("io.swagger.core.v3:swagger-annotations:2.2.9")
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Mapper
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("io.github.cdimascio:dotenv-java:3.2.0")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.1")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.1")
    runtimeOnly("io.jsonwebtoken:jjwt-gson:0.12.1")

    // PostgreSQL драйвер для приложения
    runtimeOnly("org.postgresql:postgresql")

    // Logging
    implementation("net.logstash.logback:logstash-logback-encoder:8.1")

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

// === Задача для генерации основного API ===
val generateMainApi by tasks.registering(GenerateTask::class) {
    group = "openapi"
    description = "Генерация кода по основному API"
    val specMainPath = System.getenv("OPEN_API_MAIN_SPECIFICATION")

    validateSpec.set(false)
    generatorName.set("spring")
    inputSpec.set(specMainPath)
    outputDir.set("$buildDir/generated/api/main")
    apiPackage.set("com.github.giga_chill.gigachill.web.api.main")
    modelPackage.set("com.github.giga_chill.gigachill.web.api.main.model")
    invokerPackage.set("com.github.giga_chill.gigachill.web.api.main.invoker")
    configOptions.set(
        mapOf(
            "useJakartaEe" to "true",
            "interfaceOnly" to "true",
            "skipDefaultInterface" to "true",
            "dateLibrary" to "java8",
            "useBeanValidation" to "false",
            "sourceFolder" to ""
        )
    )
}

// === Задача для генерации API для тестов===
val generateTestApi by tasks.registering(GenerateTask::class) {
    group = "openapi"
    description = "Генерация кода по API для тестов"
    val specTestPath = System.getenv("OPEN_API_TEST_SPECIFICATION")


    validateSpec.set(false)
    generatorName.set("spring")
    inputSpec.set(specTestPath)
    outputDir.set("$buildDir/generated/api/test")
    apiPackage.set("com.github.giga_chill.gigachill.web.api.test")
    modelPackage.set("com.github.giga_chill.gigachill.web.api.test.model")
    invokerPackage.set("com.github.giga_chill.gigachill.web.api.test.invoker")
    configOptions.set(
        mapOf(
            "useJakartaEe" to "true",
            "interfaceOnly" to "true",
            "skipDefaultInterface" to "true",
            "dateLibrary" to "java8",
            "useBeanValidation" to "false",
            "sourceFolder" to ""
        )
    )
}

val generateAllApis by tasks.registering {
    group = "openapi"
    description = "Сгенерировать все API сразу"
    dependsOn(generateTestApi)
}


tasks.withType<Test> {
	useJUnitPlatform()
}

val dbHost = System.getenv("DB_HOST")
val dbPort = System.getenv("DB_PORT")
val dbName = System.getenv("DB_NAME")
val dbUser = System.getenv("DB_USER")
val dbPassword = System.getenv("DB_PASSWORD")

val jdbcUrl = "jdbc:postgresql://$dbHost:$dbPort/$dbName"

sourceSets["main"].java.srcDir("build/generated-sources/jooq")
sourceSets["main"].java.srcDir("build/generated/api")

// === jOOQ codegen конфигурация ===
jooq {
    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(false) // чтобы не генерировать на каждой сборке (можно true)

            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN

                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = jdbcUrl
                    user = dbUser
                    password = dbPassword
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




