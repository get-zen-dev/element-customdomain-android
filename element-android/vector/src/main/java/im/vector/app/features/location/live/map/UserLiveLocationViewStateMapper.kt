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

package dev.getzen.element.features.location.live.map

import dev.getzen.element.core.di.ActiveSessionHolder
import dev.getzen.element.features.home.room.detail.timeline.helper.LocationPinProvider
import dev.getzen.element.features.location.toLocationData
import kotlinx.coroutines.suspendCancellableCoroutine
import org.matrix.android.sdk.api.session.getUserOrDefault
import org.matrix.android.sdk.api.session.room.model.livelocation.LiveLocationShareAggregatedSummary
import org.matrix.android.sdk.api.util.toMatrixItem
import javax.inject.Inject

class UserLiveLocationViewStateMapper @Inject constructor(
        private val locationPinProvider: LocationPinProvider,
        private val activeSessionHolder: ActiveSessionHolder,
) {

    suspend fun map(liveLocationShareAggregatedSummary: LiveLocationShareAggregatedSummary) =
            suspendCancellableCoroutine<UserLiveLocationViewState?> { continuation ->
                val userId = liveLocationShareAggregatedSummary.userId
                val locationData = liveLocationShareAggregatedSummary.lastLocationDataContent
                        ?.getBestLocationInfo()
                        ?.geoUri
                        .toLocationData()

                when {
                    userId.isNullOrEmpty() || locationData == null -> continuation.resume(null) {
                        // do nothing on cancellation
                    }
                    else -> {
                        locationPinProvider.create(userId) { pinDrawable ->
                            val session = activeSessionHolder.getActiveSession()
                            val locationTimestampMillis = liveLocationShareAggregatedSummary.lastLocationDataContent?.getBestTimestampMillis()
                            val viewState = UserLiveLocationViewState(
                                    matrixItem = session.getUserOrDefault(userId).toMatrixItem(),
                                    pinDrawable = pinDrawable,
                                    locationData = locationData,
                                    endOfLiveTimestampMillis = liveLocationShareAggregatedSummary.endOfLiveTimestampMillis,
                                    locationTimestampMillis = locationTimestampMillis,
                                    showStopSharingButton = userId == session.myUserId
                            )
                            continuation.resume(viewState) {
                                // do nothing on cancellation
                            }
                        }
                    }
                }
            }
}
