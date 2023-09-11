package io.morfly.arcane.compiler.backend

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.path
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.FqName
import java.io.File

val quoteFqName = FqName("io.morfly.arcane.runtime.quote")
val spliceFqName = FqName("io.morfly.arcane.runtime.splice")

class QuoteTransformingVisitor() : IrElementVisitorVoid {
    lateinit var file: File
    lateinit var fileSource: String

    override fun visitElement(element: IrElement) {
        println("TTAGG element: $element")
        element.acceptChildrenVoid(this)
    }

    override fun visitFile(declaration: IrFile) {
        file = File(declaration.path)
        fileSource = file.readText()
        println("TTAGG visitFile: $fileSource")
        println("TTAGG visitFile: ${declaration.dump()}")
//        declaration.acceptChildrenVoid(this)
        super.visitFile(declaration)
    }

    override fun visitFunction(declaration: IrFunction) {
        println("TTAGG visitFunction: ${declaration.name}")

        super.visitFunction(declaration)
    }

    override fun visitCall(expression: IrCall) {

        println("TTAGG visitExpression: ${expression.symbol.owner.fqNameWhenAvailable}")
        println("TTAGG args: ${expression.valueArguments.map { /*it?.acceptVoid(this)*/ }}")
        super.visitCall(expression)
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression) {
//        println("TTAGG visitFunctionExpression: ${expression.function.name}")
        println("TTAGG visitFunctionExpression: ${expression}")
        super.visitFunctionExpression(expression)
    }
}