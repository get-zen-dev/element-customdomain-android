/*
 * Copyright (c) 2023 New Vector Ltd
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

package dev.getzen.element.features.settings.notifications

import com.airbnb.mvrx.MavericksViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.getzen.element.core.di.MavericksAssistedViewModelFactory
import dev.getzen.element.core.di.hiltMavericksViewModelFactory
import dev.getzen.element.core.platform.VectorViewModel
import dev.getzen.element.features.settings.notifications.VectorSettingsPushRuleNotificationViewEvent.Failure
import dev.getzen.element.features.settings.notifications.VectorSettingsPushRuleNotificationViewEvent.PushRuleUpdated
import dev.getzen.element.features.settings.notifications.usecase.GetPushRulesOnInvalidStateUseCase
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.failure.Failure.ServerError
import org.matrix.android.sdk.api.failure.MatrixError
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.accountdata.UserAccountDataTypes
import org.matrix.android.sdk.api.session.pushrules.Action
import org.matrix.android.sdk.api.session.pushrules.RuleIds
import org.matrix.android.sdk.api.session.pushrules.RuleKind
import org.matrix.android.sdk.api.session.pushrules.rest.PushRuleAndKind
import org.matrix.android.sdk.flow.flow
import org.matrix.android.sdk.flow.unwrap

private typealias ViewModel = VectorSettingsPushRuleNotificationViewModel
private typealias ViewState = VectorSettingsPushRuleNotificationViewState

class VectorSettingsPushRuleNotificationViewModel @AssistedInject constructor(
        @Assisted initialState: ViewState,
        private val session: Session,
        private val getPushRulesOnInvalidStateUseCase: GetPushRulesOnInvalidStateUseCase,
) : VectorViewModel<VectorSettingsPushRuleNotificationViewState,
        VectorSettingsPushRuleNotificationViewAction,
        VectorSettingsPushRuleNotificationViewEvent>(initialState) {

    @AssistedFactory
    interface Factory : MavericksAssistedViewModelFactory<ViewModel, ViewState> {
        override fun create(initialState: ViewState): ViewModel
    }

    companion object : MavericksViewModelFactory<ViewModel, ViewState> by hiltMavericksViewModelFactory()

    init {
        session.flow()
                .liveUserAccountData(UserAccountDataTypes.TYPE_PUSH_RULES)
                .unwrap()
                .setOnEach {
                    val allRules = session.pushRuleService().getPushRules().getAllRules()
                    val rulesOnError = getPushRulesOnInvalidStateUseCase.execute(session).map { it.ruleId }.toSet()
                    copy(
                            allRules = allRules,
                            rulesOnError = rulesOnError
                    )
                }
    }

    override fun handle(action: VectorSettingsPushRuleNotificationViewAction) {
        when (action) {
            is VectorSettingsPushRuleNotificationViewAction.UpdatePushRule -> handleUpdatePushRule(action.ruleId, action.checked)
        }
    }

    fun getPushRuleAndKind(ruleId: String): PushRuleAndKind? {
        return session.pushRuleService().getPushRules().findDefaultRule(ruleId)
    }

    fun isPushRuleChecked(ruleId: String): Boolean {
        val rulesGroup = listOf(ruleId) + RuleIds.getSyncedRules(ruleId)
        return rulesGroup.mapNotNull { getPushRuleAndKind(it) }.any { it.pushRule.notificationIndex != NotificationIndex.OFF }
    }

    private fun handleUpdatePushRule(ruleId: String, checked: Boolean) {
        val kind = getPushRuleAndKind(ruleId)?.kind ?: return
        val newIndex = if (checked) NotificationIndex.NOISY else NotificationIndex.OFF
        val standardAction = getStandardAction(ruleId, newIndex) ?: return
        val enabled = standardAction != StandardActions.Disabled
        val newActions = standardAction.actions

        setState { copy(isLoading = true) }

        viewModelScope.launch {
            val rulesToUpdate = listOf(ruleId) + RuleIds.getSyncedRules(ruleId)
            val results = rulesToUpdate.map { ruleId ->
                runCatching {
                    updatePushRule(kind, ruleId, enabled, newActions)
                }
            }

            val failures = results.mapNotNull { result ->
                // If the failure is a rule not found error, do not consider it
                result.exceptionOrNull()?.takeUnless { it is ServerError && it.error.code == MatrixError.M_NOT_FOUND }
            }
            val hasSuccess = results.any { it.isSuccess }
            val hasFailures = failures.isNotEmpty()

            // Any rule has been checked or some rules have not been unchecked
            val newChecked = (checked && hasSuccess) || (!checked && hasFailures)
            if (hasSuccess) {
                _viewEvents.post(PushRuleUpdated(ruleId, newChecked, failures.firstOrNull()))
            } else {
                _viewEvents.post(Failure(ruleId, failures.firstOrNull()))
            }

            setState {
                copy(
                        isLoading = false,
                        rulesOnError = when {
                            hasSuccess && hasFailures -> rulesOnError.plus(ruleId) // some failed
                            hasSuccess -> rulesOnError.minus(ruleId) // all succeed
                            else -> rulesOnError // all failed
                        }
                )
            }
        }
    }

    private suspend fun updatePushRule(kind: RuleKind, ruleId: String, enable: Boolean, newActions: List<Action>?) {
        session.pushRuleService().updatePushRuleActions(
                kind = kind,
                ruleId = ruleId,
                enable = enable,
                actions = newActions
        )
    }
}
