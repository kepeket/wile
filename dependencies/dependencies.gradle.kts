plugins {
    `java-platform`
}

javaPlatform.allowDependencies()

dependencies {
    api(platform("com.google.firebase:firebase-bom:${DependenciesVersions.firebaseBoM}"))
    constraints {
        // region Test dependencies
        api("${Dependencies.androidJunitRunner}:${DependenciesVersions.androidJunitRunner}")
        api("${Dependencies.espresso}:${DependenciesVersions.espresso}")
        api("${Dependencies.junit}:${DependenciesVersions.junit}")
        // endregion
        // region Runtime
        api("${Dependencies.androidXCoreKTX}:${DependenciesVersions.androidXCoreKTX}")
        api("${Dependencies.androidXAppCompat}:${DependenciesVersions.androidXAppCompat}")
        api("${Dependencies.androidXAppStartup}:${DependenciesVersions.androidXAppStartup}")
        api("${Dependencies.androidXDataBinding}:${DependenciesVersions.androidXDataBinding}")
        api("${Dependencies.androidXFragment}:${DependenciesVersions.androidXFragment}")
        api("${Dependencies.androidXLiveDataKtx}:${DependenciesVersions.androidXLiveDataKtx}")
        api("${Dependencies.androidXNavigationFragmentKTX}:${DependenciesVersions.androidXNavigation}")
        api("${Dependencies.androidXNavigationUiKTX}:${DependenciesVersions.androidXNavigation}")
        api("${Dependencies.androidXViewModelKTX}:${DependenciesVersions.androidXViewModelKTX}")
        api("${Dependencies.constraintLayout}:${DependenciesVersions.constraintLayout}")
        api("${Dependencies.databindingCompiler}:${DependenciesVersions.databindingCompiler}")
        api("${Dependencies.hiltAndroid}:${DependenciesVersions.hiltAndroid}")
        api("${Dependencies.hiltAndroidCompiler}:${DependenciesVersions.hiltAndroid}")
        api("${Dependencies.hiltViewModel}:${DependenciesVersions.hiltCore}")
        api("${Dependencies.hiltCompiler}:${DependenciesVersions.hiltCore}")
        api("${Dependencies.materialDesign}:${DependenciesVersions.materialDesign}")
        api("${Dependencies.moshi}:${DependenciesVersions.moshi}")
        api("${Dependencies.moshiCompiler}:${DependenciesVersions.moshi}")
        api("${Dependencies.moshiAadpters}:${DependenciesVersions.moshi}")
        api("${Dependencies.room}:${DependenciesVersions.androidXRoom}")
        api("${Dependencies.roomAnnotationProcessor}:${DependenciesVersions.androidXRoom}")
        api("${Dependencies.timber}:${DependenciesVersions.timber}")
        api("${Dependencies.viewpager2}:${DependenciesVersions.viewpager2}")
        api("${Dependencies.hashids}:${DependenciesVersions.hashids}")
        api("${Dependencies.scarlet}:${DependenciesVersions.scarlet}")
        api("${Dependencies.scarletOkHttp}:${DependenciesVersions.scarlet}")
        api("${Dependencies.scarletMoshi}:${DependenciesVersions.scarlet}")
        api("${Dependencies.scarletCoroutine}:${DependenciesVersions.scarlet}")
        api("${Dependencies.scarletLifecyle}:${DependenciesVersions.scarlet}")
        api("${Dependencies.androidXPreferences}:${DependenciesVersions.preferences}")
        // endregion
    }
}

object DependenciesVersions {
    // Tests
    const val androidJunitRunner               = "1.1.2"
    const val espresso                         = "3.3.0"
    const val junit                            = "4.13"
    // Runtime
    const val androidXCoreKTX                  = "1.3.2"
    const val androidXAppCompat                = "1.2.0"
    const val androidXAppStartup               = "1.0.0"
    const val androidXDataBinding              = "4.1.1"
    const val androidXFragment                 = "1.2.0"
    const val androidXLiveDataKtx              = "2.2.0"
    const val androidXNavigation               = "2.3.1"
    const val androidXRoom                     = "2.3.0-alpha03"
    const val androidXViewModelKTX             = "2.2.0"
    const val constraintLayout                 = "2.0.4"
    const val databindingCompiler              = "3.1.4"
    const val firebaseBoM                      = "26.0.0"
    const val hiltAndroid                      = "2.29.1-alpha"
    const val hiltCore                         = "1.0.0-alpha02"
    const val materialDesign                   = "1.3.0-alpha04"
    const val moshi                            = "1.11.0"
    const val timber                           = "4.7.1"
    const val viewpager2                       = "1.0.0"
    const val hashids                          = "1.0.3"
    const val scarlet                          = "0.1.11"
    const val preferences                      = "1.1.0"
}
