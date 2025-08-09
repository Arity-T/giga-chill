import org.openapitools.generator.gradle.plugin.tasks.GenerateTask
import com.github.gradle.node.npm.task.NpmInstallTask

plugins {
	java
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("nu.studer.jooq") version "10.1"
    id("com.diffplug.spotless") version "6.19.0"
    id("org.openapi.generator") version "7.14.0"
    id("com.github.node-gradle.node") version "7.0.2"
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
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
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


// === Пути для файлов генерации ===
val specMainPath = System.getenv("OPEN_API_MAIN_SPECIFICATION")
val specTestPath = System.getenv("OPEN_API_TEST_SPECIFICATION")
var bundledPath = "$buildDir/generated/api/bundled.yaml"
var mergedPath = "$buildDir/generated/api/merged.yaml"

// === Установка Node ===
node {
    download.set(true) // скачиваем Node.js автоматически
    version.set("20.16.0")
    npmVersion.set("10.8.2")
}

// === Установка зависимостей для Node ===
val npmInstallDeps by tasks.registering(Exec::class) {
    group = "openapi"
    description = "Устанавливает swagger-cli и openapi-merge локально"

    commandLine("npm", "install", "--no-save", "swagger-cli", "@redocly/cli")
}

// === Сборка основного API ===
val bundleApi by tasks.registering(Exec::class) {
    group = "openapi";
    description = "Сборка основного файла API"

    dependsOn(npmInstallDeps)
    inputs.file(specMainPath)
    outputs.file(bundledPath)
    commandLine(
        "npx", "swagger-cli", "bundle",
        specMainPath,
        "--type", "yaml",
        "-o", bundledPath
    )
}

// === Слияние с API для тестов ===
val mergeSpecs by tasks.registering(Exec::class) {
    group = "openapi";
    description = "Слияние с API для тестов"

    dependsOn(bundleApi)
    inputs.files(specTestPath, bundledPath)
    outputs.file(mergedPath)
    commandLine(
        "$projectDir/node_modules/.bin/redocly",
        "join", bundledPath, specTestPath,
        "--output", mergedPath
    )
}

// === Задача для генерации основного API ===
val generateOpenApi by tasks.registering(GenerateTask::class) {
    group = "openapi"
    description = "Генерация кода по основному API"
    dependsOn(mergeSpecs)

    validateSpec.set(false)
    generatorName.set("spring")
    inputSpec.set(mergedPath)
    outputDir.set("$buildDir/generated/api")
    apiPackage.set("com.github.giga_chill.gigachill.web.api")
    modelPackage.set("com.github.giga_chill.gigachill.web.api.model")
    invokerPackage.set("com.github.giga_chill.gigachill.web.api.invoker")
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




