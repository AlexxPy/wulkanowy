package io.github.wulkanowy.ui.modules.message.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.data.repositories.message.MessageFolder
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.message.MessageFragment
import io.github.wulkanowy.ui.modules.message.MessageItem
import io.github.wulkanowy.ui.modules.message.preview.MessagePreviewFragment
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_message_tab.*
import javax.inject.Inject

class MessageTabFragment : BaseFragment(), MessageTabView {

    @Inject
    lateinit var presenter: MessageTabPresenter

    @Inject
    lateinit var tabAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        const val MESSAGE_TAB_FOLDER_ID = "message_tab_folder_id"

        fun newInstance(folder: MessageFolder): MessageTabFragment {
            return MessageTabFragment().apply {
                arguments = Bundle().apply {
                    putString(MESSAGE_TAB_FOLDER_ID, folder.name)
                }
            }
        }
    }

    override val noSubjectString: String
        get() = getString(R.string.message_no_subject)

    override val isViewEmpty
        get() = tabAdapter.isEmpty

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message_tab, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = messageTabRecycler
        presenter.onAttachView(this, MessageFolder.valueOf(
            (savedInstanceState ?: arguments)?.getString(MESSAGE_TAB_FOLDER_ID).orEmpty()
        ))
    }

    override fun initView() {
        tabAdapter.setOnItemClickListener { presenter.onMessageItemSelected(it) }

        messageTabRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = tabAdapter
            addItemDecoration(FlexibleItemDecoration(context)
                .withDefaultDivider()
                .withDrawDividerOnLastItem(false)
            )
        }
        messageTabSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
        messageTabErrorRetry.setOnClickListener { presenter.onRetry() }
        messageTabErrorDetails.setOnClickListener { presenter.onDetailsClick() }
    }

    override fun updateData(data: List<MessageItem>) {
        tabAdapter.updateDataSet(data)
    }

    override fun updateItem(item: AbstractFlexibleItem<*>) {
        tabAdapter.updateItem(item)
    }

    override fun clearView() {
        tabAdapter.clear()
    }

    override fun showProgress(show: Boolean) {
        messageTabProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        messageTabSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        messageTabRecycler.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showEmpty(show: Boolean) {
        messageTabEmpty.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showErrorView(show: Boolean) {
        messageTabError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        messageTabErrorMessage.text = message
    }

    override fun showRefresh(show: Boolean) {
        messageTabSwipe.isRefreshing = show
    }

    override fun openMessage(messageId: Long) {
        (activity as? MainActivity)?.pushView(MessagePreviewFragment.newInstance(messageId))
    }

    override fun notifyParentDataLoaded() {
        (parentFragment as? MessageFragment)?.onChildFragmentLoaded()
    }

    fun onParentLoadData(forceRefresh: Boolean) {
        presenter.onParentViewLoadData(forceRefresh)
    }

    fun onParentDeleteMessage() {
        presenter.onDeleteMessage()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(MESSAGE_TAB_FOLDER_ID, presenter.folder.name)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
