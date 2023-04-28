/*
 * Copyright (c) 2022 New Vector Ltd
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

package dev.getzen.element.test.fakes

import dev.getzen.element.features.settings.devices.v2.signout.SignoutSessionsReAuthNeeded
import dev.getzen.element.features.settings.devices.v2.signout.SignoutSessionsUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse

class FakeSignoutSessionsUseCase {

    val instance = mockk<SignoutSessionsUseCase>()

    fun givenSignoutSuccess(deviceIds: List<String>) {
        coEvery { instance.execute(deviceIds, any()) } returns Result.success(Unit)
    }

    fun givenSignoutReAuthNeeded(deviceIds: List<String>): SignoutSessionsReAuthNeeded {
        val flowResponse = mockk<RegistrationFlowResponse>()
        every { flowResponse.session } returns "a-session-id"
        val errorCode = "errorCode"
        val reAuthNeeded = SignoutSessionsReAuthNeeded(
                pendingAuth = mockk(),
                uiaContinuation = mockk(),
                flowResponse = flowResponse,
                errCode = errorCode,
        )
        coEvery { instance.execute(deviceIds, any()) } coAnswers {
            secondArg<(SignoutSessionsReAuthNeeded) -> Unit>().invoke(reAuthNeeded)
            Result.success(Unit)
        }

        return reAuthNeeded
    }

    fun givenSignoutError(deviceIds: List<String>, error: Throwable) {
        coEvery { instance.execute(deviceIds, any()) } returns Result.failure(error)
    }
}
