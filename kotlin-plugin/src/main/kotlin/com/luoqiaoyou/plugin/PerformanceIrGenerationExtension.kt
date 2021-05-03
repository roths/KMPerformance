package com.luoqiaoyou.plugin

import com.luoqiaoyou.plugin.bussiness.PerformanceIrOperator
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

class PerformanceIrGenerationExtension(val performanceAnnotations: List<String>) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.transformChildrenVoid(PerformanceIrOperator(performanceAnnotations, pluginContext))
    }
}