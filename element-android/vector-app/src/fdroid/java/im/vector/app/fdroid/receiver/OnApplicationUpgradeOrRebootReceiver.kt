/*
 * Copyright 2018 New Vector Ltd
 * Copyright 2019 New Vector Ltd
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

package dev.getzen.element.fdroid.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import dev.getzen.element.core.di.ActiveSessionHolder
import dev.getzen.element.fdroid.BackgroundSyncStarter
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class OnApplicationUpgradeOrRebootReceiver : BroadcastReceiver() {

    @Inject lateinit var activeSessionHolder: ActiveSessionHolder
    @Inject lateinit var backgroundSyncStarter: BackgroundSyncStarter

    override fun onReceive(context: Context, intent: Intent) {
        Timber.v("## onReceive() ${intent.action}")
        backgroundSyncStarter.start(activeSessionHolder)
    }
}