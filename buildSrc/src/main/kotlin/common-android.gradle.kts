import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * NOTE: The values set here CAN NOT be overwritten in subprojects by themselves.
 * Said in another way, setting any of these values in a module will not take effect.
 */
subprojects {
    with(pluginManager) {
        withPlugin(BuildScript.KotlinAndroid) {
            useBoM()
        }

        withPlugin(BuildScript.Kapt) {
            dependencies {
                configurations.create("kapt")(platform(project(":dependencies")))
            }

            configure<KaptExtension> {
                correctErrorTypes = true

                javacOptions {
                    option("-source", "8")
                    option("-target", "8")
                }
            }
        }

        withPlugin(BuildScript.AndroidLibrary) {
            configureAndroidProject()
        }

        withPlugin(BuildScript.AndroidApplication) {
            configureAndroidProject()
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
    }
}

fun Project.useBoM() = dependencies {
    "implementation"(platform(project(":dependencies")))
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

    dependencies {
        "androidTestImplementation"(platform(project(":dependencies")))
    }
}
