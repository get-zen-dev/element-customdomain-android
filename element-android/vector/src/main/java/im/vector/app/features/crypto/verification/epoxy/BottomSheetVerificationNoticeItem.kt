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
 *
 */
package dev.getzen.element.features.crypto.verification.epoxy

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import dev.getzen.element.R
import dev.getzen.element.core.epoxy.VectorEpoxyHolder
import dev.getzen.element.core.epoxy.VectorEpoxyModel
import im.vector.lib.core.utils.epoxy.charsequence.EpoxyCharSequence

/**
 * A action for bottom sheet.
 */
@EpoxyModelClass
abstract class BottomSheetVerificationNoticeItem : VectorEpoxyModel<BottomSheetVerificationNoticeItem.Holder>(R.layout.item_verification_notice) {

    @EpoxyAttribute
    lateinit var notice: EpoxyCharSequence

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.notice.text = notice.charSequence
    }

    class Holder : VectorEpoxyHolder() {
        val notice by bind<TextView>(R.id.itemVerificationNoticeText)
    }
}