// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.daggerHiltAndroid) apply false
    alias(libs.plugins.kotlinJvm) apply false
}

buildscript {
    val fl = rootProject.file("api.properties")
    (fl.exists()).let {
        fl.forEachLine {
            val config = it.split("=", limit = 2)
            rootProject.extra.set(config[0], config[1])
        }
    }

    repositories {
        mavenCentral()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }

    dependencies {
        classpath(libs.gradle)
    }
}