package com.luoqiaoyou.plugin

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class PerformanceGradlePlugin : KotlinCompilerPluginSupportPlugin {
    override fun apply(project: Project) {
        project.extensions.create(
            "performance",
            PerformanceGradleExtension::class.java
        )
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension = project.extensions.findByType(PerformanceGradleExtension::class.java)
            ?: PerformanceGradleExtension()

        val annotationOptions =
            extension.annotations.map { SubpluginOption(key = "performanceAnnotation", value = it) }
        return project.provider {
            annotationOptions
        }
    }

    override fun getCompilerPluginId(): String = "performance"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = "com.luoqiaoyou.performance",
        artifactId = "kotlin-plugin",
        version = VERSION
    )

    override fun getPluginArtifactForNative(): SubpluginArtifact = SubpluginArtifact(
        groupId = "com.luoqiaoyou.performance",
        artifactId = "kotlin-native-plugin",
        version = VERSION
    )

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

    companion object {
        private const val VERSION = "0.0.1"
    }
}