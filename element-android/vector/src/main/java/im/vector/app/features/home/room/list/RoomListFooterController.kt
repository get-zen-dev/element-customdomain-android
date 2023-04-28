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

package dev.getzen.element.features.home.room.list

import com.airbnb.epoxy.TypedEpoxyController
import dev.getzen.element.R
import dev.getzen.element.core.epoxy.helpFooterItem
import dev.getzen.element.core.resources.StringProvider
import dev.getzen.element.core.resources.UserPreferencesProvider
import dev.getzen.element.features.home.RoomListDisplayMode
import dev.getzen.element.features.home.room.filtered.FilteredRoomFooterItem
import dev.getzen.element.features.home.room.filtered.filteredRoomFooterItem
import javax.inject.Inject

class RoomListFooterController @Inject constructor(
        private val stringProvider: StringProvider,
        private val userPreferencesProvider: UserPreferencesProvider
) : TypedEpoxyController<RoomListViewState>() {

    var listener: FilteredRoomFooterItem.Listener? = null

    override fun buildModels(data: RoomListViewState?) {
        val host = this
        when (data?.displayMode) {
            RoomListDisplayMode.FILTERED -> {
                filteredRoomFooterItem {
                    id("filter_footer")
                    listener(host.listener)
                    currentFilter(data.roomFilter)
                    inSpace(data.asyncSelectedSpace.invoke() != null)
                }
            }
            else -> {
                if (userPreferencesProvider.shouldShowLongClickOnRoomHelp()) {
                    helpFooterItem {
                        id("long_click_help")
                        text(host.stringProvider.getString(R.string.help_long_click_on_room_for_more_options))
                    }
                }
            }
        }
    }
}
