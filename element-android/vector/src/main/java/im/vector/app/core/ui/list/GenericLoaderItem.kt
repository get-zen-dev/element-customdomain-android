/*
 * Copyright 2020 New Vector Ltd
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

import com.airbnb.epoxy.EpoxyModelClass
import dev.getzen.element.R
import dev.getzen.element.core.epoxy.VectorEpoxyHolder
import dev.getzen.element.core.epoxy.VectorEpoxyModel

/**
 * A generic list item header left aligned with notice color.
 */
@EpoxyModelClass
abstract class GenericLoaderItem : VectorEpoxyModel<GenericLoaderItem.Holder>(R.layout.item_generic_loader) {

    // Maybe/Later add some style configuration, SMALL/BIG ?

    class Holder : VectorEpoxyHolder()
}