package com.scullyapps.hendrix.data.song

import android.media.MediaMetadataRetriever
import android.util.Log
import com.scullyapps.hendrix.data.ID3GenreResolver
import java.io.File
import java.lang.IllegalStateException

class Song (file : File) {
    private val TAG: String = "Song"



    var title  : String = "Unknown Track"
    var artist : String = "Unknown Artist"
    var genre  : String = "Unknown Genre"
    var duration : String = "Unknown Length"






    init {
        require(isValidAudioExt(file.extension))

        val mmr = MediaMetadataRetriever()

        mmr.setDataSource(file.path)

        //Log.d(TAG, "Editing file ${file.name} path: ${file.path}")

        try {
            title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            genre = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)

            var te : String = ""

            // if no freetext genre is found, then it will be returned as, i.e. (00) for blues
            if(genre.matches( Regex("^\\([0-9]*\\)\$") )) {
                genre = ID3GenreResolver.codeToString(genre.substring(1, genre.length - 1).toInt())
            }

        } catch (e: IllegalStateException) {

        }



    }

    override fun toString(): String {

        // The Weeknd - Starboy 03:15 HipHop

        return "$artist - $title $duration $genre $"
    }

    companion object {
        fun isValidAudioExt(fileExt: String): Boolean {
            val acceptedFileTypes: Array<String> = arrayOf("mp3", "m4a")

            return acceptedFileTypes.contains(fileExt)
        }
    }

}