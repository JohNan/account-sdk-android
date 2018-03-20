/*
 * Copyright (c) 2018 Schibsted Products & Technology AS. Licensed under the terms of the MIT license. See LICENSE in the project root.
 */

package com.schibsted.account.service

class OnReadyListener<T>(private val onReady: (T) -> Unit) {
    fun ready(res: T) {
        onReady(res)
    }
}
