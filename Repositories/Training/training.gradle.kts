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
        // FixMe : replace by coroutines only dependency
        Dependencies.androidXViewModelKTX,
        Dependencies.hiltAndroid
    )

    implementationProjects(
        ":libraries:Database"
    )
}
