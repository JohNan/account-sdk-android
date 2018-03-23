/*
 * Copyright (c) 2018 Schibsted Products & Technology AS. Licensed under the terms of the MIT license. See LICENSE in the project root.
 */

package com.schibsted.account.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import com.schibsted.account.persistence.UserPersistence
import com.schibsted.account.session.event.EventManager

class AccountService : Service() {
    inner class AccountServiceBinder : Binder() {
        fun getService() = AccountService()
    }

    private val binder = AccountServiceBinder()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val userPersistence = UserPersistence(applicationContext)

            when (intent.action) {
                EventManager.Action.EVENT_USER_LOGIN.value -> {
                    val user = EventManager.readUser(intent)
                    userPersistence.persist(user)
                }

                EventManager.Action.EVENT_TOKEN_REFRESH.value -> {
                    val user = EventManager.readUser(intent)
                    userPersistence.persist(user)
                }

                EventManager.Action.EVENT_USER_LOGOUT.value -> {
                    val userId = EventManager.readUserId(intent)
                    userPersistence.remove(userId.id)
                }
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        throw IllegalStateException("Service should never be started, only bound")
    }

    override fun onCreate() {
        super.onCreate()
        broadcastManager = LocalBroadcastManager.getInstance(applicationContext)

        IntentFilter(EventManager.Action.EVENT_USER_LOGIN.value).apply {
            addAction(EventManager.Action.EVENT_USER_LOGOUT.value)
            addAction(EventManager.Action.EVENT_TOKEN_REFRESH.value)
        }.also { broadcastManager!!.registerReceiver(this.receiver, it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        broadcastManager?.unregisterReceiver(this.receiver)
    }

    companion object {
        var broadcastManager: LocalBroadcastManager? = null
            private set
    }
}
