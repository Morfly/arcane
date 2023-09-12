import io.morfly.arcane.runtime.quote
import io.morfly.arcane.runtime.splice

fun test() {
    val code = quote {

        //
        var s = 5
        s = 5
        s

    }
    println("TTAGG result: \n${code.text}")
}