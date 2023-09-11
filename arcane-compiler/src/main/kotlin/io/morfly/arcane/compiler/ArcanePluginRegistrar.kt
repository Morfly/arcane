package io.morfly.arcane.compiler

import io.morfly.arcane.compiler.backend.ArcaneIrExtension
import io.morfly.arcane.compiler.backend.DebugLogExtension
import io.morfly.arcane.compiler.frontend.ArcaneFirExtensionRegistrar
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

@OptIn(ExperimentalCompilerApi::class)
class ArcanePluginRegistrar : CompilerPluginRegistrar() {

    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        FirExtensionRegistrarAdapter.registerExtension(ArcaneFirExtensionRegistrar())
        IrGenerationExtension.registerExtension(ArcaneIrExtension())
        IrGenerationExtension.registerExtension(DebugLogExtension())
    }
}