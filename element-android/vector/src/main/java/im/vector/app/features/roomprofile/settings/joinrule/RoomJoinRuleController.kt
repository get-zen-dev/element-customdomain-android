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

package dev.getzen.element.features.roomprofile.settings.joinrule

import dev.getzen.element.R
import dev.getzen.element.core.resources.DrawableProvider
import dev.getzen.element.core.resources.StringProvider
import dev.getzen.element.core.ui.bottomsheet.BottomSheetGenericController
import org.matrix.android.sdk.api.session.room.model.RoomJoinRules
import javax.inject.Inject

class RoomJoinRuleController @Inject constructor(
        private val stringProvider: StringProvider,
        private val drawableProvider: DrawableProvider
) : BottomSheetGenericController<RoomJoinRuleState, RoomJoinRuleRadioAction>() {

    override fun getTitle() =
            stringProvider.getString(
                    // generic title for both room and space
                    R.string.room_settings_access_rules_pref_dialog_title
            )

    override fun getActions(state: RoomJoinRuleState): List<RoomJoinRuleRadioAction> {
        return listOf(
                RoomJoinRuleRadioAction(
                        roomJoinRule = RoomJoinRules.INVITE,
                        description = stringProvider.getString(R.string.room_settings_room_access_private_description),
                        title = stringProvider.getString(R.string.room_settings_room_access_private_title),
                        isSelected = state.currentRoomJoinRule == RoomJoinRules.INVITE
                ),
                RoomJoinRuleRadioAction(
                        roomJoinRule = RoomJoinRules.PUBLIC,
                        description = stringProvider.getString(
                                if (state.isSpace) R.string.room_settings_space_access_public_description
                                else R.string.room_settings_room_access_public_description
                        ),
                        title = stringProvider.getString(R.string.room_settings_room_access_public_title),
                        isSelected = state.currentRoomJoinRule == RoomJoinRules.PUBLIC
                ),
                RoomJoinRuleRadioAction(
                        roomJoinRule = RoomJoinRules.RESTRICTED,
                        description = if (state.parentSpaceName != null) {
                            stringProvider.getString(R.string.room_create_member_of_space_name_can_join, state.parentSpaceName)
                        } else {
                            stringProvider.getString(R.string.room_settings_room_access_restricted_description)
                        },
                        title = stringProvider.getString(R.string.room_settings_room_access_restricted_title),
                        isSelected = state.currentRoomJoinRule == RoomJoinRules.RESTRICTED
                )
        ).filter { state.allowedJoinedRules.map { it.rule }.contains(it.roomJoinRule) }
    }
}
