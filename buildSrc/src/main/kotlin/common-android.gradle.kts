import com.android.build.gradle.BaseExtension

/**
 * NOTE: The values set here CAN NOT be overwritten in subprojects by themselves.
 * Said in another way, setting any of these values in a module will not take effect.
 */
subprojects {
    with(pluginManager) {
        withPlugin(BuildScript.Kapt) {
            configure<org.jetbrains.kotlin.gradle.plugin.KaptExtension> {
                javacOptions {
                    option("-source", "8")
                    option("-target", "8")
                }
            }
        }

        withPlugin(BuildScript.AndroidApplication) {
            configureAndroidProject()
        }
    }
}

fun Project.configureAndroidProject() {
    configure<BaseExtension> {
        setCompileSdkVersion(30)

        defaultConfig {
            setMinSdkVersion(26)
            setTargetSdkVersion(30)
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables.useSupportLibrary = true
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }
}
