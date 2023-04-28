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

package dev.getzen.element.features.debug.leak

import com.airbnb.mvrx.MavericksViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.getzen.element.core.debug.LeakDetector
import dev.getzen.element.core.di.MavericksAssistedViewModelFactory
import dev.getzen.element.core.di.hiltMavericksViewModelFactory
import dev.getzen.element.core.platform.EmptyViewEvents
import dev.getzen.element.core.platform.VectorViewModel
import dev.getzen.element.features.settings.VectorPreferences
import kotlinx.coroutines.launch

class DebugMemoryLeaksViewModel @AssistedInject constructor(
        @Assisted initialState: DebugMemoryLeaksViewState,
        private val vectorPreferences: VectorPreferences,
        private val leakDetector: LeakDetector,
) : VectorViewModel<DebugMemoryLeaksViewState, DebugMemoryLeaksViewActions, EmptyViewEvents>(initialState) {

    @AssistedFactory
    interface Factory : MavericksAssistedViewModelFactory<DebugMemoryLeaksViewModel, DebugMemoryLeaksViewState> {
        override fun create(initialState: DebugMemoryLeaksViewState): DebugMemoryLeaksViewModel
    }

    companion object : MavericksViewModelFactory<DebugMemoryLeaksViewModel, DebugMemoryLeaksViewState> by hiltMavericksViewModelFactory()

    init {
        viewModelScope.launch {
            refreshStateFromPreferences()
        }
    }

    override fun handle(action: DebugMemoryLeaksViewActions) {
        when (action) {
            is DebugMemoryLeaksViewActions.EnableMemoryLeaksAnalysis -> handleEnableMemoryLeaksAnalysis(action)
        }
    }

    private fun handleEnableMemoryLeaksAnalysis(action: DebugMemoryLeaksViewActions.EnableMemoryLeaksAnalysis) {
        viewModelScope.launch {
            vectorPreferences.enableMemoryLeakAnalysis(action.isEnabled)
            leakDetector.enable(action.isEnabled)
            refreshStateFromPreferences()
        }
    }

    private fun refreshStateFromPreferences() {
        setState { copy(isMemoryLeaksAnalysisEnabled = vectorPreferences.isMemoryLeakAnalysisEnabled()) }
    }
}
