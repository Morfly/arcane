package io.morfly.arcane.runtime

import io.morfly.arcane.Template

class Quote(internal val parent: Splice?)

fun <R> quote(block: @Template Quote.() -> R): Code<R> {
    return Code(Quote(parent = null), block)
}

//fun quote(block: Quote.() -> String): String {
//    return block
//}

fun <R> Splice.quote(block: @Template Quote.() -> R): Code<R> {
    return Code(Quote(parent = this), block)
}