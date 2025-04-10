plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.henry.core"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    defaultConfig {
        buildConfigField(
            "boolean",
            Secret.ENABLE_API_LOG,
            rootProject.extra[Secret.ENABLE_API_LOG].toString()
        )
        buildConfigField(
            "Integer",
            ApiKey.API_CONNECT_TIMEOUT,
            rootProject.extra[ApiKey.API_CONNECT_TIMEOUT].toString()
        )
        buildConfigField(
            "Integer",
            ApiKey.API_WRITE_TIMEOUT,
            rootProject.extra[ApiKey.API_WRITE_TIMEOUT].toString()
        )
        buildConfigField(
            "Integer",
            ApiKey.API_READ_TIMEOUT,
            rootProject.extra[ApiKey.API_READ_TIMEOUT].toString()
        )
        buildConfigField(
            "boolean",
            Secret.ENABLE_TRUSTED_BASE_URL,
            rootProject.extra[Secret.ENABLE_TRUSTED_BASE_URL].toString()
        )
        buildConfigField(
            "boolean",
            Secret.ENABLE_TRUST_MANAGER,
            rootProject.extra[Secret.ENABLE_TRUST_MANAGER].toString()
        )
        buildConfigField(
            "boolean",
            Secret.ENABLE_UNSECURE_HTTP_PROTOCOL,
            rootProject.extra[Secret.ENABLE_UNSECURE_HTTP_PROTOCOL].toString()
        )
        buildConfigField("String", ApiKey.BASE_URL, rootProject.extra[ApiKey.BASE_URL].toString())
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.adapters)
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    kapt(libs.moshi.kotlin.codegen)

    implementation(libs.hilt.android)
    kapt(libs.dagger.hilt.compiler)

    implementation(libs.timber)
    implementation(libs.chucker.library)
    debugImplementation(libs.chucker.library)
    releaseImplementation(libs.chucker.library)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}