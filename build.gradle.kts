import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.0.1"
}

group = "me.tzhu"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    testImplementation(kotlin("test"))
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
}

kotlin {
    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("kotlin.experimental.ExperimentalTypeInference")
                optIn("androidx.compose.foundation.ExperimentalFoundationApi")
                optIn("androidx.compose.material.ExperimentalMaterialApi")
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "AdventOfCode2021"
            packageVersion = "1.0.0"
        }
    }
}