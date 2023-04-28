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

package dev.getzen.element.features.call.webrtc

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import dev.getzen.element.core.extensions.startForegroundCompat
import dev.getzen.element.core.services.VectorAndroidService
import dev.getzen.element.features.notifications.NotificationUtils
import im.vector.lib.core.utils.timer.Clock
import javax.inject.Inject

@AndroidEntryPoint
class ScreenCaptureAndroidService : VectorAndroidService() {

    @Inject lateinit var notificationUtils: NotificationUtils
    @Inject lateinit var clock: Clock
    private val binder = LocalBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showStickyNotification()

        return START_STICKY
    }

    private fun showStickyNotification() {
        val notificationId = clock.epochMillis().toInt()
        val notification = notificationUtils.buildScreenSharingNotification()
        startForegroundCompat(notificationId, notification)
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun stopService() {
        stopSelf()
    }

    inner class LocalBinder : Binder() {
        fun getService(): ScreenCaptureAndroidService = this@ScreenCaptureAndroidService
    }
}
