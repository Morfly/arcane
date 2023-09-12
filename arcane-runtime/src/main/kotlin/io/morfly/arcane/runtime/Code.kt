package io.morfly.arcane.runtime

import io.morfly.arcane.Template

class Code<T> internal constructor(
    private val quote: Quote,
    private val template: @Template Quote.() -> T
) {
    val text: String
        get() = quote.run {
            template()
            code!!
        }

    fun evaluate(): T = quote.template()

    operator fun invoke(): T = evaluate()
}