/*
 * Copyright (c) 2018 Schibsted Products & Technology AS. Licensed under the terms of the MIT license. See LICENSE in the project root.
 */

package com.schibsted.account

object Events {
    internal const val ACTION_USER_LOGIN = "AccountSdkActionUserLogin"
    const val ACTION_USER_LOGOUT = "AccountSdkActionUserLogout"
    internal const val ACTION_USER_TOKEN_REFRESH = "AccountSdkActionUserTokenRefresh"

    internal const val EXTRA_USER = "AccountSdkExtraUser"
    const val EXTRA_USER_ID = "AccountSdkExtraUserId"
}
