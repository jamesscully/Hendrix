package com.scullyapps.hendrix.services

import android.app.IntentService
import android.content.ContentResolver
import android.content.Intent
import android.provider.MediaStore
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.scullyapps.hendrix.GlobalApp
import com.scullyapps.hendrix.models.artist.Artist
import com.scullyapps.hendrix.models.song.Song


class DiscoverMusic : IntentService("DiscoverMusicService") {
    private val TAG : String = "DiscoverMusicService"

    private val cResolver: ContentResolver = GlobalApp.getAppContext().contentResolver

    enum class SearchType {
        SONGS, ALBUMS, ARTISTS
    }

    override fun onHandleIntent(intent: Intent?) {
        val type = intent?.extras?.getSerializable("searchtype") as SearchType

        when(type) {
            SearchType.SONGS -> retrieveSongs()
            SearchType.ALBUMS -> retrieveAlbums()
            SearchType.ARTISTS -> retrieveArtists()
        }
    }

    private fun retrieveArtists() {
        val cursor = cResolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Artists.DEFAULT_SORT_ORDER)
        val artists = ArrayList<Artist>()

        if(cursor == null) {
            Log.d(TAG, "No artists found, returning")
            return
        }

        cursor.moveToFirst()

        while(cursor.moveToNext()) {
            val artist = Artist(cursor)

            Log.d(TAG, "Found artist $artist")

            artists.add(artist)
        }


        stopSelf()
    }

    private fun retrieveSongs() {
        val cursor = cResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER)
        val songs = ArrayList<Song>()

        // if we have no music
        if(cursor == null) {
            return
        }

        cursor.moveToFirst()

        // add all songs into array; these are serializable
        while(cursor.moveToNext()) {
            // if file is not music (ret. 0) then skip
            if(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)) == 0)
                continue

            songs.add(Song(cursor))
        }

        val intent = Intent()

        // broadcast our data to receiver
        intent.setAction("receive-songs")
        intent.putExtra("songs", songs)

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

        stopSelf()
    }

    private fun retrieveAlbums() {


    }
}