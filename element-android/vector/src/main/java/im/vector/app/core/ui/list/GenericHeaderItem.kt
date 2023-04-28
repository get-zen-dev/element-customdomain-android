/*
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
package dev.getzen.element.core.ui.list

import android.widget.TextView
import androidx.annotation.ColorInt
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import dev.getzen.element.R
import dev.getzen.element.core.epoxy.VectorEpoxyHolder
import dev.getzen.element.core.epoxy.VectorEpoxyModel
import dev.getzen.element.core.extensions.setTextOrHide
import dev.getzen.element.features.themes.ThemeUtils

/**
 * A generic list item header left aligned with notice color.
 */
@EpoxyModelClass
abstract class GenericHeaderItem : VectorEpoxyModel<GenericHeaderItem.Holder>(R.layout.item_generic_header) {

    @EpoxyAttribute
    var text: String? = null

    @EpoxyAttribute
    @ColorInt
    var textColor: Int? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.text.setTextOrHide(text)
        if (textColor != null) {
            holder.text.setTextColor(textColor!!)
        } else {
            holder.text.setTextColor(ThemeUtils.getColor(holder.view.context, R.attr.vctr_notice_text_color))
        }
    }

    class Holder : VectorEpoxyHolder() {
        val text by bind<TextView>(R.id.itemGenericHeaderText)
    }
}
