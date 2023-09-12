import io.morfly.arcane.runtime.quote
import io.morfly.arcane.runtime.splice

fun test() {
    val code = quote {
        // addCode("val s = 5")

        var s = 5
        // sss
        s = 5


    }
    code.template.invoke(code.quote)
    println("TTAGG result: \n${code.quote.code.toString()}")
}