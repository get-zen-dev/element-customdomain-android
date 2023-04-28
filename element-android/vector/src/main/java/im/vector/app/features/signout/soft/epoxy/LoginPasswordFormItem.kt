/*
 * Copyright 2019 New Vector Ltd
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

package dev.getzen.element.features.signout.soft.epoxy

import android.os.Build
import android.text.Editable
import android.widget.Button
import androidx.autofill.HintConstants
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.getzen.element.R
import dev.getzen.element.core.epoxy.ClickListener
import dev.getzen.element.core.epoxy.TextListener
import dev.getzen.element.core.epoxy.VectorEpoxyHolder
import dev.getzen.element.core.epoxy.VectorEpoxyModel
import dev.getzen.element.core.epoxy.addTextChangedListenerOnce
import dev.getzen.element.core.epoxy.onClick
import dev.getzen.element.core.epoxy.setValueOnce
import dev.getzen.element.core.platform.SimpleTextWatcher
import dev.getzen.element.core.resources.StringProvider

@EpoxyModelClass
abstract class LoginPasswordFormItem : VectorEpoxyModel<LoginPasswordFormItem.Holder>(R.layout.item_login_password_form) {

    @EpoxyAttribute var passwordValue: String = ""
    @EpoxyAttribute var submitEnabled: Boolean = false
    @EpoxyAttribute var errorText: String? = null
    @EpoxyAttribute lateinit var stringProvider: StringProvider
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash) var forgetPasswordClickListener: ClickListener? = null
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash) var submitClickListener: ClickListener? = null
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash) var onPasswordEdited: TextListener? = null

    private val textChangeListener = object : SimpleTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            onPasswordEdited?.invoke(s.toString())
        }
    }

    override fun bind(holder: Holder) {
        super.bind(holder)

        setupAutoFill(holder)
        holder.passwordFieldTil.error = errorText
        holder.forgetPassword.onClick(forgetPasswordClickListener)
        holder.submit.isEnabled = submitEnabled
        holder.submit.onClick(submitClickListener)
        holder.setValueOnce(holder.passwordField, passwordValue)
        holder.passwordField.addTextChangedListenerOnce(textChangeListener)
    }

    override fun unbind(holder: Holder) {
        super.unbind(holder)
        holder.passwordField.removeTextChangedListener(textChangeListener)
    }

    private fun setupAutoFill(holder: Holder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.passwordField.setAutofillHints(HintConstants.AUTOFILL_HINT_PASSWORD)
        }
    }

    class Holder : VectorEpoxyHolder() {
        val passwordField by bind<TextInputEditText>(R.id.itemLoginPasswordFormPasswordField)
        val passwordFieldTil by bind<TextInputLayout>(R.id.itemLoginPasswordFormPasswordFieldTil)
        val forgetPassword by bind<Button>(R.id.itemLoginPasswordFormForgetPasswordButton)
        val submit by bind<Button>(R.id.itemLoginPasswordFormSubmit)
    }
}
