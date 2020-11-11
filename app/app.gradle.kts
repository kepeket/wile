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
    defaultConfig {
        applicationId = "com.wile.main"
        versionCode = 1
        versionName = "1.0"
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

    kotlinOptions {
        jvmTarget = "1.8"
    }

    kapt.correctErrorTypes = true
}

dependencies {
    implementation(Dependencies.androidXAppCompat)

    implementation(Dependencies.androidXLiveDataKtx)

    implementation(Dependencies.androidXNavigationFragmentKTX)
    implementation(Dependencies.androidXNavigationUiKTX)

    implementation(Dependencies.androidXViewModelKTX)

    implementation(Dependencies.constraintLayout)

    implementation(Dependencies.firebaseAnalytics)
    implementation(Dependencies.firebaseCrashlytics)

    implementation(Dependencies.hiltAndroid)
    kapt(Dependencies.hiltCompiler)
    implementation(Dependencies.hiltViewModel)

    implementation(Dependencies.materialDesign)

    implementation(Dependencies.moshi)
    kapt(Dependencies.moshiCompiler)

    implementation(Dependencies.room)
    kapt(Dependencies.roomAnnotationProcessor)

    implementation(Dependencies.androidXCoreKTX)

    testImplementation(Dependencies.junit)

    androidTestImplementation(Dependencies.androidJunitRunner)
    androidTestImplementation(Dependencies.espresso)
}