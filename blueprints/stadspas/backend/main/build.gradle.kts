@file:Suppress("UNCHECKED_CAST")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream

plugins {
    war
    // Idea
    idea
    id("org.jetbrains.gradle.plugin.idea-ext")

    // Spring
    id("org.springframework.boot")
    id("io.spring.dependency-management")

    // Kotlin
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("plugin.allopen")

    // Docker-compose plugin
    id("com.avast.gradle.docker-compose")

    // Spring boot actuator generator
    id("com.gorylenko.gradle-git-properties")

    // Checkstyle
    id("com.diffplug.spotless")

    // SonarQube
    id("org.sonarqube")
}

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven { url = uri("https://repo.ritense.com/repository/maven-snapshots/") }
    maven { url = uri("https://repo.ritense.com/repository/maven-releases/") }
    maven { url = uri("https://s01.oss.sonatype.org/content/groups/staging/") }
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
}

val valtimo_version: String by project
val jacksonVersion: String by project

dependencyManagement {
    imports {
        mavenBom ("com.fasterxml.jackson:jackson-bom:$jacksonVersion")
    }
}

dependencies {

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Valtimo
    implementation("com.ritense.valtimo:audit:$valtimo_version")
    implementation("com.ritense.valtimo:besluit:$valtimo_version")
    implementation("com.ritense.valtimo:case:$valtimo_version")
    implementation("com.ritense.valtimo:catalogi-api:$valtimo_version")
    implementation("com.ritense.valtimo:connector:$valtimo_version")
    implementation("com.ritense.valtimo:contract:$valtimo_version")
    implementation("com.ritense.valtimo:core:$valtimo_version")
    implementation("com.ritense.valtimo:document:$valtimo_version")
    implementation("com.ritense.valtimo:documenten-api:$valtimo_version")
    implementation("com.ritense.valtimo:form-flow-valtimo:$valtimo_version")
    implementation("com.ritense.valtimo:form-link:$valtimo_version")
    implementation("com.ritense.valtimo:form:$valtimo_version")
    implementation("com.ritense.valtimo:keycloak-iam:$valtimo_version")
    implementation("com.ritense.valtimo:notes:$valtimo_version")
    implementation("com.ritense.valtimo:notificaties-api-authentication:$valtimo_version")
    implementation("com.ritense.valtimo:notificaties-api:$valtimo_version")
    implementation("com.ritense.valtimo:object-management:$valtimo_version")
    implementation("com.ritense.valtimo:objecten-api-authentication:$valtimo_version")
    implementation("com.ritense.valtimo:objecten-api:$valtimo_version")
    implementation("com.ritense.valtimo:objects-api:$valtimo_version")
    implementation("com.ritense.valtimo:openzaak-resource:$valtimo_version")
    implementation("com.ritense.valtimo:openzaak:$valtimo_version")
    implementation("com.ritense.valtimo:plugin-valtimo:$valtimo_version")
    implementation("com.ritense.valtimo:portaaltaak:$valtimo_version")
    implementation("com.ritense.valtimo:process-document:$valtimo_version")
    implementation("com.ritense.valtimo:smartdocuments:$valtimo_version")
    implementation("com.ritense.valtimo:test-utils-common:$valtimo_version")
    implementation("com.ritense.valtimo:verzoek:$valtimo_version")
    implementation("com.ritense.valtimo:web:$valtimo_version")
    implementation("com.ritense.valtimo:wordpress-mail:$valtimo_version")
    implementation("com.ritense.valtimo:zaken-api:$valtimo_version")

    // Postgresql
    implementation("org.postgresql:postgresql:42.4.1")

    // Kotlin logger
    implementation("io.github.microutils:kotlin-logging:3.0.4")

    // Testing
    testImplementation("com.ritense.valtimo:test-utils-common:$valtimo_version")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.camunda.bpm.assert:camunda-bpm-assert:15.0.0")
    testImplementation("org.camunda.bpm.extension:camunda-bpm-junit5:1.1.0")
    testImplementation("org.camunda.bpm.extension:camunda-bpm-assert:1.2")
    testImplementation("org.camunda.bpm.extension:camunda-bpm-assert-scenario:1.1.1")
    testImplementation("org.camunda.bpm.extension.mockito:camunda-bpm-mockito:5.16.0")
    testImplementation("org.mockito:mockito-core:4.4.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("com.h2database:h2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

apply(from = "gradle/environment.gradle.kts")
val configureEnvironment = extra["configureEnvironment"] as (task: ProcessForkOptions) -> Unit

tasks.bootRun {
    val t = this
    doFirst {
        configureEnvironment(t)
    }
    dependsOn(tasks.composeUp)
}

tasks.register<Exec>("removeProjectContainers") {
    group = "application"
    description = "Removes all containers for the current project. Use this to start from scratch."

    println("Attempting to remove all containers for project: ${project.name}")
    commandLine("docker", "ps", "-a", "--filter", "name=${project.name}", "-q")
    standardOutput = ByteArrayOutputStream()

    doLast {
        val containers = standardOutput.toString().trimIndent().split("\n")

        exec {
            val baseCommand = mutableListOf("docker", "rm")
            baseCommand.addAll(containers)

            commandLine(baseCommand)
        }
    }
}

apply(from = "gradle/test.gradle.kts")

// TODO find a fix to use docker-compose.gradle.kts read up on https://github.com/gradle/kotlin-dsl-samples/issues/448
// apply(from = "gradle/docker-compose.gradle.kts")
dockerCompose {
    setProjectName(name) // uses projectRoot.name as the container group name

    stopContainers.set(true) // doesn't call `docker-compose down` if set to false; default is true
    removeContainers.set(false) // containers are retained upon composeDown for persistent storage
    removeVolumes.set(false) // set to false while we use local keycloak

    createNested("testConfiguration").apply {
        isRequiredBy(tasks.named("integrationTest"))
        setProjectName("${name}-test")
        useComposeFiles.set(listOf("docker-compose-integration-test.yml"))
        removeVolumes.set(true)
    }
}

// This is a fix for https://github.com/avast/gradle-docker-compose-plugin/issues/199
gradle.taskGraph.afterTask {
    if (this.name == "bootRun" && state.failure != null) { // gracefully stop the containers should bootRun task fail
        logger.log(LogLevel.WARN, "Stopping docker containers gracefully.")
        tasks.composeDown.get().down()
    }
}
