package com.m_and_a_company.canelatube.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.m_and_a_company.canelatube.DownloadedSongsAdapter
import com.m_and_a_company.canelatube.R
import com.m_and_a_company.canelatube.databinding.DownloadedSongItemBinding
import com.m_and_a_company.canelatube.domain.data.models.SongDownloaded

class   SongsDownloadedAdapter(
    private var items: List<SongDownloaded>,
    private val checkAnimIn: Boolean,
    private val deleteSongCallback: (SongDownloaded, Int) -> Unit
) : RecyclerView.Adapter<SongsDownloadedAdapter.ViewHolder>() {

    private var lastPosition = -1

    private var mOnClickSongDownloadedListener: DownloadedSongsAdapter.SelectedSongDownloadedListener? =
        null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.downloaded_song_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.render(items[position])
        if (checkAnimIn) {
            setAnim(holder.itemView, position, android.R.anim.slide_in_left)
        }

    }

    override fun getItemCount() = items.size

    fun setOnClickSongDownloadedListener(listener: DownloadedSongsAdapter.SelectedSongDownloadedListener) {
        this.mOnClickSongDownloadedListener = listener
    }

    private fun setAnim(viewToAnimate: View, position: Int, animation: Int) {
        if (position > lastPosition) {
            val animationUtils = AnimationUtils.loadAnimation(viewToAnimate.context, animation)
            viewToAnimate.startAnimation(animationUtils)
            lastPosition = position
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = DownloadedSongItemBinding.bind(itemView)

        /**
         * Establece los atributos de la canción obtenida.
         * @param song Canción a renderer
         */
        fun render(song: SongDownloaded) {
            binding.apply {
                downloadedItemTitle.text = song.artist
                downloadedItemImage.setImageBitmap(song.imageAlbumArt)
                downloadedItemDelete.setOnClickListener {
                    deleteSongCallback.invoke(song, adapterPosition)
                }
                downloadedItemPlay.setOnClickListener {
                    mOnClickSongDownloadedListener?.onSelectedSongDownload(song.uri)
                }
            }
        }

    }

}