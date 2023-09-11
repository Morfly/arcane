@file:OptIn(ExperimentalCompilerApi::class)

package io.morfly.arcane.compiler

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import junit.framework.TestCase.assertEquals
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test

class IrPluginTest {

    @Test
    fun `IR plugin success`() {
        val result = compile(
            sourceFile = SourceFile.kotlin(
                "main.kt",
                """
      annotation class DebugLog
      
      fun main() {
        println(greet())
        println(greet(name = "Kotlin IR"))
      }
      
      @DebugLog
      fun greet(greeting: String = "Hello", name: String = "World"): String { 
        Thread.sleep(15) // simulate work
        return "${'$'}greeting, ${'$'}name!"
      }
    """.trimIndent()
            )
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val kClazz = result.classLoader.loadClass("MainKt")
        val main = kClazz.declaredMethods.single { it.name == "main" && it.parameterCount == 0 }
        main.invoke(null)
    }
}

fun compile(
    sourceFiles: List<SourceFile>,
    plugin: CompilerPluginRegistrar = ArcanePluginRegistrar(),
): KotlinCompilation.Result {
    return KotlinCompilation().apply {
        sources = sourceFiles
        useIR = true
        compilerPluginRegistrars = listOf(plugin)
        inheritClassPath = true
    }.compile()
}

fun compile(
    sourceFile: SourceFile,
    plugin: CompilerPluginRegistrar = ArcanePluginRegistrar(),
): KotlinCompilation.Result {
    return compile(listOf(sourceFile), plugin)
}