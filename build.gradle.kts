import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("java")
    id("org.graalvm.buildtools.native") version "0.10.3"

    id("org.jetbrains.kotlin.jvm") version "1.8.10"
}

group = "io.flamingock"
version = "1.0.2-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

val libVersion = "1.0.1"
gradlePlugin {
    plugins {
        create("autoConfigurePlugin") {
            id = "io.flamingock.graalvmPlugin"
            implementationClass = "io.flamingock.graalvm.CopyConfigurationPlugin"
        }
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
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "17"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "17"
}