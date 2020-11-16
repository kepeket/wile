plugins {
    plugins(
            BuildScript.AndroidLibrary,
            BuildScript.KotlinAndroid,
            BuildScript.KotlinAndroidExtensions,
            BuildScript.Kapt
    )
}

android {
    defaultConfig {
        resourcePrefix("${project.name.toLowerCase()}_")
    }
}

dependencies {
    implementations(
            Dependencies.androidXLiveDataKtx,
            Dependencies.hiltAndroid
    )
}
