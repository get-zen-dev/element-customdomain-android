/*
 * Copyright 2018 New Vector Ltd
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
@file:Suppress("UNUSED_PARAMETER")

package dev.getzen.element.push.fcm

import android.content.Context
import dev.getzen.element.core.di.ActiveSessionHolder
import dev.getzen.element.core.pushers.FcmHelper
import dev.getzen.element.core.pushers.PushersManager
import dev.getzen.element.fdroid.BackgroundSyncStarter
import dev.getzen.element.fdroid.receiver.AlarmSyncBroadcastReceiver
import javax.inject.Inject

/**
 * This class has an alter ego in the gplay variant.
 */
class FdroidFcmHelper @Inject constructor(
        private val context: Context,
        private val backgroundSyncStarter: BackgroundSyncStarter,
) : FcmHelper {

    override fun isFirebaseAvailable(): Boolean = false

    override fun getFcmToken(): String? {
        return null
    }

    override fun storeFcmToken(token: String?) {
        // No op
    }

    override fun ensureFcmTokenIsRetrieved(pushersManager: PushersManager, registerPusher: Boolean) {
        // No op
    }

    override fun onEnterForeground(activeSessionHolder: ActiveSessionHolder) {
        // try to stop all regardless of background mode
        activeSessionHolder.getSafeActiveSession()?.syncService()?.stopAnyBackgroundSync()
        AlarmSyncBroadcastReceiver.cancelAlarm(context)
    }

    override fun onEnterBackground(activeSessionHolder: ActiveSessionHolder) {
        backgroundSyncStarter.start(activeSessionHolder)
    }
}
