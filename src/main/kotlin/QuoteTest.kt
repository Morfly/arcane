import io.morfly.arcane.runtime.quote
import io.morfly.arcane.runtime.splice

fun getBool() = false

fun test() {
    val code = quote {

        val s = splice {
            -10 + 5
        } + splice {
            10 * 2
        }
        val sts = splice {
            "1" + "2"
        } + splice { 'a' }

        val ch = splice { 'a' }

        val tr = splice {
            getBool()
        }
        sts + s + splice { "spl" } + tr + ch
    }
    println("TTAGG text: \n${code.text}")
    println("TTAGG value: ${code.evaluate()}")
}