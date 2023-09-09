import io.morfly.arcane.Template
import io.morfly.arcane.QuoteContent
import io.morfly.arcane.runtime.Quote
import io.morfly.arcane.runtime.quote
import io.morfly.arcane.runtime.splice

fun main(args: Array<String>) {

//    @QuoteContent("s")
    quote {
        splice {
            println("TEST")
            quote @QuoteContent("") {  }
        }
    }
}

@Template
fun Quote.mytemplate(arg: String) {

}