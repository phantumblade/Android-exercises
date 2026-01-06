plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.application1"
    compileSdk = 36

    buildFeatures {viewBinding = true}

    defaultConfig {
        applicationId = "com.example.application1"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildFeatures {
            viewBinding = true
            compose = true
        }
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(platform(libs.firebase.bom))
    // Aggiungi la dipendenza per Firestore KTX
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)
    //dipendenze per usare i componenti di navigazione
    // Navigazione Jetpack
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Dipendenze base per Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3") // Material You!
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    // Questa serve per usare Compose dentro le View classiche
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    implementation("com.google.firebase:firebase-database")
}