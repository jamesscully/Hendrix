package com.scullyapps.hendrix.data.song

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log
import com.scullyapps.hendrix.data.ID3GenreResolver
import java.io.File
import java.lang.IllegalStateException
import kotlin.system.measureTimeMillis

class Song (id : Int, path : String, title : String, artist : String, genre : String, duration : Int) {
    private val TAG: String = "Song"


    var id     : Int    = 0
    var title  : String = "Unknown Track"
    var artist : String = "Unknown Artist"
    var genre  : String = "Unknown Genre"
    var duration : String = "Unknown Length"

    var artwork : Bitmap? = null

    init { }

    override fun toString(): String {
        // The Weeknd - Starboy 03:15 HipHop
        return "$artist - $title $duration $genre $"
    }

    class Builder(id : Int) {

        var id : Int = id

        var path : String = ""

        var title  : String = "Unknown Track"
        var artist : String = "Unknown Artist"
        var genre  : String = "Unknown Genre"
        var duration : Int  = 0

        var artwork : Bitmap? = null

        fun path(path : String) : Builder {
            this.path = path
            return this
        }

        fun title(title : String) : Builder {
            this.title = title
            return this
        }

        fun artist(artist : String) : Builder {
            this.artist = artist
            return this
        }

        fun genre(genre : String) : Builder {
            this.genre = genre
            return this
        }

        fun duration(duration : Int) : Builder {
            this.duration = duration
            return this
        }

        fun build() : Song {
            return Song(id, path, title, artist, genre, duration)
        }
        
    }

    // "static" methods

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