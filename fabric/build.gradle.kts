import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.fabric.loom)
}

version = "1.0.0"

val includeBundles by configurations.creating {
    isTransitive = true
    isCanBeConsumed = false
    isCanBeResolved = true
}

fun includeBundle(bundle: Provider<*>) {
    dependencies.add("includeBundles", bundle)
}

dependencies {
    minecraft(libs.minecraft)
    mappings(libs.yarn)

    includeBundle(libs.bundles.yacla)
    includeBundle(libs.bundles.langMan)
}

gradle.projectsEvaluated {
    includeBundles.resolvedConfiguration.resolvedArtifacts.forEach { artifact ->
        val id = artifact.moduleVersion.id
        val notation = "${id.group}:${id.name}:${id.version}"
        println("Automatically including transitive dep: $notation")
        dependencies.add("include", notation)
    }
}

java {
    withSourcesJar()
    withJavadocJar()

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks.withType<JavaCompile> {
    options.release.set(17)
}

tasks.named<ProcessResources>("processResources") {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}
