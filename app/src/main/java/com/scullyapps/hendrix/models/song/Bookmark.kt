package com.scullyapps.hendrix.models.song

class Bookmark(hash : String, time : Int, caption : String) {
    private val TAG: String = "Bookmark";



    var timestamp : Int = 0
    var caption : String = "caption"

    // hash for what song we're supposed to be attached to
    var hash : String = "hash"

    constructor(song : Song, time: Int, caption: String) : this(song.calculateMD5(), time, caption) {}




}