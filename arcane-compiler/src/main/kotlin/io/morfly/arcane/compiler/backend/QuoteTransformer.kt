package io.morfly.arcane.compiler.backend

import org.jetbrains.kotlin.backend.common.FileLoweringPass
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.fir.declarations.builder.buildProperty
import org.jetbrains.kotlin.fir.expressions.builder.buildFunctionCall
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.IrFunctionBuilder
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.declarations.buildProperty
import org.jetbrains.kotlin.ir.builders.declarations.buildVariable
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.path
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import java.io.File
import kotlin.math.exp

const val RUNTIME_PACKAGE = "io.morfly.arcane.runtime"
val QUOTE_FQ_NAME = FqName("io.morfly.arcane.runtime.quote")
val SPLICE_FQ_NAME = FqName("io.morfly.arcane.runtime.splice")

class QuoteTransformer(
    private val pluginContext: IrPluginContext,
) : IrElementTransformerVoidWithContext(), FileLoweringPass {

    private lateinit var file: File
    private lateinit var fileSource: String

    private val quoteType = pluginContext
        .referenceClass(ClassId(FqName(RUNTIME_PACKAGE), Name.identifier("Quote")))!!

    private val quoteCodeSetter = quoteType.getSimpleFunction("addCode")

    override fun lower(irFile: IrFile) {
        file = File(irFile.path)
        fileSource = file.readText()
        println("TTAGG file: ${file.path}")
        println("TTAGG fileSource: ${fileSource}")
        irFile.transformChildrenVoid()
    }


    override fun visitFileNew(declaration: IrFile): IrFile {
        file = File(declaration.path)
        fileSource = file.readText()
        return super.visitFileNew(declaration)
    }

    override fun visitCall(expression: IrCall): IrExpression {
        return when (expression.symbol.owner.fqNameWhenAvailable) {
            QUOTE_FQ_NAME -> {
                lowerQuote(expression)
            }

            SPLICE_FQ_NAME -> {
                lowerSplice(expression)
            }

            else -> expression
        }
    }

    private fun lowerQuote(expression: IrCall): IrExpression {
        expression.valueArguments
            .map { argument ->
                if (argument is IrFunctionExpression) {
                    lowerQuoteLambda(argument)
                } else argument
            }
        return expression
    }

    private fun lowerQuoteLambda(expression: IrFunctionExpression): IrExpression {
        val body = expression.function.body as? IrBlockBody ?: return expression

        val loweredBody = DeclarationIrBuilder(pluginContext, expression.function.symbol).irBlockBody {
            val callSetter = irCall(quoteCodeSetter!!.owner).apply {
                dispatchReceiver = irGet(expression.function.extensionReceiverParameter!!)
            }
            callSetter.putValueArgument(0, irString("Hello, world!"))
            +callSetter
        }
        loweredBody.statements += body.statements

        body.statements.clear()
        body.statements.addAll(loweredBody.statements)
        return expression
    }

    private fun lowerSplice(expression: IrCall): IrExpression {
        println("TTAGG isSplice")
        return expression
    }
}