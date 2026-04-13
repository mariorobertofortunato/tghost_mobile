plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

object BuildType {
    const val DEBUG = "debug"
    const val RELEASE = "release"
}

android {
    namespace = "com.mrf.tghost"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.mrf.tghost"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ndk {
            debugSymbolLevel = "FULL"
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    buildTypes {
        getByName(BuildType.DEBUG) {
        }
        getByName(BuildType.RELEASE) {
            //signingConfig = signingConfigs.getByName(BuildType.RELEASE)
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

        }
        java {
            toolchain {
                languageVersion = JavaLanguageVersion.of(21)
            }
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/DEPENDENCIES"
        }
    }
}

dependencies {
    // Module dependencies
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":chain:solana"))
    implementation(project(":chain:evm"))
    implementation(project(":chain:sui"))
    implementation(project(":chain:tezos"))

    // BOMs
    implementation(platform(libs.compose.bom))
    implementation(platform(libs.kotlin.bom))

    // AndroidX + Compose UI
    implementation(libs.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.icons)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation.compose)

    // UI libraries
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
    implementation(libs.material)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)

    // Persistence
    implementation(libs.androidx.room.runtime)

    // Dependency injection
    implementation(libs.hilt.android)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    debugImplementation(libs.compose.ui.tooling)

    // Code generation
    ksp(libs.hilt.android.compiler)
}