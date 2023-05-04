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

package dev.getzen.element.features.settings.devtools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import dagger.hilt.android.AndroidEntryPoint
import dev.getzen.element.R
import dev.getzen.element.core.extensions.cleanup
import dev.getzen.element.core.extensions.configureWith
import dev.getzen.element.core.platform.VectorBaseFragment
import dev.getzen.element.databinding.FragmentGenericRecyclerBinding
import org.matrix.android.sdk.api.session.crypto.model.AuditTrail
import javax.inject.Inject

@AndroidEntryPoint
class GossipingEventsPaperTrailFragment :
        VectorBaseFragment<FragmentGenericRecyclerBinding>(),
        GossipingTrailPagedEpoxyController.InteractionListener {

    @Inject lateinit var epoxyController: GossipingTrailPagedEpoxyController

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentGenericRecyclerBinding {
        return FragmentGenericRecyclerBinding.inflate(inflater, container, false)
    }

    private val viewModel: GossipingEventsPaperTrailViewModel by fragmentViewModel(GossipingEventsPaperTrailViewModel::class)

    override fun invalidate() = withState(viewModel) { state ->
        state.events.invoke()?.let {
            epoxyController.submitList(it)
        }
        Unit
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.genericRecyclerView.configureWith(epoxyController, dividerDrawable = R.drawable.divider_horizontal)
        epoxyController.interactionListener = this
    }

    override fun onDestroyView() {
        views.genericRecyclerView.cleanup()
        epoxyController.interactionListener = null
        super.onDestroyView()
    }

    override fun didTap(event: AuditTrail) {
//        if (event.isEncrypted()) {
//            event.toClearContentStringWithIndent()
//        } else {
//            event.toContentStringWithIndent()
//        }?.let {
//            JSonViewerDialog.newInstance(
//                    it,
//                    -1,
//                    createJSonViewerStyleProvider(colorProvider)
//            ).show(childFragmentManager, "JSON_VIEWER")
//        }
    }
}