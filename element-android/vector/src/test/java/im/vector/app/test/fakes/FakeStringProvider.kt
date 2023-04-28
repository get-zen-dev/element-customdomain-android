/*
 * Copyright (c) 2021 New Vector Ltd
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

import dev.getzen.element.core.resources.StringProvider
import io.mockk.InternalPlatformDsl.toStr
import io.mockk.every
import io.mockk.mockk

class FakeStringProvider {
    val instance = mockk<StringProvider>()

    init {
        every { instance.getString(any()) } answers {
            "test-${args[0]}"
        }
        every { instance.getString(any(), any()) } answers {
            "test-${args[0]}-${args[1].toStr()}"
        }

        every { instance.getQuantityString(any(), any(), any()) } answers {
            "test-${args[0]}-${args[1]}"
        }
    }

    fun given(id: Int, result: String) {
        every { instance.getString(id) } returns result
    }
}

fun Int.toTestString() = "test-$this"
