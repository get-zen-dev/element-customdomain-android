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

package dev.getzen.element.features.spaces

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.airbnb.mvrx.Mavericks
import com.airbnb.mvrx.viewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.getzen.element.R
import dev.getzen.element.core.extensions.registerStartForActivityResult
import dev.getzen.element.core.extensions.replaceFragment
import dev.getzen.element.core.platform.VectorBaseActivity
import dev.getzen.element.databinding.ActivitySimpleBinding
import dev.getzen.element.features.analytics.plan.ViewRoom
import dev.getzen.element.features.matrixto.MatrixToBottomSheet
import dev.getzen.element.features.matrixto.OriginOfMatrixTo
import dev.getzen.element.features.navigation.Navigator
import dev.getzen.element.features.roomdirectory.createroom.CreateRoomActivity
import dev.getzen.element.features.spaces.explore.SpaceDirectoryArgs
import dev.getzen.element.features.spaces.explore.SpaceDirectoryFragment
import dev.getzen.element.features.spaces.explore.SpaceDirectoryViewAction
import dev.getzen.element.features.spaces.explore.SpaceDirectoryViewEvents
import dev.getzen.element.features.spaces.explore.SpaceDirectoryViewModel
import im.vector.lib.core.utils.compat.getParcelableExtraCompat

@AndroidEntryPoint
class SpaceExploreActivity : VectorBaseActivity<ActivitySimpleBinding>(), MatrixToBottomSheet.InteractionListener {

    override fun getBinding(): ActivitySimpleBinding = ActivitySimpleBinding.inflate(layoutInflater)

    override fun getTitleRes(): Int = R.string.space_explore_activity_title

    val sharedViewModel: SpaceDirectoryViewModel by viewModel()

    private val createRoomResultLauncher = registerStartForActivityResult { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            CreateRoomActivity.getCreatedRoomId(activityResult.data)?.let {
                // we want to refresh from API
                sharedViewModel.handle(SpaceDirectoryViewAction.RefreshUntilFound(it))
            }
        }
    }

    private val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            if (f is MatrixToBottomSheet) {
                f.interactionListener = this@SpaceExploreActivity
            }
            super.onFragmentResumed(fm, f)
        }

        override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
            if (f is MatrixToBottomSheet) {
                f.interactionListener = null
            }
            super.onFragmentPaused(fm, f)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false)

        if (isFirstCreation()) {
            val args = intent?.getParcelableExtraCompat<SpaceDirectoryArgs>(Mavericks.KEY_ARG)
            replaceFragment(
                    views.simpleFragmentContainer,
                    SpaceDirectoryFragment::class.java,
                    args
            )
        }

        sharedViewModel.observeViewEvents {
            when (it) {
                SpaceDirectoryViewEvents.Dismiss -> {
                    finish()
                }
                is SpaceDirectoryViewEvents.NavigateToRoom -> {
                    navigator.openRoom(
                            context = this,
                            roomId = it.roomId,
                            trigger = ViewRoom.Trigger.SpaceHierarchy
                    )
                }
                is SpaceDirectoryViewEvents.NavigateToMxToBottomSheet -> {
                    MatrixToBottomSheet.withLink(it.link, OriginOfMatrixTo.SPACE_EXPLORE).show(supportFragmentManager, "ShowChild")
                }
                is SpaceDirectoryViewEvents.NavigateToCreateNewRoom -> {
                    createRoomResultLauncher.launch(
                            CreateRoomActivity.getIntent(
                                    this,
                                    openAfterCreate = false,
                                    currentSpaceId = it.currentSpaceId
                            )
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
        super.onDestroy()
    }

    companion object {
        fun newIntent(context: Context, spaceId: String): Intent {
            return Intent(context, SpaceExploreActivity::class.java).apply {
                putExtra(Mavericks.KEY_ARG, SpaceDirectoryArgs(spaceId))
            }
        }
    }

    override fun mxToBottomSheetNavigateToRoom(roomId: String, trigger: ViewRoom.Trigger?) {
        navigator.openRoom(this, roomId, trigger = trigger)
    }

    override fun mxToBottomSheetSwitchToSpace(spaceId: String) {
        navigator.switchToSpace(this, spaceId, Navigator.PostSwitchSpaceAction.None)
    }
}