package io.morfly.arcane.runtime

import io.morfly.arcane.Template

class Code<T> internal constructor(
    private val quote: Quote,
    private val template: @Template Quote.() -> T
) {

    private val value: T by lazy {
        quote.template()
    }
    val text: String
        get() {
            value
            return quote.code!!
        }

    fun evaluate(): T = value

    operator fun invoke(): T = evaluate()
}