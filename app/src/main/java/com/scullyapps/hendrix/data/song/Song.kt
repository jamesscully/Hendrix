package com.scullyapps.hendrix.data.song

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log
import com.scullyapps.hendrix.data.ID3GenreResolver
import java.io.File
import java.lang.IllegalStateException
import kotlin.system.measureTimeMillis

class Song (file : File) {
    private val TAG: String = "Song"



    var title  : String = "Unknown Track"
    var artist : String = "Unknown Artist"
    var genre  : String = "Unknown Genre"
    var duration : String = "Unknown Length"

    var artwork : Bitmap? = null





    init {
        require(isValidAudioExt(file.extension))

        val mmr = MediaMetadataRetriever()

        mmr.setDataSource(file.path)

            try {
                title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: file.nameWithoutExtension
                artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                genre = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)

                duration = millisToTimestamp(duration.toIntOrNull() ?: 0)

                val artworkBytes : ByteArray = mmr.embeddedPicture
                //artwork = BitmapFactory.decodeByteArray(artworkBytes, 0, artworkBytes.size)

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

        // takes in duration as milliseconds, returns timestamp i.e. 13:37 / 1:13:37
        fun millisToTimestamp(duration : Int) : String {

            // convert from millis to seconds
            var dur = duration / 1000
            var hours : Int; var mins : Int; var secs : Int

            secs = dur % 60
            mins = (dur - secs) / 60

            if(mins >= 60) {
                hours = mins / 60
                mins %= 60

                return ("${hours.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}")
            }

            return "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
        }
    }

}