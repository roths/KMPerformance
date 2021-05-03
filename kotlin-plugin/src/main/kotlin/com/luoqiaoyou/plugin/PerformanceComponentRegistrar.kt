package com.luoqiaoyou.plugin

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration

typealias MockProject = org.jetbrains.kotlin.com.intellij.mock.MockProject

@AutoService(ComponentRegistrar::class)
class PerformanceComponentRegistrar : ComponentRegistrar {

    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
    ) {
        if (configuration[KEY_ANNOTATIONS]?.isEmpty() ?: true) {
            return
        }
        IrGenerationExtension.registerExtension(
            project,
            PerformanceIrGenerationExtension(
                performanceAnnotations = configuration[KEY_ANNOTATIONS]!!
            )
        )
    }
}

