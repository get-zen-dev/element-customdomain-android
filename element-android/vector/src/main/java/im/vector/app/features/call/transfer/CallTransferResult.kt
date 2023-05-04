/*
 * Copyright (c) 2020 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.getzen.element.features.call.transfer

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class CallTransferResult : Parcelable {
    @Parcelize data class ConnectWithUserId(val consultFirst: Boolean, val selectedUserId: String) : CallTransferResult()
    @Parcelize data class ConnectWithPhoneNumber(val consultFirst: Boolean, val phoneNumber: String) : CallTransferResult()
}