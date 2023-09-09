package io.morfly.arcane.runtime

@Target(AnnotationTarget.EXPRESSION)
@Retention(AnnotationRetention.SOURCE)
annotation class SomeAnnotation

fun test() {
    val res = quote {
        val s: Int = @SomeAnnotation splice {
            5 + 5
        }

        splice(block = @SomeAnnotation {

        })

        for(i in 0 ..< 4) {
            splice {
                10
            }
        }
    }
}