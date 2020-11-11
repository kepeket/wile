object Dependencies {
    // region Test dependencies
    // https://mvnrepository.com/artifact/androidx.test.ext/junit
    const val androidJunitRunner = "androidx.test.ext:junit"

    // https://github.com/junit-team/junit4
    const val junit = "junit:junit"

    // https://developer.android.com/jetpack/androidx/releases/test
    const val espresso = "androidx.test.espresso:espresso-core"
    // endregion

    // region Runtime
    // Guide : https://developer.android.com/kotlin/ktx#kotlin
    // Artifact : https://mvnrepository.com/artifact/androidx.core/core-ktx
    // Release notes : https://developer.android.com/jetpack/androidx/releases/core
    const val androidXCoreKTX = "androidx.core:core-ktx"

    // Artifact : https://mvnrepository.com/artifact/androidx.appcompat/appcompat
    const val androidXAppCompat = "androidx.appcompat:appcompat"

    // Artifact : https://mvnrepository.com/artifact/androidx.lifecycle/lifecycle-livedata-ktx
    const val androidXLiveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx"

    // Guide :https://developer.android.com/jetpack/androidx/releases/navigation
    // Artifact : https://mvnrepository.com/artifact/androidx.navigation/navigation-fragment-ktx
    const val androidXNavigationFragmentKTX = "androidx.navigation:navigation-fragment-ktx"
    // Artifact : https://mvnrepository.com/artifact/androidx.navigation/navigation-ui-ktx
    const val androidXNavigationUiKTX = "androidx.navigation:navigation-ui-ktx"

    // Artifact : https://mvnrepository.com/artifact/androidx.lifecycle/lifecycle-viewmodel-ktx
    const val androidXViewModelKTX = "androidx.lifecycle:lifecycle-viewmodel-ktx"

    // Guide : https://developer.android.com/training/constraint-layout/
    // Artifact : https://mvnrepository.com/artifact/androidx.constraintlayout/constraintlayout
    // Release notes : https://developer.android.com/jetpack/androidx/releases/constraintlayout
    const val constraintLayout = "androidx.constraintlayout:constraintlayout"

    // https://firebase.google.com/docs/android
    const val firebaseAnalytics = "com.google.firebase:firebase-analytics-ktx"
    const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics-ktx"

    // Artifact : https://mvnrepository.com/artifact/com.google.dagger/hilt-android
    // Release notes : https://developer.android.com/jetpack/androidx/releases/hilt
    const val hiltAndroid = "com.google.dagger:hilt-android"
    // Artifact : https://mvnrepository.com/artifact/androidx.hilt/hilt-compiler
    const val hiltCompiler = "com.google.dagger:hilt-android-compiler"
    // Artifact : https://mvnrepository.com/artifact/androidx.hilt/hilt-lifecycle-viewmodel
    const val hiltViewModel = "androidx.hilt:hilt-lifecycle-viewmodel"

    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect"
    const val kotlinStdlibJdk7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7"

    // https://material.io/develop/android/docs/getting-started/
    // https://mvnrepository.com/artifact/com.google.android.material/material
    const val materialDesign = "com.google.android.material:material"

    const val moshi = "com.squareup.moshi:moshi-kotlin"
    const val moshiCompiler = "com.squareup.moshi:moshi-kotlin-codegen"

    // Guide : https://developer.android.com/topic/libraries/architecture/room
    // Artifact : https://mvnrepository.com/artifact/androidx.room/room-ktx
    const val room = "androidx.room:room-ktx"

    // Artifact : https://mvnrepository.com/artifact/androidx.room/room-compiler
    const val roomAnnotationProcessor = "androidx.room:room-compiler"
    // endregion
}