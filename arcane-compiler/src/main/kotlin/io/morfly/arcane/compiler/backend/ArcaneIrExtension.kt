package io.morfly.arcane.compiler.backend

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.path
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import java.io.File

class ArcaneIrExtension : IrGenerationExtension, IrElementVisitorVoid {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
//        moduleFragment.acceptVoid(this)

        val typeNullableAny = pluginContext.irBuiltIns.anyNType
        val typeUnit = pluginContext.irBuiltIns.unitType

        val funPrintln = pluginContext.referenceFunctions(CallableId(FqName("kotlin.io"), Name.identifier("println")))
            .single {
                val parameters = it.owner.valueParameters
                parameters.size == 1 && parameters[0].type == typeNullableAny
            }

        val funMain = pluginContext.irFactory.buildFun {
            name = Name.identifier("main")
            visibility = DescriptorVisibilities.PUBLIC
            modality = Modality.FINAL
            returnType = typeUnit
        }

        funMain.body = DeclarationIrBuilder(pluginContext, funMain.symbol).irBlockBody {
            val callPrintln = irCall(funPrintln)
            callPrintln.putValueArgument(0, irString("Hello, world!"))
            +callPrintln
        }

        println(funMain.dump())
    }

    override fun visitElement(element: IrElement) {
        element.acceptChildrenVoid(this)
    }

    override fun visitFile(declaration: IrFile) {
        val transformer = QuoteTransformingVisitor()
        transformer.visitFile(declaration)
    }
}