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

package dev.getzen.element.features.home.room.detail.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.parentFragmentViewModel
import com.airbnb.mvrx.withState
import dagger.hilt.android.AndroidEntryPoint
import dev.getzen.element.R
import dev.getzen.element.core.extensions.cleanup
import dev.getzen.element.core.extensions.configureWith
import dev.getzen.element.core.platform.VectorBaseBottomSheetDialogFragment
import dev.getzen.element.core.resources.ColorProvider
import dev.getzen.element.databinding.BottomSheetGenericListWithTitleBinding
import dev.getzen.element.features.home.room.detail.RoomDetailAction
import dev.getzen.element.features.home.room.detail.RoomDetailViewState
import dev.getzen.element.features.home.room.detail.TimelineViewModel
import dev.getzen.element.features.navigation.Navigator
import org.matrix.android.sdk.api.session.widgets.model.Widget
import javax.inject.Inject

/**
 * Bottom sheet displaying active widgets in a room.
 */
@AndroidEntryPoint
class RoomWidgetsBottomSheet :
        VectorBaseBottomSheetDialogFragment<BottomSheetGenericListWithTitleBinding>(),
        RoomWidgetsController.Listener {

    @Inject lateinit var epoxyController: RoomWidgetsController
    @Inject lateinit var colorProvider: ColorProvider
    @Inject lateinit var navigator: Navigator

    private val timelineViewModel: TimelineViewModel by parentFragmentViewModel()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): BottomSheetGenericListWithTitleBinding {
        return BottomSheetGenericListWithTitleBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.bottomSheetRecyclerView.configureWith(epoxyController, hasFixedSize = false)
        views.bottomSheetTitle.text = getString(R.string.active_widgets_title)
        views.bottomSheetTitle.textSize = 20f
        views.bottomSheetTitle.setTextColor(colorProvider.getColorFromAttribute(R.attr.vctr_content_primary))
        epoxyController.listener = this
        timelineViewModel.onAsync(RoomDetailViewState::activeRoomWidgets) {
            epoxyController.setData(it)
        }
    }

    override fun onDestroyView() {
        views.bottomSheetRecyclerView.cleanup()
        epoxyController.listener = null
        super.onDestroyView()
    }

    override fun didSelectWidget(widget: Widget) = withState(timelineViewModel) {
        navigator.openRoomWidget(requireContext(), it.roomId, widget)
        dismiss()
    }

    override fun didSelectManageWidgets() {
        timelineViewModel.handle(RoomDetailAction.OpenIntegrationManager)
        dismiss()
    }

    companion object {
        fun newInstance(): RoomWidgetsBottomSheet {
            return RoomWidgetsBottomSheet()
        }
    }
}
