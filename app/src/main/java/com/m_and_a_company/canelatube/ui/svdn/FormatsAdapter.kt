package com.m_and_a_company.canelatube.ui.svdn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.m_and_a_company.canelatube.R
import com.m_and_a_company.canelatube.databinding.FormatItemBinding
import com.m_and_a_company.canelatube.domain.data.models.Format
import com.m_and_a_company.canelatube.domain.data.models.VideoFormat
import com.m_and_a_company.canelatube.ui.enums.TypeDownload

class FormatsAdapter(
    onClickFormatItemListener: OnClickFormatItemListener,
    private val typeDownloadToRender: TypeDownload
    ) :
    RecyclerView.Adapter<FormatsAdapter.FormatsHolder>() {

    private val formats = arrayListOf<Format>()
    private val formatsVideo = arrayListOf<VideoFormat>()

    private val onClickFormatItem: OnClickFormatItemListener by lazy {
        onClickFormatItemListener
    }

    fun setFormats(formatReceived: List<Format>) {
        formats.clear()
        formats.addAll(formatReceived)
    }

    fun setVideoFormats(formatsReceived: List<VideoFormat>) {
        formatsVideo.clear()
        formatsVideo.addAll(formatsReceived)
    }

    inner class FormatsHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val mBinding = FormatItemBinding.bind(itemView)

        private val format by lazy {
            if(typeDownloadToRender == TypeDownload.VIDEO) {
                formatsVideo[adapterPosition]
            } else {
                formats[adapterPosition]
            }
        }

        fun renderVideo(format: VideoFormat) {
            val data = "${format.format} ${format.res}"
            mBinding.apply {
                formatDownloadFormArb.text = data
                formatDownloadMb.text = format.size
                formatDownloadItemCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.canela_variant
                    )
                )
                formatDownloadItemCard.setOnClickListener(this@FormatsHolder)
            }
        }

        fun render(format: Format) {
            val dataFromFormat = "${format.format} ${format.abr}"
            mBinding.apply {
                formatDownloadFormArb.text = dataFromFormat
                formatDownloadMb.text = format.size
                formatDownloadItemCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.canela_variant
                    )
                )
                formatDownloadItemCard.setOnClickListener(this@FormatsHolder)
            }
        }

        override fun onClick(p0: View?) {
            if(typeDownloadToRender == TypeDownload.VIDEO) {
                onClickFormatItem.onClickFormatItem((format as VideoFormat).itag)
            } else {
                onClickFormatItem.onClickFormatItem((format as Format).itag)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormatsHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return FormatsHolder(
            layoutInflater.inflate(
                R.layout.format_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FormatsHolder, position: Int) {
        if(typeDownloadToRender == TypeDownload.VIDEO) {
            holder.renderVideo(formatsVideo[position])
        } else {
            holder.render(formats[position])
        }
    }

    override fun getItemCount() = if(typeDownloadToRender == TypeDownload.VIDEO) {
        formatsVideo.size
    } else {
        formats.size
    }

    interface OnClickFormatItemListener {
        fun onClickFormatItem(iTag: Int)
    }

}