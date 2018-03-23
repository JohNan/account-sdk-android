package com.schibsted.account.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.schibsted.account.common.util.Logger

class AccountServiceBridge(private var onServiceReadyListener: OnAccountServiceReadyListener?) {
    interface OnAccountServiceReadyListener {
        fun onAccountServiceReady(service: AccountService)
    }

    var service: AccountService? = null
        private set

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(componentName: ComponentName) {
            service = null
        }

        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
            val resolvedService = (binder as AccountService.AccountServiceBinder).getService()
            service = resolvedService
            onServiceReadyListener?.onAccountServiceReady(resolvedService)
        }
    }

    fun bind(appContext: Context) {
        val res = appContext.applicationContext.bindService(Intent(appContext, AccountService::class.java), connection, Context.BIND_AUTO_CREATE)
        if (!res) {
            Logger.warn("${Logger.DEFAULT_TAG}-ASB", { "Unable to bind the AccountService. Did you specify it in the manifest?" })
        }
    }

    fun unbind(appContext: Context) {
        appContext.applicationContext.unbindService(connection)
    }

    fun isBound() = service != null
}
