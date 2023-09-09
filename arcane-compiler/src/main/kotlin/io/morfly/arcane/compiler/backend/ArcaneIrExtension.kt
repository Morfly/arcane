package io.morfly.arcane.compiler.backend

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.path
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import java.io.File

class ArcaneIrExtension : IrGenerationExtension, IrElementVisitorVoid {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
//        val names = moduleFragment.descriptor.
//        println("names: $names")

        println("TTAGG ")
        moduleFragment.accept(this, null)

    }

    override fun visitFile(declaration: IrFile) {
        val fileSource = File(declaration.path).readText()
        println("TTAGG fileSource: \n$fileSource")
        declaration.acceptChildren(this, null)
    }

    override fun visitElement(element: IrElement) {
//        println("TTAGG IR element: ${element.render()}")
        element.acceptChildren(this, null)
    }

    override fun visitExpression(expression: IrExpression) {
//        println("TTAGG IR expression: ${expression.type.annotations}")
    }


    fun test(cls: IrClass) {
        cls.origin
    }
}