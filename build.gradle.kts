plugins {
    `maven-publish`
    id("java")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val jacksonVersion = "2.15.2"
dependencies {
    implementation("io.flamingock:flamingock-core-api:1.0.0-SNAPSHOT")

    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

group = "io.flamingock"
version = "1.0.3-SNAPSHOT"

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "io.flamingock"
            artifactId = "graalvm-core"
        }
    }
    repositories {
        mavenLocal()
    }
}

tasks.test {
    useJUnitPlatform()
}
