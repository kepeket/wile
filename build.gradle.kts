plugins {
    id("common-android")
}

allprojects {
    repositories {
        mavenLocal()
        google()
        jcenter()
        maven(url="https://jitpack.io")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
