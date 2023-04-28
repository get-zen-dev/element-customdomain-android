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

package dev.getzen.element.features.spaces.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.activityViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.getzen.element.R
import dev.getzen.element.core.extensions.configureWith
import dev.getzen.element.core.extensions.hideKeyboard
import dev.getzen.element.core.platform.OnBackPressed
import dev.getzen.element.core.platform.VectorBaseFragment
import dev.getzen.element.databinding.FragmentSpaceCreateGenericEpoxyFormBinding
import javax.inject.Inject

@AndroidEntryPoint
class CreateSpaceDefaultRoomsFragment :
        VectorBaseFragment<FragmentSpaceCreateGenericEpoxyFormBinding>(),
        SpaceDefaultRoomEpoxyController.Listener,
        OnBackPressed {

    @Inject lateinit var epoxyController: SpaceDefaultRoomEpoxyController

    private val sharedViewModel: CreateSpaceViewModel by activityViewModel()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
            FragmentSpaceCreateGenericEpoxyFormBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.recyclerView.configureWith(epoxyController)
        epoxyController.listener = this

        sharedViewModel.onEach {
            epoxyController.setData(it)
        }

        views.nextButton.setText(R.string.create_space)
        views.nextButton.debouncedClicks {
            view.hideKeyboard()
            sharedViewModel.handle(CreateSpaceAction.NextFromDefaultRooms)
        }
    }

    override fun onNameChange(index: Int, newName: String) {
        sharedViewModel.handle(CreateSpaceAction.DefaultRoomNameChanged(index, newName))
    }

    override fun onBackPressed(toolbarButton: Boolean): Boolean {
        sharedViewModel.handle(CreateSpaceAction.OnBackPressed)
        return true
    }
}
