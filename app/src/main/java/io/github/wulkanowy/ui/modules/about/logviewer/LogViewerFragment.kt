package io.github.wulkanowy.ui.modules.about.logviewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import kotlinx.android.synthetic.main.fragment_logviewer.*
import javax.inject.Inject

class LogViewerFragment : BaseFragment(), LogViewerView, MainView.TitledView {

    @Inject
    lateinit var presenter: LogViewerPresenter

    private val logAdapter = LogViewerAdapter()

    override val titleStringId: Int
        get() = R.string.logviewer_title

    companion object {
        fun newInstance() = LogViewerFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_logviewer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        with(logViewerRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = logAdapter
        }

        logViewRefreshButton.setOnClickListener { presenter.onRefreshClick() }
    }

    override fun setLines(lines: List<String>) {
        logAdapter.lines = lines
        logAdapter.notifyDataSetChanged()
        logViewerRecycler.scrollToPosition(lines.size - 1)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
