package com.m_and_a_company.canelatube

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.m_and_a_company.canelatube.databinding.SongDownloadedItemBinding
import com.m_and_a_company.canelatube.domain.data.models.SongDownloaded

class DownloadedSongsAdapter(
    private val songs: List<SongDownloaded>,
    private val onClickItem: (SongDownloaded, Int) -> Unit
): RecyclerView.Adapter<DownloadedSongsAdapter.ViewHolder>(){

    private var popUpItemListener: PopupMenu.OnMenuItemClickListener? = null
    private var mOnClickSongDownloadedListener: OnClickSongDownloadedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.song_downloaded_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(songs[position], onClickItem)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    fun setOnClickSongDownloadedListener(listener: OnClickSongDownloadedListener) {
        this.mOnClickSongDownloadedListener = listener
    }

    fun removeItem(position: Int){
        notifyItemRemoved(position)
    }

    fun setPopUpItemListener(listener: PopupMenu.OnMenuItemClickListener) {
        this.popUpItemListener = listener
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), OnClickListener {

        private val binding = SongDownloadedItemBinding.bind(itemView)

        private lateinit var song: SongDownloaded

        fun bind(song: SongDownloaded, onClickItemListener: (SongDownloaded, Int) -> Unit){
            this.song = song
            binding.songDownloadedItemTitle.text =
                itemView.context.getString(R.string.title_downloaded_song_item, song.title, song.artist)
            binding.songDownloadedItemOptions.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, it)
                val menuInflate = popupMenu.menuInflater
                menuInflate.inflate(R.menu.options_file_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(popUpItemListener)
                popupMenu.show()
                onClickItemListener(song, adapterPosition)
            }
            binding.songDownloadedItemContainer.setOnClickListener(this)
            binding.songDownloadedItemImage.setImageBitmap(song.imageAlbumArt)
        }

        override fun onClick(v: View?) {
            mOnClickSongDownloadedListener?.onClickSongDownloaded(song.uri)
        }

    }

    interface OnClickSongDownloadedListener {
        fun onClickSongDownloaded(uri: Uri)
    }

}