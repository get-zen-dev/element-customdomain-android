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

package dev.getzen.element.features.roomprofile.polls.detail.ui

import com.airbnb.mvrx.test.MavericksTestRule
import dev.getzen.element.core.event.GetTimelineEventUseCase
import dev.getzen.element.features.home.room.detail.poll.VoteToPollUseCase
import dev.getzen.element.test.test
import dev.getzen.element.test.testDispatcher
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

private const val A_POLL_ID = "poll-id"
private const val A_ROOM_ID = "room-id"

internal class RoomPollDetailViewModelTest {

    @get:Rule
    val mavericksTestRule = MavericksTestRule(testDispatcher = testDispatcher)

    private val initialState = RoomPollDetailViewState(pollId = A_POLL_ID, roomId = A_ROOM_ID)
    private val fakeGetTimelineEventUseCase = mockk<GetTimelineEventUseCase>()
    private val fakeRoomPollDetailMapper = mockk<RoomPollDetailMapper>()
    private val fakeVoteToPollUseCase = mockk<VoteToPollUseCase>()

    private fun createViewModel(): RoomPollDetailViewModel {
        return RoomPollDetailViewModel(
                initialState = initialState,
                getTimelineEventUseCase = fakeGetTimelineEventUseCase,
                roomPollDetailMapper = fakeRoomPollDetailMapper,
                voteToPollUseCase = fakeVoteToPollUseCase,
        )
    }

    @Test
    fun `given viewModel when created then poll detail is observed and viewState is updated`() {
        // Given
        val aPollEvent = givenAPollEvent()
        val pollDetail = givenAPollDetail()
        every { fakeGetTimelineEventUseCase.execute(A_ROOM_ID, A_POLL_ID) } returns flowOf(aPollEvent)
        every { fakeRoomPollDetailMapper.map(aPollEvent) } returns pollDetail
        val expectedViewState = initialState.copy(pollDetail = pollDetail)

        // When
        val viewModel = createViewModel()
        val viewModelTest = viewModel.test()

        // Then
        viewModelTest
                .assertLatestState(expectedViewState)
                .finish()
        verify {
            fakeGetTimelineEventUseCase.execute(A_ROOM_ID, A_POLL_ID)
            fakeRoomPollDetailMapper.map(aPollEvent)
        }
    }

    @Test
    fun `given viewModel when handle vote action then correct use case is called`() {
        // Given
        val aPollEvent = givenAPollEvent()
        val pollDetail = givenAPollDetail()
        every { fakeGetTimelineEventUseCase.execute(A_ROOM_ID, A_POLL_ID) } returns flowOf(aPollEvent)
        every { fakeRoomPollDetailMapper.map(aPollEvent) } returns pollDetail
        val viewModel = createViewModel()
        val optionId = "option-id"
        justRun {
            fakeVoteToPollUseCase.execute(
                    roomId = A_ROOM_ID,
                    pollEventId = A_POLL_ID,
                    optionId = optionId,
            )
        }
        val action = RoomPollDetailAction.Vote(
                pollEventId = A_POLL_ID,
                optionId = optionId,
        )

        // When
        val viewModelTest = viewModel.test()
        viewModel.handle(action)

        // Then
        viewModelTest.finish()
        verify {
            fakeVoteToPollUseCase.execute(
                    roomId = A_ROOM_ID,
                    pollEventId = A_POLL_ID,
                    optionId = optionId,
            )
        }
    }

    private fun givenAPollEvent(): TimelineEvent {
        return mockk()
    }

    private fun givenAPollDetail(): RoomPollDetail {
        return RoomPollDetail(
                creationTimestamp = 123L,
                isEnded = false,
                endedPollEventId = null,
                pollItemViewState = mockk(),
        )
    }
}
