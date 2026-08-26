plugins {
    java
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.5"
}

group = "fr.klemms.regioncommand"
version = "2.0.0"
description = "Execute commands when players enter or leave WorldGuard regions"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.12")
    compileOnly("net.raidstone:WorldGuardEvents:1.18.1")
    implementation("org.bstats:bstats-bukkit:3.0.2")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
    archiveBaseName.set("RegionCommand")
    relocate("org.bstats", "fr.klemms.regioncommand.libs.bstats")
    exclude("META-INF/LICENSE.txt", "META-INF/MANIFEST.MF")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
