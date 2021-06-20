import org.gradle.jvm.tasks.Jar

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.5.0"
}

group = "cz.cvut.fit"
version = "0.0.1"
application {
    mainClass.set("cz.cvut.fit.ApplicationKt")
}

repositories {
    mavenCentral()
    jcenter()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    // Core
    implementation("io.ktor", "ktor-server-core", ktorVersion)
    implementation("io.ktor","ktor-server-netty",ktorVersion)
    implementation("ch.qos.logback", "logback-classic" , logbackVersion)
    // Auth
    implementation("io.ktor", "ktor-auth", ktorVersion)
    implementation("io.ktor", "ktor-auth-jwt", ktorVersion)

    // Hashing
    implementation("org.mindrot", "jbcrypt", "0.4")
    implementation("org.hashids", "hashids", "1.0.3")
    // Serialization
    implementation("io.ktor", "ktor-jackson", ktorVersion)
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", "2.9.8")

    // Exposed
    implementation("org.jetbrains.exposed", "exposed-core", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-dao", exposedVersion)
    implementation("org.jetbrains.exposed","exposed-jdbc", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-java-time", exposedVersion)

    // Dependency injection
    implementation("org.kodein.di:kodein-di:7.5.0")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:7.5.0")

    // In-memory database
    implementation("com.h2database", "h2", "1.3.148")

    // Testing
    testImplementation("io.ktor", "ktor-server-tests", ktorVersion)
    testImplementation("io.kotest","kotest-runner-junit5","4.4.3")
    testImplementation("io.kotest", "kotest-assertions-core", "4.4.3")
    testImplementation("io.kotest", "kotest-assertions-ktor", "4.4.3")
}

val fatJar = task("fatJar", type = Jar::class) {
    baseName = "${project.name}-fat"
    manifest {
        attributes["Implementation-Title"] = "Srt fat jar"
        attributes["Implementation-Version"] = version
        attributes["Main-Class"] = "cz.cvut.fit.ApplicationKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}
tasks {
    "build" {
        dependsOn(fatJar)
    }
}
