import io.morfly.arcane.QuoteContent
import io.morfly.arcane.runtime.quote
import io.morfly.arcane.runtime.splice

fun main(args: Array<String>) {

    quote {
        splice {
            println("TEST")
            quote @QuoteContent("") { }
        }
    }
}