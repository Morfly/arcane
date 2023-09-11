package io.morfly.arcane.runtime

import io.morfly.arcane.Template

class Quote(internal val parent: Splice?) {
    var code: (() -> String)? = null
}

fun <R> quote(block: @Template Quote.() -> R): Code<R> {
    return Code(Quote(parent = null), block)
}

fun <R> Splice.quote(block: @Template Quote.() -> R): Code<R> {
    return Code(Quote(parent = this), block)
}