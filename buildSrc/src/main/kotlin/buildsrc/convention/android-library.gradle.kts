package buildsrc.convention

import buildsrc.config.Deps
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")

    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.allopen")

    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlinx.kover")

    id("buildsrc.convention.base")
}

android {
    namespace = "io.mockk"
    compileSdk = Deps.Versions.compileSdk

    lint {
        abortOnError = false
        disable += "InvalidPackage"
        warning += "NewApi"
    }

    packaging {
        resources {
            excludes += "META-INF/main.kotlin_module"
        }
    }

    defaultConfig {
        minSdk = Deps.Versions.minSdk
        targetSdk = Deps.Versions.targetSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    compileOptions {
        sourceCompatibility = Deps.Versions.jvmTarget
        targetCompatibility = Deps.Versions.jvmTarget
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = Deps.Versions.jvmTarget.toString()
    }
}

dependencies {
    testImplementation("junit:junit:${Deps.Versions.junit4}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Deps.Versions.androidxEspresso}")

    androidTestImplementation("androidx.test:rules:${Deps.Versions.androidxTestRules}")
    androidTestImplementation("androidx.test:runner:${Deps.Versions.androidxTestRunner}")
    androidTestImplementation("androidx.test.ext:junit-ktx:${Deps.Versions.androidxTestExtJunit}")
    androidTestUtil("androidx.test:orchestrator:${Deps.Versions.androidxTestOrchestrator}")

    androidTestImplementation(kotlin("test"))
    androidTestImplementation(kotlin("test-junit"))
    androidTestUtil("androidx.test:orchestrator:${Deps.Versions.androidxOrchestrator}")
}

// Fix: Task 'dokkaJavadoc' uses this output of task 'kaptReleaseKotlin' without declaring an explicit or implicit dependency.
tasks.dokkaJavadoc.configure {
    mustRunAfter(tasks.named("kaptDebugKotlin"))
    mustRunAfter(tasks.named("kaptReleaseKotlin"))
}

val javadocJar by tasks.registering(Jar::class) {
    from(tasks.dokkaJavadoc)
    archiveClassifier.set("javadoc")
}
