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

package dev.getzen.element.core.epoxy.profiles

import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import dev.getzen.element.core.extensions.setTextOrHide

@EpoxyModelClass
abstract class ProfileMatrixItemWithPowerLevel : ProfileMatrixItem() {

    @EpoxyAttribute var ignoredUser: Boolean = false
    @EpoxyAttribute var powerLevelLabel: CharSequence? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.editableView.isVisible = false
        holder.ignoredUserView.isVisible = ignoredUser
        holder.powerLabel.setTextOrHide(powerLevelLabel)
    }
}
