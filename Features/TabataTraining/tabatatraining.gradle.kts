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
        Dependencies.androidXActivityKTX,
        Dependencies.androidXCoreKTX,
        Dependencies.androidXLiveDataKtx,
        Dependencies.constraintLayout,
        Dependencies.hiltAndroid,
        Dependencies.hiltViewModel,
        Dependencies.materialDesign
    )

    implementationProjects(
        ":Libraries:Core",
        ":Libraries:Database",
        ":Libraries:Design",
        ":Repositories:Training"
    )
}
