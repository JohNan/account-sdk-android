/*
 * Copyright (c) 2018 Schibsted Products & Technology AS. Licensed under the terms of the MIT license. See LICENSE in the project root.
 */

package com.schibsted.account.engine.integration.contract

import com.schibsted.account.engine.input.Agreements
import com.schibsted.account.engine.input.Credentials
import com.schibsted.account.engine.input.Identifier
import com.schibsted.account.engine.input.RequiredFields
import com.schibsted.account.model.LoginResult

/**
 * The contract containing all the required steps to log in using the
 * [com.schibsted.account.engine.controller.PasswordController]
 */
interface LoginContract : Contract<LoginResult>, Credentials.Provider, Agreements.Provider, RequiredFields.Provider {
    fun onAccountVerificationRequested(identifier: Identifier)
}
