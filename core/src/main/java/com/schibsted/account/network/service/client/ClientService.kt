/*
 * Copyright (c) 2018 Schibsted Products & Technology AS. Licensed under the terms of the MIT license. See LICENSE in the project root.
 */

package com.schibsted.account.network.service.client

import com.schibsted.account.model.ClientToken
import com.schibsted.account.network.Environment
import com.schibsted.account.network.response.AccountStatusResponse
import com.schibsted.account.network.response.AgreementLinksResponse
import com.schibsted.account.network.response.ApiContainer
import com.schibsted.account.network.response.ClientInfo
import com.schibsted.account.network.response.ProfileData
import com.schibsted.account.network.service.BaseNetworkService
import com.schibsted.account.common.util.encodeBase64
import okhttp3.OkHttpClient
import retrofit2.Call
import java.util.HashMap

/**
 * This network service is used to perform client token based operations
 */
class ClientService(@Environment environment: String, okHttpClient: OkHttpClient) : BaseNetworkService(environment, okHttpClient) {
    private val clientService = createService(ClientContract::class.java)

    /**
     * Creates a user profile and associates it to an e-mail address if there is no such association yet.
     * @param email The e-mail address for the association.
     * @param password Optionally, a password for future authentications.
     */
    fun signUp(clientToken: ClientToken, email: String, redirectUri: String, inputParams: Map<String, Any>): Call<ApiContainer<ProfileData>> {
        val params = HashMap<String, Any>()
        params.put(PARAM_EMAIL, email)
        params.put(PARAM_REDIRECT_URI_NO_UNDERSCORE, redirectUri)
        params.putAll(inputParams)

        return clientService.signUp(clientToken.bearerAuthHeader(), params)
    }

    /**
     * Queries for the signup checkPhoneStatus of a phone number.
     *
     * @param clientToken The token to authenticate the query with.
     * @param phone The phone number to query for in the format +46736151515
     */
    fun getPhoneSignUpStatus(clientToken: ClientToken, phone: String): Call<ApiContainer<AccountStatusResponse>> {
        return clientService.checkPhoneStatus(clientToken.bearerAuthHeader(), encodeBase64(phone))
    }

    /**
     * Queries for the signup checkPhoneStatus of an e-mail address.
     * @param clientToken The token to authenticate the query with.
     * @param email The e-mail address to query for.
     */
    fun getEmailSignUpStatus(clientToken: ClientToken, email: String): Call<ApiContainer<AccountStatusResponse>> {
        return clientService.checkEmailStatus(clientToken.bearerAuthHeader(), encodeBase64(email))
    }

    fun getClientAgreementsUrls(clientId: String): Call<ApiContainer<AgreementLinksResponse>> {
        return this.clientService.retrieveTermsLinks(clientId)
    }

    fun getClientInfo(clientToken: ClientToken, clientId: String): Call<ApiContainer<ClientInfo>> {
        return this.clientService.getClientInformation(clientToken.bearerAuthHeader(), clientId)
    }

    companion object {
        private const val PARAM_EMAIL = "email"
    }
}
