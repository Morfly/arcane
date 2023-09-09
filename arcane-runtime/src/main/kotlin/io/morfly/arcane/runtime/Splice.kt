package io.morfly.arcane.runtime

class Splice(internal val parent: Quote?)

fun <R> splice(block: Splice.() -> R): Code<R> {
    return quote { splice(block) }
}

fun <R> Quote.splice(block: Splice.() -> R): R {
    return Splice(parent = this).block()
}
