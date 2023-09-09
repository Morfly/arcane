package io.morfly.arcane.runtime

import org.junit.jupiter.api.Test

object CompilationTest {
    fun testFunction(
        arg1: Int,
        arg2: String
    ) {

    }

    fun test0() {
        val intValue = 5
        val condition = true
        val code = quote {
            testFunction(
                arg1 = splice { intValue + 5 },
                arg2 = splice {
                    if (condition) "value1"
                    else "value2"
                }
            )
        }

        testFunction(
            arg1 = 10,
            arg2 = "value1"
        )
    }

    fun test1() {
        val a = quote { 1 + 2 }
        val b = quote { a() + a() }
        b()
    }

    fun test2() {
        val a: Code<Int> = quote { 1 + 2 }
        val b: Code<Int> = quote { a() + a() }
        val c: Int = b()
    }

    fun test3() {
        val a = quote {
            val numbers = mutableListOf<Int>()

            splice {
                for (i in 0 until 5) {
                    quote {
                        numbers += i
                    }
                }
            }
        }
        val b: Unit = a()
    }

    fun test4() {
        val a = splice {
            for (i in 0 until 5) {
                quote {
                    listOf(i)
                }
            }
        }
        a()
    }
}
