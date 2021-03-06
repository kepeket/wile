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
    kapts(
        Dependencies.hiltAndroidCompiler
    )

    implementations(
            // FixMe : Replace by coroutines dependency only
            Dependencies.androidXViewModelKTX,
            Dependencies.hiltAndroid
    )

    implementationProjects(
            ":Libraries:Database"
    )
}
