@file:Suppress("PropertyName")

import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.HttpURLConnection
import java.net.URI

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.shadow)
    `maven-publish`
}

val KATALIS_PAPER = "katalis-paper"
val KATALIS_FABRIC = "katalis-fabric"

val KATALIS_PAPER_VERSION = "1.0.0"
val KATALIS_FABRIC_VERSION = "1.0.0"


allprojects {
    group = "net.ririfa"

    repositories {
        mavenCentral()
        maven("https://repo.ririfa.net/maven2/") { name = "RiriFa Repository" }
        maven("https://maven.fabricmc.net/") { name = "FabricMC" }
        maven("https://repo.papermc.io/repository/maven-public/") { name = "PaperMC" }
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")
    apply(plugin = "com.gradleup.shadow")

    afterEvaluate {
        dependencies {
            runtimeOnly("${libs.yacla.core.get()}:all")
            runtimeOnly("${libs.yacla.yaml.get()}:all")
            runtimeOnly("${libs.yacla.json.get()}:all")

            runtimeOnly("${libs.langman.core.get()}:fat")
            runtimeOnly("${libs.langman.yaml.get()}:fat")

            runtimeOnly(libs.cask)

            runtimeOnly("${libs.beacon.get()}:all")
        }
    }

    when (name) {
        KATALIS_PAPER -> version = KATALIS_PAPER_VERSION
        KATALIS_FABRIC -> version = KATALIS_FABRIC_VERSION
    }

    java { withSourcesJar() }

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks.shadowJar {
        archiveClassifier.set("all")

        relocate("tools.jackson", "net.ririfa.yacla.libs.jackson")
        relocate("org.snakeyaml", "net.ririfa.yacla.libs.snakeyaml")

        dependencies {
            exclude(dependency("org.jetbrains.kotlin:.*"))
            exclude(dependency("org.jetbrains.kotlinx:.*"))
            exclude(dependency("org.jetbrains:annotations"))
        }

        exclude("META-INF/*.kotlin_module")
        exclude("META-INF/services/*kotlin*")
        exclude("META-INF/*kotlin*")
    }

    tasks.withType<JavaCompile>().configureEach { options.release.set(21) }

    tasks.withType<KotlinCompile>().configureEach { compilerOptions { jvmTarget.set(JvmTarget.JVM_21) } }

    tasks.withType<PublishToMavenRepository>().configureEach {
        onlyIf {
            val artifactId = project.name
            val ver = project.version.toString()
            val repoUrl = if (ver.endsWith("SNAPSHOT")) {
                "https://repo.ririfa.net/maven2-snap/"
            } else {
                "https://repo.ririfa.net/maven2-rel/"
            }

            val artifactUrl = "${repoUrl}net/ririfa/$artifactId/$ver/$artifactId-$ver.jar"
            logger.lifecycle("Checking existence of artifact at: $artifactUrl")

            val connection = URI(artifactUrl).toURL().openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = 3000
            connection.readTimeout = 3000

            val exists = connection.responseCode == HttpURLConnection.HTTP_OK
            connection.disconnect()

            if (exists) {
                logger.lifecycle("Artifact already exists at $artifactUrl, skipping publish.")
                false
            } else {
                logger.lifecycle("Artifact not found at $artifactUrl, proceeding with publish.")
                true
            }
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()

                from(components["shadow"])

                pom {
                    name.set(project.name)
                    description.set("Contains a set of libraries written by me, packaged into one JAR for easy integration.")
                    url.set("https://github.com/ririf4/KatalisLib")
                    licenses {
                        license {
                            name.set("MIT")
                            url.set("https://opensource.org/license/mit")
                        }
                    }
                    developers {
                        developer {
                            id.set("ririfa")
                            name.set("RiriFa")
                            email.set("main@ririfa.net")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/ririf4/KatalisLib.git")
                        developerConnection.set("scm:git:ssh://github.com/ririf4/KatalisLib.git")
                        url.set("https://github.com/ririf4/KatalisLib")
                    }
                }
            }
        }
        repositories {
            maven {
                val releasesRepoUrl = uri("https://repo.ririfa.net/maven2-rel/")
                val snapshotsRepoUrl = uri("https://repo.ririfa.net/maven2-snap/")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

                credentials {
                    username = findProperty("nxUN").toString()
                    password = findProperty("nxPW").toString()
                }
            }
        }
    }
}