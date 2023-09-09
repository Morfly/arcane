package io.morfly.arcane

@Target(
    AnnotationTarget.TYPE,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.CLASS,
)
annotation class Template

@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.EXPRESSION,
)

annotation class QuoteContent(val code: String)
annotation class QuoteContent1()