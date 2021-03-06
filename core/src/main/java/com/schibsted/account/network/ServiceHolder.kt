/*
 * Copyright (c) 2018 Schibsted Products & Technology AS. Licensed under the terms of the MIT license. See LICENSE in the project root.
 */

package com.schibsted.account.network

import android.support.annotation.RestrictTo
import com.schibsted.account.ClientConfiguration
import com.schibsted.account.network.service.authentication.OAuthService
import com.schibsted.account.network.service.client.ClientService
import com.schibsted.account.network.service.passwordless.PasswordlessService
import com.schibsted.account.network.service.session.SessionService
import com.schibsted.account.network.service.user.UserService
import com.schibsted.account.session.User
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object ServiceHolder {
    private const val TIMEOUT_MS = 10_000L

    private var oAuthService: OAuthService? = null
    private var clientService: ClientService? = null
    private var passwordlessService: PasswordlessService? = null
    private var userService: UserService? = null
    private var sessionService: SessionService? = null

    private var client: OkHttpClient? = null
    private var authClient: OkHttpClient? = null

    private val clientBuilder = OkHttpClient.Builder()
            .writeTimeout(TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .readTimeout(TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .addInterceptor(InfoInterceptor(true))

    private fun getOrCreateClient(): OkHttpClient {
        val client = this.client ?: clientBuilder.build()
        this.client = client
        return client
    }

    private fun getOrCreateAuthClient(user: User): OkHttpClient {
        val authClient = this.authClient
                ?: user.bind(clientBuilder, listOf(ClientConfiguration.get().environment)).build()
        this.authClient = authClient
        return authClient
    }

    fun oAuthService(): OAuthService {
        val service = oAuthService
                ?: OAuthService(ClientConfiguration.get().environment, getOrCreateClient())
        this.oAuthService = service
        return service
    }

    fun clientService(): ClientService {
        val service = clientService
                ?: ClientService(ClientConfiguration.get().environment, getOrCreateClient())
        this.clientService = service
        return service
    }

    fun passwordlessService(): PasswordlessService {
        val service = passwordlessService
                ?: PasswordlessService(ClientConfiguration.get().environment, getOrCreateClient())
        this.passwordlessService = service
        return service
    }

    fun userService(user: User): UserService {
        val service = userService
                ?: UserService(ClientConfiguration.get().environment, getOrCreateAuthClient(user))
        this.userService = service
        return service
    }

    fun sessionService(user: User): SessionService {

        val service = sessionService
                ?: SessionService(ClientConfiguration.get().environment, getOrCreateAuthClient(user))
        this.sessionService = service
        return service
    }

    @JvmStatic
    fun resetServices() {
        authClient = null
        userService = null
        sessionService = null
    }

    @JvmStatic
    fun cancelRequests() {
        this.client?.dispatcher()?.cancelAll()
        this.authClient?.dispatcher()?.cancelAll()
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal fun overrideService(service: Any) {
        when (service) {
            is OAuthService -> this.oAuthService = service
            is ClientService -> this.clientService = service
            is PasswordlessService -> this.passwordlessService = service
            is UserService -> this.userService = service
            is SessionService -> this.sessionService = service
            else -> IllegalArgumentException("Invalid service")
        }
    }
}
