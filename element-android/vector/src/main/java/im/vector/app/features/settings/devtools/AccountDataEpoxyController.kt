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

import android.view.View
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import dev.getzen.element.R
import dev.getzen.element.core.epoxy.loadingItem
import dev.getzen.element.core.resources.StringProvider
import dev.getzen.element.core.ui.list.genericFooterItem
import dev.getzen.element.core.ui.list.genericWithValueItem
import im.vector.lib.core.utils.epoxy.charsequence.toEpoxyCharSequence
import org.matrix.android.sdk.api.session.accountdata.UserAccountDataEvent
import javax.inject.Inject

class AccountDataEpoxyController @Inject constructor(
        private val stringProvider: StringProvider
) : TypedEpoxyController<AccountDataViewState>() {

    interface InteractionListener {
        fun didTap(data: UserAccountDataEvent)
        fun didLongTap(data: UserAccountDataEvent)
    }

    var interactionListener: InteractionListener? = null

    override fun buildModels(data: AccountDataViewState?) {
        if (data == null) return
        val host = this
        when (data.accountData) {
            is Loading -> {
                loadingItem {
                    id("loading")
                    loadingText(host.stringProvider.getString(R.string.loading))
                }
            }
            is Fail -> {
                genericFooterItem {
                    id("fail")
                    text(data.accountData.error.localizedMessage?.toEpoxyCharSequence())
                }
            }
            is Success -> {
                val dataList = data.accountData.invoke()
                if (dataList.isEmpty()) {
                    genericFooterItem {
                        id("noResults")
                        text(host.stringProvider.getString(R.string.no_result_placeholder).toEpoxyCharSequence())
                    }
                } else {
                    dataList.forEach { accountData ->
                        genericWithValueItem {
                            id(accountData.type)
                            title(accountData.type.toEpoxyCharSequence())
                            itemClickAction {
                                host.interactionListener?.didTap(accountData)
                            }
                            itemLongClickAction(View.OnLongClickListener {
                                host.interactionListener?.didLongTap(accountData)
                                true
                            })
                        }
                    }
                }
            }
            else -> Unit
        }
    }
}
