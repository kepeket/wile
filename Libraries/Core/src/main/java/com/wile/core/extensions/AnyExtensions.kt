package com.wile.core.extensions

/**
 * Use this extension for `when` expressions that you want to be exhaustive.
 *
 * Example:
 *
 *     when(sealedClassInstance) {
 *         is SealedClass.Type1 -> doSomething()
 *         is SealedClass.Type2 -> doSomethingElse()
 *     }.exhaustive
 *
 *     In the case of SealedClass has another child. This will make the compiler to fail
 *     and enforce to add the missing type to the current `when` expression.
 */
val <T> T.exhaustive: T
    get() = this
