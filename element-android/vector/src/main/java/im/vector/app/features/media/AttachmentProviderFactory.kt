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

package dev.getzen.element.features.media

import dev.getzen.element.core.date.VectorDateFormatter
import dev.getzen.element.core.resources.StringProvider
import kotlinx.coroutines.CoroutineScope
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import javax.inject.Inject

class AttachmentProviderFactory @Inject constructor(
        private val imageContentRenderer: ImageContentRenderer,
        private val vectorDateFormatter: VectorDateFormatter,
        private val stringProvider: StringProvider,
        private val session: Session
) {

    fun createProvider(
            attachments: List<TimelineEvent>,
            coroutineScope: CoroutineScope
    ): RoomEventsAttachmentProvider {
        return RoomEventsAttachmentProvider(
                attachments = attachments,
                imageContentRenderer = imageContentRenderer,
                dateFormatter = vectorDateFormatter,
                fileService = session.fileService(),
                coroutineScope = coroutineScope,
                stringProvider = stringProvider
        )
    }

    fun createProvider(
            attachments: List<AttachmentData>,
            room: Room?,
            coroutineScope: CoroutineScope
    ): DataAttachmentRoomProvider {
        return DataAttachmentRoomProvider(
                attachments = attachments,
                room = room,
                imageContentRenderer = imageContentRenderer,
                dateFormatter = vectorDateFormatter,
                fileService = session.fileService(),
                coroutineScope = coroutineScope,
                stringProvider = stringProvider
        )
    }
}
