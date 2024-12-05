import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

plugins {
    `maven-publish`
    id("java")
}

repositories {
    mavenCentral()
    mavenLocal()
}

group = "io.flamingock"
version = getFlamingockReleasedVersion()

val jacksonVersion = "2.15.2"
val flamingockVersion = "latest.release"
dependencies {
    implementation("io.flamingock:flamingock-core-api:$flamingockVersion")

    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}



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


fun getFlamingockReleasedVersion(): String {
    val metadataUrl = "https://repo.maven.apache.org/maven2/io/flamingock/flamingock-core/maven-metadata.xml"
    try {
        val metadata = URL(metadataUrl).readText()
        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val inputStream = metadata.byteInputStream()
        val document = documentBuilder.parse(inputStream)
        return document.getElementsByTagName("latest").item(0).textContent
    } catch (e: Exception) {
        throw RuntimeException("Cannot obtain Flamingock's latest version")
    }
}