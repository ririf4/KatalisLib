import java.net.HttpURLConnection
import java.net.URI

plugins {
    alias(libs.plugins.kotlin)
    id("com.github.johnrengelman.shadow") version "8.1.1"
    `maven-publish`
}

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.johnrengelman.shadow")

    group = "net.ririfa"
    version = "1.0.0"

    repositories {
        mavenCentral()
        maven("https://repo.ririfa.net/maven2/") { name = "RiriFa Repository" }
        maven("https://maven.fabricmc.net/") { name = "FabricMC" }
        maven("https://repo.papermc.io/repository/maven-public/") { name = "PaperMC" }
    }
}

subprojects {
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

                from(components["java"])

                pom {
                    name.set(project.name)
                    description.set("")
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