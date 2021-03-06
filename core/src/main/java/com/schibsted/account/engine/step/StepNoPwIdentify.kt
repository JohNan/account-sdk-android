/*
 * Copyright (c) 2018 Schibsted Products & Technology AS. Licensed under the terms of the MIT license. See LICENSE in the project root.
 */

package com.schibsted.account.engine.step

import android.os.Parcel
import android.os.Parcelable
import com.schibsted.account.engine.input.Identifier
import com.schibsted.account.network.response.AgreementLinksResponse
import com.schibsted.account.network.response.PasswordlessToken

data class StepNoPwIdentify(val identifier: Identifier,
    val passwordlessToken: PasswordlessToken,
    val isNewUser: Boolean,
    val agreementLinks: AgreementLinksResponse) : Step() {

    constructor(source: Parcel) : this(
        source.readParcelable<Identifier>(Identifier::class.java.classLoader),
        source.readParcelable<PasswordlessToken>(PasswordlessToken::class.java.classLoader),
        1 == source.readInt(),
        source.readParcelable<AgreementLinksResponse>(AgreementLinksResponse::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(identifier, 0)
        writeParcelable(passwordlessToken, 0)
        writeInt((if (isNewUser) 1 else 0))
        writeParcelable(agreementLinks, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<StepNoPwIdentify> = object : Parcelable.Creator<StepNoPwIdentify> {
            override fun createFromParcel(source: Parcel): StepNoPwIdentify = StepNoPwIdentify(source)
            override fun newArray(size: Int): Array<StepNoPwIdentify?> = arrayOfNulls(size)
        }
    }
}
