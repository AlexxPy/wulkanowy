package io.github.wulkanowy.ui.modules.about.logviewer

import io.github.wulkanowy.data.repositories.logger.LoggerRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class LogViewerPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val loggerRepository: LoggerRepository
) : BasePresenter<LogViewerView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: LogViewerView) {
        super.onAttachView(view)
        view.initView()
        loadLogFile()
    }

    private fun loadLogFile() {
        disposable.add(loggerRepository.getLastLogContent()
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                view?.setContent(it)
            }, {
                Timber.i("Loading log file result: An exception occurred")
                errorHandler.dispatch(it)
            }))
    }
}
