plugins {
    id("common-android")
}

allprojects {
    repositories {
        mavenLocal()
        google()
        jcenter()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
