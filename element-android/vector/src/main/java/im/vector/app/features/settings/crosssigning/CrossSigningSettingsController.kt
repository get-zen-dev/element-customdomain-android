/*
 * Copyright 2020 New Vector Ltd
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
package dev.getzen.element.features.settings.crosssigning

import com.airbnb.epoxy.TypedEpoxyController
import dev.getzen.element.R
import dev.getzen.element.core.resources.ColorProvider
import dev.getzen.element.core.resources.StringProvider
import dev.getzen.element.core.ui.list.genericButtonItem
import dev.getzen.element.core.ui.list.genericItem
import dev.getzen.element.core.ui.list.genericPositiveButtonItem
import dev.getzen.element.core.ui.list.genericWithValueItem
import dev.getzen.element.core.utils.DimensionConverter
import im.vector.lib.core.utils.epoxy.charsequence.toEpoxyCharSequence
import me.gujun.android.span.span
import javax.inject.Inject

class CrossSigningSettingsController @Inject constructor(
        private val stringProvider: StringProvider,
        private val colorProvider: ColorProvider,
        private val dimensionConverter: DimensionConverter
) : TypedEpoxyController<CrossSigningSettingsViewState>() {

    interface InteractionListener {
        fun didTapInitializeCrossSigning()
    }

    var interactionListener: InteractionListener? = null

    override fun buildModels(data: CrossSigningSettingsViewState?) {
        if (data == null) return
        val host = this
        when {
            data.xSigningKeyCanSign -> {
                genericItem {
                    id("can")
                    titleIconResourceId(R.drawable.ic_shield_trusted)
                    title(host.stringProvider.getString(R.string.encryption_information_dg_xsigning_complete).toEpoxyCharSequence())
                }
                genericButtonItem {
                    id("Reset")
                    text(host.stringProvider.getString(R.string.reset_cross_signing))
                    buttonClickAction {
                        host.interactionListener?.didTapInitializeCrossSigning()
                    }
                }
            }
            data.xSigningKeysAreTrusted -> {
                genericItem {
                    id("trusted")
                    titleIconResourceId(R.drawable.ic_shield_custom)
                    title(host.stringProvider.getString(R.string.encryption_information_dg_xsigning_trusted).toEpoxyCharSequence())
                }
                genericButtonItem {
                    id("Reset")
                    text(host.stringProvider.getString(R.string.reset_cross_signing))
                    buttonClickAction {
                        host.interactionListener?.didTapInitializeCrossSigning()
                    }
                }
            }
            data.xSigningIsEnableInAccount -> {
                genericItem {
                    id("enable")
                    titleIconResourceId(R.drawable.ic_shield_black)
                    title(host.stringProvider.getString(R.string.encryption_information_dg_xsigning_not_trusted).toEpoxyCharSequence())
                }
                genericButtonItem {
                    id("Reset")
                    text(host.stringProvider.getString(R.string.reset_cross_signing))
                    buttonClickAction {
                        host.interactionListener?.didTapInitializeCrossSigning()
                    }
                }
            }
            else -> {
                genericItem {
                    id("not")
                    title(host.stringProvider.getString(R.string.encryption_information_dg_xsigning_disabled).toEpoxyCharSequence())
                }

                genericPositiveButtonItem {
                    id("Initialize")
                    text(host.stringProvider.getString(R.string.initialize_cross_signing))
                    buttonClickAction {
                        host.interactionListener?.didTapInitializeCrossSigning()
                    }
                }
            }
        }

        val crossSigningKeys = data.crossSigningInfo

        crossSigningKeys?.masterKey()?.let {
            genericWithValueItem {
                id("msk")
                titleIconResourceId(R.drawable.key_small)
                title(
                        span {
                            +"Master Key:\n"
                            span {
                                text = it.unpaddedBase64PublicKey ?: ""
                                textColor = host.colorProvider.getColorFromAttribute(R.attr.vctr_content_secondary)
                                textSize = host.dimensionConverter.spToPx(12)
                            }
                        }.toEpoxyCharSequence()
                )
            }
        }
        crossSigningKeys?.userKey()?.let {
            genericWithValueItem {
                id("usk")
                titleIconResourceId(R.drawable.key_small)
                title(
                        span {
                            +"User Key:\n"
                            span {
                                text = it.unpaddedBase64PublicKey ?: ""
                                textColor = host.colorProvider.getColorFromAttribute(R.attr.vctr_content_secondary)
                                textSize = host.dimensionConverter.spToPx(12)
                            }
                        }.toEpoxyCharSequence()
                )
            }
        }
        crossSigningKeys?.selfSigningKey()?.let {
            genericWithValueItem {
                id("ssk")
                titleIconResourceId(R.drawable.key_small)
                title(
                        span {
                            +"Self Signed Key:\n"
                            span {
                                text = it.unpaddedBase64PublicKey ?: ""
                                textColor = host.colorProvider.getColorFromAttribute(R.attr.vctr_content_secondary)
                                textSize = host.dimensionConverter.spToPx(12)
                            }
                        }.toEpoxyCharSequence()
                )
            }
        }
    }
}
