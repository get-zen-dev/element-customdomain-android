/*
 * Copyright 2021 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.getzen.element.features.roomprofile.permissions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.airbnb.mvrx.args
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import dagger.hilt.android.AndroidEntryPoint
import dev.getzen.element.R
import dev.getzen.element.core.extensions.cleanup
import dev.getzen.element.core.extensions.configureWith
import dev.getzen.element.core.platform.VectorBaseFragment
import dev.getzen.element.core.utils.toast
import dev.getzen.element.databinding.FragmentRoomSettingGenericBinding
import dev.getzen.element.features.analytics.plan.MobileScreen
import dev.getzen.element.features.home.AvatarRenderer
import dev.getzen.element.features.roommemberprofile.powerlevel.EditPowerLevelDialogs
import dev.getzen.element.features.roomprofile.RoomProfileArgs
import org.matrix.android.sdk.api.session.room.powerlevels.Role
import org.matrix.android.sdk.api.util.toMatrixItem
import javax.inject.Inject

@AndroidEntryPoint
class RoomPermissionsFragment :
        VectorBaseFragment<FragmentRoomSettingGenericBinding>(),
        RoomPermissionsController.Callback {

    @Inject lateinit var controller: RoomPermissionsController
    @Inject lateinit var avatarRenderer: AvatarRenderer

    private val viewModel: RoomPermissionsViewModel by fragmentViewModel()

    private val roomProfileArgs: RoomProfileArgs by args()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentRoomSettingGenericBinding {
        return FragmentRoomSettingGenericBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsScreenName = MobileScreen.ScreenName.RoomPermissions
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller.callback = this
        setupToolbar(views.roomSettingsToolbar)
                .allowBack()
        views.roomSettingsRecyclerView.configureWith(controller, hasFixedSize = true)
        views.waitingView.waitingStatusText.setText(R.string.please_wait)
        views.waitingView.waitingStatusText.isVisible = true

        viewModel.observeViewEvents {
            when (it) {
                is RoomPermissionsViewEvents.Failure -> showFailure(it.throwable)
                RoomPermissionsViewEvents.Success -> showSuccess()
            }
        }
    }

    private fun showSuccess() {
        activity?.toast(R.string.room_settings_save_success)
    }

    override fun onDestroyView() {
        controller.callback = null
        views.roomSettingsRecyclerView.cleanup()
        super.onDestroyView()
    }

    override fun invalidate() = withState(viewModel) { state ->
        views.waitingView.root.isVisible = state.isLoading
        controller.setData(state)
        renderRoomSummary(state)
    }

    private fun renderRoomSummary(state: RoomPermissionsViewState) {
        state.roomSummary()?.let {
            views.roomSettingsToolbarTitleView.text = it.displayName
            avatarRenderer.render(it.toMatrixItem(), views.roomSettingsToolbarAvatarImageView)
            views.roomSettingsDecorationToolbarAvatarImageView.render(it.roomEncryptionTrustLevel)
        }
    }

    override fun onEditPermission(editablePermission: EditablePermission, currentRole: Role) {
        EditPowerLevelDialogs.showChoice(requireActivity(), editablePermission.labelResId, currentRole) { newPowerLevel ->
            viewModel.handle(RoomPermissionsAction.UpdatePermission(editablePermission, newPowerLevel))
        }
    }

    override fun toggleShowAllPermissions() {
        viewModel.handle(RoomPermissionsAction.ToggleShowAllPermissions)
    }
}
