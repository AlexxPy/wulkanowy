package io.github.wulkanowy.ui.modules.about.logviewer

import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import javax.inject.Inject

class LogViewerPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<LogViewerView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: LogViewerView) {
        super.onAttachView(view)
        view.initView()
    }
}
