plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.serialization") version "2.3.21"
    application
}

group = "com.example.studybuddy"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("ai.koog:koog-agents:0.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.example.studybuddy.MainKt")
}
