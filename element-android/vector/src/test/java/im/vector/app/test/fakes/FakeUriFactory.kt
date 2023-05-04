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

import dev.getzen.element.features.onboarding.UriFactory
import io.mockk.every
import io.mockk.mockk

class FakeUriFactory {

    val instance = mockk<UriFactory>().also {
        every { it.parse(any()) } answers {
            val input = it.invocation.args.first() as String
            FakeUri().also { it.givenEquals(input) }.instance
        }
    }
}