/*
 * Copyright 2019 New Vector Ltd
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

package dev.getzen.element.features.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import dev.getzen.element.R
import dev.getzen.element.core.extensions.observeK
import dev.getzen.element.core.extensions.replaceChildFragment
import dev.getzen.element.core.platform.VectorBaseFragment
import dev.getzen.element.core.resources.BuildMeta
import dev.getzen.element.core.utils.startSharePlainTextIntent
import dev.getzen.element.databinding.FragmentHomeDrawerBinding
import dev.getzen.element.features.analytics.plan.MobileScreen
import dev.getzen.element.features.permalink.PermalinkFactory
import dev.getzen.element.features.settings.VectorPreferences
import dev.getzen.element.features.settings.VectorSettingsActivity
import dev.getzen.element.features.spaces.SpaceListFragment
import dev.getzen.element.features.usercode.UserCodeActivity
import dev.getzen.element.features.workers.signout.SignOutUiWorker
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.util.toMatrixItem
import javax.inject.Inject

@AndroidEntryPoint
class HomeDrawerFragment :
        VectorBaseFragment<FragmentHomeDrawerBinding>() {

    @Inject lateinit var session: Session
    @Inject lateinit var vectorPreferences: VectorPreferences
    @Inject lateinit var avatarRenderer: AvatarRenderer
    @Inject lateinit var buildMeta: BuildMeta
    @Inject lateinit var permalinkFactory: PermalinkFactory

    private lateinit var sharedActionViewModel: HomeSharedActionViewModel

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeDrawerBinding {
        return FragmentHomeDrawerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedActionViewModel = activityViewModelProvider.get(HomeSharedActionViewModel::class.java)

        if (savedInstanceState == null) {
            replaceChildFragment(R.id.homeDrawerGroupListContainer, SpaceListFragment::class.java)
        }
        session.userService().getUserLive(session.myUserId).observeK(viewLifecycleOwner) { optionalUser ->
            val user = optionalUser?.getOrNull()
            if (user != null) {
                avatarRenderer.render(user.toMatrixItem(), views.homeDrawerHeaderAvatarView)
                views.homeDrawerUsernameView.text = user.displayName
                views.homeDrawerUserIdView.text = user.userId
            }
        }
        // Profile
        views.homeDrawerHeader.debouncedClicks {
            sharedActionViewModel.post(HomeActivitySharedAction.CloseDrawer)
            navigator.openSettings(requireActivity(), directAccess = VectorSettingsActivity.EXTRA_DIRECT_ACCESS_GENERAL)
        }
        // Settings
        views.homeDrawerHeaderSettingsView.debouncedClicks {
            sharedActionViewModel.post(HomeActivitySharedAction.CloseDrawer)
            navigator.openSettings(requireActivity())
        }
        // Sign out
        views.homeDrawerHeaderSignoutView.debouncedClicks {
            sharedActionViewModel.post(HomeActivitySharedAction.CloseDrawer)
            SignOutUiWorker(requireActivity()).perform()
        }

        views.homeDrawerQRCodeButton.debouncedClicks {
            UserCodeActivity.newIntent(requireContext(), sharedActionViewModel.session.myUserId).let {
                val options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                requireActivity(),
                                views.homeDrawerHeaderAvatarView,
                                ViewCompat.getTransitionName(views.homeDrawerHeaderAvatarView) ?: ""
                        )
                startActivity(it, options.toBundle())
            }
        }

        views.homeDrawerInviteFriendButton.debouncedClicks {
            permalinkFactory.createPermalinkOfCurrentUser()?.let { permalink ->
                analyticsTracker.screen(MobileScreen(screenName = MobileScreen.ScreenName.InviteFriends))
                val text = getString(R.string.invite_friends_text, permalink)

                startSharePlainTextIntent(
                        context = requireContext(),
                        activityResultLauncher = null,
                        chooserTitle = getString(R.string.invite_friends),
                        text = text,
                        extraTitle = getString(R.string.invite_friends_rich_title)
                )
            }
        }

        // Debug menu
        views.homeDrawerHeaderDebugView.debouncedClicks {
            sharedActionViewModel.post(HomeActivitySharedAction.CloseDrawer)
            navigator.openDebug(requireActivity())
        }
    }

    override fun onResume() {
        super.onResume()
        views.homeDrawerHeaderDebugView.isVisible = buildMeta.isDebug && vectorPreferences.developerMode()
    }
}