/*
 * Copyright (c) 2023 New Vector Ltd
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

package dev.getzen.element.features.home

import dev.getzen.element.features.home.room.list.UnreadCounterBadgeView
import dev.getzen.element.features.spaces.GetSpacesUseCase
import dev.getzen.element.features.spaces.notification.GetNotificationCountForSpacesUseCase
import dev.getzen.element.test.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.query.SpaceFilter
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.summary.RoomAggregateNotificationCount

internal class GetSpacesNotificationBadgeStateUseCaseTest {

    private val fakeGetNotificationCountForSpacesUseCase = mockk<GetNotificationCountForSpacesUseCase>()
    private val fakeGetSpacesUseCase = mockk<GetSpacesUseCase>()

    private val getSpacesNotificationBadgeStateUseCase = GetSpacesNotificationBadgeStateUseCase(
            getNotificationCountForSpacesUseCase = fakeGetNotificationCountForSpacesUseCase,
            getSpacesUseCase = fakeGetSpacesUseCase,
    )

    @Test
    fun `given flow of spaces invite and notification count then flow of state is correct`() = runTest {
        // Given
        val noSpacesInvite = emptyList<RoomSummary>()
        val existingSpaceInvite = listOf<RoomSummary>(mockk())
        val noNotification = RoomAggregateNotificationCount(
                notificationCount = 0,
                highlightCount = 0,
        )
        val existingNotificationNotHighlighted = RoomAggregateNotificationCount(
                notificationCount = 1,
                highlightCount = 0,
        )
        val existingNotificationHighlighted = RoomAggregateNotificationCount(
                notificationCount = 1,
                highlightCount = 1,
        )
        every { fakeGetSpacesUseCase.execute(any()) } returns
                flowOf(noSpacesInvite, existingSpaceInvite, existingSpaceInvite, noSpacesInvite, noSpacesInvite)
        every { fakeGetNotificationCountForSpacesUseCase.execute(any()) } returns
                flowOf(noNotification, noNotification, existingNotificationNotHighlighted, existingNotificationNotHighlighted, existingNotificationHighlighted)

        // When
        val testObserver = getSpacesNotificationBadgeStateUseCase.execute().test(this)
        advanceUntilIdle()

        // Then
        val expectedState1 = UnreadCounterBadgeView.State.Count(count = 0, highlighted = false)
        val expectedState2 = UnreadCounterBadgeView.State.Text(text = "!", highlighted = true)
        val expectedState3 = UnreadCounterBadgeView.State.Count(count = 1, highlighted = true)
        val expectedState4 = UnreadCounterBadgeView.State.Count(count = 1, highlighted = false)
        val expectedState5 = UnreadCounterBadgeView.State.Count(count = 1, highlighted = true)
        testObserver
                .assertValues(expectedState1, expectedState2, expectedState3, expectedState4, expectedState5)
                .finish()
        verify {
            fakeGetSpacesUseCase.execute(match {
                it.memberships == listOf(Membership.INVITE) && it.displayName == QueryStringValue.IsNotEmpty
            })
        }
        verify { fakeGetNotificationCountForSpacesUseCase.execute(SpaceFilter.NoFilter) }
    }
}
