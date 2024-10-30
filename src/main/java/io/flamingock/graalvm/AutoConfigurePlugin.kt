package io.flamingock.graalvm

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.register

class AutoConfigurePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val copyAnnotatedClasses = project.tasks.register<Copy>("copyAnnotatedClasses") {
            dependsOn("compileJava")
            from("${project.buildDir}/generated/sources/annotationProcessor/java/main/" + FlamingockGraalvmStatics.CONFIGURATION_FILE)
            into("${project.projectDir}/src/main/resources/META-INF/")
        }

        // Ensure that `processResources` depends on `copyAnnotatedClasses`
        project.tasks.named("processResources") {
            dependsOn(copyAnnotatedClasses)
        }
    }
}
