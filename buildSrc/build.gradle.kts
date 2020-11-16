import org.gradle.kotlin.dsl.`kotlin-dsl`

repositories {
    mavenLocal()
    google()
    jcenter()
}

plugins {
    `kotlin-dsl`
}

dependencies {
    // Android Gradle
    // https://mvnrepository.com/artifact/com.android.tools.build/gradle?repo=google
    implementation("com.android.tools.build:gradle:4.1.1")

    // Dagger Hilt
    // https://mvnrepository.com/artifact/com.google.dagger/hilt-android-gradle-plugin
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.29.1-alpha")

    // Firebase Crashlytics
    // https://mvnrepository.com/artifact/com.google.firebase/firebase-crashlytics-gradle?repo=google
    implementation("com.google.firebase:firebase-crashlytics-gradle:2.4.1")

    // Google services
    // https://bintray.com/android/android-tools/com.google.gms.google-services/
    implementation("com.google.gms:google-services:4.3.4")

    // Kotlin
    // https://github.com/JetBrains/kotlin/blob/master/ChangeLog.md
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-gradle-plugin
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
}
