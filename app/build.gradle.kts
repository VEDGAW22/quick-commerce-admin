plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.quickcommerceadmin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.quickcommerceadmin"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation(libs.google.firebase.storage)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database) // Correct format
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.1.1")
    // navigation component
    val nav_version = "2.7.6" // Check for latest version
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
    // firebase
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-database-ktx") // Realtime Database

    // Live Data and View Modle
    implementation ("android.arch.lifecycle:extensions:1.1.0")
    annotationProcessor ("android.arch.lifecycle:compiler:1.1.0")
    testImplementation ("android.arch.core:core-testing:1.1.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    implementation("com.facebook.shimmer:shimmer:0.5.0")

    implementation("com.github.denzcoskun:ImageSlideshow:0.1.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
}
