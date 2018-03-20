/*
 * Copyright (c) 2018 Schibsted Products & Technology AS. Licensed under the terms of the MIT license. See LICENSE in the project root.
 */

package com.schibsted.account.service

import com.schibsted.account.session.User

abstract class EventHook {
    internal fun

    fun onUserAvailable(user: User, onReadyListener: OnReadyListener<Void?>) = onReadyListener.ready(null)

    companion object {
        val DEFAULT = object : EventHook {}
    }
}
