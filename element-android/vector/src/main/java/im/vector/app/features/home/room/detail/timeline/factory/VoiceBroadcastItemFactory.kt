/*
 * Copyright 2022 New Vector Ltd
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
package dev.getzen.element.features.home.room.detail.timeline.factory

import dev.getzen.element.core.error.ErrorFormatter
import dev.getzen.element.core.resources.ColorProvider
import dev.getzen.element.core.resources.DrawableProvider
import dev.getzen.element.features.home.room.detail.timeline.helper.AudioMessagePlaybackTracker
import dev.getzen.element.features.home.room.detail.timeline.helper.AvatarSizeProvider
import dev.getzen.element.features.home.room.detail.timeline.helper.VoiceBroadcastEventsGroup
import dev.getzen.element.features.home.room.detail.timeline.item.AbsMessageItem
import dev.getzen.element.features.home.room.detail.timeline.item.AbsMessageVoiceBroadcastItem
import dev.getzen.element.features.home.room.detail.timeline.item.BaseEventItem
import dev.getzen.element.features.home.room.detail.timeline.item.MessageVoiceBroadcastListeningItem
import dev.getzen.element.features.home.room.detail.timeline.item.MessageVoiceBroadcastListeningItem_
import dev.getzen.element.features.home.room.detail.timeline.item.MessageVoiceBroadcastRecordingItem
import dev.getzen.element.features.home.room.detail.timeline.item.MessageVoiceBroadcastRecordingItem_
import dev.getzen.element.features.voicebroadcast.listening.VoiceBroadcastPlayer
import dev.getzen.element.features.voicebroadcast.model.MessageVoiceBroadcastInfoContent
import dev.getzen.element.features.voicebroadcast.model.VoiceBroadcast
import dev.getzen.element.features.voicebroadcast.model.VoiceBroadcastState
import dev.getzen.element.features.voicebroadcast.model.asVoiceBroadcastEvent
import dev.getzen.element.features.voicebroadcast.recording.VoiceBroadcastRecorder
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.util.toMatrixItem
import javax.inject.Inject

class VoiceBroadcastItemFactory @Inject constructor(
        private val session: Session,
        private val avatarSizeProvider: AvatarSizeProvider,
        private val colorProvider: ColorProvider,
        private val drawableProvider: DrawableProvider,
        private val errorFormatter: ErrorFormatter,
        private val voiceBroadcastRecorder: VoiceBroadcastRecorder?,
        private val voiceBroadcastPlayer: VoiceBroadcastPlayer,
        private val playbackTracker: AudioMessagePlaybackTracker,
        private val noticeItemFactory: NoticeItemFactory,
) {

    fun create(
            params: TimelineItemFactoryParams,
            messageContent: MessageVoiceBroadcastInfoContent,
            highlight: Boolean,
            attributes: AbsMessageItem.Attributes,
    ): BaseEventItem<*>? {
        // Only display item of the initial event with updated data
        if (messageContent.voiceBroadcastState != VoiceBroadcastState.STARTED) {
            return noticeItemFactory.create(params)
        }

        val voiceBroadcastEventsGroup = params.eventsGroup?.let { VoiceBroadcastEventsGroup(it) } ?: return null
        val voiceBroadcastEvent = voiceBroadcastEventsGroup.getLastDisplayableEvent().root.asVoiceBroadcastEvent() ?: return null
        val voiceBroadcastContent = voiceBroadcastEvent.content ?: return null
        val voiceBroadcast = VoiceBroadcast(voiceBroadcastId = voiceBroadcastEventsGroup.voiceBroadcastId, roomId = params.event.roomId)

        val isRecording = voiceBroadcastContent.voiceBroadcastState != VoiceBroadcastState.STOPPED &&
                voiceBroadcastEvent.root.stateKey == session.myUserId &&
                messageContent.deviceId == session.sessionParams.deviceId

        val voiceBroadcastAttributes = AbsMessageVoiceBroadcastItem.Attributes(
                voiceBroadcast = voiceBroadcast,
                voiceBroadcastState = voiceBroadcastContent.voiceBroadcastState,
                duration = voiceBroadcastEventsGroup.getDuration(),
                hasUnableToDecryptEvent = voiceBroadcastEventsGroup.hasUnableToDecryptEvent(),
                recorderName = params.event.senderInfo.disambiguatedDisplayName,
                recorder = voiceBroadcastRecorder,
                player = voiceBroadcastPlayer,
                playbackTracker = playbackTracker,
                roomItem = session.getRoom(params.event.roomId)?.roomSummary()?.toMatrixItem(),
                colorProvider = colorProvider,
                drawableProvider = drawableProvider,
                errorFormatter = errorFormatter,
        )

        return if (isRecording) {
            createRecordingItem(highlight, attributes, voiceBroadcastAttributes)
        } else {
            createListeningItem(highlight, attributes, voiceBroadcastAttributes)
        }
    }

    private fun createRecordingItem(
            highlight: Boolean,
            attributes: AbsMessageItem.Attributes,
            voiceBroadcastAttributes: AbsMessageVoiceBroadcastItem.Attributes,
    ): MessageVoiceBroadcastRecordingItem {
        return MessageVoiceBroadcastRecordingItem_()
                .id("voice_broadcast_${voiceBroadcastAttributes.voiceBroadcast.voiceBroadcastId}")
                .attributes(attributes)
                .voiceBroadcastAttributes(voiceBroadcastAttributes)
                .highlighted(highlight)
                .leftGuideline(avatarSizeProvider.leftGuideline)
    }

    private fun createListeningItem(
            highlight: Boolean,
            attributes: AbsMessageItem.Attributes,
            voiceBroadcastAttributes: AbsMessageVoiceBroadcastItem.Attributes,
    ): MessageVoiceBroadcastListeningItem {
        return MessageVoiceBroadcastListeningItem_()
                .id("voice_broadcast_${voiceBroadcastAttributes.voiceBroadcast.voiceBroadcastId}")
                .attributes(attributes)
                .voiceBroadcastAttributes(voiceBroadcastAttributes)
                .highlighted(highlight)
                .leftGuideline(avatarSizeProvider.leftGuideline)
    }
}
