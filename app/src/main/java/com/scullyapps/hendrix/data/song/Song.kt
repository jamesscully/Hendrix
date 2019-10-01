package com.scullyapps.hendrix.data.song

import java.io.File

class Song (file : File) {
    private val TAG: String = "Song"

    init {
        require(isValidAudio(file.extension))
    }





    fun isValidAudio(fileExt : String) : Boolean {
        val acceptedFileTypes : Array<String> = arrayOf("mp3", "m4a")

        return acceptedFileTypes.contains(fileExt)
    }

}