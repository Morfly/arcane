package io.morfly.arcane.runtime

import io.morfly.arcane.Template

class Code<T> internal constructor(
    private val context: Quote,
    private val template: @Template Quote.() -> T
) {

    fun evaluate(): T {
        return context.template()
    }

    operator fun invoke(): T = evaluate()
}