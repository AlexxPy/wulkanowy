package io.github.wulkanowy.ui.modules.login.recover

import io.github.wulkanowy.ui.base.BaseView

interface LoginRecoverView : BaseView {

    val recoverHostValue: String

    val recoverNameValue: String

    val recoverSymbolValue: String

    val emailHintString: String

    val loginPeselEmailHintString: String

    val invalidEmailString: String

    fun initView()

    fun setDefaultCredentials(username: String)

    fun clearUsernameError()

    fun clearSymbolError()

    fun showSymbol(show: Boolean)

    fun setErrorNameRequired()

    fun setUsernameHint(hint: String)

    fun setUsernameError(message: String)

    fun setSymbolError(focus: Boolean)

    fun showSoftKeyboard()

    fun hideSoftKeyboard()

    fun showProgress(show: Boolean)

    fun showRecoverForm(show: Boolean)

    fun showCaptcha(show: Boolean)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun showSuccessView(show: Boolean)

    fun setSuccessMessage(message: String)

    fun setSuccessTitle(title: String)

    fun loadReCaptcha(siteKey: String, url: String)
}
