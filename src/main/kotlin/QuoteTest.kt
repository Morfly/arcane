import io.morfly.arcane.runtime.quote

fun test() {
    quote {
        code = {
            "val s = 5"
        }

        val s = 5
    }
}