import io.morfly.arcane.runtime.quote
import io.morfly.arcane.runtime.splice
import java.lang.StringBuilder

fun test() {
    val code = quote {

        //
        var s = 5
        s = 7
        StringBuilder().append("123")

    }
    println("TTAGG text: \n${code.text}")
    println("TTAGG value: ${code.evaluate()}")
}