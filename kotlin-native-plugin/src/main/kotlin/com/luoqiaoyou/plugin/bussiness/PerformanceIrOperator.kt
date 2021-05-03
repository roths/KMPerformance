package com.luoqiaoyou.plugin.bussiness

import com.luoqiaoyou.plugin.operate.IrOperator
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.*
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.FqName

class PerformanceIrOperator(val performanceAnnotations: List<String>, pluginContext: IrPluginContext) : IrOperator(pluginContext) {

    override fun visitFunction(declaration: IrFunction): IrStatement {

        if (performanceAnnotations.none { declaration.annotations.hasAnnotation(FqName(it)) }) {
            return super.visitFunction(declaration)
        }

        // get `println` FunctionDescriptor
        val printlnFunc = pluginContext.referenceFunctions(FqName("kotlin.io.println")).find {
            it.owner.valueParameters.size == 1
                    && it.owner.valueParameters.get(0).type == pluginContext.irBuiltIns.anyNType
                    && it.owner.returnType == pluginContext.irBuiltIns.unitType
        }!!
        // create local variable store TimeSource
        val monotonicClass = pluginContext.referenceClass(FqName("kotlin.time.TimeSource.Monotonic"))!!
        val timeMarkClass = pluginContext.referenceClass(FqName("kotlin.time.TimeMark"))!!
        val markNowFunc = monotonicClass.functions.find {
            it.owner.name.identifier.equals("markNow")
                    && it.owner.valueParameters.isEmpty()
                    && it.owner.returnType == timeMarkClass.defaultType
        }!!
        val elapsedNow = timeMarkClass.functions.find {
            it.owner.name.identifier.equals("elapsedNow")
        }!!
        // add mark Local
        val markLocal = declaration.createTemporaryVariable("tmp_time$0",
                markNowFunc.call(IrGetObjectValueImpl(UNDEFINED_OFFSET, UNDEFINED_OFFSET, monotonicClass.defaultType, monotonicClass)))
        // add return Local
        val returnLocal = declaration.createTemporaryVariable("tmp_return$0", declaration.returnType, true)
        // the function with unit return type may not have any return expression, we need to add a mock one
        var finalReturn: IrReturn? = null
        if (declaration.returnType == pluginContext.irBuiltIns.unitType) {
            finalReturn = IrReturnImpl(UNDEFINED_OFFSET,
                    UNDEFINED_OFFSET, declaration.returnType, declaration.symbol, IrGetObjectValueImpl(UNDEFINED_OFFSET, UNDEFINED_OFFSET, pluginContext.irBuiltIns.unitType, pluginContext.irBuiltIns.unitClass))
        }

        (declaration.body?.statements as? MutableList)?.run {
            // println function name before execute function body
            add(0, printlnFunc.staticCall("⇢  ${declaration.name}(".ir()))
            add(1, markLocal)
            add(2, returnLocal)
            if (finalReturn != null) {
                add(size, finalReturn)
            }
        }

        declaration.transformChildrenVoid(object : IrElementTransformerVoid() {
            override fun visitReturn(expression: IrReturn): IrExpression {
                // if return target is current function, println elapsed time before return
                if (expression.returnTargetSymbol != declaration.symbol) {
                    return super.visitReturn(expression)
                }

                // println function name before execute function body
                val psta = printlnFunc.staticCall(IrStringConcatenationImpl(
                        UNDEFINED_OFFSET,
                        UNDEFINED_OFFSET,
                        pluginContext.irBuiltIns.stringType,
                        listOf(
                                "⇠ ${declaration.name} [ran in ".ir(),
                                elapsedNow.call(IrGetValueImpl(
                                        UNDEFINED_OFFSET,
                                        UNDEFINED_OFFSET,
                                        markLocal.symbol
                                )),
                                " ms] = ".ir()
                        )
                ))

                // pass return expression value to local variable
                val set = IrSetValueImpl(
                        UNDEFINED_OFFSET,
                        UNDEFINED_OFFSET,
                        pluginContext.irBuiltIns.unitType,
                        returnLocal.symbol,
                        expression.value,
                        IrStatementOrigin.EQ)
                // return local variable value
                expression.value = IrGetValueImpl(
                        UNDEFINED_OFFSET,
                        UNDEFINED_OFFSET,
                        returnLocal.symbol
                )
                return IrCompositeImpl(
                        expression.startOffset,
                        expression.endOffset, expression.type,
                        null,
                        statements = listOf(
                                set,
                                psta,
                                expression
                        ))
            }
        })

        return super.visitFunction(declaration)
    }
}