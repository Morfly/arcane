package io.morfly.arcane.compiler.backend

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class DebugLogExtension : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val typeAnyNullable = pluginContext.irBuiltIns.anyNType

        val debugLogAnnotation = pluginContext.referenceClass(ClassId(FqName(""), Name.identifier("DebugLog")))!!
        val funPrintln = pluginContext.referenceFunctions(CallableId(FqName("kotlin.io"), Name.identifier("println")))
            .single {
                val parameters = it.owner.valueParameters
                parameters.size == 1 && parameters[0].type == typeAnyNullable
            }

        moduleFragment.transform(DebugLogTransformer(pluginContext, debugLogAnnotation, funPrintln), null)

    }
}