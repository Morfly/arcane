package io.morfly.arcane.compiler

import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirCallableDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclarationStatus
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.extensions.FirStatusTransformerExtension
import org.jetbrains.kotlin.fir.extensions.transform

class ArcaneFirExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::ArcaneFirStatusTransformerExtension
    }
}

class ArcaneFirStatusTransformerExtension(session: FirSession) : FirStatusTransformerExtension(session) {
    override fun needTransformStatus(declaration: FirDeclaration): Boolean {
        return declaration is FirCallableDeclaration &&
                Visibilities.isPrivate(declaration.status.visibility)
    }

    override fun transformStatus(status: FirDeclarationStatus, declaration: FirDeclaration): FirDeclarationStatus {
        return status.transform(visibility = Visibilities.Public)
    }
}