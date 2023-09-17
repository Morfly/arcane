package io.morfly.arcane.runtime

class Splice(internal val parent: Quote?)

fun <R> Quote.splice(block: Splice.() -> R): R {
    return Splice(parent = this).block()
}
