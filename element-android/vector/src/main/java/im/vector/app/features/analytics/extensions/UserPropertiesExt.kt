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

package dev.getzen.element.features.analytics.extensions

import dev.getzen.element.features.analytics.plan.UserProperties
import dev.getzen.element.features.home.room.list.home.header.HomeRoomFilter
import dev.getzen.element.features.onboarding.FtueUseCase

fun FtueUseCase.toTrackingValue(): UserProperties.FtueUseCaseSelection {
    return when (this) {
        FtueUseCase.FRIENDS_FAMILY -> UserProperties.FtueUseCaseSelection.PersonalMessaging
        FtueUseCase.TEAMS -> UserProperties.FtueUseCaseSelection.WorkMessaging
        FtueUseCase.COMMUNITIES -> UserProperties.FtueUseCaseSelection.CommunityMessaging
        FtueUseCase.SKIP -> UserProperties.FtueUseCaseSelection.Skip
    }
}

fun HomeRoomFilter.toTrackingValue(): UserProperties.AllChatsActiveFilter {
    return when (this) {
        HomeRoomFilter.ALL -> UserProperties.AllChatsActiveFilter.All
        HomeRoomFilter.UNREADS -> UserProperties.AllChatsActiveFilter.Unreads
        HomeRoomFilter.FAVOURITES -> UserProperties.AllChatsActiveFilter.Favourites
        HomeRoomFilter.PEOPlE -> UserProperties.AllChatsActiveFilter.People
    }
}
