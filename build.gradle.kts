plugins {
    alias(libs.plugins.kotlin)
}

allprojects {
    apply(plugin = "kotlin")

    group = "net.ririfa"
    version = "1.0.0"

    repositories {
        mavenCentral()
        maven("https://repo.ririfa.net/maven2/") { name = "RiriFa Repository" }
        maven("https://maven.fabricmc.net/") { name = "FabricMC" }
        maven("https://repo.papermc.io/repository/maven-public/") { name = "PaperMC" }
    }

    afterEvaluate {
        dependencies {
            libs.apply {
                api(yacla.core)
                api(yacla.yaml)
                api(yacla.json)
                api(yacla.ext.db)
            }
        }
    }
}