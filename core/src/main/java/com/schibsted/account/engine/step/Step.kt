/*
 * Copyright (c) 2018 Schibsted Products & Technology AS. Licensed under the terms of the MIT license. See LICENSE in the project root.
 */

package com.schibsted.account.engine.step

import android.os.Parcel
import android.os.Parcelable

abstract class Step : Parcelable

internal fun Parcel.writeStringSet(missingFields: Set<String>) {
    this.writeStringArray(missingFields.toTypedArray())
}

internal fun Parcel.readStringSet(): Set<String> {
    val array = createStringArray()
    this.readStringArray(array)
    return array.toSet()
}
