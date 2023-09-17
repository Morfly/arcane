package io.morfly.arcane.compiler.backend

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.SourceRangeInfo
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.path
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import java.io.File
import java.lang.StringBuilder
import kotlin.math.exp


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
        println("TTAGG file: ${irFile.dump()}")
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
                        data.splices += expression
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

            val concat = irConcat()
            var lastOffset = rangeInfo.startOffset
            data.splices.forEachIndexed { i, splice: IrCall ->
                val spliceRangeInfo = irFile.fileEntry.getSourceRangeInfo(splice.startOffset, splice.endOffset)
                concat.addArgument(irString(fileSource.substring(lastOffset, spliceRangeInfo.startOffset)))
                concat.addArgument(irCall(splice, splice.symbol))
                lastOffset = spliceRangeInfo.endOffset
            }
            concat.addArgument(irString(fileSource.substring(lastOffset, rangeInfo.endOffset)))

//            val spliceRangeInfo = data.nestedSplices.first().originalRangeInfo
//
//            val concat = irConcat()
//            val fragment1 = buildString {
//                append(startIndent)
//                append(fileSource.substring(rangeInfo.startOffset, spliceRangeInfo.startOffset))
//            }
//            concat.addArgument(irString(fragment1))
//
//            concat.addArgument(irCall(data.nestedSplices.first().expression, data.nestedSplices.first().expression.symbol))
//
//            val fragment2 = buildString {
//                append(fileSource.substring(spliceRangeInfo.endOffset, rangeInfo.endOffset))
//            }
//            concat.addArgument(irString(fragment2))
//
//            println("TTAGG concat: ${concat.dump()}")

            val callSetter = irCall(quoteCodeSetter!!.owner).apply {
                dispatchReceiver = irGet(expression.function.extensionReceiverParameter!!)
            }
            callSetter.putValueArgument(0, concat)
            +callSetter
        }
        loweredBody.statements += body.statements

        body.statements.clear()
        body.statements.addAll(loweredBody.statements)
    }

    sealed interface Data
}

data class QuoteData(
    val splices: MutableList<IrCall> = mutableListOf()
) : QuoteTransformer.Data