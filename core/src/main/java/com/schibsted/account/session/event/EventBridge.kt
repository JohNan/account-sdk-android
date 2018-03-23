package com.schibsted.account.session.event

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.schibsted.account.model.UserId
import com.schibsted.account.service.AccountService
import com.schibsted.account.session.User

class AccountCommunicator(appContext: Context, private val accountCommunicatorInterface: AccountCommunicatorInterface) : BroadcastReceiver() {
    interface AccountCommunicatorInterface {
        fun onUserLogin(user: User?)

        fun onUserLogout(userId: UserId) {}
    }

    private val localBroadcastManager = LocalBroadcastManager.getInstance(appContext)

    override fun onReceive(context: Context, intent: Intent) {
        Log.e("XXX", "RECEIVED: " + intent.action)

        when (intent.action) {
            EventManager.Action.EVENT_USER_LOGIN.value -> {
                accountCommunicatorInterface.onUserLogin(EventManager.readUser(intent))
            }

            EventManager.Action.EVENT_USER_LOGOUT.value -> {
                accountCommunicatorInterface.onUserLogout(EventManager.readUserId(intent))
            }
        }
    }

    fun sendCloseCommand() {
        Log.e("XXX", "CLOSING!")
        this.localBroadcastManager.sendBroadcast(EventManager.createCommandClose())
    }

    fun onStart() {
        Log.e("XXX", "ON START!")
        IntentFilter(EventManager.Action.EVENT_USER_LOGIN.value).apply {
            addAction(EventManager.Action.EVENT_USER_LOGOUT.value)
        }.also { this.localBroadcastManager.registerReceiver(this, it) }
    }

    fun onStop() {
        this.localBroadcastManager.unregisterReceiver(this)
    }
}
