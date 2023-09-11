package io.morfly.arcane.runtime

import io.morfly.arcane.Template

class Code<T> internal constructor(
    private val quote: Quote,
    private val template: @Template Quote.() -> T
) {

    fun evaluate(): T {
        return quote.template()
    }

    operator fun invoke(): T = evaluate()

    fun asString(): String {
        return quote.code?.invoke() ?: error("code was null")
    }
}