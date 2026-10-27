import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

val apiKeyProperties = Properties().apply {
    // Accept the project-root file and the accidental app/ location used earlier.
    val rootSecretsFile = rootProject.file("secrets.properties")
    val secretsFile = if (rootSecretsFile.exists()) rootSecretsFile else file("secrets.properties")
    if (secretsFile.exists()) {
        secretsFile.inputStream().use { input -> load(input) }
    }
}
val openWeatherApiKey = apiKeyProperties.getProperty("OPENWEATHER_API_KEY", "")

android {
    namespace = "com.example.weatherapp"
    // androidx.activity:activity:1.13.0 requires Android API 36 to compile.
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.weatherapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "OPENWEATHER_API_KEY",
            "\"${openWeatherApiKey.replace("\\", "\\\\").replace("\"", "\\\"")}\""
        )
    }
    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(platform("com.google.firebase:firebase-bom:35.0.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}
