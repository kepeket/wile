import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.plugin.use.PluginDependenciesSpec

// https://guides.gradle.org/migrating-build-logic-from-groovy-to-kotlin/#configurations-and-dependencies
// Private methods exist to hide the "dirty" implementation from public methods.

// region Plugins
private fun PluginDependenciesSpec.plugin(plugin: String) = id(plugin)

fun PluginDependenciesSpec.plugins(vararg plugins: String) = plugins.forEach { plugin(it) }
// endregion

// region Kapt
private fun DependencyHandlerScope.kapt(dependency: String)
        = "kapt"(dependency)

fun DependencyHandlerScope.kapts(vararg dependencies: String)
        = dependencies.forEach { kapt(it) }

// endregion

// region Implementation
private fun DependencyHandlerScope.implementation(dependency: String)
        = "implementation"(dependency)

private fun DependencyHandlerScope.testImplementation(dependency: String)
        = "testImplementation"(dependency)

private fun DependencyHandlerScope.androidTestImplementation(dependency: String)
        = "androidTestImplementation"(dependency)

fun DependencyHandlerScope.implementations(vararg dependencies: String)
        = dependencies.forEach { implementation(it) }

fun DependencyHandlerScope.testImplementations(vararg dependencies: String)
        = dependencies.forEach { testImplementation(it) }

fun DependencyHandlerScope.androidTestImplementations(vararg dependencies: String)
        = dependencies.forEach { androidTestImplementation(it) }
// endregion
