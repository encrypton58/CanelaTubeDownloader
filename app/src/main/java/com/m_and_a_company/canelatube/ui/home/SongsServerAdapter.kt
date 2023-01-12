package com.m_and_a_company.canelatube.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.m_and_a_company.canelatube.R
import com.m_and_a_company.canelatube.databinding.SongItemBinding
import com.m_and_a_company.canelatube.domain.network.model.Song
import com.squareup.picasso.Picasso

class SongsServerAdapter(
    private val actionSongListener: ActionsSongServer,
    private val items: List<Song>): RecyclerView.Adapter<SongsServerAdapter.ViewHolder>() {

    private val listener by lazy {
        actionSongListener
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

        /**
         * Establece los atributos de la canción obtenida.
         * @param song Canción a renderear
         */
        fun render(song: Song){
            binding.apply {
                songItemTitle.text = song.name
                songItemSize.text = song.size
                Picasso.get().load(song.image).into(songItemImage)
                songItemDownload.setOnClickListener { listener.onDownloadSong(song.id) }
                songItemDelete.setOnClickListener{ listener.onDeleteSongSever(song.id, adapterPosition) }
            }
        }

    }

    /**
     * Escuchante de eventos del adaptador
     */
    interface ActionsSongServer {
        /**
         * Descarga la canción
         * @param id ID de la canción a descargar
         */
        fun onDownloadSong(id: Int)

        /**
         * Elimina la canción guardada en el servidor
         * @param id ID de la canción a eliminar
         * @param position Posición de la cancion a eliminar del adaptador
         */
        fun onDeleteSongSever(id: Int, position: Int)
    }

}