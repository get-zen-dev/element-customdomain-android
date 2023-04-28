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
package dev.getzen.element.features.settings.ignored

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import dev.getzen.element.R
import dev.getzen.element.core.epoxy.ClickListener
import dev.getzen.element.core.epoxy.VectorEpoxyHolder
import dev.getzen.element.core.epoxy.VectorEpoxyModel
import dev.getzen.element.core.epoxy.onClick
import dev.getzen.element.core.extensions.setTextOrHide
import dev.getzen.element.features.home.AvatarRenderer
import org.matrix.android.sdk.api.util.MatrixItem

/**
 * A list item for User.
 */
@EpoxyModelClass
abstract class UserItem : VectorEpoxyModel<UserItem.Holder>(R.layout.item_user) {

    @EpoxyAttribute
    lateinit var avatarRenderer: AvatarRenderer

    @EpoxyAttribute
    lateinit var matrixItem: MatrixItem

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var itemClickAction: ClickListener? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.root.onClick(itemClickAction)

        avatarRenderer.render(matrixItem, holder.avatarImage)
        holder.userIdText.setTextOrHide(matrixItem.id)
        holder.displayNameText.setTextOrHide(matrixItem.displayName)
    }

    class Holder : VectorEpoxyHolder() {
        val root by bind<View>(R.id.itemUserRoot)
        val avatarImage by bind<ImageView>(R.id.itemUserAvatar)
        val userIdText by bind<TextView>(R.id.itemUserId)
        val displayNameText by bind<TextView>(R.id.itemUserName)
    }
}
