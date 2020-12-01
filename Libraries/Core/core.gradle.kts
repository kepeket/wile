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

    buildFeatures.dataBinding = true
}

dependencies {
    implementations(
        Dependencies.androidXAppCompat,
        Dependencies.androidXDataBinding,
        Dependencies.androidXFragment,
        Dependencies.androidXLiveDataKtx,
        Dependencies.androidXViewModelKTX
    )
}
