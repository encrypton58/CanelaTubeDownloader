package com.m_and_a_company.canelatube.ui.svdn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.m_and_a_company.canelatube.R
import com.m_and_a_company.canelatube.databinding.DownloadFormatItemBinding
import com.m_and_a_company.canelatube.network.domain.model.Format
import java.lang.StringBuilder

class DownloadFormatsAdapter: RecyclerView.Adapter<DownloadFormatsAdapter.ViewHolder>() {

    private var formats: List<Format> = listOf()
    protected var clickListener: DownloadFormatsAdapterListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(R.layout.download_format_item,
                parent,
                false)
        )
    }

    override fun getItemCount(): Int {
        return formats.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.render(formats[position])
    }

    fun setFormats(formats: List<Format>) {
        this.formats = formats
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: DownloadFormatsAdapterListener) {
        this.clickListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val mBinding = DownloadFormatItemBinding.bind(itemView)
        private lateinit var mFormat: Format

        fun render(format: Format) {
            mFormat = format
            mBinding.downloadFormatItemTitle.text = StringBuilder(format.type + " " + format.quality)
            mBinding.downloadFormatItemSize.text = format.size
            mBinding.downloadFormatItemLl.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListener?.onFormatClicked(mFormat)
        }

    }

    interface DownloadFormatsAdapterListener {
        fun onFormatClicked(format: Format)
    }
}