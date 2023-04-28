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

package dev.getzen.element.features.settings.devices.v2.overview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import dev.getzen.element.R
import dev.getzen.element.core.extensions.setAttributeBackground
import dev.getzen.element.databinding.ViewSessionOverviewEntryBinding

class SessionOverviewEntryView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = ViewSessionOverviewEntryBinding.inflate(
            LayoutInflater.from(context),
            this
    )

    init {
        initBackground()
        context.obtainStyledAttributes(
                attrs,
                R.styleable.SessionOverviewEntryView,
                0,
                0
        ).use {
            setTitle(it)
            setDescription(it)
        }
    }

    private fun initBackground() {
        binding.root.setAttributeBackground(android.R.attr.selectableItemBackground)
    }

    private fun setTitle(typedArray: TypedArray) {
        val title = typedArray.getString(R.styleable.SessionOverviewEntryView_sessionOverviewEntryTitle)
        binding.sessionsOverviewEntryTitle.text = title
    }

    private fun setDescription(typedArray: TypedArray) {
        val description = typedArray.getString(R.styleable.SessionOverviewEntryView_sessionOverviewEntryDescription)
        binding.sessionsOverviewEntryDescription.text = description
    }
}
