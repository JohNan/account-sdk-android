/*
 * Copyright (c) 2018 Schibsted Products & Technology AS. Licensed under the terms of the MIT license. See LICENSE in the project root.
 */

package com.schibsted.account.engine.controller

import com.schibsted.account.common.util.Logger
import com.schibsted.account.engine.integration.contract.Contract
import com.schibsted.account.engine.step.Step
import com.schibsted.account.service.AccountService
import java.util.Stack

abstract class Controller<out T : Contract<*>>(protected val accountService: AccountService, protected val contract: T) {
    internal val navigation: Stack<Step> = Stack()

    /**
     * Perform the login sequence. Additional calls to this function will re-trigger the currently
     * active task.
     */
    abstract fun evaluate()

    /**
     * Goes back one step in the controller.
     * @return True of an element was popped off the stack, false if we're already at the beginning
     */
    @JvmOverloads
    fun back(step: Int = 1) {
        for (i in 0..step) {
            if (navigation.size > 0) {
                navigation.pop()
            } else {
                Logger.warn(Logger.DEFAULT_TAG, { "Attempted to go back when the navigation stack was empty" })
            }
        }
        evaluate()
    }

    fun start() = evaluate()

    internal inline fun <reified E : Step> findOnStack(): E? = navigation.find { it is E } as E?
}
