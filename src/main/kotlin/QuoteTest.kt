import io.morfly.arcane.runtime.quote
import io.morfly.arcane.runtime.splice

fun test() {
    val code = quote {

        val s = splice {
            5 + 5
        }
        val d = splice {
            10 * 2
        }
        s + d
    }
    println("TTAGG text: \n${code.text}")
    println("TTAGG value: ${code.evaluate()}")
}