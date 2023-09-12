import io.morfly.arcane.runtime.quote
import io.morfly.arcane.runtime.splice

fun test() {
    val code = quote {


        var s = 5
        s = 5


    }
    code.template.invoke(code.quote)
    println("TTAGG result: \n${code.quote.code.toString()}")
}