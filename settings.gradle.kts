rootProject.name = "Wile"

include(":dependencies")
include(":app")

includeModulesDirs(
    "Libraries",
    "Repositories"
)

/**
 * Look into [dirs] for existing modules.
 * Note that is made just on one level deep.
 */
fun includeModulesDirs(vararg dirs: String) {
    val foundModules = dirs
        .map(::File)
        .flatMap(::findGradleModules)
        .map { it.path.replace(File.separatorChar, ':') }
        .sorted()
    include(*foundModules.toTypedArray())
}

fun findGradleModules(dir: File): List<File> = dir.listFiles { potentialModule ->
    potentialModule.isDirectory && potentialModule.containsGradleScript()
}.orEmpty().toList()

fun File.containsGradleScript(): Boolean {
    val gradleScripts = listFiles { file -> file.isGradleScript }.orEmpty()
    if (gradleScripts.size > 1) {
        throw kotlin.IllegalStateException("More than one .gradle[.kts] files found in $this")
    }
    return gradleScripts.isNotEmpty()
}

val File.isGradleScript: Boolean get() = name.endsWith(".gradle.kts")


apply("${rootProject.projectDir}/gradle/scripts/module-gradle-files-renaming.gradle")
