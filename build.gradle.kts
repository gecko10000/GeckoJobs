plugins {
    kotlin("jvm") version "2.3.10"
    id("java-library")
    id("maven-publish")
    kotlin("plugin.serialization") version "2.3.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("de.eldoria.plugin-yml.bukkit") version "0.6.0"
    kotlin("kapt") version "2.3.10"
}

sourceSets {
    main {
        java {
            srcDir("src")
        }
        resources {
            srcDir("res")
        }
    }
}

group = "gecko10000.geckojobs"
version = "0.1"

bukkit {
    name = "GeckoJobs"
    main = "$group.$name"
    apiVersion = "1.13"
    depend = listOf("GeckoLib", "PlaceholderAPI")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://eldonexus.de/repository/maven-public/")
    maven("https://repo.helpch.at/releases/")
    maven("https://redempt.dev")
}

dependencies {
    compileOnly(kotlin("stdlib", version = "2.3.10"))
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("gecko10000.geckolib:GeckoLib:1.1")
    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("net.strokkur.commands:annotations-paper:2.0.2")
    kapt("net.strokkur.commands:processor-paper:2.0.2")
    implementation("com.github.Redempt:Crunch:2.0.3")
}

kotlin {
    jvmToolchain(21)
}

// simple script to sync the plugin to my test server
tasks.register("update", Exec::class) {
    dependsOn(tasks.build)
    commandLine("../../dot/local/bin/update.sh")
}
