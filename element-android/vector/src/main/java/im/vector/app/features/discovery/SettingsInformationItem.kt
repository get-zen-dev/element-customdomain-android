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
package dev.getzen.element.features.discovery

import android.widget.TextView
import androidx.annotation.ColorInt
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import dev.getzen.element.R
import dev.getzen.element.core.epoxy.VectorEpoxyHolder
import dev.getzen.element.core.epoxy.VectorEpoxyModel

@EpoxyModelClass
abstract class SettingsInformationItem : VectorEpoxyModel<SettingsInformationItem.Holder>(R.layout.item_settings_information) {

    @EpoxyAttribute
    lateinit var message: String

    @EpoxyAttribute
    @ColorInt
    var textColor: Int = 0

    override fun bind(holder: Holder) {
        super.bind(holder)

        holder.textView.text = message
        holder.textView.setTextColor(textColor)
    }

    class Holder : VectorEpoxyHolder() {
        val textView by bind<TextView>(R.id.settings_item_information)
    }
}