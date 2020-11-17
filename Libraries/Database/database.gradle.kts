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

        javaCompileOptions {
            annotationProcessorOptions {
                arguments.putAll(mapOf(
                    "room.schemaLocation" to "${rootProject.projectDir}/room-schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                ))
            }
        }
    }

    buildFeatures.dataBinding = true
}

dependencies {
    kapts(
        Dependencies.hiltAndroidCompiler,
        Dependencies.roomAnnotationProcessor
    )

    implementations(
            Dependencies.gson,
            Dependencies.hiltAndroid,
            Dependencies.room
    )
}
