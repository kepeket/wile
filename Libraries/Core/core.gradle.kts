plugins {
    plugins(
            BuildScript.AndroidLibrary,
            BuildScript.KotlinAndroid,
            BuildScript.KotlinAndroidExtensions
    )
}

android {
    defaultConfig {
        resourcePrefix("${project.name.toLowerCase()}_")
    }
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
