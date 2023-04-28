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

package dev.getzen.element.features.roomprofile.polls.list.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.airbnb.mvrx.parentFragmentViewModel
import com.airbnb.mvrx.withState
import dev.getzen.element.core.epoxy.onClick
import dev.getzen.element.core.extensions.cleanup
import dev.getzen.element.core.extensions.configureWith
import dev.getzen.element.core.platform.VectorBaseFragment
import dev.getzen.element.core.resources.StringProvider
import dev.getzen.element.databinding.FragmentRoomPollsListBinding
import dev.getzen.element.features.roomprofile.polls.RoomPollsAction
import dev.getzen.element.features.roomprofile.polls.RoomPollsLoadingError
import dev.getzen.element.features.roomprofile.polls.RoomPollsType
import dev.getzen.element.features.roomprofile.polls.RoomPollsViewEvent
import dev.getzen.element.features.roomprofile.polls.RoomPollsViewModel
import dev.getzen.element.features.roomprofile.polls.RoomPollsViewState
import javax.inject.Inject

abstract class RoomPollsListFragment :
        VectorBaseFragment<FragmentRoomPollsListBinding>(),
        RoomPollsController.Listener {

    @Inject
    lateinit var roomPollsController: RoomPollsController

    @Inject
    lateinit var stringProvider: StringProvider

    @Inject
    lateinit var viewNavigator: RoomPollsListNavigator

    private val viewModel: RoomPollsViewModel by parentFragmentViewModel(RoomPollsViewModel::class)

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentRoomPollsListBinding {
        return FragmentRoomPollsListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewEvents()
        setupList()
        setupLoadMoreButton()
    }

    private fun observeViewEvents() {
        viewModel.observeViewEvents { viewEvent ->
            when (viewEvent) {
                RoomPollsViewEvent.LoadingError -> showErrorInSnackbar(RoomPollsLoadingError())
            }
        }
    }

    abstract fun getEmptyListTitle(canLoadMore: Boolean, nbLoadedDays: Int): String

    abstract fun getRoomPollsType(): RoomPollsType

    private fun setupList() = withState(viewModel) { viewState ->
        roomPollsController.listener = this
        views.roomPollsList.configureWith(roomPollsController)
        views.roomPollsEmptyTitle.text = getEmptyListTitle(
                canLoadMore = viewState.canLoadMore,
                nbLoadedDays = viewState.nbSyncedDays,
        )
    }

    private fun setupLoadMoreButton() {
        views.roomPollsLoadMoreWhenEmpty.onClick {
            onLoadMoreClicked()
        }
    }

    override fun onDestroyView() {
        cleanUpList()
        super.onDestroyView()
    }

    private fun cleanUpList() {
        views.roomPollsList.cleanup()
        roomPollsController.listener = null
    }

    override fun invalidate() = withState(viewModel) { viewState ->
        val filteredPolls = when (getRoomPollsType()) {
            RoomPollsType.ACTIVE -> viewState.polls.filterIsInstance(PollSummary.ActivePoll::class.java)
            RoomPollsType.ENDED -> viewState.polls.filterIsInstance(PollSummary.EndedPoll::class.java)
        }
        val updatedViewState = viewState.copy(polls = filteredPolls)
        renderList(updatedViewState)
        renderSyncingView(updatedViewState)
    }

    private fun renderSyncingView(viewState: RoomPollsViewState) {
        views.roomPollsSyncingTitle.isVisible = viewState.isSyncing
        views.roomPollsSyncingProgress.isVisible = viewState.isSyncing
    }

    private fun renderList(viewState: RoomPollsViewState) {
        roomPollsController.setData(viewState)
        views.roomPollsEmptyTitle.text = getEmptyListTitle(
                canLoadMore = viewState.canLoadMore,
                nbLoadedDays = viewState.nbSyncedDays,
        )
        views.roomPollsEmptyTitle.isVisible = !viewState.isSyncing && viewState.hasNoPolls()
        views.roomPollsLoadMoreWhenEmpty.isVisible = viewState.hasNoPollsAndCanLoadMore()
        views.roomPollsLoadMoreWhenEmpty.isEnabled = !viewState.isLoadingMore
        views.roomPollsLoadMoreWhenEmptyProgress.isVisible = viewState.hasNoPollsAndCanLoadMore() && viewState.isLoadingMore
    }

    override fun onPollClicked(pollId: String) = withState(viewModel) {
        viewNavigator.goToPollDetails(
                context = requireContext(),
                pollId = pollId,
                roomId = it.roomId,
                isEnded = getRoomPollsType() == RoomPollsType.ENDED,
        )
    }

    override fun onLoadMoreClicked() {
        viewModel.handle(RoomPollsAction.LoadMorePolls)
    }
}
