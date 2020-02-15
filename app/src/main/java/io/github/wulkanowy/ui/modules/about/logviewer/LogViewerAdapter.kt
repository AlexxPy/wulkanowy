package io.github.wulkanowy.ui.modules.about.logviewer

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R

class LogViewerAdapter : RecyclerView.Adapter<LogViewerAdapter.ViewHolder>() {

    var lines = emptyList<String>()

    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_logviewer, parent, false) as TextView)
    }

    override fun getItemCount() = lines.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = lines[position]
    }
}
