/*
 * Copyright (c) 2018 Schibsted Products & Technology AS. Licensed under the terms of the MIT license. See LICENSE in the project root.
 */

package com.schibsted.account.session.event

import android.content.Intent
import com.schibsted.account.model.UserId
import com.schibsted.account.session.User

object EventManager {
    enum class Action(val value: String) {
        EVENT_USER_LOGIN("SCHACC_EV_USER_LOGIN"),
        EVENT_USER_LOGOUT("SCHACC_EV_USER_LOGOUT"),
        EVENT_TOKEN_REFRESH("SCHACC_EV_TOKEN_REFRESH"),
        COMMAND_CLOSE("SCHACC_CO_CLOSE");

        override fun toString() = this.value
    }

    enum class Extra(val value: String) {
        EXTRA_USER("SCHACC_EX_USER"),
        EXTRA_USER_ID("SCHACC_EX_USERID");

        override fun toString() = this.value
    }

    fun createEventUserLogin(user: User) = Intent(Action.EVENT_USER_LOGIN.value)
            .putExtra(Extra.EXTRA_USER.value, user)

    fun createEventUserLogout(userId: UserId) = Intent(Action.EVENT_USER_LOGOUT.value)
            .putExtra(Extra.EXTRA_USER_ID.value, userId)

    fun createEventTokenRefresh(user: User) = Intent(Action.EVENT_TOKEN_REFRESH.value)
            .putExtra(Extra.EXTRA_USER.value, user)

    fun createCommandClose() = Intent(Action.COMMAND_CLOSE.value)

    fun readUser(intent: Intent): User = intent.getParcelableExtra(Extra.EXTRA_USER.value)

    fun readUserId(intent: Intent): UserId = intent.getParcelableExtra(Extra.EXTRA_USER_ID.value)
}
