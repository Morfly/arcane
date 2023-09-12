package io.morfly.arcane.runtime

import io.morfly.arcane.Template

class Code<T> internal constructor(
    val quote: Quote,
    val template: @Template Quote.() -> T
) {

    fun evaluate(): T {
        return quote.template()
    }

    operator fun invoke(): T = evaluate()

    fun asString(): String {
        return quote.code ?: error("code was null")
    }
}