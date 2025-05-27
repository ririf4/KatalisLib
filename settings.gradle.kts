pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net/") { name="FabricMC" }
    }
}

rootProject.name = "katalis"

include("fabric")
project(":fabric").name = "katalis-fabric"

include(":paper")
project(":paper").name = "katalis-paper"