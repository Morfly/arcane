package io.morfly.arcane.compiler.backend

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.path
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.irCall
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import java.io.File


const val RUNTIME_PACKAGE = "io.morfly.arcane.runtime"
val QUOTE_FQ_NAME = FqName("$RUNTIME_PACKAGE.quote")
val SPLICE_FQ_NAME = FqName("$RUNTIME_PACKAGE.splice")

class QuoteTransformer(
    private val pluginContext: IrPluginContext,
    private val irFile: IrFile,
) : IrElementTransformer<QuoteTransformer.Data?> {
    private val fileSource: String = File(irFile.path).readText()

    private val stringType = pluginContext.irBuiltIns.stringType
    private val charType = pluginContext.irBuiltIns.charType

    private val quoteType = pluginContext
        .referenceClass(ClassId(FqName(RUNTIME_PACKAGE), Name.identifier("Quote")))!!

    private val quoteCodeSetter = quoteType.getSimpleFunction("addCode")!!

    fun lower() {
        irFile.transformChildren(transformer = this, data = null)
    }

    override fun visitCall(expression: IrCall, data: Data?): IrElement {
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
                        data.splices += expression
                    }
                }
            }
        }
        return super.visitCall(expression, data)
    }

    private fun lowerQuote(expression: IrFunctionExpression, data: QuoteData) {
        val body = expression.function.body as? IrBlockBody ?: return

        val loweredBody = DeclarationIrBuilder(pluginContext, expression.function.symbol).irBlockBody {
            val concat = irConcat()
            val rangeInfo = irFile.fileEntry.getSourceRangeInfo(expression.startOffset + 1, expression.endOffset - 1)

            val startIndent = " ".repeat(rangeInfo.startColumnNumber)
            concat.addArgument(irString(startIndent))

            var lastOffset = rangeInfo.startOffset
            data.splices.forEach { splice: IrCall ->
                val spliceRangeInfo = irFile.fileEntry.getSourceRangeInfo(splice.startOffset, splice.endOffset)
                concat.addArgument(irString(fileSource.substring(lastOffset, spliceRangeInfo.startOffset)))
                concat.addArgument(lowerSplice(splice))
                lastOffset = spliceRangeInfo.endOffset
            }
            concat.addArgument(irString(fileSource.substring(lastOffset, rangeInfo.endOffset)))

            val callSetter = irCall(quoteCodeSetter.owner).apply {
                dispatchReceiver = irGet(expression.function.extensionReceiverParameter!!)
            }
            callSetter.putValueArgument(0, concat)
            +callSetter
        }
        loweredBody.statements += body.statements

        body.statements.clear()
        body.statements.addAll(loweredBody.statements)
    }

    private fun IrBuilderWithScope.lowerSplice(splice: IrCall): IrExpression =
        when (splice.type) {
            stringType -> irConcat().apply {
                addArgument(irString("\""))
                addArgument(irCall(splice, splice.symbol))
                addArgument(irString("\""))
            }

            charType -> irConcat().apply {
                addArgument(irString("'"))
                addArgument(irCall(splice, splice.symbol))
                addArgument(irString("'"))
            }

            else -> irCall(splice, splice.symbol)
        }

    sealed interface Data
}

data class QuoteData(
    val splices: MutableList<IrCall> = mutableListOf()
) : QuoteTransformer.Data