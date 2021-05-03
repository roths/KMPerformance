package com.luoqiaoyou.plugin.operate

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid


abstract class IrOperator(val pluginContext: IrPluginContext) : IrElementTransformerVoid() {

    fun IrSimpleFunctionSymbol.staticCall(vararg params: IrExpression?): IrCallImpl {
        return IrCallImpl(
            UNDEFINED_OFFSET,
            UNDEFINED_OFFSET,
            this.owner.returnType,
            this,
            params.size,
            params.size
        ).apply {
            for (index in params.indices) {
                putValueArgument(index, params[index])
                putTypeArgument(index, params[index]?.type)
            }
        }
    }

    /**
     * call instance function
     */
    fun IrSimpleFunctionSymbol.call(
        receiver: IrExpression,
        vararg params: IrExpression?
    ): IrCallImpl {
        return staticCall(*params).apply {
            dispatchReceiver = receiver
        }
    }
    /**
     * end <<<<<<<<<<<<<<<<<<<<<<<<<
     * create IrCall
     */


    /**
     * transform PrimitiveType to IrConst
     * start >>>>>>>>>>>>>>>>>>>>>>>>>
     */
    /**
     * create local variable from expression
     */
    fun IrFunction.createTemporaryVariable(name: String, expression: IrExpression): IrVariable {
        return Scope(this.symbol).createTemporaryVariable(expression, name)
    }

    /**
     * create nullable local variable from `IrType`
     */
    fun IrFunction.createTemporaryVariable(
        name: String,
        type: IrType,
        isMutable: Boolean = false
    ): IrVariable {
        return Scope(this.symbol).createTemporaryVariableDeclaration(type, name, isMutable)
    }

    /**
     * transform PrimitiveType to IrConst
     * start >>>>>>>>>>>>>>>>>>>>>>>>>
     */

    fun String.ir() = IrConstImpl.string(
        UNDEFINED_OFFSET, UNDEFINED_OFFSET,
        pluginContext.irBuiltIns.stringType,
        this
    )
    /**
     * end <<<<<<<<<<<<<<<<<<<<<<<<<
     * transform PrimitiveType to IrConst
     */
}