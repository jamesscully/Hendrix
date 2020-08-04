package com.scullyapps.hendrix.ui.songs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.scullyapps.hendrix.R
import com.scullyapps.hendrix.models.song.Song

class SongsRAdapter(private val listener: (Song) -> Unit) : RecyclerView.Adapter<SongsRAdapter.ViewHolder>() {
    private val TAG: String = "SongsRAdapter"

    var dataset = mutableListOf<Song>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.widget_songdisplay, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = dataset[position]

        holder.artist.text = song.artist
        holder.title.text = song.title
        holder.duration.text = song.timestamp

        holder.itemView.setOnClickListener { listener.invoke(song) }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    inner class ViewHolder(vh : View) : RecyclerView.ViewHolder(vh) {
        val title    : TextView = vh.findViewById(R.id.sd_song_title)
        val artist   : TextView = vh.findViewById(R.id.sd_artist_name)
        val duration : TextView = vh.findViewById(R.id.sd_song_length)
    }
}