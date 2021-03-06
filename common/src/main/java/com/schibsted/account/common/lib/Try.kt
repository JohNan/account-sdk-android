/*
 * Copyright (c) 2018 Schibsted Products & Technology AS. Licensed under the terms of the MIT license. See LICENSE in the project root.
 */

package com.schibsted.account.common.lib

sealed class Try<out T> {
    abstract fun isFailure(): Boolean
    abstract fun isSuccess(): Boolean
    abstract fun get(): T

    data class Failure<out T>(val exception: Throwable) : Try<T>() {
        override fun isFailure() = true

        override fun isSuccess() = false

        override fun get(): T {
            throw exception
        }
    }

    data class Success<out T>(val value: T) : Try<T>() {
        override fun isFailure() = false

        override fun isSuccess() = true

        override fun get(): T = value
    }

    fun <O> fold(onError: (Throwable) -> O, onSuccess: (T) -> O): O {
        return when (this) {
            is Success -> try {
                onSuccess(value)
            } catch (ex: Exception) {
                onError(ex)
            }
            is Failure -> onError(exception)
        }
    }

    inline fun <O> map(crossinline block: (T) -> O): Try<O> = fold({ Failure(it) }, { Success(block(it)) })

    inline fun <O> flatMap(crossinline block: (T) -> Try<O>): Try<O> = fold({ Failure(it) }, { block(it) })

    companion object {
        operator fun <O> invoke(block: () -> O): Try<O> = try {
            Success(block())
        } catch (ex: Throwable) {
            Failure(ex)
        }
    }
}

fun <O> Try<O>.getOrNull(): O? = fold({ null }, { it })

fun <O> Try<O>.getOrDefault(default: () -> O): O = fold({ default() }, { it })

fun <O> Try<O>.getOrElse(default: (Throwable) -> O): O = fold(default, { it })

fun <O> Try<O>.recover(onError: (Throwable) -> O): Try<O> = fold({ Try.Success(onError(it)) }, { Try.Success(it) })

fun <O> Try<O>.recoverWith(onError: (Throwable) -> Try<O>): Try<O> = fold({ onError(it) }, { Try.Success(it) })
