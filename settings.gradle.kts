pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net/") { name="FabricMC" }
    }
}

rootProject.name = "katalis"

fun safeInclude(name: String, path: String) {
    val dir = file(path)
    if (dir.exists()) {
        include(name)
        project(":$name").projectDir = dir
    }
}

safeInclude("katalis-paper", "katalis/paper")
safeInclude("katalis-fabric", "katalis/fabric")
