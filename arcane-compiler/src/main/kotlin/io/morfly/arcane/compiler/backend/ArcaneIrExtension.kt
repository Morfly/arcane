package io.morfly.arcane.compiler.backend

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid

class ArcaneIrExtension : IrGenerationExtension, IrElementVisitorVoid {

    private lateinit var pluginContext: IrPluginContext
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        this.pluginContext = pluginContext
        moduleFragment.acceptVoid(this)
    }

    override fun visitElement(element: IrElement) {
        element.acceptChildrenVoid(this)
    }

    override fun visitFile(declaration: IrFile) {
        println("TTAGG file: ${declaration.fqName}")

        val transformer = QuoteTransformer(pluginContext = pluginContext)
        transformer.lower(declaration)

//        val transformer = QuoteTransformingVisitor()
//        transformer.visitFile(declaration)
    }
}