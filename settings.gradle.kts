rootProject.name = "Wile"

include(":dependencies")
include(":app")

apply("${rootProject.projectDir}/gradle/scripts/module-gradle-files-renaming.gradle")
