package io.github.wulkanowy.ui.modules.about.logviewer

import io.github.wulkanowy.ui.base.BaseView

interface LogViewerView : BaseView {

    fun initView()

    fun setLines(lines: List<String>)
}
