package io.morfly.arcane.compiler.backend

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.SourceRangeInfo
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.path
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import java.io.File


const val RUNTIME_PACKAGE = "io.morfly.arcane.runtime"
val QUOTE_FQ_NAME = FqName("io.morfly.arcane.runtime.quote")
val SPLICE_FQ_NAME = FqName("io.morfly.arcane.runtime.splice")

class QuoteTransformer(
    private val pluginContext: IrPluginContext,
    private val irFile: IrFile,
) : IrElementTransformer<QuoteTransformer.Data?> {
    private val fileSource: String = File(irFile.path).readText()

    private val quoteType = pluginContext
        .referenceClass(ClassId(FqName(RUNTIME_PACKAGE), Name.identifier("Quote")))!!

    private val quoteCodeSetter = quoteType.getSimpleFunction("addCode")

    fun lower() {
        irFile.transformChildren(transformer = this, data = null)
    }

    override fun visitCall(expression: IrCall, data: Data?): IrExpression {
        when (expression.symbol.owner.fqNameWhenAvailable) {
            QUOTE_FQ_NAME -> {
                val quoteData = when (data) {
                    null -> QuoteData()
                    else -> TODO("Not implemented!")
                }
                super.visitCall(expression, quoteData)

                val body = expression.valueArguments.filterIsInstance<IrFunctionExpression>().firstOrNull()
                if (body != null) {
                    lowerQuote(body, quoteData)
                }
            }

            SPLICE_FQ_NAME -> {
                if (data is QuoteData) {
                    val body = expression.valueArguments.filterIsInstance<IrFunctionExpression>().firstOrNull()

                    if (body != null) {
                        data.nestedSplices += lowerSplice(expression, body)
                    }
                } else {
                    error("TODO")
                }
            }
        }
        return expression
    }

    private fun lowerQuote(expression: IrFunctionExpression, data: QuoteData) {
        val body = expression.function.body as? IrBlockBody ?: return

        val loweredBody = DeclarationIrBuilder(pluginContext, expression.function.symbol).irBlockBody {
            val rangeInfo = irFile.fileEntry.getSourceRangeInfo(expression.startOffset + 1, expression.endOffset - 1)
            val startIndent = " ".repeat(rangeInfo.startColumnNumber)

            val code = startIndent + fileSource.substring(rangeInfo.startOffset, rangeInfo.endOffset)

            val callSetter = irCall(quoteCodeSetter!!.owner).apply {
                dispatchReceiver = irGet(expression.function.extensionReceiverParameter!!)
            }
            callSetter.putValueArgument(0, irString(code.trimIndent().trim('\n')))
            +callSetter
        }
        loweredBody.statements += body.statements

        body.statements.clear()
        body.statements.addAll(loweredBody.statements)
    }

    private fun lowerSplice(expression: IrCall, body: IrFunctionExpression): ModifiedIrExpression {
        val rangeInfo = irFile.fileEntry.getSourceRangeInfo(expression.startOffset, expression.endOffset)
        return ModifiedIrExpression(
            originalRangeInfo = rangeInfo,
            expression = expression,
        )
    }

    sealed interface Data
}

data class QuoteData(
    val nestedSplices: MutableList<ModifiedIrExpression> = mutableListOf()
) : QuoteTransformer.Data

data class SpliceData(
    val nestedQuotes: MutableList<ModifiedIrExpression> = mutableListOf()
) : QuoteTransformer.Data

data class ModifiedIrExpression(
    val originalRangeInfo: SourceRangeInfo,
    val expression: IrExpression,
)