/*
 * Copyright (c) 2018 Schibsted Products & Technology AS. Licensed under the terms of the MIT license. See LICENSE in the project root.
 */

package com.schibsted.account.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.schibsted.account.engine.controller.LoginController
import com.schibsted.account.engine.integration.CallbackProvider
import com.schibsted.account.engine.integration.contract.LoginContract
import com.schibsted.account.model.LoginResult

/*
 NOTES:
    * Fixed incorrect deprecation of oneTimeCode
    * Support for morgen levering
 */
class AccountService private constructor() : Service() {
    inner class AccountServiceBinder : Binder() {
        fun getService() = AccountService()
    }

    private val binder = AccountServiceBinder()

    var eventHook: EventHook = EventHook.DEFAULT

    override fun onBind(intent: Intent): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        throw IllegalStateException("Service should never be started, only bound")
    }

    fun initLogin(contract: LoginContract) = LoginController(this, contract)
}

class HookContract(private val c: LoginContract) : LoginContract by c {
    override fun onFlowReady(callbackProvider: CallbackProvider<LoginResult>) {
        val hook = CallbackProvider.
    }
}
