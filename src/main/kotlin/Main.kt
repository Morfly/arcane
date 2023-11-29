import io.morfly.arcane.runtime.quote
import io.morfly.arcane.runtime.splice

fun main() {
    val text = "100 + ${5 * 4}"
    println(text)

    val code = quote {
        100 + splice { 5 * 4 }
    }
    println(code.text)

    val value = 100 + run { 5 * 4 }
    println(value)
}