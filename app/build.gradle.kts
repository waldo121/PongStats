import java.util.Properties

val keystoreProperties = Properties().apply {
    val keystoreFile = rootProject.file("keystore.properties")
    if (keystoreFile.exists()) {
        keystoreFile.inputStream().use { load(it) }
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.symbol.processing)
}

android {
    namespace = "com.waldo121.pongstats"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.waldo121.pongstats"
        minSdk = 28
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 28
        versionCode = 7
        versionName = "1.1.5-rc1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    signingConfigs {
        create("release") {
            val envStoreFile = System.getenv("KEYSTORE_PATH")
            val envStorePassword = System.getenv("KEYSTORE_PASSWORD")
            val envKeyAlias = System.getenv("KEY_ALIAS")
            val envKeyPassword = System.getenv("KEY_PASSWORD")

            storeFile = file(
                envStoreFile
                    ?: keystoreProperties.getProperty("KEYSTORE_PATH")
                    ?: "debug.keystore"
            )
            storePassword = envStorePassword ?: keystoreProperties.getProperty("KEYSTORE_PASSWORD") ?: "android"
            keyAlias = envKeyAlias ?: keystoreProperties.getProperty("KEY_ALIAS") ?: "androiddebugkey"
            keyPassword = envKeyPassword ?: keystoreProperties.getProperty("KEY_PASSWORD") ?: "android"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

tasks.register("printVersion") {
    doLast {
        println(android.defaultConfig.versionName)
    }
}
