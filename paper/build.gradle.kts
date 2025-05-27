import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("de.eldoria.plugin-yml.paper") version "0.7.1"
}

dependencies {
    compileOnly(libs.paper)

    paperLibrary(libs.yacla)
}

java {
    withSourcesJar()
    withJavadocJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks.withType<JavaCompile> {
    options.release.set(21)
}

paper {
    main = "net.ririfa.katalis.paper.KatalisLibPaper"
    loader = "net.ririfa.katalis.paper.KatalisLibPaperPluginLoader"
    generateLibrariesJson = true
    apiVersion = "1.21"
    version = rootProject.version.toString()
    name = "KatalisLib"
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    authors = listOf("RiriFa")
    description = "Contains a set of libraries written by me, packaged into one JAR for easy integration."
}