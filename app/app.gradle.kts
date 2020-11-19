plugins {
    plugins(
        BuildScript.AndroidApplication,
        BuildScript.Crashlytics,
        BuildScript.DaggerHilt,
        BuildScript.GoogleServices,
        BuildScript.KotlinAndroid,
        BuildScript.KotlinAndroidExtensions,
        BuildScript.Kapt
    )
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

    buildFeatures.dataBinding = true

    kotlinOptions {
        jvmTarget = "1.8"
    }

    kapt.correctErrorTypes = true
}

dependencies {
    kapts(
        Dependencies.hiltAndroidCompiler,
        Dependencies.moshiCompiler,
        Dependencies.roomAnnotationProcessor,
        Dependencies.hiltCompiler,
        Dependencies.databindingCompiler
    )

    implementations(
        Dependencies.androidXAppCompat,
        Dependencies.androidXCoreKTX,
        Dependencies.androidXLiveDataKtx,
        Dependencies.androidXNavigationFragmentKTX,
        Dependencies.androidXNavigationUiKTX,
        Dependencies.androidXViewModelKTX,
        Dependencies.constraintLayout,
        Dependencies.firebaseAnalytics,
        Dependencies.firebaseCrashlytics,
        Dependencies.gson,
        Dependencies.hiltAndroid,
        Dependencies.hiltViewModel,
        Dependencies.kotlinReflect,
        Dependencies.kotlinStdlibJdk7,
        Dependencies.materialDesign,
        Dependencies.moshi,
        Dependencies.room,
        Dependencies.timber,
        Dependencies.viewpager2
    )

    testImplementations(
        Dependencies.junit
    )

    androidTestImplementations(
        Dependencies.androidJunitRunner,
        Dependencies.espresso
    )

    implementationProjects(
        ":Libraries:Database",
        ":Libraries:Logging",
        ":Libraries:Sound",
        ":Repositories:Training"
    )
}
