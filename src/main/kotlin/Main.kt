import io.morfly.arcane.runtime.quote
import io.morfly.arcane.runtime.splice

fun main() {
    val text = "5 + ${5 * 4}"
    println(text)

    val code = quote {
        5 + splice { 5 * 4 }
    }
    println(code.text)

    val value = 5 + run { 5 * 4 }
    println(value)
}