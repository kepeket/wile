plugins {
    id(BuildScript.AndroidApplication)
    id(BuildScript.Crashlytics)
    id(BuildScript.DaggerHilt)
    id(BuildScript.GoogleServices)
    id(BuildScript.KotlinAndroid)
    id(BuildScript.KotlinAndroidExtensions)
    id(BuildScript.Kapt)
}

android {
    compileSdkVersion(30)

    defaultConfig {
        applicationId = "com.wile.main"
        minSdkVersion(26)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"))
        }
    }

    buildFeatures {
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    val lifecycle_version = "2.2.0"
    val room_version = "2.2.5"
    val hilt_core_version = "2.29.1-alpha"
    val moshi_version = "1.11.0"
    val hilt_version = "1.0.0-alpha02"
    val assisted_inject_version = "0.5.2"

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.10")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.4.10")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.1")

    // hilt
    implementation("com.google.dagger:hilt-android:$hilt_core_version")
    implementation("androidx.hilt:hilt-common:$hilt_version")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:$hilt_version")
    compileOnly("com.squareup.inject:assisted-inject-annotations-dagger2:$assisted_inject_version")
    kapt("com.squareup.inject:assisted-inject-processor-dagger2:$assisted_inject_version")
    kapt("com.google.dagger:hilt-compiler:$hilt_core_version")
    kapt("androidx.hilt:hilt-compiler:$hilt_version")

    // room
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    implementation("com.squareup.moshi:moshi-kotlin:$moshi_version")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshi_version")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")

    implementation(platform("com.google.firebase:firebase-bom:26.0.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    testImplementation("junit:junit:4.13.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}