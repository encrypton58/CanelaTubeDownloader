package com.m_and_a_company.canelatube.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.m_and_a_company.canelatube.R
import com.m_and_a_company.canelatube.databinding.SongItemBinding
import com.m_and_a_company.canelatube.domain.network.model.Song
import com.squareup.picasso.Picasso

class SongsAdapter(
    private val onClickSongItemListener: OnClickSongItemListener,
    private val items: List<Song>): RecyclerView.Adapter<SongsAdapter.ViewHolder>() {

    //private val itemsList = items
    private val listener by lazy {
        onClickSongItemListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.song_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.render(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val binding = SongItemBinding.bind( itemView )

        fun render(song: Song){
            binding.apply {
                songItemTitle.text = song.name
                songItemSize.text = song.size
                Picasso.get().load(song.image).into(songItemImage)
                songItemDownload.setOnClickListener { listener.onClickSongItem(song.id) }
                songItemDelete.setOnClickListener{ listener.onClickDeleteSong(song.id, adapterPosition) }
            }
        }

    }

    interface OnClickSongItemListener {
        fun onClickSongItem(id: Int)
        fun onClickDeleteSong(id: Int, position: Int)
    }

}